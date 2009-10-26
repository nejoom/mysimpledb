/**
 * Title:        Overpower the PreparedStatement<p>
 * Description:  http://www.javaworld.com/javaworld/jw-01-2002/jw-0125-overpower.html<p>
 * Copyright:    Copyright (c) Troy Thompson Bob Byron<p>
 * Company:      JavaUnderground<p>
 * @author       Troy Thompson Bob Byron
 * @version 1.1
 */
package ac.elements.sql.debug;

import ac.elements.conf.Configuration;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * PreparedStatements have no way to retrieve the statement that was executed on
 * the database. This is due to the nature of prepared statements, which are
 * database driver specific. This class proxies for a PreparedStatement and
 * creates the SQL string that is created from the sets done on the
 * PreparedStatement.
 * <p>
 * Some of the objects such as blob, clob, and Ref are only represented as
 * Strings and are not the actual objects populating the database. Array is
 * represented by the object type within the array.
 * 
 * Example code:
 * 
 * <pre>
 * int payPeriod = 1;
 * String name = &quot;Troy Thompson&quot;;
 * ArrayList employeePay = new ArrayList();
 * ResultSet rs = null;
 * PreparedStatement ps = null;
 * Connection con = null;
 * try {
 *     Class.forName(&quot;sun.jdbc.odbc.JdbcOdbcDriver&quot;);
 *     String url = &quot;jdbc:odbc:Employee&quot;;
 *     con = DriverManager.getConnection(url);
 *     String sql =
 *             &quot;SELECT e.name,e.employee_number,e.pay_rate,e.type,&quot;
 *                     + &quot;e.hire_date,h.pay_period,h.hours,h.commissions&quot;
 *                     + &quot; FROM Employee_tbl e,hours_tbl h &quot;
 *                     + &quot; WHERE h.pay_period = ?&quot; + &quot; AND e.name = ?&quot;
 *                     + &quot; AND h.employee_number = e.employee_number&quot;;
 *     ps = StatementFactory.getStatement(con, sql); // insert to debug
 *     con.prepareStatement(sql);
 *     ps.setInt(1, payPeriod);
 *     ps.setString(2, name);
 *     System.out.println();
 *     System.out.println(&quot; debuggable statement= &quot; + ps.toString());
 *     rs = ps.executeQuery();
 * } catch (SQLException e) {
 *     e.printStackTrace();
 * } catch (ClassNotFoundException ce) {
 *     ce.printStackTrace();
 * } finally {
 *     try {
 *         if (rs != null) {
 *             rs.close();
 *         }
 *         if (ps != null) {
 *             ps.close();
 *         }
 *         if (!con.isClosed())
 *             con.close();
 *     } catch (SQLException e) {
 *         e.printStackTrace();
 *     }
 * }
 * </pre>
 * 
 * Note: One of the main differences between databases is how they handle
 * dates/times. Since we use Oracle, the debug string for Dates, Times,
 * Timestamps are using an Oracle specific SqlFormatter called
 * OracleSqlFormatter.
 * 
 * The following is in our debug class: static{
 * StatementFactory.setDefaultFormatter(new OracleSqlFormatter()); }
 * 
 */
public class DebuggableStatement implements PreparedStatement {

    private class DebugObject {
        private final Object debugObject;

        private final boolean valueAssigned;

        public DebugObject(Object debugObject) {
            this.debugObject = debugObject;
            valueAssigned = true;
        }

        public Object getDebugObject() {
            return debugObject;
        }

        public boolean isValueAssigned() {
            return valueAssigned;
        }
    }

    /** The Constant log. */
    private final static Log logger =
            LogFactory.getLog(DebuggableStatement.class);

    private static boolean triggerRegex = true;

    @SuppressWarnings("null")
    public static void main(String[] args) {

        System.out.println("Hello World");
        @SuppressWarnings("unused")
        String[] a = new String[0];

        SqlFormatter formatter = new OracleSqlFormatter();
        Object o = new java.sql.Date(new java.util.Date().getTime());
        try {
            System.out.println("date=" + formatter.format(o));
            o = new Long(198);
            System.out.println("Long=" + formatter.format(o));
            o = new Boolean(true);
            System.out.println("Boolean=" + formatter.format(o));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;

        String databaseURL =
                "jdbc:mysql://localhost/?autoReconnectForPools="
                        + "true&amp;autoReconnect=true";
        System.out.println(databaseURL);
        String user = "root";
        String password = "";

        String driverName = "com.mysql.jdbc.Driver";
        int interval = 500;
        // test debuggable
        DebuggableConnection con = null;
        try {

            String sql;
            long t0 = System.currentTimeMillis();
            for (int i = 0; i < interval; i++) {
                Class.forName(driverName);
                // System.out.println(driverName);
                java.util.Properties props = new java.util.Properties();
                props.put("user", user);
                props.put("password", password);
                // System.out.println("user = " + props.get("user"));
                Connection con1 =
                        DriverManager.getConnection(databaseURL, props);
                // System.out.println("Connection established.");
                sql =
                        "select * from mysql.user where User = \"'app?\" or User = ? or User = 'john?'";
                ps = con1.prepareStatement(sql);
                ps.setObject(1, "root");
                // System.out.println("toString: " + ps.toString());
                rs = ps.executeQuery();
                while (rs.next()) {
                    // System.out.println("User = " + rs.getString("User"));
                }
            }
            System.out.println("normal: " + (System.currentTimeMillis() - t0)
                    + "[ms]");
            t0 = System.currentTimeMillis();
            for (int i = 0; i < interval; i++) {
                Class.forName(driverName);
                // System.out.println(driverName);
                java.util.Properties props = new java.util.Properties();
                props.put("user", user);
                props.put("password", password);
                // System.out.println("user = " + props.get("user"));
                Connection con1 =
                        DriverManager.getConnection(databaseURL, props);
                // System.out.println("Connection established.");
                sql =
                        "select * from mysql.user where User = \"'app?\" or User = ? or User = 'john?'";
                ps = con1.prepareStatement(sql);
                ps.setObject(1, "root");
                // System.out.println("toString: " + ps.toString());
                rs = ps.executeQuery();
                while (rs.next()) {
                    // System.out.println("User = " + rs.getString("User"));
                }
            }
            System.out.println("normal: " + (System.currentTimeMillis() - t0)
                    + "[ms]");
            t0 = System.currentTimeMillis();
            for (int i = 0; i < interval; i++) {
                Class.forName(driverName);
                // System.out.println(driverName);
                java.util.Properties props = new java.util.Properties();
                props.put("user", user);
                props.put("password", password);
                // System.out.println("user = " + props.get("user"));
                Connection con1 =
                        DriverManager.getConnection(databaseURL, props);
                // System.out.println("Connection established.");
                sql =
                        "select * from mysql.user where User = \"'app?\" or User = ? or User = 'john?'";
                ps = con1.prepareStatement(sql);
                ps.setObject(1, "root");
                // System.out.println("toString: " + ps.toString());
                rs = ps.executeQuery();
                while (rs.next()) {
                    // System.out.println("User = " + rs.getString("User"));
                }
            }
            System.out.println("normal: " + (System.currentTimeMillis() - t0)
                    + "[ms]");
            t0 = System.currentTimeMillis();
            for (int i = 0; i < interval; i++) {
                Class.forName(driverName);
                // System.out.println(driverName);
                java.util.Properties props = new java.util.Properties();
                props.put("user", user);
                props.put("password", password);
                // System.out.println("user = " + props.get("user"));
                DebuggableConnection con1 =
                        new DebuggableConnection(DriverManager.getConnection(
                                databaseURL, props));
                // System.out.println("Connection established.");
                sql =
                        "select * from mysql.user where User = \"'app?\" or User = ? or User = 'john?'";
                ps = con1.prepareStatement(sql);
                ps.setObject(1, "root");
                // System.out.println("toString: " + ps.toString());
                rs = ps.executeQuery();
                while (rs.next()) {
                    // System.out.println("User = " + rs.getString("User"));
                }
            }
            System.out.println("debug: " + (System.currentTimeMillis() - t0)
                    + "[ms]");

            t0 = System.currentTimeMillis();
            for (int i = 0; i < interval; i++) {
                Class.forName(driverName);
                // System.out.println(driverName);
                java.util.Properties props = new java.util.Properties();
                props.put("user", user);
                props.put("password", password);
                // System.out.println("user = " + props.get("user"));
                DebuggableConnection con1 =
                        new DebuggableConnection(DriverManager.getConnection(
                                databaseURL, props));
                // System.out.println("Connection established.");
                sql =
                        "SELECT \"hello?\", \"'hello?'\", ?, \"''hello?''\", \"hel?\"\"?lo\", \"?\\\"?hello?\"";

                ps = con1.prepareStatement(sql);
                ps.setObject(1, "root");
                // System.out.println("toString: " + ps.toString());
                rs = ps.executeQuery();
                while (rs.next()) {
                    // System.out.println("User = " + rs.getString("User"));
                }
            }
            System.out.println("debug: " + (System.currentTimeMillis() - t0)
                    + "[ms]");

            t0 = System.currentTimeMillis();
            for (int i = 0; i < interval; i++) {
                Class.forName(driverName);
                // System.out.println(driverName);
                java.util.Properties props = new java.util.Properties();
                props.put("user", user);
                props.put("password", password);
                // System.out.println("user = " + props.get("user"));
                Connection con1 =
                        DriverManager.getConnection(databaseURL, props);
                // System.out.println("Connection established.");
                sql =
                        "SELECT \"hello?\", \"'hello?'\", ?, \"''hello?''\", \"hel?\"\"?lo\", \"?\\\"?hello?\"";

                ps = con1.prepareStatement(sql);
                ps.setObject(1, "root");
                // System.out.println("toString: " + ps.toString());
                rs = ps.executeQuery();
                while (rs.next()) {
                    // System.out.println("User = " + rs.getString("User"));
                }
            }
            System.out.println("normal: " + (System.currentTimeMillis() - t0)
                    + "[ms]");

        } catch (java.lang.ClassNotFoundException e) {
            e.printStackTrace();
        } catch (java.sql.SQLException se) {
            se.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (!con.isClosed())
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    private long executeTime; // time elapsed while executing statement

    private String filteredSql = ""; // statement filtered for rogue '?'

    private SqlFormatter formatter; // format for dates

    private int parameterCount = 0;

    private final PreparedStatement ps; // preparedStatement being proxied for.

    private String regex;

    private final String sql; // original statement going to database.

    private long startTime; // time that statement began execution

    // the regular expression pattern
    private Pattern trigger;

    private final DebugObject[] variables; // array of bind variables

    /**
     * Construct new DebugableStatement. Uses the SqlFormatter to format date,
     * time, timestamp outputs
     * 
     * @param connection
     *            Connection to be used to construct PreparedStatement
     * @param sql
     *            sql statement to be sent to database.
     * 
     */
    public DebuggableStatement(String sql, Connection connection,
            SqlFormatter formatter) throws SQLException {
        // set values for member variables
        if (connection == null)
            throw new SQLException("Connection object is null");

        this.sql = sql;
        this.formatter = formatter;

        getBindVariables(sql);

        // create array for bind variables
        variables = new DebugObject[parameterCount];

        this.ps = connection.prepareStatement(sql);
    }

    public DebuggableStatement(String sql, int autoGeneratedKeys,
            Connection connection, SqlFormatter formatter) throws SQLException {
        // set values for member variables
        if (connection == null)
            throw new SQLException("Connection object is null");

        this.sql = sql;
        this.formatter = formatter;

        getBindVariables(sql);

        // create array for bind variables
        variables = new DebugObject[parameterCount];

        this.ps = connection.prepareStatement(sql);

    }

    public DebuggableStatement(String sql, int resultSetType,
            int resultSetConcurrency, Connection connection,
            SqlFormatter formatter) throws SQLException {
        // set values for member variables
        if (connection == null)
            throw new SQLException("Connection object is null");

        this.sql = sql;
        this.formatter = formatter;

        getBindVariables(sql);

        // create array for bind variables
        variables = new DebugObject[parameterCount];

        this.ps =
                connection.prepareStatement(sql, resultSetType,
                        resultSetConcurrency);
    }

    public DebuggableStatement(String sql, int resultSetType,
            int resultSetConcurrency, int resultSetHoldability,
            Connection connection, SqlFormatter formatter) throws SQLException {
        // set values for member variables
        if (connection == null)
            throw new SQLException("Connection object is null");

        this.sql = sql;
        this.formatter = formatter;

        getBindVariables(sql);

        // create array for bind variables
        variables = new DebugObject[parameterCount];

        this.ps =
                connection.prepareStatement(sql, resultSetType,
                        resultSetConcurrency, resultSetHoldability);
    }

    public DebuggableStatement(String sql, int[] columnIndexes,
            Connection connection, SqlFormatter formatter) throws SQLException {
        // set values for member variables
        if (connection == null)
            throw new SQLException("Connection object is null");

        this.sql = sql;
        this.formatter = formatter;

        getBindVariables(sql);

        // create array for bind variables
        variables = new DebugObject[parameterCount];

        this.ps = connection.prepareStatement(sql);
    }

    public DebuggableStatement(String sql, String[] columnNames,
            Connection connection, SqlFormatter formatter) throws SQLException {
        // set values for member variables
        if (connection == null)
            throw new SQLException("Connection object is null");

        this.sql = sql;
        this.formatter = formatter;

        getBindVariables(sql);

        // create array for bind variables
        variables = new DebugObject[parameterCount];

        this.ps = connection.prepareStatement(sql, columnNames);
    }

    /**
     * Facade for PreparedStatement
     */
    public void addBatch() throws SQLException {
        ps.addBatch();
    }

    /**
     * Facade for PreparedStatement
     */
    public void addBatch(String sql) throws SQLException {
        ps.addBatch();
    }

    /**
     * Facade for PreparedStatement
     */
    public void cancel() throws SQLException {
        ps.cancel();
    }

    /**
     * Facade for PreparedStatement
     */
    public void clearBatch() throws SQLException {
        ps.clearBatch();
    }

    /**
     * Facade for PreparedStatement
     */
    public void clearParameters() throws SQLException {
        ps.clearParameters();
    }

    /**
     * Facade for PreparedStatement
     */
    public void clearWarnings() throws SQLException {
        ps.clearWarnings();
    }

    /**
     * Facade for PreparedStatement
     */
    public void close() throws SQLException {
        ps.close();
    }

    /**
     * Executes query and Calculates query execution time
     * 
     * @return results of query
     */
    public boolean execute() throws SQLException {
        startExecute();
        boolean rs = ps.execute();
        finishExecute();
        return rs;
    }

    /**
     * This method is only here for convenience. If a different sql string is
     * executed than was passed into Debuggable, unknown results will occur.
     * Executes query and Calculates query execution time
     * 
     * @param sql
     *            should be same string that was passed into Debuggable
     * @return results of query
     */
    public boolean execute(String sql) throws SQLException {
        startExecute();
        boolean rs = ps.execute(sql);
        finishExecute();
        return rs;
    }

    /**
     * Generated Facade for PreparedStatement
     */
    public boolean execute(String sql, int autoGeneratedKeys)
            throws SQLException {
        startExecute();
        boolean rs = ps.execute(sql, autoGeneratedKeys);
        finishExecute();
        return rs;
    }

    /**
     * Generated Facade for PreparedStatement
     */
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        startExecute();
        boolean rs = ps.execute(sql, columnIndexes);
        finishExecute();
        return rs;
    }

    /**
     * Generated Facade for PreparedStatement
     */
    public boolean execute(String sql, String[] columnNames)
            throws SQLException {
        startExecute();
        boolean rs = ps.execute(sql, columnNames);
        finishExecute();
        return rs;
    }

    /**
     * Executes query and Calculates query execution time
     * 
     * @return results of query
     */
    public int[] executeBatch() throws SQLException {
        startExecute();
        int[] rs = ps.executeBatch();
        finishExecute();
        return rs;
    }

    /**
     * Executes query and Calculates query execution time
     * 
     * @return results of query
     */
    public ResultSet executeQuery() throws SQLException {
        startExecute();
        ResultSet rs = ps.executeQuery();
        finishExecute();
        return rs;
    }

    /**
     * This method is only here for convenience. If a different sql string is
     * executed than was passed into Debuggable, unknown results will occur.
     * Executes query and Calculates query execution time
     * 
     * @param sql
     *            should be same string that was passed into Debuggable
     * @return results of query
     */
    public ResultSet executeQuery(String sql) throws SQLException {
        startExecute();
        ResultSet rs = ps.executeQuery(sql);
        finishExecute();
        return rs;
    }

    /**
     * Executes query and Calculates query execution time
     * 
     * @return results of query
     */
    public int executeUpdate() throws SQLException {
        startExecute();
        int i = ps.executeUpdate();
        finishExecute();
        return i;
    }

    /**
     * This method is only here for convenience. If a different sql string is
     * executed than was passed into Debuggable, unknown results will occur.
     * Executes query and Calculates query execution time
     * 
     * @param sql
     *            should be same string that was passed into Debuggable
     * @return results of query
     */
    public int executeUpdate(String sql) throws SQLException {
        startExecute();
        int i = ps.executeUpdate(sql);
        finishExecute();
        return i;
    }

    /**
     * Generated Facade for PreparedStatement
     */
    public int executeUpdate(String sql, int autoGeneratedKeys)
            throws SQLException {
        startExecute();
        int i = ps.executeUpdate(sql, autoGeneratedKeys);
        finishExecute();
        return i;
    }

    /**
     * Generated Facade for PreparedStatement
     */
    public int executeUpdate(String sql, int[] columnIndexes)
            throws SQLException {
        startExecute();
        int i = ps.executeUpdate(sql, columnIndexes);
        finishExecute();
        return i;
    }

    /**
     * Generated Facade for PreparedStatement
     */
    public int executeUpdate(String sql, String[] columnNames)
            throws SQLException {
        startExecute();
        int i = ps.executeUpdate(sql, columnNames);
        finishExecute();
        return i;
    }

    private void finishExecute() {
        executeTime = System.currentTimeMillis() - startTime;

        String regularExpression = null;
        Matcher matcher = null;

        if (triggerRegex) {

            Configuration conf = Configuration.getInstance();
            regularExpression =

                    conf.getValue("log4j",
                            "glowaxes.sql.debug.DebuggableStatement"
                                    + ".regularExpression");
            if (regularExpression == null)
                triggerRegex = false;

        }
        if (regularExpression != null) {
            if (!regularExpression.equals(regex)) {
                regex = regularExpression;
                trigger = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            }
            matcher = trigger.matcher(toString());

        }

        if (matcher != null && matcher.find()) {

            logger.warn("PreparedStatement: " + sql);
            StringBuffer bs = new StringBuffer();
            bs.append("{");
            if (variables != null)
                for (int i = 0; i < variables.length; i++) {
                    bs.append(variables[i].getDebugObject().toString());
                    if (i != variables.length - 1)
                        bs.append(", ");
                }
            bs.append("}");
            logger.warn("Parameters: " + bs.toString());
            logger.warn("FilteredStatement: " + toString());
            logger.warn("Execute time: [" + executeTime + "ms]");

        } else if (logger.isDebugEnabled()) {
            logger.debug("PreparedStatement: " + sql);
            StringBuffer bs = new StringBuffer();
            bs.append("{");
            if (variables != null)
                for (int i = 0; i < variables.length; i++) {
                    bs.append(variables[i].getDebugObject().toString());
                    if (i != variables.length - 1)
                        bs.append(", ");
                }
            bs.append("}");
            logger.debug("Parameters: " + bs.toString());
            logger.debug("FilteredStatement: " + toString());
            logger.debug("Execute time: [" + executeTime + "ms]");
        } else if (executeTime > 1000 && executeTime <= 10000) {
            logger.warn("PreparedStatement: " + sql);
            StringBuffer bs = new StringBuffer();
            bs.append("{");
            if (variables != null)
                for (int i = 0; i < variables.length; i++) {
                    bs.append(variables[i].getDebugObject().toString());
                    if (i != variables.length - 1)
                        bs.append(", ");
                }
            bs.append("}");
            logger.warn("Parameters: " + bs.toString());
            logger.warn("FilteredStatement: " + toString());
            logger.warn("Execute time: [" + executeTime + "ms]");
        } else if (executeTime > 10000) {
            logger.error("PreparedStatement: " + sql);
            StringBuffer bs = new StringBuffer();
            bs.append("{");
            if (variables != null)
                for (int i = 0; i < variables.length; i++) {
                    bs.append(variables[i].getDebugObject().toString());
                    if (i != variables.length - 1)
                        bs.append(", ");
                }
            bs.append("}");
            logger.error("Parameters: " + toString());
            logger.error("FilteredStatement: " + ps.toString());
            logger.error("Execute time: [" + executeTime + "ms]");
        }

    }

    private void getBindVariables(String sqlStatement) {
        // see if there are any '?' in the statement that are not bind variables
        // and filter them out.
        boolean isString = false;
        char currentQuoteChar = ' ';
        char[] sqlString = sqlStatement.toCharArray();
        for (int i = 0; i < sqlString.length; i++) {
            // following takes mysql escape strings into account
            if (sqlString[i] == '\''
                    && (currentQuoteChar == ' ' || currentQuoteChar == '\'')
                    && ((i > 1 && sqlString[i - 1] != '\'') || (i < sqlString.length - 1 && sqlString[i + 1] != '\''))
                    && (i > 1 && sqlString[i - 1] != '\\')) {
                if (!isString) {
                    currentQuoteChar = '\'';
                } else {
                    currentQuoteChar = ' ';
                }
                isString = !isString;
            } else if (sqlString[i] == '\"'
                    && (currentQuoteChar == ' ' || currentQuoteChar == '\"')
                    && ((i > 1 && sqlString[i - 1] != '\"') || (i < sqlString.length - 1 && sqlString[i + 1] != '\"'))
                    && (i > 1 && sqlString[i - 1] != '\\')) {
                if (!isString) {
                    currentQuoteChar = '\"';
                } else {
                    currentQuoteChar = ' ';
                }
                isString = !isString;
            }
            // substitute the ? with an unprintable character if the ? is in a
            // string.
            if (sqlString[i] == '?' && isString)
                sqlString[i] = '\u0007';
        }
        filteredSql = new String(sqlString);

        // find out how many variables are present in statement.
        int index = -1;
        while ((index = filteredSql.indexOf("?", index + 1)) != -1) {
            parameterCount++;
        }

    }

    /**
     * Facade for PreparedStatement
     */
    public Connection getConnection() throws SQLException {
        return ps.getConnection();
    }

    /**
     * Facade for PreparedStatement
     */
    public int getFetchDirection() throws SQLException {
        return ps.getFetchDirection();
    }

    /**
     * Facade for PreparedStatement
     */
    public int getFetchSize() throws SQLException {
        return ps.getFetchSize();
    }

    /**
     * Generated Facade for PreparedStatement
     */
    public ResultSet getGeneratedKeys() throws SQLException {
        return ps.getGeneratedKeys();
    }

    /**
     * Facade for PreparedStatement
     */
    public int getMaxFieldSize() throws SQLException {
        return ps.getMaxFieldSize();
    }

    /**
     * Facade for PreparedStatement
     */
    public int getMaxRows() throws SQLException {
        return ps.getMaxRows();
    }

    /**
     * Facade for PreparedStatement
     */
    public ResultSetMetaData getMetaData() throws SQLException {
        return ps.getMetaData();
    }

    /**
     * Facade for PreparedStatement
     */
    public boolean getMoreResults() throws SQLException {
        return ps.getMoreResults();
    }

    /**
     * Generated Facade for PreparedStatement
     */
    public boolean getMoreResults(int current) throws SQLException {
        return ps.getMoreResults(current);
    }

    /**
     * Generated Facade for PreparedStatement
     */
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return ps.getParameterMetaData();
    }

    /**
     * Facade for PreparedStatement
     */
    public int getQueryTimeout() throws SQLException {
        return ps.getQueryTimeout();
    }

    /**
     * Facade for PreparedStatement
     */
    public ResultSet getResultSet() throws SQLException {
        return ps.getResultSet();
    }

    /**
     * Facade for PreparedStatement
     */
    public int getResultSetConcurrency() throws SQLException {
        return ps.getResultSetConcurrency();
    }

    /**
     * Generated Facade for PreparedStatement
     */
    public int getResultSetHoldability() throws SQLException {
        return ps.getResultSetHoldability();
    }

    /**
     * Facade for PreparedStatement
     */
    public int getResultSetType() throws SQLException {
        return ps.getResultSetType();
    }

    /**
     * Facade for PreparedStatement
     */
    public String getStatement() {
        return sql;
    }

    /**
     * Facade for PreparedStatement
     */
    public int getUpdateCount() throws SQLException {
        return ps.getUpdateCount();
    }

    /**
     * Facade for PreparedStatement
     */
    public SQLWarning getWarnings() throws SQLException {
        return ps.getWarnings();
    }

    /**
     * Tests Object o for parameterIndex (which parameter is being set) and
     * places object in array of variables.
     * 
     * @param parameterIndex
     *            which PreparedStatement parameter is being set. Sequence
     *            begins at 1.
     * @param o
     *            Object being stored as parameter
     * @throws ParameterIndexOutOfBoundsException
     * @exception Thrown
     *                if index exceeds number of variables.
     */
    private void saveObject(int parameterIndex, Object o)
            throws ParameterIndexOutOfBoundsException {
        if (parameterIndex > variables.length)
            throw new ParameterIndexOutOfBoundsException("Parameter index of "
                    + parameterIndex
                    + " exceeds actual parameter parameterCount of "
                    + variables.length);

        variables[parameterIndex - 1] = new DebugObject(o);
    }

    /**
     * Adds name of the Array's internal class type(by using
     * x.getBaseTypeName()) to the debug String. If x is null, NULL is added to
     * debug String.
     * 
     * @param i
     *            index of parameter
     * @param x
     *            parameter Object
     */
    public void setArray(int i, java.sql.Array x) throws SQLException {
        saveObject(i, x);
        ps.setArray(i, x);
    }

    /**
     * Debug string prints NULL if InputStream is null, or adds "stream length = " +
     * length
     */
    public void setAsciiStream(int parameterIndex, InputStream x, int length)
            throws SQLException {
        saveObject(parameterIndex, (x == null ? "NULL" : "<stream length= "
                + length + ">"));
        ps.setAsciiStream(parameterIndex, x, length);
    }

    /**
     * Adds BigDecimal to debug string in parameterIndex position.
     * 
     * @param parameterIndex
     *            index of parameter
     * @param x
     *            parameter Object
     */
    public void setBigDecimal(int parameterIndex, BigDecimal x)
            throws SQLException {
        saveObject(parameterIndex, x);
        ps.setBigDecimal(parameterIndex, x);
    }

    /**
     * Debug string prints NULL if InputStream is null, or adds "stream length= " +
     * length.
     * 
     * @param parameterIndex
     *            index of parameter
     * @param x
     *            parameter Object
     * @param length
     *            length of InputStream
     */
    public void setBinaryStream(int parameterIndex, InputStream x, int length)
            throws SQLException {
        saveObject(parameterIndex, (x == null ? "NULL" : "<stream length= "
                + length + ">"));
        ps.setBinaryStream(parameterIndex, x, length);
    }

    /**
     * Adds name of the object's class type(Blob) to the debug String. If object
     * is null, NULL is added to debug String.
     * 
     * @param parameterIndex
     *            index of parameter
     * @param x
     *            parameter Object
     */
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        saveObject(parameterIndex, x);
        ps.setBlob(parameterIndex, x);
    }

    /**
     * Adds boolean to debug string in parameterIndex position.
     * 
     * @param parameterIndex
     *            index of parameter
     * @param x
     *            parameter Object
     */
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        saveObject(parameterIndex, new Boolean(x));
        ps.setBoolean(parameterIndex, x);
    }

    /**
     * Adds byte to debug string in parameterIndex position.
     * 
     * @param parameterIndex
     *            index of parameter
     * @param x
     *            parameter Object
     */
    public void setByte(int parameterIndex, byte x) throws SQLException {
        saveObject(parameterIndex, new Byte(x));
        ps.setByte(parameterIndex, x);
    }

    /**
     * Adds byte[] to debug string in parameterIndex position.
     * 
     * @param parameterIndex
     *            index of parameter
     * @param x
     *            parameter Object
     */
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        saveObject(parameterIndex, (x == null ? "NULL" : "byte[] length="
                + x.length));
        ps.setBytes(parameterIndex, x);
    }

    /**
     * Debug string prints NULL if reader is null, or adds "stream length= " +
     * length.
     * 
     * @param parameterIndex
     *            index of parameter
     * @param x
     *            parameter Object
     * @param length
     *            length of InputStream
     */
    public void setCharacterStream(int parameterIndex, Reader reader, int length)
            throws SQLException {
        saveObject(parameterIndex, (reader == null ? "NULL"
                : "<stream length= " + length + ">"));
        ps.setCharacterStream(parameterIndex, reader, length);
    }

    /**
     * Adds name of the object's class type(Clob) to the debug String. If object
     * is null, NULL is added to debug String.
     * 
     * @param parameterIndex
     *            index of parameter
     * @param x
     *            parameter Object
     */
    public void setClob(int i, Clob x) throws SQLException {
        saveObject(i, x);
        ps.setClob(i, x);
    }

    /**
     * Generated Facade for PreparedStatement
     */
    public void setCursorName(String name) throws SQLException {
        ps.setCursorName(name);
    }

    /**
     * Debug string displays date in YYYY-MM-DD HH24:MI:SS.# format.
     * 
     * @param parameterIndex
     *            index of parameter
     * @param x
     *            parameter Object
     */
    public void setDate(int parameterIndex, java.sql.Date x)
            throws SQLException {
        saveObject(parameterIndex, x);
        ps.setDate(parameterIndex, x);
    }

    /**
     * this implementation assumes that the Date has the date, and the calendar
     * has the local info. For the debug string, the cal date is set to the date
     * of x. Debug string displays date in YYYY-MM-DD HH24:MI:SS.# format.
     * 
     * @param parameterIndex
     *            index of parameter
     * @param x
     *            parameter Object
     * @param cal
     *            uses x to set time
     */
    public void setDate(int parameterIndex, java.sql.Date x, Calendar cal)
            throws SQLException {
        cal.setTime(new java.util.Date(x.getTime()));
        saveObject(parameterIndex, cal);
        ps.setDate(parameterIndex, x, cal);
    }

    /**
     * Adds double to debug string in parameterIndex position.
     * 
     * @param parameterIndex
     *            index of parameter
     * @param x
     *            parameter Object
     */
    public void setDouble(int parameterIndex, double x) throws SQLException {
        saveObject(parameterIndex, new Double(x));
        ps.setDouble(parameterIndex, x);
    }

    /**
     * Facade for PreparedStatement
     */
    public void setEscapeProcessing(boolean enable) throws SQLException {
        ps.setEscapeProcessing(enable);
    }

    /**
     * Facade for PreparedStatement
     */
    public void setFetchDirection(int direction) throws SQLException {
        ps.setFetchDirection(direction);
    }

    /**
     * Facade for PreparedStatement
     */
    public void setFetchSize(int rows) throws SQLException {
        ps.setFetchSize(rows);
    }

    /**
     * Adds float to debug string in parameterIndex position.
     * 
     * @param parameterIndex
     *            index of parameter
     * @param x
     *            parameter Object
     */
    public void setFloat(int parameterIndex, float x) throws SQLException {
        saveObject(parameterIndex, new Float(x));
        ps.setFloat(parameterIndex, x);
    }

    /**
     * Facade for PreparedStatement
     */
    public void setFormatter(SqlFormatter formatter) {
        this.formatter = formatter;
    }

    /**
     * Adds int to debug string in parameterIndex position.
     * 
     * @param parameterIndex
     *            index of parameter
     * @param x
     *            parameter Object
     */
    public void setInt(int parameterIndex, int x) throws SQLException {
        saveObject(parameterIndex, new Integer(x));
        ps.setInt(parameterIndex, x);
    }

    /**
     * Adds long to debug string in parameterIndex position.
     * 
     * @param parameterIndex
     *            index of parameter
     * @param x
     *            parameter Object
     */
    public void setLong(int parameterIndex, long x) throws SQLException {
        saveObject(parameterIndex, new Long(x));
        ps.setLong(parameterIndex, x);
    }

    /**
     * Facade for PreparedStatement
     */
    public void setMaxFieldSize(int max) throws SQLException {
        ps.setMaxFieldSize(max);
    }

    /**
     * Facade for PreparedStatement
     */
    public void setMaxRows(int max) throws SQLException {
        ps.setMaxRows(max);
    }

    /**
     * Adds a NULL to the debug String.
     * 
     * @param parameterIndex
     *            index of parameter
     * @param x
     *            parameter Object
     */
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        saveObject(parameterIndex, "NULL");
        ps.setNull(parameterIndex, sqlType);
    }

    /**
     * Adds a NULL to the debug String.
     * 
     * @param parameterIndex
     *            index of parameter
     * @param x
     *            parameter Object
     * @param typeName
     *            type of Object
     */
    public void setNull(int parameterIndex, int sqlType, String typeName)
            throws SQLException {
        saveObject(parameterIndex, "NULL");
        ps.setNull(parameterIndex, sqlType, typeName);
    }

    /**
     * Adds name of the object's class type to the debug String. If object is
     * null, NULL is added to debug String.
     * 
     * @param parameterIndex
     *            index of parameter
     * @param x
     *            parameter Object
     */
    public void setObject(int parameterIndex, Object x) throws SQLException {
        saveObject(parameterIndex, (x == null ? "NULL" : x.toString()));
        ps.setObject(parameterIndex, x);
    }

    /**
     * Adds name of the object's class type to the debug String. If object is
     * null, NULL is added to debug String.
     * 
     * @param parameterIndex
     *            index of parameter
     * @param x
     *            parameter Object
     * @param targetSqlType
     *            database type
     */
    public void setObject(int parameterIndex, Object x, int targetSqlType)
            throws SQLException {
        saveObject(parameterIndex, (x == null ? "NULL" : x.toString()));
        ps.setObject(parameterIndex, x, targetSqlType);
    }

    /**
     * Adds name of the object's class type to the debug String. If object is
     * null, NULL is added to debug String.
     * 
     * @param parameterIndex
     *            index of parameter
     * @param x
     *            parameter Object
     * @param targetSqlType
     *            database type
     * @param scale
     *            see PreparedStatement
     */
    public void setObject(int parameterIndex, Object x, int targetSqlType,
            int scale) throws SQLException {
        saveObject(parameterIndex, (x == null ? "NULL" : x.toString()));
        ps.setObject(parameterIndex, x, targetSqlType, scale);
    }

    /**
     * Facade for PreparedStatement
     */
    public void setQueryTimeout(int seconds) throws SQLException {
        ps.setQueryTimeout(seconds);
    }

    /**
     * From the javadocs: A reference to an SQL structured type value in the
     * database. A Ref can be saved to persistent storage. The output from this
     * method call in DebuggableStatement is a string representation of the Ref
     * object by calling the Ref object's getBaseTypeName() method. Again, this
     * will only be a String representation of the actual object being stored in
     * the database.
     * 
     * @param i
     *            index of parameter
     * @param x
     *            parameter Object
     */

    public void setRef(int i, Ref x) throws SQLException {
        saveObject(i, x);
        ps.setRef(i, x);
    }

    /**
     * Adds short to debug string in parameterIndex position.
     * 
     * @param parameterIndex
     *            index of parameter
     * @param x
     *            parameter Object
     */
    public void setShort(int parameterIndex, short x) throws SQLException {
        saveObject(parameterIndex, new Short(x));
        ps.setShort(parameterIndex, x);
    }

    /**
     * Adds String to debug string in parameterIndex position. If String is null
     * "NULL" is inserted in debug string. ***note**** In situations where a
     * single ' is in the string being inserted in the database. The debug
     * string will need to be modified to reflect this when running the debug
     * statement in the database.
     * 
     * @param parameterIndex
     *            index of parameter
     * @param x
     *            parameter Object
     */
    public void setString(int parameterIndex, String x) throws SQLException {
        saveObject(parameterIndex, x);
        ps.setString(parameterIndex, x);
    }

    /**
     * Debug string displays Time in HH24:MI:SS.# format.
     * 
     * @param parameterIndex
     *            index of parameter
     * @param x
     *            parameter Object
     */
    public void setTime(int parameterIndex, Time x) throws SQLException {
        saveObject(parameterIndex, x);
        ps.setTime(parameterIndex, x);
    }

    /**
     * This implementation assumes that the Time object has the time and
     * Calendar has the locale info. For the debug string, the cal time is set
     * to the value of x. Debug string displays time in HH24:MI:SS.# format.
     * 
     * @param parameterIndex
     *            index of parameter
     * @param x
     *            parameter Object
     * @param cal
     *            sets time based on x
     */
    public void setTime(int parameterIndex, Time x, Calendar cal)
            throws SQLException {
        cal.setTime(new java.util.Date(x.getTime()));
        saveObject(parameterIndex, cal);
        ps.setTime(parameterIndex, x, cal);
    }

    /**
     * Debug string displays timestamp in YYYY-MM-DD HH24:MI:SS.# format.
     * 
     * @param parameterIndex
     *            index of parameter
     * @param x
     *            parameter Object
     */
    public void setTimestamp(int parameterIndex, Timestamp x)
            throws SQLException {
        saveObject(parameterIndex, x);
        ps.setTimestamp(parameterIndex, x);
    }

    /**
     * This implementation assumes that the Timestamp has the date/time and
     * Calendar has the locale info. For the debug string, the cal date/time is
     * set to the default value of Timestamp which is YYYY-MM-DD HH24:MI:SS.#.
     * Debug string displays timestamp in DateFormat.LONG format.
     * 
     * @param parameterIndex
     *            index of parameter
     * @param x
     *            parameter Object
     * @param cal
     *            sets time based on x
     */
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
            throws SQLException {
        cal.setTime(new java.util.Date(x.getTime()));
        saveObject(parameterIndex, cal);
        ps.setTimestamp(parameterIndex, x, cal);
    }

    /**
     * Method has been deprecated in PreparedStatement interface. This method is
     * present only to satisfy interface and does not do anything. Do not use...
     * 
     * @deprecated
     */
    @Deprecated
    public void setUnicodeStream(int parameterIndex, InputStream x, int length)
            throws SQLException {
        ps.setUnicodeStream(parameterIndex, x, length);
    }

    /**
     * Generated Facade for PreparedStatement
     */
    public void setURL(int parameterIndex, URL x) throws SQLException {
        ps.setURL(parameterIndex, x);

    }

    private void startExecute() {
        startTime = System.currentTimeMillis();
    }

    /**
     * this toString is overidden to return a String representation of the sql
     * statement being sent to the database. If a bind variable is missing then
     * the String contains a ? [missing variable #]
     * 
     * @return the above string representation
     */
    @Override
    public String toString() {
        StringTokenizer st = new StringTokenizer(filteredSql, "?");
        int count = 1;
        StringBuffer statement = new StringBuffer();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            statement.append(token);
            if (count <= variables.length) {
                if (variables[count - 1] != null
                        && variables[count - 1].isValueAssigned()) {
                    try {
                        statement.append(formatter.format(variables[count - 1]
                                .getDebugObject()));
                    } catch (SQLException e) {
                        statement.append("SQLException");
                    }
                } else {
                    statement.append("? [missing variable #" + count + "] ");
                }
            }
            count++;
        }
        // unfilter the string in case there where rogue '?' in query string.
        char[] unfilterSql = statement.toString().toCharArray();
        for (int i = 0; i < unfilterSql.length; i++) {
            if (unfilterSql[i] == '\u0007')
                unfilterSql[i] = '?';
        }

        return new String(unfilterSql);

    }

    public void setAsciiStream(int arg0, InputStream arg1) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void setAsciiStream(int arg0, InputStream arg1, long arg2)
            throws SQLException {
        ps.setAsciiStream(arg0, arg1, arg2);

    }

    public void setBinaryStream(int arg0, InputStream arg1) throws SQLException {
        ps.setBinaryStream(arg0, arg1);

    }

    public void setBinaryStream(int arg0, InputStream arg1, long arg2)
            throws SQLException {
        ps.setBinaryStream(arg0, arg1, arg2);

    }

    public void setBlob(int arg0, InputStream arg1) throws SQLException {
        ps.setBlob(arg0, arg1);

    }

    public void setBlob(int arg0, InputStream arg1, long arg2)
            throws SQLException {
        ps.setBlob(arg0, arg1, arg2);

    }

    public void setCharacterStream(int arg0, Reader arg1) throws SQLException {
        ps.setCharacterStream(arg0, arg1);

    }

    public void setCharacterStream(int arg0, Reader arg1, long arg2)
            throws SQLException {
        ps.setCharacterStream(arg0, arg1);

    }

    public void setClob(int arg0, Reader arg1) throws SQLException {
        ps.setClob(arg0, arg1);

    }

    public void setClob(int arg0, Reader arg1, long arg2) throws SQLException {
        ps.setClob(arg0, arg1, arg2);

    }

    public void setNCharacterStream(int arg0, Reader arg1) throws SQLException {
        ps.setNCharacterStream(arg0, arg1);

    }

    public void setNCharacterStream(int arg0, Reader arg1, long arg2)
            throws SQLException {
        ps.setNCharacterStream(arg0, arg1, arg2);

    }

    public void setNClob(int arg0, NClob arg1) throws SQLException {
        ps.setNClob(arg0, arg1);
    }

    public void setNClob(int arg0, Reader arg1) throws SQLException {
        ps.setNClob(arg0, arg1);

    }

    public void setNClob(int arg0, Reader arg1, long arg2) throws SQLException {
        ps.setNClob(arg0, arg1, arg2);

    }

    public void setNString(int arg0, String arg1) throws SQLException {
        ps.setNString(arg0, arg1);

    }

    public void setRowId(int arg0, RowId arg1) throws SQLException {
        ps.setRowId(arg0, arg1);

    }

    public void setSQLXML(int arg0, SQLXML arg1) throws SQLException {
        ps.setSQLXML(arg0, arg1);

    }

    public boolean isClosed() throws SQLException {
        return ps.isClosed();
    }

    public boolean isPoolable() throws SQLException {
        return ps.isPoolable();
    }

    public void setPoolable(boolean arg0) throws SQLException {
        ps.setPoolable(arg0);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return ps.isWrapperFor(iface);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return ps.unwrap(iface);
    }

}
