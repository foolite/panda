package panda.app.action.property;

import java.util.ArrayList;
import java.util.List;
import panda.app.action.crud.GenericListAction;
import panda.app.auth.Auth;
import panda.app.constant.AUTH;
import panda.app.entity.Property;
import panda.mvc.annotation.At;
import panda.mvc.annotation.To;
import panda.mvc.annotation.param.Param;
import panda.mvc.annotation.validate.VisitValidate;
import panda.mvc.bean.Queryer;
import panda.mvc.bean.QueryerEx;
import panda.mvc.view.Views;
import panda.mvc.view.util.ListColumn;

@At("${super_path}/property")
@Auth(AUTH.SUPER)
public class PropertyListAction extends GenericListAction<Property> {

	/**
	 * Constructor
	 */
	public PropertyListAction() {
		setType(Property.class);
		addDisplayFields(Property.ID, Property.CLAZZ, Property.LOCALE, Property.NAME, Property.VALUE, Property.MEMO, Property.STATUS, Property.UPDATED_AT, Property.UPDATED_BY, Property.UPDATED_BY_USER);
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
	public Object list(@Param @VisitValidate Queryer qr) {
		return super.list(qr);
	}
	
	/**
	 * list_print
	 * @param qr queryer
	 * @return result or view
	 */
	@At
	@To(value=Views.SFTL, error=Views.SFTL)
	public Object list_print(@Param @VisitValidate Queryer qr) {
		return super.list_print(qr);
	}
	
	/**
	 * expo_csv
	 * @param qr queryer
	 * @return result or view
	 */
	@At
	@To(value=Views.SFTL, error=Views.SFTL)
	public Object expo_csv(@Param @VisitValidate QueryerEx qr) {
		List<ListColumn> columns = new ArrayList<ListColumn>();
		if (displayField("id")) {
			ListColumn lc = new ListColumn();
			lc.name = "id";
			lc.header = getFieldLabel("id");
			lc.hidden = false;
			columns.add(lc);
		}
		if (displayField("clazz")) {
			ListColumn lc = new ListColumn();
			lc.name = "clazz";
			lc.header = getFieldLabel("clazz");
			lc.hidden = false;
			columns.add(lc);
		}
		if (displayField("locale")) {
			ListColumn lc = new ListColumn();
			lc.name = "locale";
			lc.header = getFieldLabel("locale");
			lc.hidden = false;
			ListColumn.Format lcf = new ListColumn.Format();
			lcf.type = "code";
			lcf.codemap = consts().getAppLocaleMap();
			lc.format = lcf;
			columns.add(lc);
		}
		if (displayField("name")) {
			ListColumn lc = new ListColumn();
			lc.name = "name";
			lc.header = getFieldLabel("name");
			lc.hidden = false;
			columns.add(lc);
		}
		if (displayField("value")) {
			ListColumn lc = new ListColumn();
			lc.name = "value";
			lc.header = getFieldLabel("value");
			lc.hidden = false;
			columns.add(lc);
		}
		if (displayField("memo")) {
			ListColumn lc = new ListColumn();
			lc.name = "memo";
			lc.header = getFieldLabel("memo");
			lc.hidden = false;
			columns.add(lc);
		}
		if (displayField("status")) {
			ListColumn lc = new ListColumn();
			lc.name = "status";
			lc.header = getFieldLabel("status");
			lc.hidden = false;
			ListColumn.Format lcf = new ListColumn.Format();
			lcf.type = "code";
			lcf.codemap = consts().getDataStatusMap();
			lc.format = lcf;
			columns.add(lc);
		}
		if (displayField("updatedAt")) {
			ListColumn lc = new ListColumn();
			lc.name = "updatedAt";
			lc.header = getFieldLabel("updatedAt");
			lc.hidden = false;
			ListColumn.Format lcf = new ListColumn.Format();
			lcf.type = "datetime";
			lc.format = lcf;
			columns.add(lc);
		}
		if (displayField("updatedBy")) {
			ListColumn lc = new ListColumn();
			lc.name = "updatedBy";
			lc.header = getFieldLabel("updatedBy");
			lc.hidden = true;
			columns.add(lc);
		}
		if (displayField("updatedByUser")) {
			ListColumn lc = new ListColumn();
			lc.name = "updatedByUser";
			lc.header = getFieldLabel("updatedByUser");
			lc.hidden = false;
			columns.add(lc);
		}
		return super.expo_csv(qr, columns);
	}
	
	/**
	 * expo_json
	 * @param qr queryer
	 * @return result or view
	 */
	@At
	@To(Views.SJSON)
	public Object expo_json(@Param @VisitValidate QueryerEx qr) {
		return super.expo_json(qr);
	}
	
	/**
	 * expo_xml
	 * @param qr queryer
	 * @return result or view
	 */
	@At
	@To(Views.SXML)
	public Object expo_xml(@Param @VisitValidate QueryerEx qr) {
		return super.expo_xml(qr);
	}
	
}
