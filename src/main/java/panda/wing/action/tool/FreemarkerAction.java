package panda.wing.action.tool;

import javax.servlet.http.HttpServletResponse;

import panda.ioc.annotation.IocInject;
import panda.mvc.annotation.view.Ok;
import panda.mvc.view.VoidView;
import panda.mvc.view.ftl.MvcFreemarkerHelper;
import panda.servlet.HttpServlets;
import panda.wing.mvc.AbstractAction;

public class FreemarkerAction extends AbstractAction {

	@IocInject
	MvcFreemarkerHelper freemarker;
	
	@Ok("ftl:${result}")
	public Object execute() throws Exception {
		String location = HttpServlets.getRelativeURI(getRequest());
		if (!freemarker.hasTemplate(location)) {
			getResponse().sendError(HttpServletResponse.SC_NOT_FOUND, location);
			return VoidView.INSTANCE;
		}
		return location;
	}

}
