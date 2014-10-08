package panda.mvc.processor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import panda.lang.Exceptions;
import panda.mvc.ActionContext;

public class MethodInvokeProcessor extends AbstractProcessor {

	public void process(ActionContext ac) throws Throwable {
		Object action = ac.getAction();
		Method method = ac.getMethod();
		Object[] args = ac.getArgs();
		try {
			Object r = method.invoke(action, args);
			ac.setResult(r);
			doNext(ac);
		}
		catch (IllegalAccessException e) {
			throw Exceptions.unwrapThrow(e);
		}
		catch (IllegalArgumentException e) {
			throw Exceptions.unwrapThrow(e);
		}
		catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}
}
