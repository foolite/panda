package panda.mvc.processor;

import panda.mvc.ActionContext;
import panda.mvc.ActionProcessor;

/**
 * 抽象的Processor实现. 任何Processor实现都应该继承这个类,以获取正确的执行逻辑.
 */
public abstract class AbstractActionProcessor implements ActionProcessor {
	/**
	 * 继续执行下一个Processor
	 * <p/>
	 * <b>一般情形下都不应该覆盖这个方法<b>
	 * 
	 * @param ac 执行方法的上下文
	 */
	protected void doNext(ActionContext ac) {
		ac.getChain().doNext(ac);
	}
}
