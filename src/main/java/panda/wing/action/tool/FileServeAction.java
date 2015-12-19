package panda.wing.action.tool;

import panda.filepool.FileItem;
import panda.filepool.FilePool;
import panda.ioc.annotation.IocInject;
import panda.mvc.annotation.At;
import panda.mvc.annotation.param.Param;

/**
 * File download/upload Action
 */
@At("/file")
public class FileServeAction extends DataServeAction {
	@IocInject
	protected FilePool filePool;
	
	protected int bufferSize = 4096;
	protected boolean cache = true;
	protected boolean attachment = true;

	/**
	 * @return the cache
	 */
	public boolean isCache() {
		return cache;
	}

	/**
	 * @param cache the cache to set
	 */
	public void setCache(boolean cache) {
		this.cache = cache;
	}

	/**
	 * @return the bufferSize
	 */
	public int getBufferSize() {
		return bufferSize;
	}

	/**
	 * @param bufferSize the bufferSize to set
	 */
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	/**
	 * @return the attachment
	 */
	public boolean isAttachment() {
		return attachment;
	}

	/**
	 * @param attachment the attachment to set
	 */
	public void setAttachment(boolean attachment) {
		this.attachment = attachment;
	}

	/**
	 * upload
	 * 
	 * @return file
	 * @throws Exception if an error occurs
	 */
	@At
	public FileItem upload(@Param("file") FileItem file) throws Exception {
		return file;
	}

}
