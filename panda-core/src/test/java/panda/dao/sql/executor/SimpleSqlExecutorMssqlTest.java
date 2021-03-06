package panda.dao.sql.executor;

import java.sql.Connection;
import java.util.Date;
import java.util.Map;

import org.junit.Test;

/**
 */
public class SimpleSqlExecutorMssqlTest extends SimpleSqlExecutorTestCase {

	@Override
	protected Connection getConnection() throws Exception {
		return TestSupport.getMssqlConnection();
	}

	@Override
	protected Object getExpectedDate(String date) {
		Date d = convertToDate(date);
		return new java.sql.Timestamp(d.getTime());
	}
	
	@Override
	protected Object getExpectedTime(String time) {
		Date d = convertToTime(time);
		return new java.sql.Timestamp(d.getTime());
	}

	@Override
	protected void prepareActualBean(TestBean actual) {
		// fix for real precision error 
		Double n = trimDouble(actual.getFreal());
		actual.setFreal(n);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected void prepareActualMap(Map actual) {
		super.prepareActualMap(actual);

		// fix for postgre real precision bug 
		Double d = (Double)actual.get("freal");
		if (d != null) {
			Double n = trimDouble(d);
			actual.put("freal", n);
		}
	}

	@Test
	public void testInsertIdAuto() throws Exception {
		super.testInsertIdAuto();
	}
}
