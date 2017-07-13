package panda.mvc.view.tag.io.jsp;

import panda.mvc.ActionContext;
import panda.mvc.view.tag.TagBean;
import panda.mvc.view.tag.ui.Uploader;

public class UploaderTag extends AbstractTag {
	private static final long serialVersionUID = 1L;

	protected TagBean getBean(ActionContext ac) {
		return getBean(ac, Uploader.class);
	}
}
