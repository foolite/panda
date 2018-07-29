package panda.app.action.template;

import panda.app.action.crud.GenericListAction;
import panda.app.auth.Auth;
import panda.app.constant.AUTH;
import panda.app.entity.Template;
import panda.mvc.annotation.At;
import panda.mvc.annotation.To;
import panda.mvc.annotation.Validates;
import panda.mvc.annotation.param.Param;
import panda.mvc.bean.Queryer;
import panda.mvc.view.Views;

@At("${super_path}/template")
@Auth(AUTH.SUPER)
public class TemplateListAction extends GenericListAction<Template> {

	/**
	 * Constructor
	 */
	public TemplateListAction() {
		setType(Template.class);
		addDisplayFields(Template.ID, Template.NAME, Template.LANGUAGE, Template.COUNTRY, Template.STATUS, Template.UPDATED_AT, Template.UPDATED_BY, Template.UPDATED_BY_USER);
	}


	/*----------------------------------------------------------------------*
	 * Actions
	 *----------------------------------------------------------------------*/
	/**
	 * list
	 * @param qr queryer
	 * @return result or view
	 */
	@At
	@To(value=Views.SFTL, error=Views.SFTL)
	public Object list(@Param @Validates Queryer qr) {
		return super.list(qr);
	}
	
	/**
	 * list_print
	 * @param qr queryer
	 * @return result or view
	 */
	@At
	@To(value=Views.SFTL, error=Views.SFTL)
	public Object list_print(@Param @Validates Queryer qr) {
		return super.list_print(qr);
	}
	
	/**
	 * list_json
	 * @param qr queryer
	 * @return result or view
	 */
	@At
	@To(Views.SJSON)
	public Object list_json(@Param @Validates Queryer qr) {
		return super.list_json(qr);
	}
	
	/**
	 * list_xml
	 * @param qr queryer
	 * @return result or view
	 */
	@At
	@To(Views.SXML)
	public Object list_xml(@Param @Validates Queryer qr) {
		return super.list_xml(qr);
	}
	
}

