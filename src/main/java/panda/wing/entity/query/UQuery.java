package panda.wing.entity.query;

import java.util.Date;

import panda.dao.query.ComparableCondition;
import panda.dao.query.EntityQuery;
import panda.dao.query.GenericQuery;
import panda.wing.entity.UBean;

@SuppressWarnings("unchecked")
public class UQuery<T extends UBean, Q extends UQuery> extends EntityQuery<T, Q> {
	/**
	 * Constructor
	 * @param type table type
	 */
	public UQuery(Class<T> type) {
		super(type);
	}

	/**
	 * @param query the query to set
	 */
	public UQuery(GenericQuery<T> query) {
		super(query);
	}

	//----------------------------------------------------------------------
	// field conditions
	//----------------------------------------------------------------------
	/**
	 * @return condition of uusid
	 */
	public ComparableCondition<Q, Number> uusid() {
		return new ComparableCondition(this, UBean.UUSID);
	}

	/**
	 * @return condition of uusnm
	 */
	public ComparableCondition<Q, String> uusnm() {
		return new ComparableCondition(this, UBean.UUSNM);
	}

	/**
	 * @return condition of utime
	 */
	public ComparableCondition<Q, Date> utime() {
		return new ComparableCondition(this, UBean.UTIME);
	}
}
