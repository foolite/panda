package panda.mvc.view.tag.ui;

import java.util.Locale;

import panda.io.Settings;
import panda.ioc.annotation.IocBean;
import panda.ioc.annotation.IocInject;
import panda.mvc.SiteConstants;


/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Write link.
 *
 * <p/>
 *
 * Configurable attributes are :-
 * <ul>
 *    <li>cdn</li>
 *    <li>debug</li>
 *    <li>version</li>
 *    <li>jquery</li>
 *    <li>jqplugins</li>
 *    <li>jqextras</li>
 *    <li>bootstrap</li>
 *    <li>bsplugins</li>
 *    <li>panda</li>
 *    <li>hammer</li>
 *    <li>lightbox</li>
 *    <li>meiomask</li>
 *    <li>mousewheel</li>
 *    <li>tablesorter</li>
 * </ul>
 *
 * <p></p>
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <p></p> <b>Examples</b>
 * <pre>
 *  <!-- START SNIPPET: example -->
 *  &lt;r:link /&gt;
 *  <!-- END SNIPPET: example -->
 * </pre>
 *
 */
@IocBean(singleton=false)
public class Link extends UIBean {
	@IocInject
	private Settings settings;
	
	// attributes
	protected String statics;
	protected String version;
	protected Boolean cdn;
	protected Boolean debug;
	protected boolean js;
	protected boolean css;
	protected Locale locale;
	protected boolean jquery;
	protected boolean jqplugins;
	protected boolean bootstrap;
	protected boolean bsplugins;
	protected boolean i18n;
	protected boolean panda;
	protected boolean extras;
	protected boolean bswitch;
	protected boolean hammer;
	protected boolean lightbox;
	protected boolean meiomask;
	protected boolean mousewheel;
	protected boolean tablesorter;
	

	/**
	 * Evaluate extra parameters
	 */
	@Override
	public void evaluateParams() {
		super.evaluateParams();

		if (locale == null) {
			locale = context.getLocale();
		}

		if (bootstrap || jqplugins || extras) {
			jquery = true;
		}
		if (bsplugins) {
			jquery = true;
			bootstrap = true;
		}
		if (panda) {
			jquery = true;
			jqplugins = true;
			bootstrap = true;
			bsplugins = true;
		}
		
		if (bswitch || hammer || lightbox || meiomask || mousewheel || tablesorter) {
			jquery = true;
		}
		if (bswitch) {
			bootstrap = true;
		}
		if (extras) {
			bswitch = false;
			hammer = false;
			lightbox = false;
			meiomask = false;
			mousewheel = false;
			tablesorter = false;
		}
		
		if (cdn == null) {
			cdn = settings.getPropertyAsBoolean(SiteConstants.SITE_CDN);
		}
		if (debug == null) {
			debug = settings.getPropertyAsBoolean(SiteConstants.SITE_DEBUG);
		}
	}

	/**
	 * @return the statics
	 */
	public String getStatics() {
		return statics;
	}

	/**
	 * @param statics the statics to set
	 */
	public void setStatics(String statics) {
		this.statics = statics;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the cdn
	 */
	public boolean useCdn() {
		return cdn;
	}

	/**
	 * @return the cdn
	 */
	public Boolean getCdn() {
		return cdn;
	}

	/**
	 * @param cdn the cdn to set
	 */
	public void setCdn(Boolean cdn) {
		this.cdn = cdn;
	}

	/**
	 * @return the debug
	 */
	public boolean useDebug() {
		return debug;
	}

	/**
	 * @return the debug
	 */
	public Boolean getDebug() {
		return debug;
	}

	/**
	 * @param debug the debug to set
	 */
	public void setDebug(Boolean debug) {
		this.debug = debug;
	}

	/**
	 * @return the js
	 */
	public boolean isJs() {
		return js;
	}

	/**
	 * @param js the js to set
	 */
	public void setJs(boolean js) {
		this.js = js;
	}

	/**
	 * @return the css
	 */
	public boolean isCss() {
		return css;
	}

	/**
	 * @param css the css to set
	 */
	public void setCss(boolean css) {
		this.css = css;
	}

	/**
	 * @return the locale
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * @param locale the locale to set
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * @return the jquery
	 */
	public boolean isJquery() {
		return jquery;
	}

	/**
	 * @param jquery the jquery to set
	 */
	public void setJquery(boolean jquery) {
		this.jquery = jquery;
	}

	/**
	 * @return the jqplugins
	 */
	public boolean isJqplugins() {
		return jqplugins;
	}

	/**
	 * @param jqplugins the jqplugins to set
	 */
	public void setJqplugins(boolean jqplugins) {
		this.jqplugins = jqplugins;
	}

	/**
	 * @return the bootstrap
	 */
	public boolean isBootstrap() {
		return bootstrap;
	}

	/**
	 * @param bootstrap the bootstrap to set
	 */
	public void setBootstrap(boolean bootstrap) {
		this.bootstrap = bootstrap;
	}

	/**
	 * @return the bsplugins
	 */
	public boolean isBsplugins() {
		return bsplugins;
	}

	/**
	 * @param bsplugins the bsplugins to set
	 */
	public void setBsplugins(boolean bsplugins) {
		this.bsplugins = bsplugins;
	}

	/**
	 * @return the i18n
	 */
	public boolean isI18n() {
		return i18n;
	}

	/**
	 * @param i18n the i18n to set
	 */
	public void setI18n(boolean i18n) {
		this.i18n = i18n;
	}

	/**
	 * @return the panda
	 */
	public boolean isPanda() {
		return panda;
	}

	/**
	 * @param panda the panda to set
	 */
	public void setPanda(boolean panda) {
		this.panda = panda;
	}

	/**
	 * @return the extras
	 */
	public boolean isExtras() {
		return extras;
	}

	/**
	 * @param extras the extras to set
	 */
	public void setExtras(boolean extras) {
		this.extras = extras;
	}

	
	/**
	 * @return the bswitch
	 */
	public boolean isSwitch() {
		return bswitch;
	}

	/**
	 * @param bswitch the bswitch to set
	 */
	public void setSwitch(boolean bswitch) {
		this.bswitch = bswitch;
	}

	/**
	 * @return the hammer
	 */
	public boolean isHammer() {
		return hammer;
	}

	/**
	 * @param hammer the hammer to set
	 */
	public void setHammer(boolean hammer) {
		this.hammer = hammer;
	}

	/**
	 * @return the lightbox
	 */
	public boolean isLightbox() {
		return lightbox;
	}

	/**
	 * @param lightbox the lightbox to set
	 */
	public void setLightbox(boolean lightbox) {
		this.lightbox = lightbox;
	}

	/**
	 * @return the meiomask
	 */
	public boolean isMeiomask() {
		return meiomask;
	}

	/**
	 * @param meiomask the meiomask to set
	 */
	public void setMeiomask(boolean meiomask) {
		this.meiomask = meiomask;
	}

	/**
	 * @return the mousewheel
	 */
	public boolean isMousewheel() {
		return mousewheel;
	}

	/**
	 * @param mousewheel the mousewheel to set
	 */
	public void setMousewheel(boolean mousewheel) {
		this.mousewheel = mousewheel;
	}

	/**
	 * @return the tablesorter
	 */
	public boolean isTablesorter() {
		return tablesorter;
	}

	/**
	 * @param tablesorter the tablesorter to set
	 */
	public void setTablesorter(boolean tablesorter) {
		this.tablesorter = tablesorter;
	}

}

