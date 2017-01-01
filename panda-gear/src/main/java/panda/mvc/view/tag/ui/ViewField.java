package panda.mvc.view.tag.ui;

import panda.ioc.annotation.IocBean;
import panda.mvc.view.tag.Escapes;


@IocBean(singleton=false)
public class ViewField extends ListUIBean {
	protected String escape = Escapes.ESCAPE_PHTML;
	protected String fieldValue;
	protected String format;
	protected String icon;

	/**
	 * @return the escape
	 */
	public String getEscape() {
		return escape;
	}

	/**
	 * @param escape the escape to set
	 */
	public void setEscape(String escape) {
		this.escape = escape;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * @param format the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @param icon the icon to set
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * @return the fieldValue
	 */
	public String getFieldValue() {
		return fieldValue;
	}

	/**
	 * The actual HTML value attribute of the checkbox
	 */
	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}
}