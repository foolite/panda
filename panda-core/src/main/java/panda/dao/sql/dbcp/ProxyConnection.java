package panda.dao.sql.dbcp;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class ProxyConnection implements Connection {

	private int hashCode = 0;

	private Connection realConnection;

	private boolean valid;

	/**
	 * Constructor for SimplePooledConnection that uses the Connection and SimpleDataSource
	 * passed in
	 * 
	 * @param connection - the connection that is to be presented as a pooled connection
	 */
	public ProxyConnection(Connection connection) {
		this.hashCode = connection.hashCode();
		this.realConnection = connection;
		this.valid = true;
	}

	/**
	 * Invalidates the connection
	 */
	public void invalidate() {
		valid = false;
	}

	/**
	 * Method to see if the connection is usable
	 * 
	 * @return True if the connection is usable
	 */
	public boolean isValid() {
		return valid && realConnection != null;
	}

	/**
	 * Getter for the *real* connection that this wraps
	 * 
	 * @return The connection
	 */
	public Connection getRealConnection() {
		return realConnection;
	}

	/**
	 * Gets the hashcode of the real connection (or 0 if it is null)
	 * 
	 * @return The hashcode of the real connection (or 0 if it is null)
	 */
	public int getRealHashCode() {
		if (realConnection == null) {
			return 0;
		}
		return realConnection.hashCode();
	}

	private Connection getValidConnection() {
		if (!valid) {
			throw new RuntimeException("Error accessing SimplePooledConnection. Connection is invalid.");
		}
		return realConnection;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return hashCode;
	}

	/**
	 * Allows comparing this connection to another
	 * 
	 * @param obj - the other connection to test for equality
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof ProxyConnection) {
			return realConnection.hashCode() == ((ProxyConnection)obj).realConnection.hashCode();
		}
		
		if (obj instanceof Connection) {
			return hashCode == obj.hashCode();
		}

		return false;
	}

	//--------------------------------------------------------------------
	// Implemented Connection Methods
	//--------------------------------------------------------------------
	/**
	 * @see java.sql.Connection#createStatement()
	 */
	public Statement createStatement() throws SQLException {
		return getValidConnection().createStatement();
	}

	/**
	 * @see java.sql.Connection#prepareStatement(java.lang.String)
	 */
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return getValidConnection().prepareStatement(sql);
	}

	/**
	 * @see java.sql.Connection#prepareCall(java.lang.String)
	 */
	public CallableStatement prepareCall(String sql) throws SQLException {
		return getValidConnection().prepareCall(sql);
	}

	/**
	 * @see java.sql.Connection#nativeSQL(java.lang.String)
	 */
	public String nativeSQL(String sql) throws SQLException {
		return getValidConnection().nativeSQL(sql);
	}

	/**
	 * @see java.sql.Connection#setAutoCommit(boolean)
	 */
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		getValidConnection().setAutoCommit(autoCommit);
	}

	/**
	 * @see java.sql.Connection#getAutoCommit()
	 */
	public boolean getAutoCommit() throws SQLException {
		return getValidConnection().getAutoCommit();
	}

	/**
	 * @see java.sql.Connection#commit()
	 */
	public void commit() throws SQLException {
		getValidConnection().commit();
	}

	/**
	 * @see java.sql.Connection#rollback()
	 */
	public void rollback() throws SQLException {
		getValidConnection().rollback();
	}

	/**
	 * @see java.sql.Connection#close()
	 */
	public void close() throws SQLException {
		getValidConnection().close();
	}

	/**
	 * @see java.sql.Connection#isClosed()
	 */
	public boolean isClosed() throws SQLException {
		return !valid;
	}

	/**
	 * @see java.sql.Connection#getMetaData()
	 */
	public DatabaseMetaData getMetaData() throws SQLException {
		return getValidConnection().getMetaData();
	}

	/**
	 * @see java.sql.Connection#setReadOnly(boolean)
	 */
	public void setReadOnly(boolean readOnly) throws SQLException {
		getValidConnection().setReadOnly(readOnly);
	}

	/**
	 * @see java.sql.Connection#isReadOnly()
	 */
	public boolean isReadOnly() throws SQLException {
		return getValidConnection().isReadOnly();
	}

	/**
	 * @see java.sql.Connection#setCatalog(java.lang.String)
	 */
	public void setCatalog(String catalog) throws SQLException {
		getValidConnection().setCatalog(catalog);
	}

	/**
	 * @see java.sql.Connection#getCatalog()
	 */
	public String getCatalog() throws SQLException {
		return getValidConnection().getCatalog();
	}

	/**
	 * @see java.sql.Connection#setTransactionIsolation(int)
	 */
	public void setTransactionIsolation(int level) throws SQLException {
		getValidConnection().setTransactionIsolation(level);
	}

	/**
	 * @see java.sql.Connection#getTransactionIsolation()
	 */
	public int getTransactionIsolation() throws SQLException {
		return getValidConnection().getTransactionIsolation();
	}

	/**
	 * @see java.sql.Connection#getWarnings()
	 */
	public SQLWarning getWarnings() throws SQLException {
		return getValidConnection().getWarnings();
	}

	/**
	 * @see java.sql.Connection#clearWarnings()
	 */
	public void clearWarnings() throws SQLException {
		getValidConnection().clearWarnings();
	}

	/**
	 * @see java.sql.Connection#createStatement(int, int)
	 */
	public Statement createStatement(int resultSetType, int resultSetConcurrency)
			throws SQLException {
		return getValidConnection().createStatement(resultSetType, resultSetConcurrency);
	}

	/**
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int, int)
	 */
	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		return getValidConnection().prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	/**
	 * @see java.sql.Connection#prepareCall(java.lang.String, int, int)
	 */
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		return getValidConnection().prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	/**
	 * @see java.sql.Connection#getTypeMap()
	 */
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return getValidConnection().getTypeMap();
	}

	/**
	 * @see java.sql.Connection#setTypeMap(java.util.Map)
	 */
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		getValidConnection().setTypeMap(map);
	}

	//--------------------------------------------------------------------
	// JDK 1.4 JDBC 3.0 Methods below
	//--------------------------------------------------------------------
	/**
	 * @see java.sql.Connection#setHoldability(int)
	 */
	public void setHoldability(int holdability) throws SQLException {
		getValidConnection().setHoldability(holdability);
	}

	/**
	 * @see java.sql.Connection#getHoldability()
	 */
	public int getHoldability() throws SQLException {
		return getValidConnection().getHoldability();
	}

	/**
	 * @see java.sql.Connection#setSavepoint()
	 */
	public Savepoint setSavepoint() throws SQLException {
		return getValidConnection().setSavepoint();
	}

	/**
	 * @see java.sql.Connection#setSavepoint(java.lang.String)
	 */
	public Savepoint setSavepoint(String name) throws SQLException {
		return getValidConnection().setSavepoint(name);
	}

	/**
	 * @see java.sql.Connection#rollback(java.sql.Savepoint)
	 */
	public void rollback(Savepoint savepoint) throws SQLException {
		getValidConnection().rollback(savepoint);
	}

	/**
	 * @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)
	 */
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		getValidConnection().releaseSavepoint(savepoint);
	}

	/**
	 * @see java.sql.Connection#createStatement(int, int, int)
	 */
	public Statement createStatement(int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		return getValidConnection().createStatement(resultSetType, resultSetConcurrency,
			resultSetHoldability);
	}

	/**
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int, int, int)
	 */
	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return getValidConnection().prepareStatement(sql, resultSetType, resultSetConcurrency,
			resultSetHoldability);
	}

	/**
	 * @see java.sql.Connection#prepareCall(java.lang.String, int, int, int)
	 */
	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return getValidConnection().prepareCall(sql, resultSetType, resultSetConcurrency,
			resultSetHoldability);
	}

	/**
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int)
	 */
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
			throws SQLException {
		return getValidConnection().prepareStatement(sql, autoGeneratedKeys);
	}

	/**
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int[])
	 */
	public PreparedStatement prepareStatement(String sql, int columnIndexes[])
			throws SQLException {
		return getValidConnection().prepareStatement(sql, columnIndexes);
	}

	/**
	 * @see java.sql.Connection#prepareStatement(java.lang.String, java.lang.String[])
	 */
	public PreparedStatement prepareStatement(String sql, String columnNames[])
			throws SQLException {
		return getValidConnection().prepareStatement(sql, columnNames);
	}

	//--------------------------------------------------------------------
	// JDK 1.6 Methods below
	//--------------------------------------------------------------------
	/**
	 * @see java.sql.Connection#isValid(int)
	 */
	public boolean isValid(int timeout) throws SQLException {
		return valid && realConnection != null && realConnection.isValid(timeout);
	}

	/**
	 * @see java.sql.Connection#createArrayOf(java.lang.String, java.lang.Object[])
	 */
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		return getValidConnection().createArrayOf(typeName, elements);
	}

	/**
	 * @see java.sql.Connection#createBlob()
	 */
	public Blob createBlob() throws SQLException {
		return getValidConnection().createBlob();
	}

	/**
	 * @see java.sql.Connection#createClob()
	 */
	public Clob createClob() throws SQLException {
		return getValidConnection().createClob();
	}

	/**
	 * @see java.sql.Connection#createNClob()
	 */
	public NClob createNClob() throws SQLException {
		return getValidConnection().createNClob();
	}

	/**
	 * @see java.sql.Connection#createSQLXML()
	 */
	public SQLXML createSQLXML() throws SQLException {
		return getValidConnection().createSQLXML();
	}

	/**
	 * @see java.sql.Connection#createStruct(java.lang.String, java.lang.Object[])
	 */
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		return getValidConnection().createStruct(typeName, attributes);
	}

	/**
	 * @see java.sql.Connection#getClientInfo()
	 */
	public Properties getClientInfo() throws SQLException {
		return getValidConnection().getClientInfo();
	}

	/**
	 * @see java.sql.Connection#getClientInfo(java.lang.String)
	 */
	public String getClientInfo(String name) throws SQLException {
		return getValidConnection().getClientInfo(name);
	}

	/**
	 * @see java.sql.Connection#setClientInfo(java.util.Properties)
	 */
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		getValidConnection().setClientInfo(properties);
	}

	/**
	 * @see java.sql.Connection#setClientInfo(java.lang.String, java.lang.String)
	 */
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		getValidConnection().setClientInfo(name, value);
	}

	/**
	 * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
	 */
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return getValidConnection().isWrapperFor(iface);
	}

	/**
	 * @see java.sql.Wrapper#unwrap(java.lang.Class)
	 */
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return getValidConnection().unwrap(iface);
	}

	//--------------------------------------------------------------------
	// JDBC 4.1 JDK 1.7 Methods below
	//--------------------------------------------------------------------
	public void setSchema(String schema) throws SQLException {
		getValidConnection().setSchema(schema);
	}

	public String getSchema() throws SQLException {
		return getValidConnection().getSchema();
	}

	public void abort(Executor executor) throws SQLException {
		getValidConnection().abort(executor);
	}

	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		getValidConnection().setNetworkTimeout(executor, milliseconds);
	}

	public int getNetworkTimeout() throws SQLException {
		return getValidConnection().getNetworkTimeout();
	}
}
