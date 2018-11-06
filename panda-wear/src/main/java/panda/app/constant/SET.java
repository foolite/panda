package panda.app.constant;

import panda.mvc.SetConstants;

/**
 * Settings Constants
 */
public interface SET extends SetConstants {
	//--------------------------------------------------------
	// project/app version
	//
	public static final String APP_VERSION = "app.version";
	public static final String PRJ_VERSION = "prj.version";
	public static final String PRJ_REVISION = "prj.revision";

	//--------------------------------------------------------
	// data properties
	//
	public static final String DATA = "data";

	public static final String DATA_PREFIX = "data.prefix";

	public static final String DATA_SOURCE = "data.source";

	public static final String DATA_MONGO_URL = "data.%s.url";

	public static final String DATA_JNDI_RESOURCE = "data.%s.resource";

	public static final String DATA_QUERY_TIMEOUT = "data.query.timeout";

	//--------------------------------------------------------
	public static final String LUCENE_LOCATION = "lucene.location";

	//------------------------------------------------
	// scheduler & executor
	//
	public static final String SCHEDULER_ENABLE = "scheduler.enable";

	public static final String EXECUTOR_ENABLE = "executor.enable";

	public static final String TASK_ACTION_SCHEME = "task.action.scheme";

	//------------------------------------------------
	// html to pdf
	//
	/** The wkhtmltopdf exe path */
	public static final String WKHTML2PDF_PATH = "wkhtml2pdf.path";

	/** The wkhtmltopdf processor wait timeout */
	public static final String WKHTML2PDF_TIMEOUT = "wkhtml2pdf.timeout";

	//--------------------------------------------------------
	// auth view
	//
	public static final String VIEW_FORBIDDEN = "view.forbidden";
	
	public static final String VIEW_UNLOGIN = "view.unlogin";
	
	public static final String VIEW_UNSECURE = "view.unsecure";

	//--------------------------------------------------------
	// mail properties
	//
	/**
	 * MAIL_DEBUG = "mail-debug";
	 */
	public static final String MAIL_DEBUG = "mail-debug";

	/**
	 * MAIL_SMTP_HELO = "mail-smtp-helo";
	 */
	public static final String MAIL_SMTP_HELO = "mail-smtp-helo";

	/**
	 * MAIL_SMTP_HOST = "mail-smtp-host";
	 */
	public static final String MAIL_SMTP_HOST = "mail-smtp-host";
	
	/**
	 * MAIL_SMTP_HOST_PORT = "mail-smtp-port";
	 */
	public static final String MAIL_SMTP_PORT = "mail-smtp-port";
	
	/**
	 * MAIL_SMTP_SSL = "mail-smtp-ssl";
	 */
	public static final String MAIL_SMTP_SSL = "mail-smtp-ssl";
	
	/**
	 * MAIL_SMTP_TLS = "mail-smtp-tls";
	 */
	public static final String MAIL_SMTP_TLS = "mail-smtp-tls";

	/**
	 * MAIL_SMTP_CONN_TIMEOUT = "mail-smtp-conn-timeout";
	 */
	public static final String MAIL_SMTP_CONN_TIMEOUT = "mail-smtp-conn-timeout";
	
	/**
	 * MAIL_SMTP_SEND_TIMEOUT = "mail-smtp-send-timeout";
	 */
	public static final String MAIL_SMTP_SEND_TIMEOUT = "mail-smtp-send-timeout";
	
	/**
	 * MAIL_SMTP_USER = "mail-smtp-user";
	 */
	public static final String MAIL_SMTP_USER = "mail-smtp-user";
	
	/**
	 * MAIL_SMTP_PASSWORD = "mail-smtp-password";
	 */
	public static final String MAIL_SMTP_PASSWORD = "mail-smtp-password";
	
	/**
	 * MAIL_SENDER = "mail-sender";
	 */
	public static final String MAIL_SENDER = "mail-sender";
	
	/**
	 * MAIL_FROM = "mail-from";
	 */
	public static final String MAIL_FROM = "mail-from";
	
	/**
	 * MAIL_BOUNCE = "mail-smtp-bounce";
	 */
	public static final String MAIL_SMTP_BOUNCE = "mail-smtp-bounce";
	
	/**
	 * MAIL_CHARSET = "mail-charset";
	 */
	public static final String MAIL_CHARSET = "mail-charset";

}