package panda.app.entity.query;

import java.util.Date;

import panda.app.entity.SCUBean;
import panda.dao.entity.Entity;
import panda.dao.query.ComparableCondition;
import panda.dao.query.EntityQuery;
import panda.dao.query.DataQuery;

@SuppressWarnings("unchecked")
public class SCUQuery<T extends SCUBean, Q extends SCUQuery> extends EntityQuery<T, Q> {
	/**
	 * Constructor
	 * @param type table type
	 */
	public SCUQuery(Class<T> type) {
		super(type);
	}

	/**
	 * constructor
	 * @param entity query entity
	 */
	public SCUQuery(Entity<T> entity) {
		super(entity);
	}

	/**
	 * @param query the query to set
	 */
	public SCUQuery(DataQuery<T> query) {
		super(query);
	}

	//----------------------------------------------------------------------
	// field conditions
	//----------------------------------------------------------------------
	/**
	 * @return condition of status
	 */
	public ComparableCondition<Q, Character> status() {
		return new ComparableCondition(this, SCUBean.STATUS);
	}

	/**
	 * @return condition of createdAt
	 */
	public ComparableCondition<Q, Date> createdAt() {
		return new ComparableCondition(this, SCUBean.CREATED_AT);
	}

	/**
	 * @return condition of createdBy
	 */
	public ComparableCondition<Q, Number> createdBy() {
		return new ComparableCondition(this, SCUBean.CREATED_BY);
	}

	/**
	 * @return condition of createdByName
	 */
	public ComparableCondition<Q, String> createdByName() {
		return new ComparableCondition(this, SCUBean.CREATED_BY_NAME);
	}

	/**
	 * @return condition of updatedAt
	 */
	public ComparableCondition<Q, Date> updatedAt() {
		return new ComparableCondition(this, SCUBean.UPDATED_AT);
	}

	/**
	 * @return condition of updatedBy
	 */
	public ComparableCondition<Q, Number> updatedBy() {
		return new ComparableCondition(this, SCUBean.UPDATED_BY);
	}

	/**
	 * @return condition of updatedByName
	 */
	public ComparableCondition<Q, String> updatedByName() {
		return new ComparableCondition(this, SCUBean.UPDATED_BY_NAME);
	}
}

