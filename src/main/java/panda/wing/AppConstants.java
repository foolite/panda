package panda.wing;

import panda.mvc.MvcConstants;

public interface AppConstants extends MvcConstants {
	/** The fonts path */
	public static final String PANDA_FONTS_PATH = "panda.fonts.path";

	/** The cache */
	public static final String PANDA_CACHE = "panda.cache";

	/** The authenticate */
	public static final String PANDA_AUTH_ALLOW_UNKNOWN_URL = "panda.auth.allow.unknown.url";
	
	public static final String PANDA_AUTH_UNLOGIN_VIEW = "panda.auth.unlogin.view";
	
	public static final String PANDA_AUTH_UNSECURE_VIEW = "panda.auth.unsecure.view";
}