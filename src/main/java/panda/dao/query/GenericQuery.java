package panda.dao.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import panda.dao.entity.Entity;
import panda.dao.entity.EntityField;
import panda.dao.entity.EntityJoin;
import panda.dao.query.Filter.ComboFilter;
import panda.dao.query.Filter.ReferFilter;
import panda.dao.query.Filter.SimpleFilter;
import panda.dao.query.Filter.ValueFilter;
import panda.lang.Arrays;
import panda.lang.Asserts;
import panda.lang.Collections;
import panda.lang.Objects;
import panda.lang.Strings;

/**
 * 
 * @author yf.frank.wang@gmail.com
 *
 * @param <T> table
 */
public class GenericQuery<T> implements Query<T>, Cloneable {
	protected Object target;
	protected boolean distinct;
	protected long start;
	protected long limit;
	protected Map<String, String> columns;
	protected Map<String, Join> joins;

	protected ComboFilter filters;
	
	/** filter stack */
	protected List<ComboFilter> stack;

	protected Map<String, Order> orders;
	protected List<String> groups;
	
	protected static final int HAS_INCLUDES = 0x01;
	protected static final int HAS_EXCLUDES = 0x02;
	protected Integer flags;

	/**
	 * constructor
	 * @param type query type
	 */
	public GenericQuery(Class<T> type) {
		this.target = type;
	}

	/**
	 * constructor
	 * @param entity query entity
	 */
	public GenericQuery(Entity<T> entity) {
		this.target = entity;
	}

	/**
	 * @param target query target
	 */
	public GenericQuery(String target) {
		this.target = target;
	}
	
	/**
	 * @param query query to clone
	 */
	public GenericQuery(Query<T> query) {
		target = query.getTarget();
		start = query.getStart();
		limit = query.getLimit();
		distinct = query.isDistinct();

		if (query.hasColumns()) {
			columns = new LinkedHashMap<String, String>();
			columns.putAll(query.getColumns());
		}

		if (query.hasJoins()) {
			joins = new LinkedHashMap<String, Join>();
			joins.putAll(query.getJoins());
		}

		if (query.hasFilters()) {
			// shadow clone
			filters = query.getFilters().clone();
		}
		
		if (query.hasOrders()) {
			orders = new LinkedHashMap<String, Order>();
			orders.putAll(query.getOrders());
		}
		
		if (query.hasGroups()) {
			groups = new ArrayList<String>();
			groups.addAll(query.getGroups());
		}
	}

	/**
	 * @return the target
	 */
	public Object getTarget() {
		return target;
	}

	/**
	 * @return the entity
	 */
	@SuppressWarnings("unchecked")
	public Entity<T> getEntity() {
		return target instanceof Entity ? (Entity<T>)target : null;
	}

	/**
	 * @param entity the entity to set
	 */
	public void setEntity(Entity<T> entity) {
		this.target = entity;
	}

	/**
	 * @return the type
	 */
	@SuppressWarnings("unchecked")
	public Class<T> getType() {
		if (target instanceof Entity) {
			return ((Entity<T>)target).getType();
		}
		if (target instanceof Class) {
			return (Class<T>)target;
		}
		return (Class<T>)Map.class;
	}

	/**
	 * @return the table
	 */
	@SuppressWarnings("unchecked")
	public String getTable() {
		if (target instanceof Entity) {
			return ((Entity<T>)target).getView();
		}
		if (target instanceof String) {
			return (String)target;
		}
		return null;
	}

	/**
	 * clear
	 */
	public GenericQuery clear() {
		start = 0;
		limit = 0;
		if (columns != null) {
			columns.clear();
		};
		if (joins != null) {
			joins.clear();
		}
		if (filters != null) {
			filters.setLogical(Logical.AND);
			filters.clear();
		}
		if (orders != null) {
			orders.clear();
		};
		if (groups != null) {
			groups.clear();
		};
		
		return this;
	}

	//---------------------------------------------------------------
	//
	/**
	 * @return the distinct
	 */
	public boolean isDistinct() {
		return distinct;
	}

	/**
	 * @param distinct the distinct to set
	 */
	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	//---------------------------------------------------------------
	// columns
	//
	/**
	 * @return the columns
	 */
	public Map<String, String> getColumns() {
		return columns;
	}

	/**
	 * @return true if the columns is not empty
	 */
	public boolean hasColumns() {
		return columns != null && !columns.isEmpty();
	}

	protected int flags() {
		if (flags == null) {
			flags = 0;
			if (columns != null && !columns.isEmpty()) {
				Collection<String> vs = columns.values();
				for (String v : vs) {
					if ("".equals(v)) {
						flags |= HAS_INCLUDES;
					}
					if (v == null) {
						flags |= HAS_EXCLUDES;
					}
				}
			}
		}
		return flags;
	}
	
	/**
	 * @return true if has included columns
	 */
	protected boolean hasIncludes() {
		return (flags() & HAS_INCLUDES) != 0;
	}
	
	/**
	 * @return true if has excluded columns
	 */
	protected boolean hasExcludes() {
		return (flags() & HAS_EXCLUDES) != 0;
	}

	/**
	 * @param name column name
	 * @return column value
	 */
	public String getColumn(String name) {
		if (columns == null) {
			return null;
		}
		return columns.get(name);
	}

	/**
	 * @param name column name
	 * @param value column value
	 * @return this
	 */
	public GenericQuery column(String name, String value) {
		setColumn(name, value);
		flags = null;
		return this;
	}

	/**
	 * @param name the field name to include
	 * @param value the column value
	 * @return this
	 */
	private GenericQuery setColumn(String name, String value) {
		Asserts.notEmpty(name);
		if (columns == null) {
			columns = new LinkedHashMap<String, String>();
		}
		columns.put(name, value);
		return this;
	}

	/**
	 * include column
	 * @param name column name
	 */
	private void incColumn(String name) {
		String c = getColumn(name);
		setColumn(name, Strings.defaultString(c, ""));
	}

	/**
	 * exclude column
	 * @param name column name
	 */
	private void excColumn(String name) {
		setColumn(name, null);
	}
	
	/**
	 * include primary keys
	 * @return this
	 */
	public GenericQuery includePrimayKeys() {
		Entity<?> entity = getEntity();
		for (EntityField ef : entity.getPrimaryKeys()) {
			incColumn(ef.getName());
		}
		flags = null;
		return this;
	}

	/**
	 * include not primary keys
	 * @return this
	 */
	public GenericQuery includeNotPrimaryKeys() {
		Entity<?> entity = getEntity();
		for (EntityField ef : entity.getFields()) {
			if (!ef.isPrimaryKey()) {
				incColumn(ef.getName());
			}
		}
		flags = null;
		return this;
	}

	/**
	 * @return this
	 */
	public GenericQuery includeAll() {
		Entity<?> entity = getEntity();
		for (EntityField ef : entity.getFields()) {
			incColumn(ef.getName());
		}
		flags = null;
		return this;
	}

	/**
	 * @param names include name
	 * @return this
	 */
	public GenericQuery include(String... names) {
		return include(Arrays.asList(names));
	}

	/**
	 * @param names include name
	 * @return this
	 */
	public GenericQuery include(Collection<String> names) {
		if (Collections.isNotEmpty(names)) {
			for (String name : names) {
				incColumn(name);
			}
			flags = null;
		}
		return this;
	}

	/**
	 * @param names include name
	 * @return this
	 */
	public GenericQuery includeOnly(String... names) {
		return includeOnly(Arrays.asList(names));
	}

	/**
	 * @param names include name
	 * @return this
	 */
	public GenericQuery includeOnly(Collection<String> names) {
		if (Collections.isEmpty(names)) {
			clearColumns();
			return this;
		}
		
		for (String name : names) {
			incColumn(name);
		}

		Iterator<Entry<String, String>> it = columns.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> en = it.next();
			if (!names.contains(en.getKey())) {
				it.remove();
			}
		}
		flags = null;
		return this;
	}
	
	/**
	 * exclude primary keys
	 * @return this
	 */
	public GenericQuery excludePrimaryKeys() {
		Entity<?> entity = getEntity();
		for (EntityField ef : entity.getFields()) {
			if (ef.isPrimaryKey()) {
				excColumn(ef.getName());
			}
		}
		flags = null;
		return this;
	}

	/**
	 * exclude not primary keys
	 * @return this
	 */
	public GenericQuery excludeNotPrimaryKeys() {
		Entity<?> entity = getEntity();
		for (EntityField ef : entity.getFields()) {
			if (!ef.isPrimaryKey()) {
				excColumn(ef.getName());
			}
		}
		flags = null;
		return this;
	}

	/**
	 * @return this
	 */
	public GenericQuery excludeAll() {
		Entity<?> entity = getEntity();
		for (EntityField ef : entity.getFields()) {
			excColumn(ef.getName());
		}
		flags = null;
		return this;
	}

	/**
	 * @param names the field names to exclude
	 * @return this
	 */
	public GenericQuery exclude(String... names) {
		return exclude(Arrays.asList(names));
	}

	/**
	 * @param names the field names to exclude
	 * @return this
	 */
	public GenericQuery exclude(Collection<String> names) {
		if (Collections.isNotEmpty(names)) {
			for (String name : names) {
				excColumn(name);
			}
			flags = null;
		}
		return this;
	}

	/**
	 * @param name field name
	 * @return true if the name should include
	 */
	public boolean shouldInclude(String name) {
		return !hasIncludes() || columns.get(name) != null;
	}

	/**
	 * @param name field name
	 * @return true if the name should exclude
	 */
	public boolean shouldExclude(String name) {
		if (hasIncludes()) {
			return columns.get(name) == null;
		}
		if (hasExcludes()) {
			return columns.containsKey(name) && columns.get(name) == null;
		}
		return false;
	}

	/**
	 * clearColumns
	 * 
	 * @return this
	 */
	public GenericQuery clearColumns() {
		if (columns != null) {
			columns.clear();
			flags = null;
		}
		return this;
	}

	//---------------------------------------------------------------
	// join
	//
	/**
	 * @return true if has orders
	 */
	public boolean hasJoins() {
		return joins != null && !joins.isEmpty();
	}

	/**
	 * @return joins
	 */
	public Map<String, Join> getJoins() {
		return joins;
	}

	/**
	 * add left join
	 * @param query join query
	 * @param alias join alias
	 * @param conditions join conditions
	 * @return this
	 */
	public GenericQuery leftJoin(Query<?> query, String alias, String ... conditions) {
		return join(Join.LEFT, query, alias, conditions);
	}

	/**
	 * add right join
	 * @param query join query
	 * @param alias join alias
	 * @param conditions join conditions
	 * @return this
	 */
	public GenericQuery rightJoin(Query<?> query, String alias, String ... conditions) {
		return join(Join.RIGHT, query, alias, conditions);
	}

	/**
	 * add inner join
	 * @param query join query
	 * @param alias join alias
	 * @param conditions join conditions
	 * @return this
	 */
	public GenericQuery innerJoin(Query<?> query, String alias, String ... conditions) {
		return join(Join.INNER, query, alias, conditions);
	}

	/**
	 * add join
	 * @param query join query
	 * @param alias join alias
	 * @param conditions join conditions
	 * @return this
	 */
	public GenericQuery join(Query<?> query, String alias, String ... conditions) {
		return join(null, query, alias, conditions);
	}

	/**
	 * add join
	 * @param type join type
	 * @param query join query
	 * @param alias join alias
	 * @param conditions join conditions
	 * @return this
	 */
	public GenericQuery join(String type, Query<?> query, String alias, String ... conditions) {
		Asserts.notEmpty(alias, "The parameter alias is empty");
		if (joins == null) {
			joins = new LinkedHashMap<String, Join>();
		}
		joins.put(alias, new Join(type, query, conditions));
		return this;
	}

	//---------------------------------------------------------------
	// auto joins
	//
	/**
	 * auto add left join from @Join
	 * @param join join name
	 * @return this
	 */
	public GenericQuery autoLeftJoin(String join) {
		return autoJoin(Join.LEFT, join);
	}

	/**
	 * auto add left join from @Join
	 * @param join join name
	 * @param query join table query
	 * @return this
	 */
	public GenericQuery autoLeftJoin(String join, Query<?> query) {
		return autoJoin(Join.LEFT, join);
	}

	/**
	 * auto add right join from @Join
	 * @param join join name
	 * @return this
	 */
	public GenericQuery autoRightJoin(String join) {
		return autoJoin(Join.RIGHT, join);
	}

	/**
	 * auto add right join from @Join
	 * @param join join name
	 * @param query join table query
	 * @return this
	 */
	public GenericQuery autoRightJoin(String join, Query<?> query) {
		return autoJoin(Join.RIGHT, join, query);
	}

	/**
	 * auto add inner join from @Join
	 * @param join join name
	 * @return this
	 */
	public GenericQuery autoInnerJoin(String join) {
		return autoJoin(Join.INNER, join);
	}

	/**
	 * auto add inner join from @Join
	 * @param join join name
	 * @param query join table query
	 * @return this
	 */
	public GenericQuery autoInnerJoin(String join, Query<?> query) {
		return autoJoin(Join.INNER, join, query);
	}

	/**
	 * auto add join from @Join
	 * @param join join name
	 * @return this
	 */
	public GenericQuery autoJoin(String join) {
		return autoJoin(null, join);
	}

	/**
	 * auto add join from @Join
	 * @param join join name
	 * @param query join table query
	 * @return this
	 */
	public GenericQuery autoJoin(String join, Query<?> query) {
		return autoJoin(null, join, query);
	}

	/**
	 * auto add join from @Join
	 * @param type join type
	 * @param join join name
	 * @return this
	 */
	public GenericQuery autoJoin(String type, String join) {
		return autoJoin(type, join, null);
	}
	
	/**
	 * auto add join from @Join
	 * @param type join type
	 * @param join join name
	 * @param query join table query
	 * @return this
	 */
	@SuppressWarnings("unchecked")
	public GenericQuery autoJoin(String type, String join, Query<?> query) {
		Asserts.notEmpty(join, "The parameter join is empty");
		Entity<?> entity = getEntity();

		Asserts.notNull(entity, "The entity is null");
		Asserts.notEmpty(entity.getJoins(), "The entity joins is null");

		EntityJoin ej = entity.getJoin(join);
		if (ej == null) {
			throw new IllegalArgumentException("The entity join [" + join + "] is not found");
		}

		if (query == null) {
			query = new GenericQuery(ej.getTarget());
		}
		else {
			if (query.getEntity() != ej.getTarget()) {
				throw new IllegalArgumentException("The join query [" + query.getEntity().getType() + "] is compatible to " + ej.getTarget().getType());
			}
		}
		
		String[] cs = new String[ej.getKeys().size()];
		for (int i = 0; i < cs.length; i++) {
			cs[i] = ej.getKeys().get(i).getName() + '=' + ej.getRefs().get(i).getName();
		}
		
		join(type, query, join, cs);
		for (EntityField ef : entity.getFields()) {
			if (join.equals(ef.getJoinName())) {
				column(ef.getName(), join + '.' + ef.getJoinField());
			}
		}
		return this;
	}

	//---------------------------------------------------------------
	// orders
	//
	
	/**
	 * @param name property/field/column name
	 * @return true if the property has some order
	 */
	public boolean hasOrder(String name) {
		if (hasOrders()) {
			return orders.containsKey(name);
		}
		return false;
	}
	
	/**
	 * @return true if has orders
	 */
	public boolean hasOrders() {
		return orders != null && !orders.isEmpty();
	}

	/**
	 * @return orders
	 */
	public Map<String, Order> getOrders() {
		return orders;
	}

	/**
	 * add ascend order
	 * @param column column
	 * @return this
	 */
	public GenericQuery orderBy(String column) {
		return orderBy(column, true);
	}

	/**
	 * add order
	 * @param name name
	 * @param order order direction
	 * @return this
	 */
	public GenericQuery orderBy(String name, Order order) {
		Asserts.notEmpty(name, "The parameter name is empty");
		if (orders == null) {
			orders = new LinkedHashMap<String, Order>();
		}
		orders.put(name, order);
		return this;
	}

	/**
	 * add order
	 * @param name name
	 * @param order order direction
	 * @return this
	 */
	public GenericQuery orderBy(String name, String order) {
		return orderBy(name, Order.parse(order));
	}

	/**
	 * add order
	 * @param name name
	 * @param ascend direction
	 * @return this
	 */
	public GenericQuery orderBy(String name, boolean ascend) {
		return orderBy(name, ascend ? Order.ASC : Order.DESC);
	}

	/**
	 * add ascend order
	 * @param name name
	 * @return this
	 */
	public GenericQuery orderByAsc(String name) {
		return orderBy(name, true);
	}

	/**
	 * add descend order
	 * @param name name
	 * @return this
	 */
	public GenericQuery orderByDesc(String name) {
		return orderBy(name, false);
	}

	//---------------------------------------------------------------
	// groups
	//
	/**
	 * @return true if has groups
	 */
	public boolean hasGroups() {
		return groups != null && !groups.isEmpty();
	}

	/**
	 * @return groups
	 */
	public List<String> getGroups() {
		return groups;
	}

	/**
	 * add group
	 * @param column column
	 * @return this
	 */
	public GenericQuery groupBy(String ... column) {
		if (groups == null) {
			groups = new ArrayList<String>();
		}
		for (String s : column) {
			groups.add(s);
		}
		return this;
	}

	//---------------------------------------------------------------
	// start & limit
	//
	/**
	 * @return the start
	 */
	public long getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(long start) {
		Asserts.isTrue(start >= 0, "The start must >= 0");
		this.start = start;
	}

	/**
	 * @return the limit
	 */
	public long getLimit() {
		return limit;
	}

	/**
	 * @param limit the limit to set
	 */
	public void setLimit(long limit) {
		Asserts.isTrue(limit >= 0, "The limit must >= 0");
		this.limit = limit;
	}

	/**
	 * @param start the start to set
	 */
	public GenericQuery start(long start) {
		setStart(start);
		return this;
	}

	/**
	 * @param limit the limit to set
	 */
	public GenericQuery limit(long limit) {
		setLimit(limit);
		return this;
	}

	/**
	 * is this query needs paginate
	 * @return true if start or limit > 0
	 */
	public boolean needsPaginate() {
		return start > 0 || limit > 0;
	}

	//---------------------------------------------------------------
	// conditions
	//
	/**
	 * @return filters
	 */
	public ComboFilter getFilters() {
		return filters;
	}

	/**
	 * @return true if has conditions
	 */
	public boolean hasFilters() {
		return filters != null && !filters.isEmpty();
	}
	
	/**
	 * @param name property/field/column name
	 * @return true if the property has some filter
	 */
	public boolean hasFilter(String name) {
		if (hasFilters()) {
			return filters.hasFilter(name);
		}
		return false;
	}
	
	private GenericQuery addSimpleExpression(String field, Operator operator) {
		getCurrent().add(new SimpleFilter(field, operator));
		return this;
	}

	private GenericQuery addCompareValueExpression(String field, Operator operator, Object compareValue) {
		getCurrent().add(new ValueFilter(field, operator, compareValue));
		return this;
	}

	private GenericQuery addCompareFieldExpression(String field, Operator operator, String compareField) {
		getCurrent().add(new ReferFilter(field, operator, compareField));
		return this;
	}

	private GenericQuery addCompareCollectionExpression(String field, Operator operator, Object[] values) {
		getCurrent().add(new ValueFilter(field, operator, values));
		return this;
	}

	private GenericQuery addCompareCollectionExpression(String field, Operator operator, Collection<?> values) {
		getCurrent().add(new ValueFilter(field, operator, values));
		return this;
	}

	private GenericQuery addCompareRanageExpression(String field, Operator operator, Object minValue, Object maxValue) {
		getCurrent().add(new ValueFilter(field, operator, new Object[] { minValue, maxValue }));
		return this;
	}

	private ComboFilter getCurrent() {
		if (Collections.isNotEmpty(stack)) {
			return stack.get(stack.size() - 1);
		}
		
		if (filters != null) {
			return filters;
		}
		
		filters = new ComboFilter(Logical.AND);
		return filters;
	}
	
	private void setCurrent(ComboFilter cf) {
		if (stack == null) {
			stack = new ArrayList<ComboFilter>();
		}
		stack.add(cf);
	}
	
	/**
	 * starts with AND
	 * @return this
	 */
	public GenericQuery and() {
		ComboFilter cf = new ComboFilter(Logical.AND);
		getCurrent().add(cf);
		setCurrent(cf);
		return this;
	}
	
	/**
	 * starts with OR
	 * @return this
	 */
	public GenericQuery or() {
		ComboFilter cf = new ComboFilter(Logical.OR);
		getCurrent().add(cf);
		setCurrent(cf);
		return this;
	}
	
	/**
	 * end with AND/OR
	 * @return this
	 */
	public GenericQuery end() {
		ComboFilter cf = getCurrent();

		// remove current from stack
		if (Collections.isNotEmpty(stack)) {
			stack.remove(stack.size() - 1);
		}
		
		if (cf.isEmpty()) {
			// remove empty ComboFilter for and().end()
			ComboFilter pf = getCurrent();
			pf.getFilters().remove(cf);
		}

		return this;
	}
	
	/**
	 * add "field IS NULL" expression
	 * @param field field 
	 * @return this
	 */
	public GenericQuery isNull(String field) {
		return addSimpleExpression(field, Operator.IS_NULL);
	}

	/**
	 * add "field IS NOT NULL" expression 
	 * @param field field 
	 * @return this
	 */
	public GenericQuery isNotNull(String field) {
		return addSimpleExpression(field, Operator.IS_NOT_NULL);
	}

	/**
	 * add "field = value" expression
	 * @param field field 
	 * @param value value
	 * @return this
	 */
	public GenericQuery equalTo(String field, Object value) {
		return addCompareValueExpression(field, Operator.EQUAL, value);
	}

	/**
	 * add "field &lt;&gt; value" expression
	 * @param field field 
	 * @param value value
	 * @return this
	 */
	public GenericQuery notEqualTo(String field, Object value) {
		return addCompareValueExpression(field, Operator.NOT_EQUAL, value);
	}

	/**
	 * add "field &gt; value" expression
	 * @param field field 
	 * @param value value
	 * @return this
	 */
	public GenericQuery greaterThan(String field, Object value) {
		return addCompareValueExpression(field, Operator.GREATER_THAN, value);
	}

	/**
	 * add "field %gt;= value" expression
	 * @param field field 
	 * @param value value
	 * @return this
	 */
	public GenericQuery greaterThanOrEqualTo(String field, Object value) {
		return addCompareValueExpression(field, Operator.GREATER_EQUAL, value);
	}

	/**
	 * add "field &lt; value" expression
	 * @param field field 
	 * @param value value
	 * @return this
	 */
	public GenericQuery lessThan(String field, Object value) {
		return addCompareValueExpression(field, Operator.LESS_THAN, value);
	}

	/**
	 * add "field &lt;= value" expression
	 * @param field field 
	 * @param value value
	 * @return this
	 */
	public GenericQuery lessThanOrEqualTo(String field, Object value) {
		return addCompareValueExpression(field, Operator.LESS_EQUAL, value);
	}

	/**
	 * add "field LIKE value" expression
	 * @param field field 
	 * @param value value
	 * @return this
	 */
	public GenericQuery like(String field, String value) {
		return addCompareValueExpression(field, Operator.LIKE, value);
	}

	/**
	 * add "field NOT LIKE value" expression
	 * @param field field 
	 * @param value value
	 * @return this
	 */
	public GenericQuery notLike(String field, String value) {
		return addCompareValueExpression(field, Operator.NOT_LIKE, value);
	}

	/**
	 * add "field LIKE %value%" expression
	 * @param field field 
	 * @param value value
	 * @return this
	 */
	public GenericQuery match(String field, String value) {
		return addCompareValueExpression(field, Operator.MATCH, value);
	}

	/**
	 * add "field NOT LIKE %value%" expression
	 * @param field field 
	 * @param value value
	 * @return this
	 */
	public GenericQuery notMatch(String field, String value) {
		return addCompareValueExpression(field, Operator.NOT_MATCH, value);
	}

	/**
	 * add "field LIKE value%" expression
	 * @param field field 
	 * @param value value
	 * @return this
	 */
	public GenericQuery leftMatch(String field, String value) {
		return addCompareValueExpression(field, Operator.LEFT_MATCH, value);
	}

	/**
	 * add "field NOT LIKE value%" expression
	 * @param field field 
	 * @param value value
	 * @return this
	 */
	public GenericQuery notLeftMatch(String field, String value) {
		return addCompareValueExpression(field, Operator.NOT_LEFT_MATCH, value);
	}

	/**
	 * add "field LIKE %value" expression
	 * @param field field 
	 * @param value value
	 * @return this
	 */
	public GenericQuery rightMatch(String field, String value) {
		return addCompareValueExpression(field, Operator.RIGHT_MATCH, value);
	}

	/**
	 * add "field NOT LIKE %value" expression
	 * @param field field 
	 * @param value value
	 * @return this
	 */
	public GenericQuery notRightMatch(String field, String value) {
		return addCompareValueExpression(field, Operator.NOT_RIGHT_MATCH, value);
	}

	/**
	 * add "field = compareField" expression
	 * @param field field 
	 * @param compareField field to compare
	 * @return this
	 */
	public GenericQuery equalToField(String field, String compareField) {
		return addCompareFieldExpression(field, Operator.EQUAL, compareField);
	}

	/**
	 * add "field &lt;&gt; compareField" expression
	 * @param field field 
	 * @param compareField field to compare
	 * @return this
	 */
	public GenericQuery notEqualToField(String field, String compareField) {
		return addCompareFieldExpression(field, Operator.NOT_EQUAL, compareField);
	}

	/**
	 * add "field %gt; compareField" expression
	 * @param field field 
	 * @param compareField field to compare
	 * @return this
	 */
	public GenericQuery greaterThanField(String field, String compareField) {
		return addCompareFieldExpression(field, Operator.GREATER_THAN, compareField);
	}

	/**
	 * add "field &gt;= compareField" expression
	 * @param field field 
	 * @param compareField field to compare
	 * @return this
	 */
	public GenericQuery greaterThanOrEqualToField(String field, String compareField) {
		return addCompareFieldExpression(field, Operator.GREATER_EQUAL, compareField);
	}

	/**
	 * add "field &lt; compareField" expression
	 * @param field field 
	 * @param compareField field to compare
	 * @return this
	 */
	public GenericQuery lessThanField(String field, String compareField) {
		return addCompareFieldExpression(field, Operator.LESS_THAN, compareField);
	}

	/**
	 * add "field %lt;= compareField" expression
	 * @param field field 
	 * @param compareField field to compare
	 * @return this
	 */
	public GenericQuery lessThanOrEqualToField(String field, String compareField) {
		return addCompareFieldExpression(field, Operator.LESS_EQUAL, compareField);
	}

	/**
	 * add "field IN (value1, value2 ...)" expression
	 * @param field field 
	 * @param values values
	 * @return this
	 */
	public GenericQuery in(String field, Object... values) {
		return addCompareCollectionExpression(field, Operator.IN, values);
	}

	/**
	 * add "field IN (value1, value2 ...)" expression
	 * @param field field 
	 * @param values values
	 * @return this
	 */
	public GenericQuery in(String field, Collection<?> values) {
		return addCompareCollectionExpression(field, Operator.IN, values);
	}

	/**
	 * add "field NOT IN (value1, value2 ...)" expression
	 * @param field field 
	 * @param values values
	 * @return this
	 */
	public GenericQuery notIn(String field, Object[] values) {
		return addCompareCollectionExpression(field, Operator.NOT_IN, values);
	}

	/**
	 * add "field NOT IN (value1, value2 ...)" expression
	 * @param field field 
	 * @param values values
	 * @return this
	 */
	public GenericQuery notIn(String field, Collection values) {
		return addCompareCollectionExpression(field, Operator.NOT_IN, values);
	}

	/**
	 * add "field BETWEEN (value1, value2)" expression
	 * @param field field 
	 * @param value1 value from
	 * @param value2 value to
	 * @return this
	 */
	public GenericQuery between(String field, Object value1, Object value2) {
		return addCompareRanageExpression(field, Operator.BETWEEN, value1, value2);
	}

	/**
	 * add "field NOT BETWEEN (value1, value2)" expression
	 * @param field field 
	 * @param value1 value from
	 * @param value2 value to
	 * @return this
	 */
	public GenericQuery notBetween(String field, Object value1, Object value2) {
		return addCompareRanageExpression(field, Operator.NOT_BETWEEN, value1, value2);
	}

	/**
	 * equal to primary keys, Entity must be set
	 * @param keys
	 * @return this
	 */
	public GenericQuery equalToPrimaryKeys(Object ... keys) {
		Entity<?> entity = getEntity();
		
		if (keys == null || keys.length == 0) {
			throw new IllegalArgumentException("Illegal primary keys for Entity [" + entity.getType() + "]: " + Objects.toString(keys));
		}
		
		if (keys.length == 1 && entity.getType().isInstance(keys[0])) {
			for (EntityField ef : entity.getPrimaryKeys()) {
				Object k = ef.getValue(keys[0]);
				if (k == null) {
					throw new IllegalArgumentException("Null primary keys for Entity [" + entity.getType() + "]: " + Objects.toString(keys));
				}
				equalTo(ef.getName(), k);
			}
		}
		else {
			if (keys.length != entity.getPrimaryKeys().size()) {
				throw new IllegalArgumentException("Illegal primary keys for Entity [" + entity.getType() + "]: " + Objects.toString(keys));
			}
			
			int i = 0;
			for (EntityField ef : entity.getFields()) {
				if (ef.isPrimaryKey()) {
					equalTo(ef.getName(), keys[i++]);
				}
			}
		}
		
		return this;
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	public GenericQuery<T> clone() {
		return new GenericQuery<T>(this);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hashCodes(target, distinct, start, limit, columns, joins, filters, orders, groups);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		GenericQuery rhs = (GenericQuery) obj;
		return Objects.equalsBuilder()
				.append(target, rhs.target)
				.append(distinct, rhs.distinct)
				.append(start, rhs.start)
				.append(limit, rhs.limit)
				.append(columns, rhs.columns)
				.append(joins, rhs.joins)
				.append(filters, rhs.filters)
				.append(orders, rhs.orders)
				.append(groups, rhs.groups)
				.isEquals();
	}

	/**
	 * @return a string representation of the object.
	 */
	@Override
	public String toString() {
		return Objects.toStringBuilder()
				.append("target", target)
				.append("distinct", distinct)
				.append("start", start)
				.append("limit", limit)
				.append("columns", columns)
				.append("joins", joins)
				.append("filters", filters)
				.append("orders", orders)
				.append("groups", groups)
				.toString();
	}
}