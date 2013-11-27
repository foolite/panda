package panda.tool.codegen.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import panda.lang.Arrays;
import panda.lang.Strings;

/**
 * <p>
 * Java class for ListUI complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;ListUI&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;column&quot; type=&quot;{panda.tool.codegen}ListColumn&quot; maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;query&quot; type=&quot;{panda.tool.codegen}ListQuery&quot; maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;param&quot; type=&quot;{panda.tool.codegen}Param&quot; maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;header&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; maxOccurs=&quot;1&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;footer&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; maxOccurs=&quot;1&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name=&quot;generate&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}boolean&quot; /&gt;
 *       &lt;attribute name=&quot;cssColumn&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; /&gt;
 *       &lt;attribute name=&quot;template&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; /&gt;
 *       &lt;attribute name=&quot;extend&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; /&gt;
 *       &lt;attribute name=&quot;name&quot; use=&quot;required&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "ListUI")
public class ListUI implements Comparable<ListUI> {

	@XmlElement(name = "column")
	private List<ListColumn> columnList;
	@XmlElement(name = "query")
	private List<ListQuery> queryList;
	@XmlElement(name = "param")
	private List<Param> paramList;
	@XmlElement
	private String header;
	@XmlElement
	private String footer;

	@XmlAttribute
	private Boolean generate;
	@XmlAttribute
	private String cssColumn;
	@XmlAttribute
	private String template;
	@XmlAttribute
	private String extend;
	@XmlAttribute(required = true)
	private String name;

	/**
	 * Constructor
	 */
	public ListUI() {
	}

	/**
	 * Constructor - copy properties from source
	 * 
	 * @param lui source list ui
	 */
	public ListUI(ListUI lui) {
		this.cssColumn = lui.cssColumn;
		this.template = lui.template;
		this.extend = lui.extend;
		this.generate = lui.generate;
		this.name = lui.name;

		columnList = new ArrayList<ListColumn>();
		for (ListColumn lc : lui.getColumnList()) {
			columnList.add(new ListColumn(lc));
		}

		queryList = new ArrayList<ListQuery>();
		for (ListQuery lq : lui.getQueryList()) {
			queryList.add(new ListQuery(lq));
		}

		paramList = new ArrayList<Param>();
		for (Param p : lui.getParamList()) {
			paramList.add(new Param(p));
		}
		
		this.header = lui.header;
		this.footer = lui.footer;
	}

	/**
	 * extend listui
	 * 
	 * @param src source listui
	 * @param parent extend listui
	 * @return listui
	 */
	public static ListUI extend(ListUI src, ListUI parent) {
		ListUI me = new ListUI(parent);

		if (src.cssColumn != null) {
			me.cssColumn = src.cssColumn;
		}
		if (src.template != null) {
			me.template = src.template;
		}
		if (src.generate != null) {
			me.generate = src.generate;
		}
		if (src.name != null) {
			me.name = src.name;
		}
		if (src.header != null) {
			me.header = src.header;
		}
		if (src.footer != null) {
			me.footer = src.footer;
		}

		List<ListColumn> mlcList = me.getColumnList();
		List<ListColumn> slcList = src.getColumnList();
		for (ListColumn slc : slcList) {
			boolean add = false;
			for (int i = 0; i < mlcList.size(); i++) {
				ListColumn mlc = mlcList.get(i);
				if (mlc.getName().equals(slc.getName())) {
					mlcList.set(i, ListColumn.extend(slc, mlc));
					add = true;
					break;
				}
			}
			if (!add) {
				mlcList.add(new ListColumn(slc));
			}
		}

		List<ListQuery> mlqList = me.getQueryList();
		List<ListQuery> slqList = src.getQueryList();
		for (ListQuery slq : slqList) {
			boolean add = false;
			for (int i = 0; i < mlqList.size(); i++) {
				ListQuery mlq = mlqList.get(i);
				if (mlq.getName().equals(slq.getName())) {
					mlqList.set(i, ListQuery.extend(slq, mlq));
					add = true;
					break;
				}
			}
			if (!add) {
				mlqList.add(new ListQuery(slq));
			}
		}

		List<Param> mpList = me.getParamList();
		List<Param> spList = src.getParamList();
		for (Param sp : spList) {
			boolean add = false;
			for (int i = 0; i < mpList.size(); i++) {
				Param mp = mpList.get(i);
				if (mp.getName().equals(sp.getName())) {
					mp.setValue(sp.getValue());
					add = true;
					break;
				}
			}
			if (!add) {
				mpList.add(new Param(sp));
			}
		}

		return me;
	}

	/**
	 * @return the ordered column list which ListColumn.display is not false
	 */
	public Set<ListColumn> getDisplayColumnList() {
		Set<ListColumn> set = new TreeSet<ListColumn>();
		List<ListColumn> list = getColumnList();
		for (int i = 0; i < list.size(); i++) {
			ListColumn lc = list.get(i);
			if (lc.getOrder() == null) {
				lc.setOrder((i + 1) * 100);
			}
			if (!Boolean.FALSE.equals(lc.getDisplay())) {
				set.add(lc);
			}
		}
		return set;
	}

	/**
	 * @return the ordered column list 
	 */
	public Set<ListColumn> getOrderedColumnList() {
		Set<ListColumn> set = new TreeSet<ListColumn>();
		List<ListColumn> list = getColumnList();
		for (int i = 0; i < list.size(); i++) {
			ListColumn lc = list.get(i);
			if (lc.getOrder() == null) {
				lc.setOrder((i + 1) * 100);
			}
			set.add(lc);
		}
		return set;
	}

	/**
	 * @return the columnList
	 */
	public List<ListColumn> getColumnList() {
		if (columnList == null) {
			columnList = new ArrayList<ListColumn>();
		}
		return this.columnList;
	}

	/**
	 * @return the queryList
	 */
	public List<ListQuery> getQueryList() {
		if (queryList == null) {
			queryList = new ArrayList<ListQuery>();
		}
		return queryList;
	}

	/**
	 * @return the display query list which ListQuery.display is not false
	 */
	public Set<ListQuery> getDisplayQueryList() {
		Set<ListQuery> set = new TreeSet<ListQuery>();
		List<ListQuery> list = getQueryList();
		for (int i = 0; i < list.size(); i++) {
			ListQuery q = list.get(i);
			if (q.getOrder() == null) {
				q.setOrder((i + 1) * 100);
			}

			if (!Boolean.FALSE.equals(q.getDisplay())) {
				set.add(q);
			}
		}
		return set;
	}

	/**
	 * @return the paramList
	 */
	public List<Param> getParamList() {
		if (paramList == null) {
			paramList = new ArrayList<Param>();
		}
		return this.paramList;
	}

	private Map<String, String> params;

	/**
	 * @return the params map
	 */
	public Map<String, String> getParams() {
		if (params == null) {
			params = new HashMap<String, String>();
			for (Param p : getParamList()) {
				params.put(p.getName(), p.getValue());
			}
		}
		return params;
	}
	
	/**
	 * Gets the value of the cssColumn property.
	 * 
	 * @return possible object is {@link String }
	 */
	public String getCssColumn() {
		return cssColumn;
	}

	/**
	 * Sets the value of the cssColumn property.
	 * 
	 * @param value allowed object is {@link String }
	 */
	public void setCssColumn(String value) {
		this.cssColumn = value;
	}

	/**
	 * Gets the value of the templates property.
	 * 
	 * @return templates
	 */
	public List<String> getTemplates() {
		return Arrays.asList(Strings.split(template));
	}

	/**
	 * Gets the value of the template property.
	 * 
	 * @return possible object is {@link String }
	 */
	public String getTemplate() {
		return template;
	}

	/**
	 * Sets the value of the template property.
	 * 
	 * @param value allowed object is {@link String }
	 */
	public void setTemplate(String value) {
		this.template = value;
	}

	/**
	 * Gets the value of the extend property.
	 * 
	 * @return possible object is {@link String }
	 */
	public String getExtend() {
		return extend;
	}

	/**
	 * Sets the value of the extend property.
	 * 
	 * @param value allowed object is {@link String }
	 */
	public void setExtend(String value) {
		this.extend = value;
	}

	/**
	 * Gets the value of the generate property.
	 * 
	 * @return possible object is {@link Boolean }
	 */
	public Boolean getGenerate() {
		return generate;
	}

	/**
	 * Sets the value of the generate property.
	 * 
	 * @param value allowed object is {@link Boolean }
	 */
	public void setGenerate(Boolean value) {
		this.generate = value;
	}

	/**
	 * Gets the value of the name property.
	 * 
	 * @return possible object is {@link String }
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the value of the name property.
	 * 
	 * @param value allowed object is {@link String }
	 */
	public void setName(String value) {
		this.name = value;
	}

	/**
	 * @return the header
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * @param header the header to set
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * @return the footer
	 */
	public String getFooter() {
		return footer;
	}

	/**
	 * @param footer the footer to set
	 */
	public void setFooter(String footer) {
		this.footer = footer;
	}

	/**
	 * @param o compare target
	 * @return -1/0/1
	 */
	public int compareTo(ListUI o) {
		if (this == o) {
			return 0;
		}
		int i = this.name.compareTo(o.name);
		return i == 0 ? this.hashCode() - o.hashCode() : i;
	}

}
