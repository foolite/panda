package panda.dao.sql.executor;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;

import panda.dao.sql.SqlExecutor;
import panda.dao.sql.TestA;
import panda.log.Log;
import panda.log.Logs;
import panda.mock.sql.MockConnection;

/**
 * SqlExecutorTestCase
 */
public abstract class SqlExecutorTestCase {
	/**
	 * log
	 */
	protected static Log log = Logs.getLog(SqlExecutorTestCase.class);

	protected SqlExecutor executor;
	
	protected abstract Connection getConnection() throws Exception;

	protected SqlExecutor createExecutor(Connection c) throws Exception {
		return new SimpleSqlManager().getExecutor(c);
	}
	
	@Before
	public void setUp() throws Exception {
		Connection c = getConnection();
		if (c instanceof MockConnection) {
			log.warn(this.getClass().getName() + " - skip test!");
			Assume.assumeTrue(false);
		}
		executor = createExecutor(c);
	}

	@After
	public void tearDown() throws Exception {
		executor.getConnection().close();
	}

	protected Object getExpectedInteger(int num) {
		return new Long(num);
	}

	protected Object getExpectedDecimal(String num) {
		return new BigDecimal(num);
	}
	
	protected Object getExpectedTimestamp(String time) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			//sdf.setTimeZone(TimeZones.GMT);
			long t = sdf.parse(time).getTime();
			return new Timestamp(t);
		}
		catch (Exception e) {
			log.error("exception", e);
			throw new RuntimeException(e);
		}
	}
	
	protected Calendar convertToCalendar(String date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			//sdf.setTimeZone(TimeZones.GMT);
			Date d = sdf.parse(date);
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			return c;
		}
		catch (Exception e) {
			log.error("exception", e);
			throw new RuntimeException(e);
		}
	}

	protected void logTestMethod() {
		StackTraceElement stack[] = (new Throwable()).getStackTrace();
		StackTraceElement ste = stack[1];
		String mn = this.getClass().getSimpleName() + "." + ste.getMethodName() + "()";
		
		log.debug("");
		log.debug(mn);
	}

	protected void logTestMethodName() {
		StackTraceElement stack[] = (new Throwable()).getStackTrace();
		StackTraceElement ste = stack[2];
		String mn = this.getClass().getSimpleName() + "." + ste.getMethodName() + "()";
		
		log.debug("");
		log.debug(mn);
	}

	protected void compareResult(Map expected, Map actual) {
		if (expected == null) {
			Assert.assertNull(actual);
			return;
		}

		Assert.assertNotNull(actual);
		prepareActualResultMap(actual);
		
		Set eks = expected.keySet();
		Set aks = actual.keySet();
		
		log.debug("expected map keys: " + eks);
		log.debug("actual   map keys: " + aks);
		Assert.assertEquals(eks, aks);

		StringBuilder sb;
		
		sb = new StringBuilder();
		sb.append("expected map value Class: [");
		for (Iterator it = eks.iterator(); it.hasNext(); ) {
			Object k = it.next();
			Object v = expected.get(k);
			sb.append(v == null ? "<NULL>" : v.getClass().getName());
			if (it.hasNext()) {
				sb.append(", ");
			}
		}
		sb.append("]");
		log.debug(sb.toString());

		sb = new StringBuilder();
		sb.append("actual   map value Class: [");
		for (Iterator it = eks.iterator(); it.hasNext(); ) {
			Object k = it.next();
			Object v = actual.get(k);
			sb.append(v == null ? "<NULL>" : v.getClass().getName());
			if (it.hasNext()) {
				sb.append(", ");
			}
		}
		sb.append("]");
		log.debug(sb.toString());

		sb = new StringBuilder();
		sb.append("expected map values: [");
		for (Iterator it = eks.iterator(); it.hasNext(); ) {
			Object k = it.next();
			Object v = expected.get(k);
			sb.append(v);
			if (it.hasNext()) {
				sb.append(", ");
			}
		}
		sb.append("]");
		log.debug(sb.toString());

		sb = new StringBuilder();
		sb.append("actual   map values: [");
		for (Iterator it = eks.iterator(); it.hasNext(); ) {
			Object k = it.next();
			Object v = actual.get(k);
			sb.append(v);
			if (it.hasNext()) {
				sb.append(", ");
			}
		}
		sb.append("]");
		log.debug(sb.toString());

		Assert.assertEquals(expected, actual);
	}

	protected void testQueryForInt(String sql, Object param, int expectedResult) {
		logTestMethodName();

		int actualResult = 0;
		try {
			actualResult = executor.fetch(sql, param, int.class);
		}
		catch (Exception e) {
			log.error("exception", e);
			Assert.fail(e.getMessage());
		}
		
		Assert.assertEquals(expectedResult, actualResult);
	}
	
	protected void testQueryForObject(String sql, Object param, Object expectedResult) {
		logTestMethodName();

		Object actualResult = null;
		try {
			actualResult = executor.fetch(sql, param, expectedResult.getClass());
		}
		catch (Exception e) {
			log.error("exception", e);
			Assert.fail(e.getMessage());
		}
		
		Assert.assertEquals(expectedResult, actualResult);
	}
	
	protected void testQueryForObject(String sql, Object param, Map expectedResult) {
		logTestMethodName();

		Map actualResult = null;
		try {
			actualResult = executor.fetch(sql, param, Map.class);
		}
		catch (Exception e) {
			log.error("exception", e);
			Assert.fail(e.getMessage());
		}
		
		compareResult(expectedResult, actualResult);
	}

	protected void compareResult(TestA expected, TestA actual) {
		if (expected == null) {
			Assert.assertNull(actual);
			return;
		}

		Assert.assertNotNull(actual);
		
		log.debug("expected: " + expected);
		log.debug("actual  : " + actual);
		Assert.assertEquals(expected, actual);
	}
	
	protected void testQueryForObject(String sql, Object param, TestA expectedResult) {
		logTestMethodName();

		TestA actualResult = null;
		try {
			actualResult = executor.fetch(sql, param, TestA.class);
		}
		catch (Exception e) {
			log.error("exception", e);
			Assert.fail(e.getMessage());
		}
		
		compareResult(expectedResult, actualResult);
	}

	protected void prepareActualResultMap(Map actual) {
	}
	
	protected void compareListResult(List expected, List actual) {
		if (expected == null) {
			Assert.assertNull(actual);
			return;
		}

		Assert.assertNotNull(actual);
		
		for (Object o : actual) {
			if (o instanceof Map) {
				prepareActualResultMap((Map)o);
			}
		}
		Assert.assertEquals(expected, actual);
	}
	
	@SuppressWarnings("unchecked")
	protected void testQueryForList(String sql, Object param, Class type, List expectedResult) {
		logTestMethodName();

		List actualResult = null;
		try {
			actualResult = executor.selectList(sql, param, type);
		}
		catch (Exception e) {
			log.error("exception", e);
			Assert.fail(e.getMessage());
		}

		compareListResult(expectedResult, actualResult);
	}

	@SuppressWarnings("unchecked")
	protected void testQueryForList(String sql, Object param, int start, int limit, Class type, List expectedResult) {
		logTestMethodName();

		List actualResult = null;
		try {
			actualResult = executor.selectList(sql, param, type, start, limit);
		}
		catch (Exception e) {
			log.error("exception", e);
			Assert.fail(e.getMessage());
		}

		compareListResult(expectedResult, actualResult);
	}

	protected void compareMapResult(Map expected, Map actual) {
		if (expected == null) {
			Assert.assertNull(actual);
			return;
		}

		Assert.assertNotNull(actual);
		
		for (Object o : actual.values()) {
			if (o instanceof Map) {
				prepareActualResultMap((Map)o);
			}
		}
		Assert.assertEquals(expected, actual);
	}
	
	@SuppressWarnings("unchecked")
	protected void testQueryForMap(String sql, Object param, Class type, Map expectedResult) {
		logTestMethodName();

		Map actualResult = null;
		try {
			actualResult = executor.selectMap(sql, param, type, "id", null);
		}
		catch (Exception e) {
			log.error("exception", e);
			Assert.fail(e.getMessage());
		}

		compareMapResult(expectedResult, actualResult);
	}

	protected void testExecuteUpdate(String updateSql, Object param, String selectSql, Map expectedResult) {
		logTestMethodName();

		try {
			int cnt = executor.update(updateSql, param);
			Assert.assertEquals(cnt, 1);
		}
		catch (Exception e) {
			log.error("exception", e);
			Assert.fail(e.getMessage());
		}

		Map actualResult = null;
		try {
			actualResult = executor.fetch(selectSql, param, Map.class);
		}
		catch (Exception e) {
			log.error("exception", e);
			Assert.fail(e.getMessage());
		}
		
		if (expectedResult == null) {
			Assert.assertNull(actualResult);
		}
		else {
			Assert.assertNotNull(actualResult);
			compareResult(expectedResult, actualResult);
		}
	}

	protected void testExecuteUpdate(String updateSql, Object param, String selectSql, TestA expectedResult) {
		logTestMethodName();

		try {
			int cnt = executor.update(updateSql, param);
			Assert.assertEquals(cnt, 1);
		}
		catch (Exception e) {
			log.error("exception", e);
			Assert.fail(e.getMessage());
		}

		TestA actualResult = null;
		try {
			actualResult = executor.fetch(selectSql, param, TestA.class);
		}
		catch (Exception e) {
			log.error("exception", e);
			Assert.fail(e.getMessage());
		}
		
		if (expectedResult == null) {
			Assert.assertNull(actualResult);
		}
		else {
			Assert.assertNotNull(actualResult);
			compareResult(expectedResult, actualResult);
		}
	}

	protected void testExecuteInsert(String insertSql, Map param, String selectSql, Map expect) {
		logTestMethodName();

		Map key = null;
		try {
			key = executor.insert(insertSql, param, Map.class, "id");
		}
		catch (Exception e) {
			log.error("exception", e);
			Assert.fail(e.getMessage());
		}
		Object id = param.get("id");
		if (id == null) {
			// id is generated by DB
			Assert.assertNotNull(key);
			Assert.assertEquals(expect.get("id").toString(), key.get("id").toString());
		}

		Map actualResult = null;
		try {
			actualResult = executor.fetch(selectSql, expect, Map.class);
		}
		catch (Exception e) {
			log.error("exception", e);
			Assert.fail(e.getMessage());
		}
		
		Assert.assertNotNull(actualResult);
		compareResult(expect, actualResult);
	}

	protected void testExecuteInsert(String insertSql, TestA param, String selectSql, TestA expect) {
		logTestMethodName();

		TestA key = new TestA();
		try {
			key = executor.insert(insertSql, param, key, "id");
		}
		catch (Exception e) {
			log.error("exception", e);
			Assert.fail(e.getMessage());
		}
		if (param.getId() < 1) {
			// id is generated by DB
			Assert.assertNotNull(key);
			Assert.assertEquals(key.getId(), expect.getId());
		}

		TestA actualResult = null;
		try {
			actualResult = executor.fetch(selectSql, expect, TestA.class);
		}
		catch (Exception e) {
			log.error("exception", e);
			Assert.fail(e.getMessage());
		}
		
		Assert.assertNotNull(actualResult);
		compareResult(expect, actualResult);
	}
}
