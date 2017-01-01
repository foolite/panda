package panda.mvc.validation.validator;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import panda.bean.BeanHandler;
import panda.bean.Beans;
import panda.ioc.annotation.IocBean;
import panda.lang.Collections;
import panda.lang.Strings;
import panda.mvc.ActionContext;
import panda.mvc.validation.Validators;
import panda.vfs.FileItem;

@IocBean(singleton=false)
public class RequiredValidator extends AbstractValidator {

	private Map<String, String> fields;
	
	/**
	 * 
	 */
	public RequiredValidator() {
		setMsgId(Validators.MSGID_REQUIRED);
	}

	/**
	 * @return the fields
	 */
	public Map<String, String> getFields() {
		return fields;
	}

	/**
	 * @param fields the fields to set
	 */
	public void setFields(Map<String, String> fields) {
		this.fields = fields;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected boolean validateValue(ActionContext ac, Object value) {
		if (value != null) {
			if (Collections.isNotEmpty(fields)) {
				boolean errs = false;
				BeanHandler bh = Beans.i().getBeanHandler(value.getClass());
				for (Entry<String, String> en : fields.entrySet()) {
					Object v = bh.getBeanValue(value, en.getKey());
					if (!exists(v)) {
						addChildFieldError(ac, en.getKey(), en.getValue());
						errs = true;
					}
				}
				return !errs;
			}
			return exists(value);
		}
		
		if (Collections.isEmpty(fields)) {
			addFieldError(ac);
		}
		else {
			for (Entry<String, String> en : fields.entrySet()) {
				addChildFieldError(ac, en.getKey(), en.getValue());
			}
		}
		return false;
	}
	
	private boolean exists(Object v) {
		if (v == null) {
			return false;
		}
		
		if (v instanceof File) {
			File f = (File)v;
			return f.exists();
		}
		
		if (v instanceof FileItem) {
			FileItem f = (FileItem)v;
			return f.isExists();
		}
		
		return true;
	}

	private void addChildFieldError(ActionContext ac, String field, String refer) {
		String pn = Strings.isEmpty(refer) ? field : refer;
		addFieldError(ac, Strings.isEmpty(getName()) ? pn : getName() + "." + pn);
	}
}