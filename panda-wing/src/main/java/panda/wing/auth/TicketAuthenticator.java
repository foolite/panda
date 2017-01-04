package panda.wing.auth;

import java.util.Collections;
import java.util.List;

import panda.ioc.annotation.IocInject;
import panda.lang.Arrays;
import panda.mvc.ActionContext;

public class TicketAuthenticator extends UserAuthenticator {

	@IocInject
	protected AuthHelper authHelper;
	
	@Override
	protected Object getSessionUser(ActionContext ac) {
		Object u = super.getSessionUser(ac);
		if (u != null) {
			return u;
		}

		u = authHelper.getTicketUser(ac);
		authHelper.saveUserToSession(ac, u);
		return u;
	}
	
	@Override
	protected boolean isSecureSessionUser(Object su) {
		return authHelper.isSecureSessionUser(su);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected List<String> getUserPermits(Object su) {
		if (su == null) {
			return Collections.EMPTY_LIST;
		}
		if (su instanceof IPermits) {
			return ((IPermits)su).getPermits();
		}
		if (su instanceof IRole) {
			return Arrays.asList(((IRole)su).getRole());
		}
		return Collections.EMPTY_LIST;
	}
}