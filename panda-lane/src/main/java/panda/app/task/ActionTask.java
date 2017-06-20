package panda.app.task;

import java.util.HashMap;
import java.util.Map;

import panda.io.Streams;
import panda.lang.Strings;
import panda.lang.time.StopWatch;
import panda.log.Log;
import panda.log.Logs;
import panda.net.http.HttpClient;
import panda.net.http.HttpMethod;
import panda.net.http.HttpRequest;
import panda.net.http.HttpResponse;

public class ActionTask implements Runnable {
	private static Log log = Logs.getLog(ActionTask.class);
	
	private String url;
	private String method = HttpMethod.GET;
	private Map<String, String> headers;
	private Map<String, String> params;
	private int errorLimit = 3;

	private int errors;
	
	public ActionTask(String url) {
		this.url = url;
	}

	
	/**
	 * @return the errorLimit
	 */
	public int getErrorLimit() {
		return errorLimit;
	}


	/**
	 * @param errorLimit the errorLimit to set
	 */
	public void setErrorLimit(int errorLimit) {
		this.errorLimit = errorLimit;
	}


	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = Strings.upperCase(method);
	}

	/**
	 * @return the headers
	 */
	public Map<String, String> getHeaders() {
		if (headers == null) {
			headers = new HashMap<String, String>();
		}
		return headers;
	}

	/**
	 * @param headers the headers to set
	 */
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	/**
	 * @return the params
	 */
	public Map<String, String> getParams() {
		if (params == null) {
			params = new HashMap<String, String>();
		}
		return params;
	}

	/**
	 * @param params the params to set
	 */
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	
	@Override
	public void run() {
		if (log.isInfoEnabled()) {
			log.info("Start action task: " + url);
		}

		HttpResponse hres = null;
		try {
			HttpClient hc = new HttpClient();
			
			hc.setAutoRedirect(false);
			HttpRequest hreq = HttpRequest.create(url, method);
			if (params != null) {
				hreq.getParams().putAll(params);
			}
			if (headers != null) {
				hreq.getHeader().putAll(headers);
			}
			hc.setRequest(hreq);
			
			if (log.isDebugEnabled()) {
				log.debug("Task> " + url);
			}

			StopWatch sw = new StopWatch();
			hres = hc.send();

			if (hres.isOK()) {
				if (log.isDebugEnabled()) {
					log.debug("Task> " + url + " : " + hres.getStatusLine() 
						+ Streams.LINE_SEPARATOR
						+ hres.getContentText());
					log.debug("Task> " + url + " : " + hres.getStatusLine() + " (" + sw + ")");
				}
				else if (log.isInfoEnabled()) {
					hres.safeDrain();
					log.info("Task> " + url + " : " + hres.getStatusLine() + " (" + sw + ")");
				}
				else {
					hres.safeDrain();
				}
				errors = 0;
			}
			else {
				errors++;
				String msg = "Failed to GET " + url + " : " + hres.getStatusLine() 
						+ Streams.LINE_SEPARATOR
						+ hres.getHeader().toString()
						+ Streams.LINE_SEPARATOR
						+ hres.getContentText();
				if (errors < errorLimit) {
					log.warn(msg);
				}
				else {
					log.error(msg);
				}
			}
		}
		catch (Throwable e) {
			log.error("Failed to GET " + getUrl(), e);
		}
		finally {
			Streams.safeClose(hres);
		}
	}
}