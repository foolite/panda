package panda.gems.bundle.property.action;

import panda.app.action.crud.GenericEditAction;
import panda.app.auth.Auth;
import panda.app.constant.AUTH;
import panda.gems.bundle.property.entity.Property;
import panda.mvc.annotation.At;
import panda.mvc.annotation.To;
import panda.mvc.annotation.TokenProtect;
import panda.mvc.annotation.param.Param;
import panda.mvc.annotation.validate.RequiredValidate;
import panda.mvc.annotation.validate.VisitValidate;
import panda.mvc.view.Views;
import panda.net.http.HttpMethod;

@At("${!!super_path|||'/super'}/property")
@Auth(AUTH.SUPER)
public class PropertyEditAction extends GenericEditAction<Property> {

	/**
	 * Constructor
	 */
	public PropertyEditAction() {
		setType(Property.class);
		setDisplayFields(Property.ID, Property.CLAZZ, Property.LOCALE, Property.NAME, Property.VALUE, Property.MEMO, Property.STATUS, Property.UPDATED_AT, Property.UPDATED_BY, Property.UPDATED_BY_NAME, Property.UPDATED_BY_USER);
	}


	/*----------------------------------------------------------------------*
	 * Actions
	 *----------------------------------------------------------------------*/
	/**
	 * view
	 * @param key the input key
	 * @return result or view
	 */
	@At
	@To(value=Views.SFTL, error=Views.SFTL)
	public Object view(@Param Property key) {
		return super.view(key);
	}

	/**
	 * view_input
	 * @param data the input data
	 * @return result or view
	 */
	@At
	@To(value="sftl:~view", error="sftl:~view")
	public Object view_input(@Param Property data) {
		return super.view_input(data);
	}

	/**
	 * view_json
	 * @param key the input key
	 * @return result
	 */
	@At
	@To(Views.SJSON)
	public Object view_json(@Param Property key) {
		return super.view(key);
	}

	/**
	 * view_xml
	 * @param key the input key
	 * @return result
	 */
	@At
	@To(Views.SXML)
	public Object view_xml(@Param Property key) {
		return super.view(key);
	}

	/**
	 * print
	 * @param key the input key
	 * @return result or view
	 */
	@At
	@To(value=Views.SFTL, error=Views.SFTL)
	public Object print(@Param Property key) {
		return super.print(key);
	}

	/**
	 * print_input
	 * @param data the input data
	 * @return result or view
	 */
	@At
	@To(value="sftl:~print", error="sftl:~print")
	public Object print_input(@Param Property data) {
		return super.print_input(data);
	}

	/**
	 * add
	 * @return result or view
	 */
	@At
	@To(value=Views.SFTL, error=Views.SFTL)
	public Object add() {
		return super.add();
	}

	/**
	 * add_input
	 * @param data the input data
	 * @return result or view
	 */
	@At
	@To(value="sftl:~add", error="sftl:~add")
	public Object add_input(@Param Property data) {
		return super.add_input(data);
	}

	/**
	 * add_confirm
	 * @param data the input data
	 * @return result or view
	 */
	@At(method=HttpMethod.POST)
	@To(value=Views.SFTL, error="sftl:~add")
	@TokenProtect
	public Object add_confirm(@Param 
			@RequiredValidate(fields={ "clazz", "locale", "name" })
			@VisitValidate
			Property data) {
		return super.add_confirm(data);
	}

	/**
	 * add_execute
	 * @param data the input data
	 * @return result or view
	 */
	@At(method=HttpMethod.POST)
	@To(value=Views.SFTL, error="sftl:~add")
	@TokenProtect
	public Object add_execute(@Param 
			@RequiredValidate(fields={ "clazz", "locale", "name" })
			@VisitValidate
			Property data) {
		return super.add_execute(data);
	}

	/**
	 * copy
	 * @param key the input key
	 * @return result or view
	 */
	@At
	@To(value=Views.SFTL, error=Views.SFTL)
	public Object copy(@Param Property key) {
		return super.copy(key);
	}

	/**
	 * copy_input
	 * @param data the input data
	 * @return result or view
	 */
	@At
	@To(value="sftl:~copy", error="sftl:~copy")
	public Object copy_input(@Param Property data) {
		return super.copy_input(data);
	}

	/**
	 * copy_confirm
	 * @param data the input data
	 * @return result or view
	 */
	@At(method=HttpMethod.POST)
	@To(value=Views.SFTL, error="sftl:~copy")
	@TokenProtect
	public Object copy_confirm(@Param 
			@RequiredValidate(fields={ "clazz", "locale", "name" })
			@VisitValidate
			Property data) {
		return super.copy_confirm(data);
	}

	/**
	 * copy_execute
	 * @param data the input data
	 * @return result or view
	 */
	@At(method=HttpMethod.POST)
	@To(value=Views.SFTL, error="sftl:~copy")
	@TokenProtect
	public Object copy_execute(@Param 
			@RequiredValidate(fields={ "clazz", "locale", "name" })
			@VisitValidate
			Property data) {
		return super.copy_execute(data);
	}

	/**
	 * edit
	 * @param key the input key
	 * @return result or view
	 */
	@At
	@To(value=Views.SFTL, error=Views.SFTL)
	public Object edit(@Param Property key) {
		return super.edit(key);
	}

	/**
	 * edit_input
	 * @param data the input data
	 * @return result or view
	 */
	@At
	@To(value="sftl:~edit", error="sftl:~edit")
	public Object edit_input(@Param Property data) {
		return super.edit_input(data);
	}

	/**
	 * edit_confirm
	 * @param data the input data
	 * @return result or view
	 */
	@At(method=HttpMethod.POST)
	@To(value=Views.SFTL, error="sftl:~edit")
	@TokenProtect
	public Object edit_confirm(@Param 
			@RequiredValidate(fields={ "clazz", "locale", "name" })
			@VisitValidate
			Property data) {
		return super.edit_confirm(data);
	}

	/**
	 * edit_execute
	 * @param data the input data
	 * @return result or view
	 */
	@At(method=HttpMethod.POST)
	@To(value=Views.SFTL, error="sftl:~edit")
	@TokenProtect
	public Object edit_execute(@Param 
			@RequiredValidate(fields={ "clazz", "locale", "name" })
			@VisitValidate
			Property data) {
		return super.edit_execute(data);
	}

	/**
	 * delete
	 * @param key the input key
	 * @return result or view
	 */
	@At
	@To(value=Views.SFTL, error=Views.SFTL)
	public Object delete(@Param Property key) {
		return super.delete(key);
	}

	/**
	 * delete_execute
	 * @param key the input key
	 * @return result or view
	 */
	@At(method=HttpMethod.POST)
	@To(value=Views.SFTL, error="sftl:~delete")
	@TokenProtect
	public Object delete_execute(@Param Property key) {
		return super.delete_execute(key);
	}

}

