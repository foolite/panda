package panda.dao.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import panda.dao.Dao;
import panda.dao.DaoClient;
import panda.dao.sql.executor.ExtendSqlManager;
import panda.dao.sql.expert.SqlExpert;
import panda.dao.sql.expert.SqlExpertConfig;
import panda.lang.Exceptions;
import panda.log.Log;
import panda.log.Logs;

/**
 * @author yf.frank.wang@gmail.com
 */
public class SqlDaoClient extends DaoClient {
	private static Log log = Logs.getLog(SqlDaoClient.class);
	
	private static SqlExpertConfig sqlExpertConfig = new SqlExpertConfig();
	
	protected DataSource dataSource;
	protected SqlExpert sqlExpert;
	protected SqlManager sqlManager = new ExtendSqlManager();
	
	/**
	 * Constructor
	 */
	public SqlDaoClient() {
	}
	
	/**
	 * @return the dataSource
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.sqlExpert = getExpert(dataSource);
	}

	/**
	 * @return the sqlExpert
	 */
	public SqlExpert getSqlExpert() {
		return sqlExpert;
	}

	/**
	 * @param sqlExpert the sqlExpert to set
	 */
	public void setSqlExpert(SqlExpert sqlExpert) {
		this.sqlExpert = sqlExpert;
	}

	/**
	 * @return the sqlManager
	 */
	public SqlManager getSqlManager() {
		return sqlManager;
	}

	/**
	 * @param sqlManager the sqlManager to set
	 */
	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	/**
	 * set jndi data source
	 * @param jndi jndi string such as "java:comp/env/jdbc/xxx"
	 */
	public void setJndiDataSource(String jndi) {
		try {
			Context ic = new InitialContext();
			DataSource ds = (DataSource)ic.lookup(jndi);
			if (dataSource == null) {
				throw new RuntimeException("Failed to lookup data source: " + jndi);
			}
			setDataSource(ds);
		}
		catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * get SqlExpert by DataSource
	 * 
	 * @param dataSource data source
	 * @return SqlExpert
	 */
	public static SqlExpert getExpert(DataSource dataSource) {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			DatabaseMetaData meta = conn.getMetaData();
			String pnm = meta.getDatabaseProductName();
			String ver = meta.getDatabaseProductVersion();
			return getExpert(pnm, ver);
		}
		catch (SQLException e) {
			throw Exceptions.wrapThrow(e);
		}
		finally {
			SqlUtils.safeClose(conn);
		}
	}

	/**
	 * get SqlExpert by productName and version
	 * 
	 * @param productName database product name
	 * @param version database version
	 * @return SqlExpert
	 * @see java.sql.Connection#getMetaData()
	 * @see java.sql.DatabaseMetaData#getDatabaseProductName()
	 */
	public static SqlExpert getExpert(String productName, String version) {
		String dbName = (productName + " " + version).toLowerCase();
		log.info("Get SqlExpert for " + dbName);

		SqlExpert se = sqlExpertConfig.matchExpert(dbName);
		if (null == se) {
			throw Exceptions.makeThrow("Can not support database '%s %s'", productName, version);
		}
		se.setProperties(sqlExpertConfig.getProperties());
		
		return se;
	}

	/**
	 * @return a new dao instance
	 */
	public Dao getDao() {
		return new SqlDao(this);
	}
}
