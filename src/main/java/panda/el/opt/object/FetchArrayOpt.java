package panda.el.opt.object;

import java.util.Queue;

import panda.el.ElContext;
import panda.el.opt.AbstractOpt;

/**
 * ']',数组封装. 本身没做什么操作,只是对'[' ArrayOpt 做了一个封装而已
 * 
 * @author juqkai(juqkai@gmail.com)
 */
public class FetchArrayOpt extends AbstractOpt {
	private Object left;

	public void wrap(Queue<Object> operand) {
		left = operand.poll();
	}

	public int getPriority() {
		return 1;
	}

	public Object calculate(ElContext ec) {
		if (left instanceof ArrayOpt) {
			return ((ArrayOpt)left).calculate(ec);
		}
		return null;
	}

	public String operator() {
		return "]";
	}
}
