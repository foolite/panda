package panda.mvc.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import panda.lang.Arrays;
import panda.lang.Strings;
import panda.log.Log;
import panda.log.Logs;
import panda.mvc.ActionChain;
import panda.mvc.ActionChainMaker;
import panda.mvc.ActionContext;
import panda.mvc.ActionInfo;
import panda.mvc.MvcConfig;
import panda.mvc.UrlMapping;
import panda.net.http.HttpMethod;
import panda.servlet.HttpServlets;

public abstract class AbstractUrlMapping implements UrlMapping {
	private static final Log log = Logs.getLog(AbstractUrlMapping.class);

	protected abstract void addInvoker(String path, ActionInvoker invoker);
	
	protected abstract ActionInvoker getInvoker(String path, List<String> args);

	private Map<String, ActionInvoker> map = new HashMap<String, ActionInvoker>();
	
	public void add(ActionChainMaker maker, ActionInfo ai, MvcConfig config) {
		// check path
		String[] paths = ai.getPaths();
		if (Arrays.isEmpty(paths)) {
			throw new IllegalArgumentException(String.format("Empty @At of %s.%s", ai.getActionType().getName(), ai.getMethod().getName()));
		}

		for (int i = 0; i < paths.length; i++) {
			String path = paths[i];
			if (Strings.isEmpty(path)) {
				paths[i] = "/";
			}
			else if (path.charAt(0) != '/') {
				paths[i] = '/' + path;
			}
		}

		ActionChain chain = maker.eval(config, ai);

		// mapping path
		for (String path : paths) {
			ActionInvoker invoker = map.get(path);
			if (invoker == null) {
				invoker = new ActionInvoker();
				map.put(path, invoker);
			}
			if (ai.hasHttpMethod()) {
				for (HttpMethod hm : ai.getHttpMethods()) {
					if (invoker.hasChain(hm)) {
						throw new IllegalArgumentException(String.format("%s.%s @At(%s, %s) is already mapped for %s.%s().", 
							ai.getActionType().getName(), ai.getMethod().getName(), path, hm.toString(), 
							invoker.getChain(hm).getInfo().getActionType().getName(), invoker.getChain(hm).getInfo().getMethod().getName()));
					}
					invoker.addChain(hm, chain);
				}
			}
			else {
				if (invoker.getDefaultChain() != null) {
					throw new IllegalArgumentException(String.format("%s.%s @At(%s) is already mapped for %s.%s().", 
						ai.getActionType().getName(), ai.getMethod().getName(), path, 
						invoker.getDefaultChain().getInfo().getActionType().getName(), invoker.getDefaultChain().getInfo().getMethod().getName()));
				}
				invoker.setDefaultChain(chain);
			}
			addInvoker(path, invoker);
		}

		printActionMapping(ai);
	}

	public ActionInvoker getActionInvoker(ActionContext ac) {
		String path = HttpServlets.getServletPath(ac.getRequest());

		ac.setPath(path);
		ac.setPathArgs(new ArrayList<String>());

		ActionInvoker invoker = getInvoker(path, ac.getPathArgs());
		if (invoker != null) {
			ActionChain chain = invoker.getActionChain(ac);
			if (chain != null) {
				if (log.isDebugEnabled()) {
					log.debugf("Found mapping for [%s] path=%s : %s", ac.getRequest().getMethod(), path, chain);
				}
				return invoker;
			}
		}
		if (log.isDebugEnabled()) {
			log.debugf("Search mapping for path=%s : No action match", path);
		}
		return null;
	}

	public ActionInfo getActionInfo(String path) {
		ActionInvoker invoker = getInvoker(path, null);
		if (invoker != null) {
			ActionChain chain = invoker.getDefaultChain();
			if (chain != null) {
				return chain.getInfo();
			}
		}
		if (log.isDebugEnabled()) {
			log.debugf("Search mapping for path=%s : No action match", path);
		}
		return null;
	}

	protected void printActionMapping(ActionInfo ai) {
		if (log.isInfoEnabled()) {
			// print path
			String[] paths = ai.getPaths();
			String sb = Strings.join(paths, ", ");

			// print method
			Method method = ai.getMethod();
			String sm = String.format("%-30s : %-10s", method.toString(), method.getReturnType().getSimpleName());

			log.infof("%s >> %s | @Ok(%-5s) @Err(%-5s) @Fail(%-5s)",
				Strings.rightPad(sb, 30), sm, ai.getOkView(), ai.getErrorView(), ai.getFatalView());
		}
	}
}
