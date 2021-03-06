package panda.el.opt.object;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import panda.el.ELContext;
import panda.el.ELException;
import panda.el.obj.ELObj;
import panda.el.opt.RunMethod;
import panda.el.opt.AbstractTwoOpt;
import panda.el.opt.Operator;

/**
 * 方法体封装. 主要是把方法的左括号做为边界
 */
public class MethodOpt extends AbstractTwoOpt {

	private int size = 0;

	public void setSize(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}

	public int getPriority() {
		return 1;
	}

	public void wrap(Queue<Object> rpn) {
		if (getSize() <= 0) {
			left = rpn.poll();
		}
		else {
			right = rpn.poll();
			left = rpn.poll();
		}
	}

	public Object calculate(ELContext ec) {
		return fetchMethod(ec).run(ec, fetchParam(ec));
	}

	private RunMethod fetchMethod(ELContext ec) {
		if (left instanceof RunMethod) {
			return (RunMethod)left;
		}
		
		if (left instanceof ELObj) {
			return new AccessOpt(null, left);
		}
		
		throw new ELException("left is unsupported method: " + left + (left == null ? "" : " / " + left.getClass()));
	}

	/**
	 * 取得方法执行的参数
	 */
	@SuppressWarnings("unchecked")
	private List<Object> fetchParam(ELContext ec) {
		List<Object> rvals = new ArrayList<Object>();
		if (right != null) {
			if (right instanceof CommaOpt) {
				rvals = (List<Object>)((CommaOpt)right).calculate(ec);
			}
			else {
				rvals.add(calculateItem(ec, right));
			}
		}
		
		if (!rvals.isEmpty()) {
			for (int i = 0; i < rvals.size(); i++) {
				if (rvals.get(i) instanceof Operator) {
					rvals.set(i, ((Operator)rvals.get(i)).calculate(ec));
				}
			}
		}
		return rvals;
	}

	public String operator() {
		return "method";
	}

	public String toString() {
		return super.toString() + "(" + size + ")";
	}
}
