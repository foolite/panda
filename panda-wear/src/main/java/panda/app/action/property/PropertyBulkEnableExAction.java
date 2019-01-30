package panda.app.action.property;

import java.util.List;

import panda.app.auth.Auth;
import panda.app.constant.AUTH;
import panda.app.constant.VAL;
import panda.app.entity.Property;
import panda.dao.query.DataQuery;
import panda.mvc.annotation.At;

@At("${super_path}/property")
@Auth(AUTH.SUPER)
public class PropertyBulkEnableExAction extends PropertyBulkEnableAction {
	public PropertyBulkEnableExAction() {
		super();
	}

	@Override
	protected Property getBulkUpdateSample(List<Property> dataList, DataQuery<Property> dq) {
		Property d = new Property();
		d.setStatus(VAL.STATUS_ACTIVE);

		dq.excludeAll().include(Property.STATUS);

		return d;
	}

}