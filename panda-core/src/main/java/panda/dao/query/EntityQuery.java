package panda.dao.query;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import panda.dao.entity.Entity;
import panda.dao.query.Filter.ComboFilter;
import panda.lang.Objects;
import panda.lang.Order;


@SuppressWarnings({ "unchecked", "rawtypes" })
public class EntityQuery<T, Q extends EntityQuery> implements Query<T> {
	protected DataQuery<T> query;

	/**
	 * constructor
	 * @param table table
	 */
	public EntityQuery(Class<T> table) {
		this.query = new DataQuery<T>(table);
	}

	/**
	 * constructor
	 * @param entity query entity
	 */
	public EntityQuery(Entity<T> entity) {
		this.query = new DataQuery<T>(entity);
	}

	/**
	 * @param query the query to set
	 */
	public EntityQuery(DataQuery<T> query) {
		this.query = query;
	}

	/**
	 * @return the query
	 */
	protected DataQuery<T> getQuery() {
		return query;
	}
	

	//-----------------------------------------------------------------------------------------
	// Query methods implement
	//
	@Override
	public Object getTarget() {
		return query.getTarget();
	}

	@Override
	public Entity<T> getEntity() {
		return query.getEntity();
	}

	@Override
	public Class<T> getType() {
		return query.getType();
	}

	@Override
	public String getTable() {
		return query.getTable();
	}

	@Override
	public boolean isDistinct() {
		return query.isDistinct();
	}
	
	@Override
	public Map<String, String> getColumns() {
		return query.getColumns();
	}

	@Override
	public boolean hasColumns() {
		return query.hasColumns();
	}

	/**
	 * @param name column name
	 * @return column value
	 */
	public String getColumn(String name) {
		return query.getColumn(name);
	}

	/**
	 * @param name column name
	 * @param value column value
	 * @return this
	 */
	public Q column(String name, String value) {
		query.column(name, value);
		return (Q)this;
	}

	/**
	 * include primary keys
	 * @return this
	 */
	public Q includePrimayKeys() {
		query.includePrimayKeys();
		return (Q)this;
	}

	/**
	 * include not primary keys
	 * @return this
	 */
	public Q includeNotPrimaryKeys() {
		query.includeNotPrimaryKeys();
		return (Q)this;
	}
	
	/**
	 * @return this
	 */
	public Q includeAll() {
		query.includeAll();
		return (Q)this;
	}
	
	/**
	 * @param names include name
	 * @return this
	 */
	public Q include(String... names) {
		query.include(names);
		return (Q)this;
	}
	
	/**
	 * @param names include name
	 * @return this
	 */
	public Q include(Collection<String> names) {
		query.include(names);
		return (Q)this;
	}
	
	/**
	 * exclude primary keys
	 * @return this
	 */
	public Q excludePrimaryKeys() {
		query.excludePrimaryKeys();
		return (Q)this;
	}

	/**
	 * exclude not primary keys
	 * @return this
	 */
	public Q excludeNotPrimaryKeys() {
		query.excludeNotPrimaryKeys();
		return (Q)this;
	}
	
	/**
	 * @return this
	 */
	public Q excludeAll() {
		query.excludeAll();
		return (Q)this;
	}

	/**
	 * @param names the field name to exclude
	 * @return this
	 */
	public Q exclude(String... names) {
		query.exclude(names);
		return (Q)this;
	}

	/**
	 * @param names the field name to exclude
	 * @return this
	 */
	public Q exclude(Collection<String> names) {
		query.exclude(names);
		return (Q)this;
	}

	@Override
	public boolean shouldInclude(String name) {
		return query.shouldInclude(name);
	}

	@Override
	public boolean shouldExclude(String name) {
		return query.shouldExclude(name);
	}

	@Override
	public boolean hasJoins() {
		return query.hasJoins();
	}

	@Override
	public Map<String, Join> getJoins() {
		return query.getJoins();
	}

	@Override
	public Join getJoin(String join) {
		return query.getJoin(join);
	}

	@Override
	public boolean hasOrder(String name) {
		return query.hasOrder(name);
	}

	@Override
	public boolean hasOrders() {
		return query.hasOrders();
	}

	@Override
	public Map<String, Order> getOrders() {
		return query.getOrders();
	}

	@Override
	public long getStart() {
		return query.getStart();
	}

	@Override
	public long getLimit() {
		return query.getLimit();
	}

	@Override
	public boolean needsPaginate() {
		return query.needsPaginate();
	}

	@Override
	public ComboFilter getFilters() {
		return query.getFilters();
	}

	@Override
	public boolean hasFilters() {
		return query.hasFilters();
	}
	
	@Override
	public boolean hasFilter(String name) {
		return query.hasFilter(name);
	}
	
	//----------------------------------------------------------------------
	public Q clear() {
		query.clear();
		return (Q)this;
	}
	
	//----------------------------------------------------------------------
	public Q distinct() {
		return distinct(true);
	}
	
	public Q distinct(boolean distinct) {
		query.setDistinct(distinct);
		return (Q)this;
	}

	//----------------------------------------------------------------------
	// conjunction
	//----------------------------------------------------------------------
	/**
	 * start with AND
	 * @return this
	 */
	public Q and() {
		query.and();
		return (Q)this;
	}

	/**
	 * start with OR
	 * @return this
	 */
	public Q or() {
		query.or();
		return (Q)this;
	}

	/**
	 * end with AND/OR
	 * @return this
	 */
	public Q end() {
		query.end();
		return (Q)this;
	}

	//----------------------------------------------------------------------
	// limit
	//----------------------------------------------------------------------
	/**
	 * set start
	 * 
	 * @param start the start position
	 * @return this
	 */
	public Q start(int start) {
		query.start(start);
		return (Q)this;
	}

	/**
	 * set start
	 * @param start the start position
	 * @return this
	 */
	public Q start(long start) {
		query.start(start);
		return (Q)this;
	}

	/**
	 * set limit
	 * @param limit the fetch limitation
	 * @return this
	 */
	public Q limit(int limit) {
		query.limit(limit);
		return (Q)this;
	}

	/**
	 * set limit
	 * @param limit the fetch limitation
	 * @return this
	 */
	public Q limit(long limit) {
		query.limit(limit);
		return (Q)this;
	}

	
	//----------------------------------------------------------------------
	// order
	//----------------------------------------------------------------------
	/**
	 * add ascend order
	 * @param column column
	 * @return this
	 */
	public Q orderBy(String column) {
		query.orderBy(column);
		return (Q)this;
	}

	/**
	 * add order
	 * @param name name
	 * @param order order direction
	 * @return this
	 */
	public Q orderBy(String name, Order order) {
		query.orderBy(name, order);
		return (Q)this;
	}

	/**
	 * add order
	 * @param name name
	 * @param order order direction
	 * @return this
	 */
	public Q orderBy(String name, String order) {
		query.orderBy(name, order);
		return (Q)this;
	}

	/**
	 * add order
	 * @param name name
	 * @param ascend direction
	 * @return this
	 */
	public Q orderBy(String name, boolean ascend) {
		query.orderBy(name, ascend);
		return (Q)this;
	}

	/**
	 * add ascend order
	 * @param name		name
	 * @return this
	 */
	public Q orderByAsc(String name) {
		query.orderByAsc(name);
		return (Q)this;
	}

	/**
	 * add descend order
	 * @param name		name
	 * @return this
	 */
	public Q orderByDesc(String name) {
		query.orderByDesc(name);
		return (Q)this;
	}
	

	//---------------------------------------------------------------
	// groups
	//---------------------------------------------------------------
	/**
	 * @return true if has groups
	 */
	public boolean hasGroups() {
		return query.hasGroups();
	}

	/**
	 * @return groups
	 */
	public List<String> getGroups() {
		return query.getGroups();
	}

	/**
	 * add group
	 * @param column column
	 * @return this
	 */
	public Q groupBy(String ... column) {
		query.groupBy(column);
		return (Q)this;
	}

	//----------------------------------------------------------------------
	// join
	//----------------------------------------------------------------------
	/**
	 * add left join
	 * @param jquery join query
	 * @param alias join alias
	 * @param conditions join conditions
	 * @return this
	 */
	public Q leftJoin(Query<?> jquery, String alias, String ... conditions) {
		query.leftJoin(jquery, alias, conditions);
		return (Q)this;
	}

	/**
	 * add right join
	 * @param jquery join query
	 * @param alias join alias
	 * @param conditions join conditions
	 * @return this
	 */
	public Q rightJoin(Query<?> jquery, String alias, String ... conditions) {
		query.rightJoin(jquery, alias, conditions);
		return (Q)this;
	}

	/**
	 * add inner join
	 * @param jquery join query
	 * @param alias join alias
	 * @param conditions join conditions
	 * @return this
	 */
	public Q innerJoin(Query<?> jquery, String alias, String ... conditions) {
		query.innerJoin(jquery, alias, conditions);
		return (Q)this;
	}

	/**
	 * add join
	 * @param jquery join query
	 * @param alias join alias
	 * @param conditions join conditions
	 * @return this
	 */
	public Q join(Query<?> jquery, String alias, String ... conditions) {
		query.join(query, alias, conditions);
		return (Q)this;
	}

	/**
	 * add join
	 * @param type join type
	 * @param jquery join query
	 * @param alias join alias
	 * @param conditions join conditions
	 * @return this
	 */
	public Q join(String type, Query<?> jquery, String alias, String ... conditions) {
		query.join(type, jquery, alias, conditions);
		return (Q)this;
	}

	//---------------------------------------------------------------
	// auto joins
	//
	/**
	 * auto add left join from @Join
	 * @param join join name
	 * @return this
	 */
	public Q autoLeftJoin(String join) {
		query.autoLeftJoin(join);
		return (Q)this;
	}

	/**
	 * auto add left join from @Join
	 * @param join join name
	 * @param jq join table query
	 * @return this
	 */
	public Q autoLeftJoin(String join, Query<?> jq) {
		query.autoLeftJoin(join, jq);
		return (Q)this;
	}

	/**
	 * auto add right join from @Join
	 * @param join join name
	 * @return this
	 */
	public Q autoRightJoin(String join) {
		query.autoRightJoin(join);
		return (Q)this;
	}

	/**
	 * auto add right join from @Join
	 * @param join join name
	 * @param jq join table query
	 * @return this
	 */
	public Q autoRightJoin(String join, Query<?> jq) {
		query.autoRightJoin(join, jq);
		return (Q)this;
	}

	/**
	 * auto add inner join from @Join
	 * @param join join name
	 * @return this
	 */
	public Q autoInnerJoin(String join) {
		query.autoInnerJoin(join);
		return (Q)this;
	}

	/**
	 * auto add inner join from @Join
	 * @param join join name
	 * @param jq join table query
	 * @return this
	 */
	public Q autoInnerJoin(String join, Query<?> jq) {
		query.autoInnerJoin(join, jq);
		return (Q)this;
	}

	/**
	 * auto add join from @Join
	 * @param join join name
	 * @return this
	 */
	public Q autoJoin(String join) {
		query.autoInnerJoin(join);
		return (Q)this;
	}

	/**
	 * auto add join from @Join
	 * @param join join name
	 * @param jq join table query
	 * @return this
	 */
	public Q autoJoin(String join, Query<?> jq) {
		query.autoJoin(join, jq);
		return (Q)this;
	}

	/**
	 * auto add join from @Join
	 * @param type join type
	 * @param join join name
	 * @return this
	 */
	public Q autoJoin(String type, String join) {
		query.autoJoin(type, join);
		return (Q)this;
	}

	/**
	 * auto add join from @Join
	 * @param type join type
	 * @param join join name
	 * @param jq join table query
	 * @return this
	 */
	public Q autoJoin(String type, String join, Query<?> jq) {
		query.autoJoin(type, join, jq);
		return (Q)this;
	}

	//-------------------------------------------------------------------------------------------
	/**
	 * equal to primary keys, Entity must be set
	 * 
	 * @param keys primary keys
	 * @return this
	 */
	public Q equalToPrimaryKeys(Object ... keys) {
		query.equalToPrimaryKeys(keys);
		return (Q)this;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return query.hashCode();
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
		
		EntityQuery rhs = (EntityQuery)obj;
		return query.equals(rhs.query);
	}

	/**
	 * @return a string representation of the object.
	 */
	@Override
	public String toString() {
		return Objects.toStringBuilder()
				.append("query", query)
				.toString();
	}
}

