package panda.mvc.util;

import javax.servlet.http.Cookie;

import panda.ioc.Scope;
import panda.ioc.annotation.IocBean;
import panda.ioc.annotation.IocInject;
import panda.lang.Strings;
import panda.log.Log;
import panda.log.Logs;
import panda.mvc.ActionContext;
import panda.servlet.HttpServlets;
import panda.util.crypto.Cryptor;


/**
 * CookieStateProvider
 */
@IocBean(type=StateProvider.class, scope=Scope.REQUEST)
public class CookieStateProvider implements StateProvider {
	private final static Log log = Logs.getLog(CookieStateProvider.class);
	
	private final static String DOMAIN = "cookie-state-domain";
	private final static String PATH = "cookie-state-path";
	private final static String MAXAGE = "cookie-state-maxage";
	private final static String SECURE = "cookie-state-secure";
	
	@IocInject
	protected ActionContext context;

	@IocInject
	protected Cryptor cryptor;

	private String domain;
	private String path;
	private Integer maxAge;
	private Boolean secure;
	
	/**
	 * Constructor
	 */
	public CookieStateProvider() {
	}
	
	/**
	 * @param textProvider text provider
	 */
	@IocInject
	public void setTextProvider(TextProvider textProvider) {
		domain = textProvider.getText(DOMAIN, (String)null);
		path = textProvider.getText(PATH, (String)null);
		maxAge = textProvider.getTextAsInt(MAXAGE, 0);
		secure = textProvider.getTextAsBoolean(SECURE);
	}
	
	/**
	 * @return the maxAge
	 */
	public int getMaxAge() {
		return maxAge;
	}

	/**
	 * @param maxAge the maxAge to set
	 */
	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	/**
	 * @return the domain
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * @param domain the domain to set
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * encode value 
	 * @param value value
	 * @return encoded value
	 */
	private String encodeValue(String value) {
		if (Strings.isEmpty(value)) {
			return value;
		}
		
		try {
			return cryptor.encrypt(value);
		}
		catch (Exception e) {
			log.error("Failed to encrypt:" + value);
			return null;
		}
	}
	
	/**
	 * decode value 
	 * @param value value
	 * @return encoded value
	 */
	private String decodeValue(String value) {
		if (Strings.isEmpty(value)) {
			return value;
		}
		
		try {
			return cryptor.decrypt(value);
		}
		catch (Exception e) {
			log.warn("Failed to decrypt:" + value);
			return null;
		}
	}

	/**
	 * Save state
	 * @param name state name
	 * @param value state value
	 * @return true if state value saved successfully
	 */
	@Override
	public boolean saveState(String name, String value) {
		String val = encodeValue(value);
		if (val == null) {
			return false;
		}

		Cookie c = new Cookie(name, val);

		if (Strings.isNotEmpty(domain)) {
			c.setDomain(domain);
		}
		
		if (Strings.isEmpty(path)) {
			path = context.getRequest().getRequestURI();
		}
		else {
			path = MvcURLBuilder.buildPath(context, path);
		}
		c.setPath(path);

		if (secure != null) {
			c.setSecure(secure);
		}

		if (value == null) {
			c.setMaxAge(0);
		}
		else if (maxAge != null) {
			c.setMaxAge(maxAge);
		}
		
		context.getResponse().addCookie(c);
		return true;
	}
	
	/**
	 * Load state
	 * @param name state name
	 * @return state value 
	 */
	@Override
	public String loadState(String name) {
		Cookie c = HttpServlets.getCookie(context.getRequest(), name);
		if (c != null) {
			return decodeValue(c.getValue());
		}
		return null;
	}
}
