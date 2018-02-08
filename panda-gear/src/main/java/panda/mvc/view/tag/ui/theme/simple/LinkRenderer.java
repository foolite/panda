package panda.mvc.view.tag.ui.theme.simple;

import java.io.IOException;
import java.util.Locale;

import panda.Panda;
import panda.lang.Strings;
import panda.mvc.Mvcs;
import panda.mvc.view.tag.ui.Link;
import panda.mvc.view.tag.ui.theme.AbstractEndRenderer;
import panda.mvc.view.tag.ui.theme.RenderingContext;
import panda.net.http.UserAgent;
import panda.servlet.HttpServlets;

public class LinkRenderer extends AbstractEndRenderer<Link> {
	private static final String CDN_BASE = Mvcs.PANDA_CDN + '/' + Panda.VERSION;
	private static final String JQUERY_VERSION = "1.12.4";
	private static final String BOOTSTRAP_VERSION = "3.3.7";
	private static final String FONTAWESOME_VERSION = "4.7.0";
	
	private boolean js;
	private boolean css;
	private String lang;

	public LinkRenderer(RenderingContext rc) {
		super(rc);
	}
	
	@Override
	protected void render() throws IOException {
		// output css first for client performance
		css = tag.isCss();
		if (css) {
			js = false;
			writeJquery();
			writeJqueryPlugins();
			writeBootstrap();
			writeBootstrapPlugins();
			writeExtras();
			writePanda();
		}

		js = tag.isJs();
		if (js) {
			css = false;
			writeJquery();
			writeJqueryPlugins();
			writeBootstrap();
			writeBootstrapPlugins();
			writeExtras();
			writeRespondJs();
			writePanda();
		}
	}

	private void writeJquery() throws IOException {
		if (js && tag.isJquery()) {
			if (tag.getCdn()) {
				writeCdnJs("//code.jquery.com/jquery-" + JQUERY_VERSION);
			}
			else {
				writeStaticJs("/jquery/js/jquery-" + JQUERY_VERSION);
			}
		}
	}

	private void writeJqueryPlugins() throws IOException {
		if (tag.isJqplugins()) {
			if (tag.useCdn()) {
				writePandaCdnCss("/jquery/css/jquery-plugins");
				writePandaCdnJs("/jquery/js/jquery-plugins");
			}
			else {
				writeStaticCss("/jquery/css/jquery-plugins");
				writeStaticJs("/jquery/js/jquery-plugins");
			}
		}
	}

	private void writeExtras() throws IOException {
		if (tag.isExtras()) {
			writeExtra("extras", true);

			String la = getBootstrapLang();
			if ("ja".equals(la) || la.startsWith("zh")) {
				writeStaticJs("/extras/i18n/extras." + la);
			}
		}
		
		if (tag.isHammer()) {
			writeExtra("jquery.ui.hammer", false);
		}
		
		if (tag.isLightbox()) {
			writeExtra("jquery.ui.lightbox", true);

			String la = getBootstrapLang();
			if ("ja".equals(la) || la.startsWith("zh")) {
				writeStaticJs("/extras/i18n/jquery.ui.lightbox." + la);
			}
		}

		if (tag.isMeiomask()) {
			writeExtra("jquery.ui.meio.mask", false);

			String la = getBootstrapLang();
			if ("ja".equals(la)) {
				writeStaticJs("/extras/i18n/jquery.ui.meio.mask." + la);
			}
		}

		if (tag.isMousewheel()) {
			writeExtra("jquery.ui.mousewheel", false);
		}

		if (tag.isTablesorter()) {
			writeExtra("jquery.ui.tablesorter", true);
		}
		
		if (tag.isSwitch()) {
			writeExtra("bootstrap-switch", true);
		}
	}
	
	private void writeExtra(String name, boolean css) throws IOException {
		if (tag.useCdn()) {
			if (css) {
				writePandaCdnCss("/extras/css/" + name);
			}
			writePandaCdnJs("/extras/js/" + name);
		}
		else {
			if (css) {
				writeStaticCss("/extras/css/" + name);
			}
			writeStaticJs("/extras/js/" + name);
		}
	}
	
	private void writeBootstrap() throws IOException {
		boolean bs = tag.isBootstrap();
		if (css && bs) {
			if (tag.useCdn()) {
				writeCdnCss("//netdna.bootstrapcdn.com/bootstrap/" 
						+ BOOTSTRAP_VERSION 
						+ "/css/bootstrap");
				writeCdnCss("//netdna.bootstrapcdn.com/font-awesome/" 
						+ FONTAWESOME_VERSION 
						+ "/css/font-awesome");
			}
			else {
				writeStaticCss("/bootstrap3/css/bootstrap");
				writeStaticCss("/font-awesome/css/font-awesome");
			}
		}
		if (js && bs) {
			if (tag.useCdn()) {
				writeCdnJs("//netdna.bootstrapcdn.com/bootstrap/" 
						+ BOOTSTRAP_VERSION 
						+ "/js/bootstrap");
			}
			else {
				writeStaticJs("/bootstrap3/js/bootstrap");
			}
		}
	}

	private void writeBootstrapPlugins() throws IOException {
		boolean bsp = tag.isBsplugins();
		if (css && bsp) {
			if (tag.useCdn()) {
				writePandaCdnCss("/bootstrap3/css/bootstrap-plugins");
			}
			else {
				writeStaticCss("/bootstrap3/css/bootstrap-plugins");
			}
		}
		
		if (js && bsp) {
			if (tag.useCdn()) {
				writePandaCdnJs("/bootstrap3/js/bootstrap-plugins");
			}
			else {
				writeStaticJs("/bootstrap3/js/bootstrap-plugins");
			}
			
			String la = getBootstrapLang();
			if (tag.isI18n()) {
				if (tag.useCdn()) {
					writePandaCdnJs("/bootstrap3/js/bootstrap-plugins-i18n");
				}
				else {
					writeStaticJs("/bootstrap3/js/bootstrap-plugins-i18n");
				}
			}
			else if (!"en".equals(la)) {
				if (tag.useCdn()) {
					writePandaCdnJs("/bootstrap3/js/i18n/bootstrap-datetimepicker." + la, false);
				}
				else {
					writeStaticJs("/bootstrap3/js/i18n/bootstrap-datetimepicker." + la, false);
				}
			}
			writeJscript("$(function() { $.fn.datetimepicker.defaults.language = '" + la + "'; });");
		}
	}

	private String getBootstrapLang() {
		if (Strings.isEmpty(lang)) {
			Locale locale = tag.getLocale();
			lang = locale.getLanguage();
			if ("zh".equals(lang)) {
				if ("TW".equalsIgnoreCase(locale.getCountry())
						|| "HK".equalsIgnoreCase(locale.getCountry())) {
					lang += "_TW";
				}
				else {
					lang += "_CN";
				}
			}
		}
		return lang;
	}

	private void writePanda() throws IOException {
		boolean panda = tag.isPanda();
		if (css && panda) {
			if (tag.useCdn()) {
				writePandaCdnCss("/panda/css/panda");
			}
			else {
				writeStaticCss("/panda/css/panda");
			}
		}
		if (js && panda) {
			if (tag.useCdn()) {
				writePandaCdnJs("/panda/js/panda");
			}
			else {
				writeStaticJs("/panda/js/panda");
			}
		}
	}
	
	private void writeRespondJs() throws IOException {
		if (js && tag.isRespondjs()) {
			UserAgent ua = HttpServlets.getUserAgent(context.getRequest());
			if (ua.isMsie() && ua.getMajorVersion(UserAgent.MSIE) < 9) {
				if (tag.useCdn()) {
					writeCdnJs("//cdnjs.cloudflare.com/ajax/libs/respond.js/1.4.2/respond");
				}
				else {
					writeStaticJs("/respondjs/respondjs");
				}
			}
		}
	}

	//-------------------------------
	private String debug() {
		return (tag.useDebug() ? "" : ".min");
	}

	protected void writeJscript(String jsc) throws IOException {
		if (js) {
			writeJsc(jsc);
		}
	}

	protected void writeCdnJs(String jsl) throws IOException {
		writeCdnJs(jsl, true);
	}
	
	protected void writeCdnJs(String jsl, boolean debug) throws IOException {
		if (js) {
			writeJs(jsl + (debug ? debug() : "") + ".js");
		}
	}
	
	protected void writeCdnCss(String cssl) throws IOException {
		if (css) {
			writeCss(cssl + debug() + ".css");
		}
	}

	protected void writePandaCdnJs(String path) throws IOException {
		writePandaCdnJs(path, true);
	}
	
	protected void writePandaCdnJs(String path, boolean debug) throws IOException {
		if (js) {
			writeCdnJs(CDN_BASE + path, debug);
		}
	}
	
	protected void writePandaCdnCss(String path) throws IOException {
		if (css) {
			writeCdnCss(CDN_BASE + path);
		}
	}
	
	protected void writeStaticJs(String jsl) throws IOException {
		writeStaticJs(jsl, true);
	}
	
	protected void writeStaticJs(String jsl, boolean debug) throws IOException {
		if (js) {
			writeJs(suri(jsl + (debug ? debug() : "") + ".js", tag.getVersion()));
		}
	}

	protected void writeStaticCss(String cssl) throws IOException {
		if (css) {
			writeCss(suri(cssl + debug() + ".css", tag.getVersion()));
		}
	}
	
	private String sbase;
	protected String suri(String uri, String version) {
		if (sbase == null) {
			sbase = Mvcs.getStaticBase(context, ((Link)tag).getStatics());
		}
		
		StringBuilder s = new StringBuilder();
		s.append(sbase).append(uri);
		if (Strings.isNotEmpty(version)) {
			s.append("?v=").append(version);
		}

		return s.toString();
	}
}
