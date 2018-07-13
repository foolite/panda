package panda.app.util;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import panda.app.auth.AppAuthenticator;
import panda.app.auth.IUser;
import panda.app.constant.RES;
import panda.app.constant.SET;
import panda.app.constant.VAL;
import panda.app.entity.ICreate;
import panda.app.entity.IStatus;
import panda.app.entity.IUpdate;
import panda.bind.json.JsonObject;
import panda.bind.json.JsonSerializer;
import panda.bind.json.Jsons;
import panda.cast.Castors;
import panda.ioc.Scope;
import panda.ioc.annotation.IocBean;
import panda.ioc.annotation.IocInject;
import panda.lang.Locales;
import panda.lang.Numbers;
import panda.lang.Strings;
import panda.lang.Systems;
import panda.lang.time.DateTimes;
import panda.mvc.MvcConstants;
import panda.mvc.bean.Pager;
import panda.mvc.bean.Sorter;
import panda.mvc.bind.adapter.SorterAdapter;
import panda.mvc.util.AccessHandler;
import panda.mvc.util.ActionAssist;
import panda.mvc.util.MvcURLBuilder;
import panda.mvc.util.StateProvider;
import panda.mvc.view.ftl.FreemarkerHelper;

import freemarker.template.TemplateException;


@IocBean(type=ActionAssist.class, scope=Scope.REQUEST)
public class AppActionAssist extends ActionAssist implements AccessHandler {
	@IocInject
	protected AppSettings settings;

	@IocInject
	protected FreemarkerHelper freemarker;
	
	@IocInject
	protected AppFreemarkerTemplateLoader ftlTemplateLoader;

	@IocInject
	protected AppResourceBundleLoader resBundleLoader;

	@IocInject
	protected AppAuthenticator authenticator;

	@IocInject(value=MvcConstants.APP_DEBUG, required=false)
	protected Boolean appDebug;
	
	//--------------------------------------------------------------------------	
	/**
	 * hasTemplate
	 * @param name template name
	 * @return true if template exists
	 */
	public boolean hasTemplate(String name) {
		return freemarker.hasTemplate(name);
	}
	
	/**
	 * process template
	 * @param name template name
	 * @param model model
	 * @return result
	 * @throws TemplateException if a template error occurs
	 * @throws IOException if an I/O error occurs
	 */
	public String execTemplate(String name, Object model) throws TemplateException, IOException {
		return freemarker.execTemplate(name, model);
	}
	
	//--------------------------------------------------------------------------	
	/**
	 * @return app version
	 */
	public String getAppVersion() {
		return settings.getAppVersion();
	}
	
	/**
	 * @return true if database resource loader is activated
	 */
	public boolean isDatabaseResourceLoader() {
		return resBundleLoader.getDatabaseResourceLoader() != null;
	}
	
	/**
	 * @return true if database template loader is activated
	 */
	public boolean isDatabaseTemplateLoader() {
		return ftlTemplateLoader.getDatabaseTemplateLoader() != null;
	}
	
	/**
	 * @return true if gae support
	 */
	public boolean isGaeSupport() {
		return Systems.IS_OS_APPENGINE;
	}

	//--------------------------------------------------------------------------	
	/**
	 * isValidLocale (used by Property, Template, Resource Action)
	 * @param language language
	 * @param country country
	 * @return true - if locale is valid
	 */
	public boolean isValidLocale(String language, String country) {
		if (Strings.isNotEmpty(language) && Strings.isNotEmpty(country)) {
			if (!VAL.LOCALE_ALL.equals(country)) {
				return Locales.isAvailableLocale(new Locale(language, country));
			}
		}
		return true;
	}

	//--------------------------------------------------------------------------	
	public String encrypt(String value) {
		return authenticator.encrypt(value);
	}
	
	public String decrypt(String value) {
		return authenticator.decrypt(value);
	}
	
	//--------------------------------------------------------------------------	
	/**
	 * @return true if remote host is local network host
	 */
	@Override
	public boolean isDebugEnabled() {
		if (appDebug == null) {
			String dbg = settings.getProperty(SET.APP_DEBUG);
			if ("true".equalsIgnoreCase(dbg)) {
				appDebug = true;
			}
			else if ("false".equalsIgnoreCase(dbg)) {
				appDebug = false;
			}
			else {
				appDebug = isLoopbackIP() || isSuperUser();
			}
		}
		return appDebug;
	}

	/**
	 * @return true - if the login user is administrator
	 */
	public boolean isAdminUser() {
		return isAdminUser(getLoginUser());
	}

	/**
	 * @param u user
	 * @return true - if the user is administrator
	 */
	public boolean isAdminUser(IUser u) {
		return authenticator.isAdminUser(u);
	}

	/**
	 * @return true - if the login user is super
	 */
	public boolean isSuperUser() {
		return isSuperUser(getLoginUser());
	}

	/**
	 * @param u user
	 * @return true - if the user is super
	 */
	public boolean isSuperUser(IUser u) {
		return authenticator.isSuperUser(u);
	}

	//--------------------------------------------------------------------------
	public long getLoginUserId() {
		return authenticator.getLoginUserId(context);
	}

	/**
	 * getLoginUser
	 * @return user
	 */
	public IUser getLoginUser() {
		return authenticator.getLoginUser(context);
	}

	/**
	 * setLoginUser
	 * @param user user
	 */
	public void setLoginUser(IUser user) {
		authenticator.setAuthenticatedUser(context, user);
	}

	/**
	 * removeLoginUser
	 */
	public void removeLoginUser() {
		authenticator.removeAuthenticatedUser(context);
	}

	/**
	 * can access
	 * @param action action
	 * @return true if action can access
	 */
	@Override
	public boolean canAccess(String action) {
		if (authenticator == null) {
			return true;
		}

		String path = MvcURLBuilder.buildPath(context, action, false);
		return authenticator.canAccess(context, path);
	}

	/**
	 * canAccessData
	 * @param action action
	 * @param data data
	 * @return true if action can access the data
	 */
	@Override
	public boolean canAccessData(String action, Object data) {
		return canAccess(action);
	}

	//-------------------------------------------------------------
	/**
	 * initialize common fields of data
	 * @param data data
	 */
	public void initCommonFields(Object data) {
		if (data instanceof IStatus) {
			if (((IStatus)data).getStatus() == null) {
				((IStatus)data).setStatus(VAL.STATUS_ACTIVE);
			}
		}
		
		Date now = DateTimes.getDate();
		if (data instanceof ICreate) {
			ICreate cb = (ICreate)data;
			cb.setCusid(getLoginUserId());
			cb.setCtime(now);
		}
		
		if (data instanceof IUpdate) {
			IUpdate ub = (IUpdate)data;
	
			ub.setUusid(getLoginUserId());
			ub.setUtime(now);
		}
	}

	
	/**
	 * initialize update fields of data
	 * @param data input data
	 * @param sdat source data
	 */
	public void initUpdateFields(Object data, Object sdat) {
		if (data instanceof IStatus) {
			if (((IStatus)data).getStatus() == null) {
				((IStatus)data).setStatus(VAL.STATUS_ACTIVE);
			}
		}
		
		if (data instanceof ICreate) {
			ICreate cb = (ICreate)data;
			ICreate sb = (ICreate)sdat;
	
			cb.setCusid(sb.getCusid());
			cb.setCtime(sb.getCtime());
		}
		
		if (data instanceof IUpdate) {
			IUpdate ub = (IUpdate)data;

			ub.setUusid(getLoginUserId());
			ub.setUtime(DateTimes.getDate());
		}
	}
	
	//-------------------------------------------------------------
	/**
	 * load sorter parameters from stateProvider
	 * @param sorter sorter
	 * @param orders orders
	 */
	public void loadSorterParams(Sorter sorter, Map<String, String> orders) {
		StateProvider sp = getState();
		if (sp == null) {
			return;
		}
		
		// first check
		if (Strings.isNotEmpty(sorter.getColumn()) && orders.containsKey(sorter.getColumn())) {
			return;
		}
		
		String sc = (String)sp.loadState("sorter");
		if (Strings.isEmpty(sc)) {
			sc = getText(getMethodName() + RES.SORTER_SUFFIX, null);
		}
		castToSorter(sorter, sc);

		// second check
		if (Strings.isNotEmpty(sorter.getColumn()) && orders.containsKey(sorter.getColumn())) {
			return;
		}

		// set default
		for (Entry<String, String> en : orders.entrySet()) {
			sorter.setColumn(en.getKey());
			sorter.setDirection(en.getValue());
			return;
		}
	}

	public void castToSorter(Sorter sorter, String sc) {
		if (Strings.isEmpty(sc)) {
			return;
		}
		
		try {
			JsonObject jo = JsonObject.fromJson(sc);
			Castors.scastTo(jo, sorter);
		}
		catch (Exception e) {
			getLog().debug("Invalid JSON sorter: " + sc, e);
		}
	}

	/**
	 * save sorter parameters to stateProvider
	 * @param sorter sorter
	 */
	public void saveSorterParams(Sorter sorter) {
		JsonSerializer js = Jsons.newJsonSerializer();
		js.registerAdapter(Sorter.class, new SorterAdapter(true));

		String ss = js.serialize(sorter);
		getState().saveState("sorter", ss);
	}
	
	/**
	 * load pager limit parameters from stateProvider
	 * @param pager pager
	 */
	public void loadLimitParams(Pager pager) {
		if (!pager.hasLimit()) {
			StateProvider sp = getState();
			if (sp != null) {
				pager.setLimit(Numbers.toLong((String)sp.loadState("limit"), 0L));
			}
		}
		setLimitToPager(pager);
	}

	/**
	 * if pager.limit > maxLimit then set pager.limit = maxLimit
	 * @param pager pager
	 */
	public void setLimitToPager(Pager pager) {
		setLimitToPager(pager, VAL.DEFAULT_LIST_PAGESIZE, VAL.MAXIMUM_LIST_PAGESIZE);
	}
	
	/**
	 * if pager.limit > maxLimit then set pager.limit = maxLimit
	 * @param pager pager
	 * @param def default limit
	 * @param max maximum limit
	 */
	public void setLimitToPager(Pager pager, long def, long max) {
		if (!pager.hasLimit()) {
			long l = getTextAsLong(getMethodName() + RES.PAGESIZE_DEFAULT_SUFFIX, def);
			pager.setLimit(l);
		}

		long m = getTextAsLong(getMethodName() + RES.PAGESIZE_MAXIMUM_SUFFIX, max);
		if (m > 0 && (!pager.hasLimit() || pager.getLimit() > m)) {
			pager.setLimit(m);
		}
	}
	
	/**
	 * save pager limit parameters to stateProvider
	 * @param pager pager
	 */
	public void saveLimitParams(Pager pager) {
		if (pager.getLimit() != null) {
			getState().saveState("limit", pager.getLimit().toString());
		}
	}
}
