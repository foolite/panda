package panda.app.action.tool;

import panda.mvc.annotation.At;
import panda.app.action.base.BaseTempFileAction;
import panda.app.auth.Auth;
import panda.app.constant.AUTH;


@At("${super_path}/tmp")
@Auth(AUTH.SUPER)
public class TempFileAction extends BaseTempFileAction {
}