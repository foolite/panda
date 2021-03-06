package panda.gems.admin.action;

import java.util.List;

import panda.app.action.BaseAction;
import panda.app.auth.Auth;
import panda.app.constant.AUTH;
import panda.app.constant.MVC;
import panda.app.task.CronActionTask;
import panda.ioc.annotation.IocInject;
import panda.mvc.annotation.At;
import panda.mvc.annotation.To;
import panda.mvc.view.Views;

@At("${!!super_path|||'/super'}/crons")
@Auth(AUTH.SUPER)
public class CronsAction extends BaseAction {
	@IocInject(value=MVC.SCHEDULER_CRONS, required=false)
	private List<CronActionTask> crons;
	
	/**
	 * execute
	 * 
	 * @return SUCCESS
	 * @throws Exception if an error occurs
	 */
	@At("")
	@To(Views.SFTL)
	public List<CronActionTask> execute() throws Exception {
		return crons;
	}
}
