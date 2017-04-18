package panda.wordpress.bean;

import java.util.List;

public class Taxonomy extends BaseBean {
	public static final String CATEGORY = "category";
	public static final String POST_TAG = "post_tag";
	public static final String POST_FORMAT = "post_format";
	
	public String name;
	public String label;
	public boolean hierarchical;
	public boolean _public;
	public boolean show_ui;
	public boolean _builtin;
	public Object labels;
	public Object cap;
	public List<Object> object_type;
	/**
	 * @return the _public
	 */
	public boolean isPublic() {
		return _public;
	}
	/**
	 * @param _public the _public to set
	 */
	public void setPublic(boolean _public) {
		this._public = _public;
	}
}
