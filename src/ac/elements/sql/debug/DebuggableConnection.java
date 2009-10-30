/**
 * Title:        Overpower the PreparedStatement<p>
 * Description:  http://www.javaworld.com/javaworld/jw-01-2002/jw-0125-overpower.html<p>
 * Copyright:    Copyright (c) Troy Thompson Bob Byron<p>
 * Company:      JavaUnderground<p>
 * @author       Troy Thompson Bob Byron
 * @version 1.1
 */
package ac.elements.sql.debug;

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

import org.apache.log4j.Logger;

public class DebuggableConnection implements Connection {

    /** The Constant log. */
    private final static Logger logger =
            Logger.getLogger(DebuggableConnection.class);

    private final Connection connection; // connection being proxied for.

    /* Default sql formatter */
    private SqlFormatter defaultFormatter = new DefaultSqlFormatter();

    public DebuggableConnection(Connection connection) throws SQLException {
        if (connection == null)
            throw new SQLException(
                    "Connection passed to DebuggableConnection is null");
        this.connection = connection;
    }

    public DebuggableConnection(Connection connection, SqlFormatter formatter)
            throws SQLException {
        if (connection == null)
            throw new SQLException(
                    "Connection passed to DebuggableConnection is null");
        this.defaultFormatter = formatter;
        this.connection = connection;
    }

    public void clearWarnings() throws SQLException {
        connection.clearWarnings();
    }

    public void close() throws SQLException {
        connection.close();

    }

    public void commit() throws SQLException {
        connection.commit();

    }

    public Statement createStatement() throws SQLException {
        return connection.createStatement();
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency)
            throws SQLException {
        return connection.createStatement(resultSetType, resultSetConcurrency);
    }

    public Statement createStatement(int resultSetType,
            int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        return connection.createStatement(resultSetType, resultSetConcurrency,
                resultSetHoldability);
    }

    public boolean getAutoCommit() throws SQLException {
        return connection.getAutoCommit();
    }

    public String getCatalog() throws SQLException {
        return connection.getCatalog();
    }

    public int getHoldability() throws SQLException {
        return connection.getHoldability();
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return connection.getMetaData();
    }

    public int getTransactionIsolation() throws SQLException {
        return connection.getTransactionIsolation();
    }

    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return connection.getTypeMap();
    }

    public SQLWarning getWarnings() throws SQLException {
        return connection.getWarnings();
    }

    public boolean isClosed() throws SQLException {
        return connection.isClosed();
    }

    public boolean isReadOnly() throws SQLException {
        return connection.isReadOnly();
    }

    public String nativeSQL(String sql) throws SQLException {
        return connection.nativeSQL(sql);
    }

    public CallableStatement prepareCall(String sql) throws SQLException {
        return connection.prepareCall(sql);
    }

    public CallableStatement prepareCall(String sql, int resultSetType,
            int resultSetConcurrency) throws SQLException {
        return connection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    public CallableStatement prepareCall(String sql, int resultSetType,
            int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        return connection.prepareCall(sql, resultSetConcurrency,
                resultSetHoldability);
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        if (logger.isDebugEnabled()) {
            return new DebuggableStatement(sql, connection, defaultFormatter);
        } else {
            return connection.prepareStatement(sql);
        }
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
            throws SQLException {
        if (logger.isDebugEnabled()) {
            return new DebuggableStatement(sql, autoGeneratedKeys, connection,
                    defaultFormatter);
        } else {
            return connection.prepareStatement(sql, autoGeneratedKeys);
        }

    }

    public PreparedStatement prepareStatement(String sql, int resultSetType,
            int resultSetConcurrency) throws SQLException {
        if (logger.isDebugEnabled()) {
            return new DebuggableStatement(sql, resultSetType,
                    resultSetConcurrency, connection, defaultFormatter);
        } else {
            return connection.prepareStatement(sql, resultSetType,
                    resultSetConcurrency);
        }
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType,
            int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        if (logger.isDebugEnabled()) {
            return new DebuggableStatement(sql, resultSetType,
                    resultSetConcurrency, resultSetHoldability, connection,
                    defaultFormatter);
        } else {
            return connection.prepareStatement(sql, resultSetType,
                    resultSetConcurrency, resultSetHoldability);
        }
    }

    public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
            throws SQLException {
        if (logger.isDebugEnabled()) {
            return new DebuggableStatement(sql, columnIndexes, connection,
                    defaultFormatter);
        } else {
            return connection.prepareStatement(sql, columnIndexes);
        }
    }

    public PreparedStatement prepareStatement(String sql, String[] columnNames)
            throws SQLException {
        if (logger.isDebugEnabled()) {
            return new DebuggableStatement(sql, columnNames, connection,
                    defaultFormatter);
        } else {
            return connection.prepareStatement(sql, columnNames);
        }
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        connection.releaseSavepoint(savepoint);

    }

    public void rollback() throws SQLException {
        connection.rollback();

    }

    public void rollback(Savepoint savepoint) throws SQLException {
        connection.rollback();

    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        connection.setAutoCommit(autoCommit);

    }

    public void setCatalog(String catalog) throws SQLException {
        connection.setCatalog(catalog);

    }

    public void setHoldability(int holdability) throws SQLException {
        connection.setHoldability(holdability);

    }

    public void setReadOnly(boolean readOnly) throws SQLException {
        connection.setReadOnly(readOnly);

    }

    public Savepoint setSavepoint() throws SQLException {
        return connection.setSavepoint();
    }

    public Savepoint setSavepoint(String name) throws SQLException {
        return connection.setSavepoint(name);
    }

    public void setTransactionIsolation(int level) throws SQLException {
        connection.setTransactionIsolation(level);

    }

    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        connection.setTypeMap(map);

    }

    public Array createArrayOf(String arg0, Object[] arg1) throws SQLException {
        return connection.createArrayOf(arg0, arg1);
    }

    public Blob createBlob() throws SQLException {
        return connection.createBlob();
    }

    public Clob createClob() throws SQLException {
        return connection.createClob();
    }

    public NClob createNClob() throws SQLException {
        return connection.createNClob();
    }

    public SQLXML createSQLXML() throws SQLException {
        return connection.createSQLXML();
    }

    public Struct createStruct(String arg0, Object[] arg1) throws SQLException {
        return connection.createStruct(arg0, arg1);
    }

    public Properties getClientInfo() throws SQLException {
        return connection.getClientInfo();
    }

    public String getClientInfo(String arg0) throws SQLException {
        return connection.getClientInfo(arg0);
    }

    public boolean isValid(int arg0) throws SQLException {
        return connection.isValid(arg0);
    }

    public void setClientInfo(Properties arg0) throws SQLClientInfoException {
        connection.setClientInfo(arg0);

    }

    public void setClientInfo(String arg0, String arg1)
            throws SQLClientInfoException {
        connection.setClientInfo(arg0, arg1);

    }

    public boolean isWrapperFor(Class<?> arg0) throws SQLException {
        return connection.isWrapperFor(arg0);
    }

    public <T> T unwrap(Class<T> arg0) throws SQLException {
        return connection.unwrap(arg0);
    }

}
