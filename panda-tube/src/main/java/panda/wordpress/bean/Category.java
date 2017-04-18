package panda.wordpress.bean;


/**
 * Category object for a blog.
 */
public class Category extends BaseBean {
	public String categoryId;

	public String parentId;

	public String categoryName;

	public String categoryDescription;

	public String description;

	public String htmlUrl;

	public String rssUrl;
	
	public String slug;

	//----------------------------------------------------
	// alias
	//
	/**
	 * @return the categoryId
	 */
	public String getCategory_id() {
		return categoryId;
	}

	/**
	 * @param categoryId the categoryId to set
	 */
	public void setCategory_id(String categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * @return the categoryName
	 */
	public String getCategory_name() {
		return categoryName;
	}

	/**
	 * @param categoryName the categoryName to set
	 */
	public void setCategory_name(String categoryName) {
		this.categoryName = categoryName;
	}
}
