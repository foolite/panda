package panda.app.constant;

/**
 * Authentication constants
 */
public interface AUTH {
	
	/**
	 * AUTH AND
	 */
	public static final char AND = '+';

	/**
	 * SUPER = "*";
	 */
	public static final String SUPER = "*";
	
	/**
	 * NONE = "-";
	 */
	public static final String NONE = "-";

	/**
	 * Ticket (Sign in)
	 */
	public static final String TICKET = "~";

	/**
	 * Admin
	 */
	public static final String ADMIN = "A";

	//------------------------------------------------
	// special authentication
	//
	/**
	 * Remote Address is local network address
	 */
	public static final String LOCAL = "_local_";
	
	/**
	 * secure user (retype password)
	 */
	public static final String SECURE = "_secure_";

	/**
	 * AND LOCAL
	 */
	public static final String ALOCAL = "+_local_";

	/**
	 * AND SECURE
	 */
	public static final String ASECURE = "+_secure_";
}