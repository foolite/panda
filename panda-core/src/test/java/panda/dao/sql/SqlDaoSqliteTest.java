package panda.dao.sql;

import panda.dao.DaoClient;



/**
 */
public class SqlDaoSqliteTest extends SqlDaoTestCase {
	private static SqlDaoClient client = createSqlDaoClient("sqlite");
	
	protected DaoClient getDaoClient() {
		return client;
	}

	protected String concatSql(String a, String b) {
		return "(" + a + " || " + b + ")";
	}

}
