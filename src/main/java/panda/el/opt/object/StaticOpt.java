package panda.el.opt.object;

import java.util.List;

import panda.el.ElContext;
import panda.el.ElException;
import panda.el.opt.RunMethod;
import panda.el.opt.TwoTernary;
import panda.lang.Exceptions;
import panda.lang.Fields;
import panda.lang.Methods;

/**
 * operator: '@'
 */
public class StaticOpt extends TwoTernary implements RunMethod {
	public int getPriority() {
		return 1;
	}

	private Class getClass(ElContext ec) {
		Object obj = getLeftVar(ec);
		if (obj == null) {
			throw new ElException("obj is NULL, can't call obj@" + right);
		}
		
		Class clz;
		if (obj instanceof Class) {
			clz = (Class)obj;
		}
		else {
			clz = obj.getClass();
		}
		return clz;
	}
	
	public Object calculate(ElContext ec) {
		Class clz = getClass(ec);
		try {
			return Fields.readStaticField(clz, right.toString(), false);
		}
		catch (IllegalAccessException e) {
			throw Exceptions.wrapThrow(e);
		}
	}

	public Object run(ElContext ec, List<Object> param) {
		Class clz = getClass(ec);

		Object[] arg = param.toArray();
		try {
			return Methods.invokeStaticMethod(clz, right.toString(), arg);
		}
		catch (Exception e) {
			throw Exceptions.wrapThrow(e);
		}
	}

	public String operator() {
		return "@";
	}
}
