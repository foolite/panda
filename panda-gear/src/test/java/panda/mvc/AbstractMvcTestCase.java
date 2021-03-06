package panda.mvc;

import javax.servlet.ServletException;

import org.junit.After;
import org.junit.Before;

import panda.mvc.mock.Mock;
import panda.servlet.mock.MockHttpServletRequest;
import panda.servlet.mock.MockHttpServletResponse;
import panda.servlet.mock.MockHttpSession;
import panda.servlet.mock.MockServletConfig;
import panda.servlet.mock.MockServletContext;

public abstract class AbstractMvcTestCase {

	protected MvcServlet servlet;

	protected MockHttpServletRequest request;

	protected MockHttpServletResponse response;

	protected MockHttpSession session;

	protected MockServletContext servletContext;

	protected MockServletConfig servletConfig;

	@Before
	public void init() {
		servletContext = Mock.servletContext();
		servletConfig = Mock.servletConfig(servletContext, "test");
		
		initServletConfig();
		servlet = new MvcServlet();
		try {
			servlet.init(servletConfig);
		}
		catch (ServletException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		session = Mock.servletSession(servletContext);
		newreq();
	}

	protected void newreq() {
		request = Mock.servletRequest(servletContext);
		request.setMethod("GET");
		request.setContextPath("");
		request.setSession(session);
		response = Mock.servletResponse();
	}

	protected abstract void initServletConfig();

	@After
	public void destroy() {
		if (servlet != null) {
			servlet.destroy();
		}
	}

}
