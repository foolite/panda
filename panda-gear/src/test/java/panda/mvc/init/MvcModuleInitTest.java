package panda.mvc.init;

import static org.junit.Assert.*;

import org.junit.Test;

import panda.mvc.AbstractMvcTestCase;
import panda.mvc.init.conf.MainModule;

public class MvcModuleInitTest extends AbstractMvcTestCase {

	protected void initServletConfig() {
		servletConfig.addInitParameter("modules", MainModule.class.getName());
	}

	@Test
	public void test_array_long_param() throws Exception {
		request.setRequestURI("/param/json");
		request.setParameter("ids", new String[] { "1", "2", "3" });
		servlet.service(request, response);
		String re = response.getContentAsString();
		assertEquals("{\"success\":true,\"result\":[\"1\",\"2\",\"3\"]}", re);
	}

	@Test
	public void test_array_long_param_raw() throws Exception {
		request.setRequestURI("/param/raw");
		request.setParameter("nm", "xyz");
		request.setParameter("ids", new String[] { "1", "2", "3" });
		servlet.service(request, response);
		String re = response.getContentAsString();
		assertEquals("[1,2,3]", re);
	}

	@Test
	public void test_array_long_param_inc() throws Exception {
		request.setRequestURI("/param/inc");
		request.setParameter("ids", new String[] { "1", "2", "3" });
		servlet.service(request, response);
		String re = response.getContentAsString();
		assertEquals("{\"success\":true,\"params\":{\"ids\":[\"1\",\"2\",\"3\"]},\"result\":[\"1\",\"2\",\"3\"]}", re);
	}
}
