package panda.mvc.vfs;

import panda.ioc.annotation.IocBean;
import panda.ioc.annotation.IocInject;
import panda.mvc.MvcConstants;
import panda.vfs.FileStore;
import panda.vfs.gae.GaeFileStore;

@IocBean(type=FileStore.class)
public class MvcGaeFileStore extends GaeFileStore {

	/**
	 * @param bucket the bucket to set
	 */
	@Override
	@IocInject(value=MvcConstants.FILESTORE_GCS_BUCKET, required=false)
	public void setBucket(String bucket) {
		super.setBucket(bucket);
	}

	/**
	 * @param prefix the prefix to set
	 */
	@Override
	@IocInject(value=MvcConstants.FILESTORE_GCS_PREFIX, required=false)
	public void setPrefix(String prefix) {
		super.setPrefix(prefix);
	}
}
