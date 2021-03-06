package panda.app.auth;

import panda.app.constant.MVC;
import panda.app.constant.RES;
import panda.app.constant.SET;
import panda.app.util.AppSettings;
import panda.ioc.annotation.IocBean;
import panda.ioc.annotation.IocInject;
import panda.lang.Strings;
import panda.log.Logs;
import panda.mvc.ActionContext;
import panda.mvc.View;
import panda.mvc.ViewCreator;
import panda.mvc.processor.AuthenticateProcessor;
import panda.mvc.view.Views;
import panda.net.URLBuilder;
import panda.net.URLHelper;
import panda.net.http.HttpStatus;
import panda.servlet.HttpServlets;

@IocBean(type=AuthenticateProcessor.class, create="initialize")
public class UserAuthenticateProcessor extends AuthenticateProcessor {
	@IocInject(value=MVC.AUTH_VIEW_UNLOGIN, required=false)
	protected String unloginView;
	
	@IocInject(value=MVC.AUTH_VIEW_UNSECURE, required=false)
	protected String unsecureView;
	
	@IocInject(value=MVC.AUTH_VIEW_FORBIDDEN, required=false)
	protected String forbiddenView = Views.SE_FORBIDDEN;

	@IocInject
	protected AppSettings settings;

	@IocInject
	protected ViewCreator vcreator;
	
	@IocInject(required=false)
	protected UserAuthenticator authenticator;
	
	public void initialize() {
		if (authenticator == null) {
			Logs.getLog(getClass()).info("User authentication is disabled for undefined UserAuthenticator.");
		}
	}
	
	/**
	 * add error to action
	 * @param ac the action context
	 * @param id msgId
	 */
	protected void addActionError(ActionContext ac, String id) {
		String msg = ac.text(id, id);
		ac.getActionAlert().addError(msg);
	}

	protected void doView(ActionContext ac, String type) {
		View view = Views.createView(ac, type);
		if (view != null) {
			view.render(ac);
		}
	}

	protected String getRedirectURL(ActionContext ac) {
		String uri = ac.getBase() + ac.getPath();
		String query = HttpServlets.getRequestQueryString(ac.getRequest());
		
		URLBuilder ub = new URLBuilder();
		ub.setPath(uri);
		ub.setQuery(query);
		String url = ub.build();

		String red = URLHelper.encodeURL(url);
		return red;
	}
	
	protected String getBindView(ActionContext ac) {
		String ev = ac.getConfig().getErrorView();
		if (Strings.isNotEmpty(ev)) {
			return vcreator.isBindView(ev) ? ev : null;
		}
		
		String dv = ac.getConfig().getDefaultView();
		return vcreator.isBindView(dv) ? dv : null;
	}

	@Override
	public void process(ActionContext ac) {
		if (authenticator == null) {
			doNext(ac);
			return;
		}
		
		int r = authenticator.authenticate(ac);
		if (r <= UserAuthenticator.OK) {
			doNext(ac);
			return;
		}
		
		ac.getResponse().setStatus(HttpStatus.SC_FORBIDDEN);

		String bv = getBindView(ac);
		
		// unlogin view
		if (r == UserAuthenticator.UNLOGIN) {
			if (Strings.isNotEmpty(bv)) {
				addActionError(ac, RES.ERROR_UNLOGIN);
				ac.push(getRedirectURL(ac));
				doView(ac, bv);
				return;
			}

			String unlogin = settings.getProperty(SET.AUTH_VIEW_UNLOGIN, unloginView);
			if (Strings.isNotEmpty(unlogin)) {
				addActionError(ac, RES.ERROR_UNLOGIN);
				ac.push(getRedirectURL(ac));
				doView(ac, unlogin);
				return;
			}
		}

		// unsecure view
		if (r == UserAuthenticator.UNSECURE) {
			if (Strings.isNotEmpty(bv)) {
				addActionError(ac, RES.ERROR_UNSECURE);
				ac.push(getRedirectURL(ac));
				doView(ac, bv);
				return;
			}

			String unsecure = settings.getProperty(SET.AUTH_VIEW_UNLOGIN, unsecureView);
			if (Strings.isEmpty(unsecure)) {
				unsecure = settings.getProperty(SET.AUTH_VIEW_UNLOGIN, unloginView);
			}
			if (Strings.isNotEmpty(unsecure)) {
				addActionError(ac, RES.ERROR_UNSECURE);
				ac.push(getRedirectURL(ac));
				doView(ac, unsecure);
				return;
			}
		}

		// forbidden view
		if (Strings.isNotEmpty(bv)) {
			addActionError(ac, RES.ERROR_FORBIDDEN);
			ac.push(getRedirectURL(ac));
			doView(ac, bv);
			return;
		}

		String forbidden = settings.getProperty(SET.AUTH_VIEW_FORBIDDEN, forbiddenView);
		if (Strings.isNotEmpty(forbidden)) {
			addActionError(ac, RES.ERROR_FORBIDDEN);
			ac.push(getRedirectURL(ac));
			doView(ac, forbidden);
			return;
		}
	}
}
