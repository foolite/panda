package panda.app.action.tool;

import java.util.List;

import panda.app.action.AbstractAction;
import panda.app.auth.Auth;
import panda.app.constant.AUTH;
import panda.app.constant.MVC;
import panda.app.task.CronEntry;
import panda.ioc.annotation.IocInject;
import panda.mvc.View;
import panda.mvc.annotation.At;
import panda.mvc.annotation.To;

@At("${super_path}/crons")
@Auth(AUTH.SUPER)
public class CronsAction extends AbstractAction {
	@IocInject(value=MVC.SCHEDULER_CRONS, required=false)
	private List<CronEntry> crons;
	
	/**
	 * execute
	 * 
	 * @return SUCCESS
	 * @throws Exception if an error occurs
	 */
	@At("")
	@To(View.SFTL)
	public List<CronEntry> execute() throws Exception {
		return crons;
	}
}