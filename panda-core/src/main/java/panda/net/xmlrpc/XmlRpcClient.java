package panda.net.xmlrpc;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import panda.bind.xmlrpc.XmlRpcDocument;
import panda.bind.xmlrpc.XmlRpcs;
import panda.cast.Castors;
import panda.io.MimeTypes;
import panda.io.Streams;
import panda.lang.Arrays;
import panda.lang.Strings;
import panda.lang.time.StopWatch;
import panda.log.Log;
import panda.log.Logs;
import panda.net.http.HttpClient;
import panda.net.http.HttpRequest;
import panda.net.http.HttpResponse;


public class XmlRpcClient {
	private static final Log log = Logs.getLog(XmlRpcClient.class);
	
	private Castors castors;
	
	private HttpClient http;
	
	private String url;
	
	private String userAgent;

	/**
	 * @param url the Xml-Rpc EntryPoint URL
	 */
	public XmlRpcClient(String url) {
		this.url = url;
		castors = Castors.i();
		http = new HttpClient();
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
	 * @return the userAgent
	 */
	public String getUserAgent() {
		return userAgent;
	}

	/**
	 * @param userAgent the userAgent to set
	 */
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}


	public <T> T call(String method, Type resultType, Object... params) throws XmlRpcFaultException, IOException {
		XmlRpcDocument<Object> xreq = new XmlRpcDocument<Object>();
		
		xreq.setMethodName(method);

		List<Object> pls = Arrays.toList(params);
		while (pls.size() > 0) {
			if (pls.get(pls.size() - 1) != null) {
				break;
			}
			pls.remove(pls.size() - 1);
		}
		xreq.setParams(pls);
		String xbody = XmlRpcs.toXml(xreq, true, log.isDebugEnabled());

		HttpRequest hreq = HttpRequest.post(url);
		hreq.setDefault().setContentType(MimeTypes.TEXT_XML);
		if (Strings.isNotEmpty(userAgent)) {
			hreq.setUserAgent(userAgent);
		}
		hreq.setBody(xbody);
		
		http.setRequest(hreq);
		
		if (log.isDebugEnabled()) {
			log.debug("XML-RPC Call> " + url + " [" + method + "]: " + Streams.EOL + xbody);
		}

		StopWatch sw = new StopWatch();
		HttpResponse hres = http.send();
		
		XmlRpcDocument<Object> xres;
		if (hres.isOK()) {
			if (log.isDebugEnabled()) {
				log.debug("XML-RPC Call> " + url + " [" + method + "]: " + hres.getStatusLine() 
					+ Streams.EOL
					+ hres.getContentText());
				xres = XmlRpcs.fromXml(hres.getReader(), XmlRpcDocument.class);
				log.debug("XML-RPC Call> " + url + " [" + method + "]: " + hres.getStatusLine() + " (" + sw + ")");
			}
			else if (log.isInfoEnabled()) {
				xres = XmlRpcs.fromXml(hres.getReader(), XmlRpcDocument.class);
				log.info("XML-RPC Call> " + url + " [" + method + "]: " + hres.getStatusLine() + " (" + sw + ")");
			}
			else {
				xres = XmlRpcs.fromXml(hres.getReader(), XmlRpcDocument.class);
			}
			
			if (xres.getFault() != null) {
				throw new XmlRpcFaultException(xres.getFault());
			}
			
			T ores = castors.cast(xres.getParams(), resultType);
			return ores;
		}

		log.warn("XML-RPC Call Failed> " + url + " [" + method + "]: " + hres.getStatusLine() 
			+ Streams.EOL
			+ hres.getContentText());

		throw new XmlRpcFaultException(hres.getStatusCode(), hres.getStatusReason());
	}
}
