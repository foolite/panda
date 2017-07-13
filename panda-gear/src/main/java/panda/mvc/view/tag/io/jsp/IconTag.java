package panda.mvc.view.tag.io.jsp;

import panda.mvc.ActionContext;
import panda.mvc.view.tag.TagBean;
import panda.mvc.view.tag.ui.Icon;

public class IconTag extends AbstractTag {
	private static final long serialVersionUID = 1L;

	protected TagBean getBean(ActionContext ac) {
		return getBean(ac, Icon.class);
	}
}
