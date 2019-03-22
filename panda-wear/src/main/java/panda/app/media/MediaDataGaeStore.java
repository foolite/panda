package panda.app.media;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;

import com.google.appengine.tools.cloudstorage.GcsFileMetadata;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.ListItem;
import com.google.appengine.tools.cloudstorage.ListOptions;
import com.google.appengine.tools.cloudstorage.ListResult;
import com.google.appengine.tools.cloudstorage.RetryParams;

import panda.app.constant.MVC;
import panda.app.entity.Media;
import panda.dao.Dao;
import panda.image.ImageWrapper;
import panda.image.Images;
import panda.io.FileNames;
import panda.io.Streams;
import panda.ioc.annotation.IocBean;
import panda.ioc.annotation.IocInject;
import panda.lang.Arrays;
import panda.lang.Strings;
import panda.log.Log;
import panda.log.Logs;
import panda.vfs.FileStores;

@IocBean(type=MediaDataStore.class)
public class MediaDataGaeStore extends AbstractMediaDataStore {
	private static final Log log = Logs.getLog(MediaDataGaeStore.class);

	/** Used below to determine the size of chucks to read in. Should be > 1kb and < 10MB */
	private static final int BUFFER_SIZE = 2 * 1024 * 1024;
	
	protected GcsService gcsService;
	
	@IocInject(value=MVC.MEDIA_GCS_BUCKET, required=false)
	protected String bucket;

	@IocInject(value=MVC.MEDIA_GCS_PREFIX, required=false)
	protected String prefix = "media/";

	public MediaDataGaeStore() {
		gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
			.initialRetryDelayMillis(10).retryMaxAttempts(10).totalRetryPeriodMillis(15000)
			.build());
	}

	/**
	 * @return the bucket
	 */
	public String getBucket() {
		return bucket;
	}

	/**
	 * @param bucket the bucket to set
	 */
	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	protected String getFileName(Media m, int sz) {
		StringBuilder name = new StringBuilder();
		
		name.append(prefix).append(m.getId()).append('/');
		if (sz == Medias.ORIGINAL) {
			name.append(m.getName());
		}
		else {
			name.append(FileNames.getBaseName(m.getName())).append('.').append(sz);
			String ext = FileNames.getExtension(m.getName());
			if (Strings.isNotEmpty(ext)) {
				name.append('.').append(ext);
			}
		}

		return name.toString();
	}
	
	protected GcsFilename toGcsFilename(Media m, int sz) {
		return new GcsFilename(bucket, getFileName(m, sz));
	}

	protected byte[] readData(GcsFilename gfn) throws IOException {
		GcsInputChannel gic = gcsService.openPrefetchingReadChannel(gfn, 0, BUFFER_SIZE);
		InputStream in = Channels.newInputStream(gic);
		try {
			return Streams.toByteArray(in);
		}
		finally {
			Streams.safeClose(in);
		}
	}

	protected boolean exists(GcsFilename gfn) {
		try {
			GcsFileMetadata gfm = gcsService.getMetadata(gfn);
			return gfm != null;
		}
		catch (IOException e) {
			log.error("Failed to get metadata of " + gfn.getBucketName() + ": " + gfn.getObjectName(), e);
			return false;
		}
	}

	@Override
	public MediaData find(Dao dao, Media m, int sz) {
		byte[] data = null;
		
		GcsFilename gfn = toGcsFilename(m, sz);
		if (exists(gfn)) {
			try {
				data = readData(gfn);
			}
			catch (IOException e) {
				log.error("Failed to read data of media [" + m.getId() + "] (" + sz + ")", e);
				return null;
			}
		}
		else if (sz != Medias.ORIGINAL) {
			GcsFilename org = toGcsFilename(m, Medias.ORIGINAL);
			if (!exists(org)) {
				return null;
			}

			try {
				// resize
				byte[] odata = readData(org);
				ImageWrapper iw = Images.i().read(odata);
				iw = iw.resize(sz);
				data = iw.getData();

				// save
				save(gfn, data);
			}
			catch (Exception e) {
				log.error("Failed to save data of media [" + m.getId() + "] (" + sz + ")", e);
				return null;
			}
		}
		else {
			return null;
		}

		MediaData md = new MediaData();
		md.setMid(m.getId());
		md.setMsz(sz);
		md.setSize(data.length);
		md.setData(data);

		return md;
	}

	protected void save(GcsFilename gfn, byte[] data) throws IOException {
		GcsFileOptions gfo = GcsFileOptions.getDefaultInstance();
		GcsOutputChannel goc = gcsService.createOrReplace(gfn, gfo);
		goc.write(ByteBuffer.wrap(data));
		goc.close();
	}
	
	@Override
	public void save(Dao dao, Media m) {
		GcsFilename gfn = toGcsFilename(m, Medias.ORIGINAL);

		try {
			byte[] data = FileStores.toByteArray(m.getFile());

			save(gfn, data);
		}
		catch (IOException e) {
			throw new RuntimeException("Failed to save media data of [" + m.getId() + "]", e);
		}
	}

	@Override
	public void delete(Dao dao, String... mids) {
		if (Arrays.isEmpty(mids)) {
			return;
		}
		
		for (String mid : mids) {
			try {
				ListOptions.Builder lob = new ListOptions.Builder();
				ListResult lr = gcsService.list(bucket, lob.setPrefix(prefix + mid + '/').setRecursive(true).build());
				while (lr.hasNext()) {
					ListItem i = lr.next();

					GcsFilename gfn = new GcsFilename(bucket, i.getName());
					gcsService.delete(gfn);
				}
			}
			catch (Exception e) {
				log.error("Failed to delete media data for [" + mid + "]", e);
			}
		}
	}
}
