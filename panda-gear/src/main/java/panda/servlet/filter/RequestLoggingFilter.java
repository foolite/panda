package panda.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import panda.lang.Strings;
import panda.lang.time.DateTimes;
import panda.lang.time.StopWatch;
import panda.log.Log;
import panda.log.Logs;
import panda.servlet.HttpServlets;


/**
 * RequestLoggingFilter
 * 
 * <pre>
 * Log request information
 *
 * &lt;filter&gt;
 *  &lt;filter-name&gt;logging-filter&lt;/filter-name&gt;
 *  &lt;filter-class&gt;panda.servlet.filters.RequestLoggingFilter&lt;/filter-class&gt;
 *  &lt;init-param&gt;            
 *    &lt;param-name&gt;accessLogName&lt;/param-name&gt;            
 *    &lt;param-value&gt;access&lt;/param-value&gt;        
 *  &lt;/init-param&gt;
 * &lt;/filter&gt;
 * &lt;filter-mapping&gt;
 *  &lt;filter-name&gt;logging-filter&lt;/filter-name&gt;
 *  &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 * </pre>
 *
 * Access Log Format:
 * <ul>
 *     <li><b>%t</b> - Date and time, in Common Log Format</li>
 *     <li><b>%a</b> - Remote IP address</li>
 *     <li><b>%h</b> - Remote host name</li>
 *     <li><b>%p</b> - Request protocol</li>
 *     <li><b>%m</b> - Request method (GET, POST, etc.)</li>
 *     <li><b>%q</b> - Query string (prepended with a '?' if it exists)</li>
 *     <li><b>%s</b> - User session ID</li>
 *     <li><b>%u</b> - Remote user that was authenticated (if any), else '-'</li>
 *     <li><b>%A</b> - Local IP address</li>
 *     <li><b>%V</b> - Local server name</li>
 *     <li><b>%P</b> - Local port on which this request was received</li>
 *     <li><b>%S</b> - HTTP status code of the response</li>
 *     <li><b>%T</b> - Time taken to process the request, in milliseconds</li>
 *     <li><b>%I</b> - current request thread name (can compare later with stacktraces)</li>
 *     <li><b>%u</b> - Requested URL path</li>
 * </ul> 
 * 
 */
public class RequestLoggingFilter implements Filter {
	private static Log log = Logs.getLog(RequestLoggingFilter.class);

	/**
	 * REQUEST_TIME = "panda.servlet.request.time";
	 */
	public static final String REQUEST_TIME = "panda.servlet.request.time";

	private Log accessLog;
	private String[] accessFormat;

	private static class FilterResponseWrapper extends HttpServletResponseWrapper {
		private int status = SC_OK;
		
		public FilterResponseWrapper(HttpServletResponse res) throws IOException {
			super(res);
		}

		@Override
		public void sendError(int sc, String msg) throws IOException {
			status = sc;
			super.sendError(sc, msg);
		}

		@Override
		public void sendError(int sc) throws IOException {
			status = sc;
			super.sendError(sc);
		}

		@Override
		public void setStatus(int sc) {
			status = sc;
			super.setStatus(sc);
		}

		@Override
		@SuppressWarnings("deprecation")
		public void setStatus(int sc, String sm) {
			status = sc;
			super.setStatus(sc, sm);
		}
	}
	
	/**
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig config) throws ServletException {
		String accessLogName = config.getInitParameter("accessLogName");
		if (Strings.isNotEmpty(accessLogName)) {
			accessLog = Logs.getLog(accessLogName);
			if (!accessLog.isInfoEnabled()) {
				accessLog = null;
			}
		}

		if (accessLog != null) {
			String accessFormat = config.getInitParameter("accessLogFormat");
			if (Strings.isEmpty(accessFormat)) {
				accessFormat = "%t %a %h %p %m %s %A %V %P %S %T %I %u";
			}
			accessFormat = Strings.remove(accessFormat, '%');
			this.accessFormat = Strings.split(accessFormat);
		}
	}

	/**
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest)req;

		FilterResponseWrapper frw = null;
		if (accessLog != null) {
			frw = new FilterResponseWrapper((HttpServletResponse)res);
		}

		StopWatch sw = new StopWatch();

		request.setAttribute(REQUEST_TIME, sw.getStartTime());

		logRequest(request);
		
		try {
			chain.doFilter(req, frw == null ? res : frw);
		}
		catch (Throwable e) {
			HttpServlets.logException(request, e);
			if (frw != null) {
				frw.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
			else {
				((HttpServletResponse)res).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
		finally {
			if (accessLog != null) {
				logAccess(request, frw, sw);
			}
		}
	}

	private void logRequest(HttpServletRequest request) {
		try {
			if (log.isTraceEnabled()) {
				log.trace(HttpServlets.dumpRequestTrace(request));
				return;
			}
			if (log.isDebugEnabled()) {
				log.debug(HttpServlets.dumpRequestDebug(request));
				return;
			}
			if (log.isInfoEnabled()) {
				log.info(HttpServlets.dumpRequestInfo(request));
			}
		}
		catch (Throwable e) {
			//pass
		}
	}

	private void append(StringBuilder msg, String value) {
		append(msg, value, "");
	}
	
	private void append(StringBuilder msg, String value, String defv) {
		msg.append(Strings.isEmpty(value) ? defv : value);
	}
	
	private void logAccess(HttpServletRequest request, FilterResponseWrapper response, StopWatch sw) {
		try {
			if (accessLog != null) {
				sw.stop();
	
				StringBuilder msg = new StringBuilder();
				
				for (String s : accessFormat) {
					char f = s.charAt(0);
					switch (f) {
					case 't': // Date and time, in Common Log Format
						append(msg, DateTimes.timestampFormat().format(sw.getStartTime()));
						break;
					case 'a': // Remote IP address
						append(msg, request.getRemoteAddr());
						break;
					case 'h': // Remote host name
						append(msg, request.getRemoteHost());
						break;
					case 'p': // Request protocol
						append(msg, request.getProtocol());
						break;
					case 'm': // Request method (GET, POST, etc.)
						append(msg, request.getMethod());
						break;
					case 'q': // Query string (prepended with a '?' if it exists)
						append(msg, request.getQueryString());
						break;
					case 's': // Requested session ID
						append(msg, request.getRequestedSessionId());
						break;
					case 'A': // Local IP address
						append(msg, request.getLocalAddr());
						break;
					case 'V': // Local server name
						append(msg, request.getLocalName());
						break;
					case 'P': // Local port on which this request was received
						msg.append(request.getLocalPort());
						break;
					case 'S': // HTTP status code of the response
						msg.append(response.status);
						break;
					case 'T': // Time taken to process the request, in milliseconds
						msg.append(sw.getTime());
						break;
					case 'I': // current request thread name (can compare later with stacktraces)
						append(msg, Thread.currentThread().getName());
						break;
					case 'u': // Requested URL path
						msg.append(request.getRequestURL());
						break;
					default:
						msg.append('-');
						break;
					}
					msg.append('\t');
				}
				
				accessLog.info(msg.toString());
			}
		}
		catch (Throwable e) {
			//pass
		}
	}
	
	/**
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
	}
}

