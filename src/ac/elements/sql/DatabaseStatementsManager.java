/**
 *
 * Copyright 2008-2009 Elements. All Rights Reserved.
 *
 * License version: CPAL 1.0
 *
 * The Original Code is glowaxes.org code. Please visit glowaxes.org to see how
 * you can contribute and improve this software.
 *
 * The contents of this file are licensed under the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *    http://glowaxes.org/license.
 *
 * The License is based on the Mozilla Public License Version 1.1.
 *
 * Sections 14 and 15 have been added to cover use of software over a computer
 * network and provide for attribution determined by Elements.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 *
 * Elements is the Initial Developer and the Original Developer of the Original
 * Code.
 *
 * The contents of this file may be used under the terms of the Elements 
 * End-User License Agreement (the Elements License), in which case the 
 * provisions of the Elements License are applicable instead of those above.
 *
 * You may wish to allow use of your version of this file under the terms of
 * the Elements License please visit http://glowaxes.org/license for details.
 *
 */

package ac.elements.sql;

import ac.elements.sql.debug.DebuggableConnection;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * DatabaseStatementsManager manages giving HashMap representations of the
 * resultset through the "singleton" databaseConfiguration which pools
 * connections independently from database vendor specifications.
 * 
 * Databases can be distributed geographically through naming conventions. The
 * databaseConfiguration object holds all information (siteName, databaseName,
 * dataSource) to be able to make a connection.
 * 
 * <h2>Example Code</h2>
 * 
 * <pre><p class=example>
 * LinkedHashMap [] myHashMapArray =
 * <br>
 * DatabaseManager.getHashMapArray(&quot;select
 * * from database.table&quot;, &quot;jdbc/default&quot;);
 * </p></pre>
 * 
 * @author <a href="mailto:eddie@tinyelements.com">Eddie Moojen</a>
 * @version 1.00, 30/10/01
 */
public class DatabaseStatementsManager {

    /** The Constant log. */
    private final static Log logger =
            LogFactory.getLog(DatabaseStatementsManager.class);

    /**
     * Get the ArrayList representation of table columns.
     * 
     * @param database
     *            the database
     * @param table
     *            the table
     * @param dataSource
     *            the dataSource
     * 
     * @return the column names
     */
    public static ArrayList<String> getColumnNames(String database,
            String table, String dataSource) {

        ArrayList<String> lhs = new ArrayList<String>();
        Connection con = null;
        ResultSet rs = null;
        DatabaseMetaData meta = null;

        try {

            try {

                con =
                        new DebuggableConnection(DataSourceCache.SINGLETON
                                .getDataSource(dataSource).getConnection());
            } catch (Exception e) {
                logger.fatal("Can't make connection for dataSource " + e);
                throw new RuntimeException(e);
            }

            if (con == null) {
                logger.fatal("Can't make connection for dataSource "
                        + "(Connection is null): " + dataSource);
                throw new RuntimeException("Connection is null for: "
                        + dataSource);
            }

            try {
                meta = con.getMetaData();
            } catch (SQLException sqlEx) {
                logger.error("SQLException: " + sqlEx);
                throw new RuntimeException(sqlEx);
            }

            try {
                rs = meta.getColumns(database, null, table, "%");
            } catch (SQLException sqlEx) {
                logger.error("SQLException: " + sqlEx);
                throw new RuntimeException(sqlEx);
            }

            try {
                while (rs.next()) {
                    lhs.add(rs.getString(4));
                }
            } catch (SQLException sqlEx) {
                logger.error("SQLException: " + sqlEx);
                logger.error("dataSource: " + dataSource);
                logger.error("database: " + database);
                logger.error("table: " + table);
                throw new RuntimeException(sqlEx);
            }
        } finally {

            // it is a good idea to release
            // resources in a finally{} block
            // in reverse-order of their creation
            // if they are no-longer needed

            if (rs != null) {

                try {
                    rs.close();
                } catch (SQLException sqlEx) {
                    logger.error(sqlEx);
                }

                rs = null;
            }

            if (con != null) {
                try {
                    con.close();
                } catch (SQLException sqlEx) {
                    // ignore -- as we can't do anything about it here
                    logger.error(sqlEx);
                }

                con = null;
            }

        }

        return lhs;

    }

    /**
     * Get the HashMap[] representation of the resultset for the sql expression
     * usingthe dataSource encapsulated in the DatabaseConfiguration parameters.
     * 
     * @param preparedStatement
     *            the prepared statement
     * @param arrayList
     *            the array list
     * @param dc
     *            the dc
     * 
     * @return the hash map array
     */
    public static LinkedHashMap<String, Object>[] getHashMapArray(
            String preparedStatement, ArrayList<Object> arrayList,
            DatabaseConfiguration dc) {

        String dataSource;
        String databaseName;

        if (dc == null) {
            databaseName = "";
            dataSource = "jdbc/default";
        } else {
            databaseName = dc.getDatabaseName();
            dataSource = dc.getDataSource();
        }

        // first replace database for sql statements
        preparedStatement =
                replace(preparedStatement, "{database}.", "`".concat(
                        databaseName).concat("`."));

        // replace other occurences
        preparedStatement =
                replace(preparedStatement, "{database}", databaseName);

        if (logger.isDebugEnabled())
            logger.debug("Resolved database, dataSource: [{" + dc.toStringF()
                    + "}, {" + dataSource + "}] ");

        return getHashMapArray(preparedStatement, arrayList, dataSource);

    }

    /**
     * Get the HashMap[] representation of the resultset for the sql expression
     * and dataSource passed as a parameter.
     * 
     * @param preparedStatement
     *            the prepared statement
     * @param arrayList
     *            the array list
     * @param dataSource
     *            the dataSource
     * 
     * @return the hash map array
     */
    public static LinkedHashMap<String, Object>[] getHashMapArray(
            String preparedStatement, ArrayList<Object> arrayList,
            String dataSource) {

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        LinkedHashMap<String, Object>[] result = null;

        try {

            try {

                con =
                        new DebuggableConnection(DataSourceCache.SINGLETON
                                .getDataSource(dataSource).getConnection());

            } catch (Exception e) {
                logger.fatal("Can't make connection for dataSource " + e);
                throw new RuntimeException(e);
            }

            if (con == null) {
                logger.fatal("Can't make connection for dataSource "
                        + "(Connection is null): " + dataSource);
                throw new RuntimeException("Connection is null for: "
                        + dataSource);
            }

            try {
                statement =
                        con.prepareStatement(preparedStatement,
                                ResultSet.TYPE_SCROLL_SENSITIVE,
                                ResultSet.CONCUR_READ_ONLY);

            } catch (SQLException sqlEx) {
                logger.error("SQL statement: \n" + preparedStatement);
                logger.error("SQLException: " + sqlEx);
                throw new RuntimeException(sqlEx);
            }

            if (arrayList != null) {
                Object test = null;
                int count = 0;
                for (Iterator<Object> i = arrayList.iterator(); i.hasNext();) {

                    test = i.next();

                    try {

                        statement.setObject(++count, test);

                    } catch (SQLException sqlEx) {
                        logger.error("SQL statement: \n" + preparedStatement);
                        logger.error("SQLException: " + sqlEx);
                        logger.error("Exception getSQLState: "
                                + sqlEx.getSQLState());
                        throw new RuntimeException(sqlEx);
                    } catch (NullPointerException e) {
                        logger.error(e);
                        throw new RuntimeException(e);
                    }

                }
            }

            try {

                rs = statement.executeQuery();

                result = ResultsetToHashMapArray.getHashMapArray(rs);

            } catch (SQLException sqlEx) {
                logger.error("SQL statement: \n" + preparedStatement);
                logger.error("SQLException: " + sqlEx);
                logger.error("Exception getSQLState: " + sqlEx.getSQLState());
                throw new RuntimeException(sqlEx);
            } catch (ArrayIndexOutOfBoundsException e) {
                logger.error("SQL statement: \n" + preparedStatement);
                logger.error("ArrayIndexOutOfBoundsException: " + e);
                throw new RuntimeException(e);
            } catch (NullPointerException e) {
                logger.error("SQL statement: \n" + preparedStatement);
                logger.error("NullPointerException: " + e);
                throw new RuntimeException(e);
            }

        } finally {

            // it is a good idea to release
            // resources in a finally{} block
            // in reverse-order of their creation
            // if they are no-longer needed
            if (rs != null) {

                try {
                    rs.close();
                } catch (SQLException sqlEx) {
                    logger.error(sqlEx);
                }

                rs = null;
            }

            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException sqlEx) {
                    logger.error(sqlEx);
                }

                statement = null;
            }

            if (con != null) {
                try {
                    con.close();
                } catch (SQLException sqlEx) {
                    // ignore -- as we can't do anything about it here
                    logger.error(sqlEx);
                }

                con = null;
            }

        }

        return result;
    }

    /**
     * Gets the multiple execute.
     * 
     * @param multiplePreparedStatement
     *            the multiple prepared statement
     * @param arrayList
     *            the array list
     * @param dc
     *            the dc
     * 
     * @return the multiple execute
     */
    public static LinkedHashMap<String, Object>[] getMultipleExecute(
            String multiplePreparedStatement, ArrayList<Object> arrayList,
            DatabaseConfiguration dc) {

        String dataSource;
        String databaseName;

        if (dc == null) {
            databaseName = "";
            dataSource = "jdbc/default";
        } else {
            databaseName = dc.getDatabaseName();
            dataSource = dc.getDataSource();
        }

        // first replace database for sql statements
        multiplePreparedStatement =
                replace(multiplePreparedStatement, "{database}.", "`".concat(
                        databaseName).concat("`."));

        // replace other occurences
        multiplePreparedStatement =
                replace(multiplePreparedStatement, "{database}", databaseName);

        if (logger.isDebugEnabled())
            logger.debug("Resolved database, dataSource: [{" + dc.toStringF()
                    + "}, {" + dataSource + "}] ");

        return getMultipleExecute(multiplePreparedStatement, arrayList,
                dataSource);

    }

    /**
     * Execute the sql expressions, separated by semicolons used for update or
     * insert statements given the dataSource. <br />
     * Returns the results of the last select statement. <br />
     * Method ensures same connection is being used for all statements, so can
     * be used as a script executer with temporary tables etc.<br />
     * Method is transaction safe, if any statement fails, all transactions are
     * rolled back.
     * 
     * @param multiplePreparedStatement
     *            the multiple prepared statement
     * @param arrayList1
     *            the array list1
     * @param dataSource
     *            the dataSource
     * 
     * @return the multiple execute
     */
    public static LinkedHashMap<String, Object>[] getMultipleExecute(
            String multiplePreparedStatement, ArrayList<Object> arrayList1,
            String dataSource) {
        return getMultipleExecute(multiplePreparedStatement, arrayList1,
                dataSource, true);
    }

    /**
     * Execute the sql expressions, separated by semicolons used for update or
     * insert statements given the dataSource. <br />
     * Returns the results of the last select statement. <br />
     * Method ensures same connection is being used for all statements, so can
     * be used as a script executer with temporary tables etc. <br />
     * Method is transaction safe if rollback parameter is used on a traction
     * db, ie if any statement fails, all transactions are rolled back.
     * 
     * @param multiplePreparedStatement
     *            the multiple prepared statement
     * @param parameterList
     *            the array list1
     * @param dataSource
     *            the dataSource
     * @param rollback
     *            do a rollback if any statements fail
     * 
     * @return the multiple execute
     */
    @SuppressWarnings("unchecked")
    public static LinkedHashMap<String, Object>[] getMultipleExecute(
            String multiplePreparedStatement, ArrayList<Object> parameterList,
            String dataSource, boolean rollback) {

        // flag to track success
        // boolean flag = false;

        // the results of the last select statement
        LinkedHashMap<String, Object>[] result = null;

        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {

            try {

                con =
                        new DebuggableConnection(DataSourceCache.SINGLETON
                                .getDataSource(dataSource).getConnection());

            } catch (Exception e) {
                logger.fatal("Can't make connection for dataSource " + e);
                throw new RuntimeException(e);
            }

            if (con == null) {
                logger.fatal("Can't make connection for dataSource "
                        + "(Connection is null): " + dataSource);
                throw new RuntimeException("Connection is null for: "
                        + dataSource);
            }

            if (rollback)
                try {

                    // Disable auto-commit
                    con.setAutoCommit(false);

                } catch (SQLException sqle) {
                    logger.error("SQL statement: \n" + preparedStatement);
                    logger.error("SQLException disabling auto-commit: " + sqle);
                    logger
                            .error("Exception getSQLState: "
                                    + sqle.getSQLState());
                    throw new RuntimeException(sqle);

                }

            ArrayList<String> statementsList = new ArrayList<String>();

            // extract statements separated by semicolons
            while (multiplePreparedStatement.indexOf(";") != -1) {
                String statement =
                        multiplePreparedStatement.substring(0,
                                multiplePreparedStatement.indexOf(";")).trim();
                if (!statement.trim().equals("")) {
                    statementsList.add(statement);
                }
                multiplePreparedStatement =
                        multiplePreparedStatement.substring(
                                multiplePreparedStatement.indexOf(";") + 1,
                                multiplePreparedStatement.length());
            }

            Iterator i = null;
            if (parameterList != null)
                i = parameterList.iterator();
            for (int j = 0; j < statementsList.size(); j++) {

                preparedStatement = null;
                String statement = statementsList.get(j);

                try {
                    preparedStatement = con.prepareStatement(statement);
                } catch (SQLException sqlEx) {
                    logger.error("SQL statement: \n" + preparedStatement);
                    logger.error("SQLException: " + sqlEx);
                    logger.error("Exception getSQLState: "
                            + sqlEx.getSQLState());
                    throw new RuntimeException(sqlEx);
                }
                if (parameterList != null) {

                    int count = 0;

                    try {
                        int parameters =
                                preparedStatement.getParameterMetaData()
                                        .getParameterCount();
                        while (parameters >= count && parameters != 0
                                && i.hasNext()) {
                            Object value = i.next();
                            preparedStatement.setObject(++count, value);
                        }

                    } catch (SQLException sqlEx) {
                        logger.error("SQL statement: \n" + preparedStatement);
                        logger.error("SQLException: " + sqlEx);
                        logger.error("Exception getSQLState: "
                                + sqlEx.getSQLState());
                        throw new RuntimeException(sqlEx);
                    } catch (NullPointerException e) {
                        logger.error("SQL statement: \n" + preparedStatement);
                        logger.error("NullPointerException: " + e);
                        throw new RuntimeException(e);
                    }

                }

                try {

                    // is this the select statement?
                    if (statement.toLowerCase().trim().indexOf("select") != 0) {

                        preparedStatement.execute();

                    } else {

                        rs = preparedStatement.executeQuery();

                        result = ResultsetToHashMapArray.getHashMapArray(rs);

                    }

                } catch (NullPointerException e) {
                    logger.error("SQL statement: \n" + preparedStatement);
                    logger.error("NullPointerException: " + e);
                    if (rollback)
                        try {
                            logger.error("roll back!");
                            con.rollback();
                        } catch (SQLException sqlEx) {
                            logger.error("Cant roll back!");
                            throw new RuntimeException(sqlEx);
                        }
                    throw new RuntimeException(e);
                } catch (SQLException sqlEx) {
                    logger.error("SQL statement: \n" + preparedStatement);
                    logger.error("SQLException: " + sqlEx);
                    logger.error("Exception getSQLState: "
                            + sqlEx.getSQLState());
                    if (rollback)
                        try {
                            logger.error("roll back!");
                            con.rollback();
                        } catch (SQLException sqle) {
                            logger.error("Cant roll back!");
                            throw new RuntimeException(sqle);
                        }
                    throw new RuntimeException(sqlEx);
                } finally {
                    if (logger.isDebugEnabled())
                        logger.debug("Closing current statement");
                    if (preparedStatement != null) {
                        try {
                            preparedStatement.close();
                        } catch (SQLException sqlEx) {
                            logger.error("SQL statement: \n"
                                    + preparedStatement);
                            logger.error("SQLException: " + sqlEx);
                            logger.error("Exception getSQLState: "
                                    + sqlEx.getSQLState());
                        }

                        statement = null;
                    }
                }

            }// for multiple semicolon separated loop

            // Since there were no errors, commit
            if (rollback)
                try {
                    logger.info("Committing");
                    con.commit();
                } catch (SQLException sqlEx) {
                    logger.error("Cant commit");
                    throw new RuntimeException(sqlEx);
                }
        } finally {
            logger.info("Closing all statements");

            // it is a good idea to release
            // resources in a finally{} block
            // in reverse-order of their creation
            // if they are no-longer needed

            if (rs != null) {

                try {
                    rs.close();
                } catch (SQLException sqlEx) {
                    logger.error(sqlEx);
                }

                rs = null;
            }

            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException sqlEx) {
                    logger.error(sqlEx);
                }

                preparedStatement = null;
            }

            if (con != null) {
                try {
                    con.close();
                } catch (SQLException sqlEx) {
                    // ignore -- as we can't do anything about it here
                    logger.error(sqlEx);
                }

                con = null;
            }

        }

        return result;

    }

    /**
     * Process update counts.
     * 
     * @param updateCounts
     *            the update counts
     * @param arrayList
     *            the array list
     */
    public static void processUpdateCounts(int[] updateCounts,
            ArrayList<Object>[] arrayList) {

        for (int i = 0; i < updateCounts.length; i++) {
            if (updateCounts[i] >= 0) {
                // Successfully executed;
                // the number represents number of affected rows
            } else if (updateCounts[i] == Statement.SUCCESS_NO_INFO) {
                // Successfully executed;
                // number of affected rows not available
            } else if (updateCounts[i] == Statement.EXECUTE_FAILED) {
                // Failed to execute
                logger.error("Failed row: " + i);
                logger.error("arrayList values: " + arrayList[i]);

            }
        }
    }

    /**
     * Replace text.
     * 
     * @param source
     *            the original string
     * @param from
     *            the string to be replaced
     * @param to
     *            the string which will used to replace
     * 
     * @return a new String!
     */
    private static String replace(String source, String from, String to) {

        int start = source.indexOf(from);
        if (start == -1)
            return source;
        int lf = from.length();
        char[] targetChars = source.toCharArray();
        StringBuffer buffer = new StringBuffer();
        int copyFrom = 0;
        while (start != -1) {
            buffer.append(targetChars, copyFrom, start - copyFrom);
            buffer.append(to);
            copyFrom = start + lf;
            start = source.indexOf(from, copyFrom);
        }
        buffer.append(targetChars, copyFrom, targetChars.length - copyFrom);
        return buffer.toString();
    }

    /**
     * Excecute the the sql expression, used for update or insert statements
     * given the dataSource String "jdbc/default".
     * 
     * @param preparedStatement
     *            the prepared statement
     * @param arrayList
     *            the array list
     * @param dc
     *            the dc
     * 
     * @return true, if sets the excecute
     */
    public static boolean setExecute(String preparedStatement,
            ArrayList<Object> arrayList, DatabaseConfiguration dc) {

        String dataSource;
        String databaseName;

        if (dc == null) {
            databaseName = "";
            dataSource = "jdbc/default";
        } else {
            databaseName = dc.getDatabaseName();
            dataSource = dc.getDataSource();
        }

        // first replace database for sql statements
        preparedStatement =
                replace(preparedStatement, "{database}.", "`".concat(
                        databaseName).concat("`."));

        // replace other occurences
        preparedStatement =
                replace(preparedStatement, "{database}", databaseName);

        return setExecute(preparedStatement, arrayList, dataSource);

    }

    /**
     * Excecute the the sql expression, used for update or insert statements
     * given the dataSource. returns true if success/ false if failure
     * 
     * @param preparedStatement
     *            the prepared statement
     * @param arrayList
     *            the array list
     * @param dataSource
     *            the dataSource
     * 
     * @return true, if sets the excecute
     */
    public static boolean setExecute(String preparedStatement,
            ArrayList<Object> arrayList, String dataSource) {

        // flag to track success
        boolean flag = false;
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {

            con =
                    new DebuggableConnection(DataSourceCache.SINGLETON
                            .getDataSource(dataSource).getConnection());

        } catch (Exception e) {
            logger.fatal("Can't make connection for dataSource " + e);
            throw new RuntimeException(e);
        }

        if (con == null) {
            logger.fatal("Can't make connection for dataSource "
                    + "(Connection is null): " + dataSource);
            throw new RuntimeException("Connection is null for: " + dataSource);
        }

        try {
            statement = con.prepareStatement(preparedStatement);
        } catch (SQLException sqlEx) {
            logger.error("SQL statement: \n" + preparedStatement);
            logger.error("SQLException: " + sqlEx);
            logger.error("Exception getSQLState: " + sqlEx.getSQLState());
            throw new RuntimeException(sqlEx);
        }

        if (arrayList != null) {
            Object test = null;
            int count = 0;
            for (Iterator<Object> i = arrayList.iterator(); i.hasNext();) {

                test = i.next();

                try {

                    statement.setObject(++count, test);

                } catch (SQLException sqlEx) {
                    logger.error("SQL statement: \n" + preparedStatement);
                    logger.error("SQLException: " + sqlEx);
                    logger.error("Exception getSQLState: "
                            + sqlEx.getSQLState());
                    throw new RuntimeException(sqlEx);
                } catch (NullPointerException e) {
                    logger.error("SQL statement: \n" + preparedStatement);
                    logger.error("NullPointerException: " + e);
                    throw new RuntimeException(e);
                }

            }
        }

        try {

            statement.execute();

            flag = true;

        } catch (SQLException sqlEx) {
            logger.error("SQL statement: \n" + preparedStatement);
            logger.error("SQLException: " + sqlEx);
            logger.error("Exception getSQLState: " + sqlEx.getSQLState());
            throw new RuntimeException(sqlEx);
        } catch (NullPointerException e) {
            logger.error("SQL statement: \n" + preparedStatement);
            logger.error("NullPointerException: " + e);
            throw new RuntimeException(e);
        } finally {

            // it is a good idea to release
            // resources in a finally{} block
            // in reverse-order of their creation
            // if they are no-longer needed
            if (rs != null) {

                try {
                    rs.close();
                } catch (SQLException sqlEx) {
                    logger.error(sqlEx);
                }

                rs = null;
            }

            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException sqlEx) {
                    logger.error(sqlEx);
                }

                statement = null;
            }

            if (con != null) {
                try {
                    con.close();
                } catch (SQLException sqlEx) {
                    // ignore -- as we can't do anything about it here
                    logger.error(sqlEx);
                }

                con = null;
            }

        }

        return flag;

    }

    /**
     * Sets the excecute batch last insert.
     * 
     * @param preparedStatement
     *            the prepared statement
     * @param arrayList
     *            the array list
     * @param dc
     *            the dc
     * 
     * @return the linked hash map< string, object>[]
     */
    public static LinkedHashMap<String, Object>[] setExecuteBatchLastInsert(
            String preparedStatement, ArrayList<Object>[] arrayList,
            DatabaseConfiguration dc) {

        String dataSource;
        String databaseName;

        if (dc == null) {
            databaseName = "";
            dataSource = "jdbc/default";
        } else {
            databaseName = dc.getDatabaseName();
            dataSource = dc.getDataSource();
        }

        // first replace database for sql statements
        preparedStatement =
                replace(preparedStatement, "{database}.", "`".concat(
                        databaseName).concat("`."));

        // replace other occurences
        preparedStatement =
                replace(preparedStatement, "{database}", databaseName);

        return setExecuteBatchLastInsert(preparedStatement, arrayList,
                dataSource);

    }

    /**
     * Sets the excecute batch last insert.
     * 
     * @param templateStatement
     *            the template statement
     * @param arrayList
     *            the array list
     * @param dataSource
     *            the dataSource
     * 
     * @return the linked hash map< string, object>[]
     */
    public static LinkedHashMap<String, Object>[] setExecuteBatchLastInsert(
            String templateStatement, ArrayList<Object>[] arrayList,
            String dataSource) {

        Connection con = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        LinkedHashMap<String, Object>[] result = null;

        try {
            try {

                con =
                        new DebuggableConnection(DataSourceCache.SINGLETON
                                .getDataSource(dataSource).getConnection());

            } catch (Exception e) {
                logger.fatal("Can't make connection for dataSource " + e);
                throw new RuntimeException(e);
            }

            if (con == null) {
                logger.fatal("Can't make connection for dataSource "
                        + "(Connection is null): " + dataSource);
                throw new RuntimeException("Connection is null for: "
                        + dataSource);
            }

            try {

                // Disable auto-commit
                con.setAutoCommit(false);

            } catch (SQLException sqlEx) {
                logger.error("SQL statement: \n" + preparedStatement);
                logger.error("SQLException disabling auto-commit: " + sqlEx);
                logger.error("Exception getSQLState: " + sqlEx.getSQLState());
                throw new RuntimeException(sqlEx);

            }

            // Create a prepared statement
            try {
                preparedStatement =
                        con.prepareStatement(templateStatement,
                                Statement.RETURN_GENERATED_KEYS,
                                ResultSet.TYPE_FORWARD_ONLY);
            } catch (SQLException sqlEx) {
                logger.error("SQL statement: \n" + preparedStatement);
                logger.error("SQLException: " + sqlEx);
                logger.error("Exception getSQLState: " + sqlEx.getSQLState());
                throw new RuntimeException(sqlEx);
            }

            for (int j = 0; j < arrayList.length; j++) {

                if (arrayList[j] != null) {
                    Object test = null;
                    int count = 0;
                    for (Iterator<Object> i = arrayList[j].iterator(); i
                            .hasNext();) {

                        test = i.next();

                        try {

                            preparedStatement.setObject(++count, test);

                        } catch (SQLException sqlEx) {
                            logger.error("SQL statement: \n"
                                    + preparedStatement);
                            logger.error("SQLException: " + sqlEx);
                            logger.error("Exception getSQLState: "
                                    + sqlEx.getSQLState());
                            throw new RuntimeException(sqlEx);
                        } catch (NullPointerException e) {
                            logger.error("SQL statement: \n"
                                    + preparedStatement);
                            logger.error("NullPointerException: " + e);
                            throw new RuntimeException(e);
                        }

                    }
                }

                try {
                    preparedStatement.addBatch();
                } catch (SQLException sqlEx) {
                    logger.error("SQL statement: \n" + preparedStatement);
                    logger.error("SQLException: " + sqlEx);
                    logger.error("Exception getSQLState: "
                            + sqlEx.getSQLState());
                    throw new RuntimeException(sqlEx);
                }

            }

            try {

                // Execute the batch
                // int [] updateCounts =
                preparedStatement.executeBatch();

                // All statements were successfully executed.
                // updateCounts contains one element for each batched statement.
                // updateCounts[i] contains the number of rows affected by that
                // statement.
                // processUpdateCounts(updateCounts);

                rs = preparedStatement.getGeneratedKeys();

                result = ResultsetToHashMapArray.getHashMapArray(rs);

                // Since there were no errors, commit
                con.commit();

            } catch (ArrayIndexOutOfBoundsException e) {
                logger.error("SQL statement: \n" + preparedStatement);
                logger.error("ArrayIndexOutOfBoundsException: " + e);
                throw new RuntimeException(e);
            } catch (NullPointerException e) {
                logger.error("SQL statement: \n" + preparedStatement);
                logger.error("NullPointerException: " + e);
                throw new RuntimeException(e);
            } catch (BatchUpdateException e) {

                logger.error("SQL statement: \n" + preparedStatement);
                logger.error("Batch statements failed: " + e);
                logger.error("Exception getSQLState: " + e.getSQLState());

                // Not all of the statements were successfully executed
                int[] updateCounts = e.getUpdateCounts();

                // Some databases will continue to execute after one fails.
                // If so, updateCounts.length will equal
                // the number of batched statements.
                // If not, updateCounts.length will equal
                // the number of successfully executed statements
                processUpdateCounts(updateCounts, arrayList);

                // Either commit the successfully executed
                // statements or rollback the entire batch
                try {

                    logger.error("Failed batch initiating rollback");
                    con.rollback();

                } catch (SQLException sqlEx) {
                    logger.error("SQL statement: \n" + preparedStatement);
                    logger.error("Batch statements failed: " + sqlEx);
                    logger.error("Exception getSQLState: "
                            + sqlEx.getSQLState());
                    throw new RuntimeException(sqlEx);

                }

            } catch (SQLException sqlEx) {
                logger.error("SQL statement: \n" + preparedStatement);
                logger.error("Batch statements failed: " + sqlEx);
                logger.error("Exception getSQLState: " + sqlEx.getSQLState());
                throw new RuntimeException(sqlEx);
            }
        } finally {

            // it is a good idea to release
            // resources in a finally{} block
            // in reverse-order of their creation
            // if they are no-longer needed

            if (rs != null) {

                try {
                    rs.close();
                } catch (SQLException sqlEx) {
                    logger.error(sqlEx);
                }

                rs = null;
            }

            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException sqlEx) {
                    logger.error(sqlEx);
                }

                preparedStatement = null;
            }

            if (con != null) {
                try {
                    con.close();
                } catch (SQLException sqlEx) {
                    // ignore -- as we can't do anything about it here
                    logger.error(sqlEx);
                }

                con = null;
            }

        }

        return result;

    }

    /**
     * Sets the excecute last insert.
     * 
     * @param preparedStatement
     *            the prepared statement
     * @param arrayList
     *            the array list
     * @param dc
     *            the dc
     * 
     * @return the linked hash map< string, object>[]
     */
    public static LinkedHashMap<String, Object>[] setExecuteLastInsert(
            String preparedStatement, ArrayList<Object> arrayList,
            DatabaseConfiguration dc) {

        String dataSource;
        String databaseName;

        if (dc == null) {
            databaseName = "";
            dataSource = "jdbc/default";
        } else {
            databaseName = dc.getDatabaseName();
            dataSource = dc.getDataSource();
        }

        // first replace database for sql statements
        preparedStatement =
                replace(preparedStatement, "{database}.", "`".concat(
                        databaseName).concat("`."));

        // replace other occurences
        preparedStatement =
                replace(preparedStatement, "{database}", databaseName);

        return setExecuteLastInsert(preparedStatement, arrayList, dataSource);

    }

    /**
     * Get the HashMap[] representation of the ids added by an insert,
     * statement, HashMap[].get("GENERATED_KEYS), HashMap[].get("ROWS_AFFECTED")
     * 
     * @param preparedStatement
     *            the prepared statement
     * @param arrayList
     *            the array list
     * @param dataSource
     *            the dataSource
     * 
     * @return the linked hash map< string, object>[]
     */
    @SuppressWarnings("unchecked")
    public static LinkedHashMap<String, Object>[] setExecuteLastInsert(
            String preparedStatement, ArrayList<Object> arrayList,
            String dataSource) {

        int rowCount = -1;

        DebuggableConnection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        LinkedHashMap<String, Object>[] result = null;

        try {
            try {

                con =
                        new DebuggableConnection(DataSourceCache.SINGLETON
                                .getDataSource(dataSource).getConnection());

            } catch (Exception e) {
                logger.fatal("Can't make connection for dataSource " + e);
                throw new RuntimeException(e);
            }

            if (con == null) {
                logger.fatal("Can't make connection for dataSource "
                        + "(Connection is null): " + dataSource);
                throw new RuntimeException("Connection is null for: "
                        + dataSource);
            }

            try {
                statement =
                        con.prepareStatement(preparedStatement,
                                Statement.RETURN_GENERATED_KEYS);
            } catch (SQLException sqlEx) {
                logger.error("PrepareStatement: " + preparedStatement);
                logger.error("Exception getSQLState: " + sqlEx.getSQLState());
                throw new RuntimeException(sqlEx);
            }

            if (arrayList != null) {
                Object test = null;
                int count = 0;
                for (Iterator<Object> i = arrayList.iterator(); i.hasNext();) {

                    test = i.next();

                    try {

                        statement.setObject(++count, test);

                    } catch (SQLException sqlEx) {
                        logger.error("SQL statement: \n" + preparedStatement);
                        logger.error("SQLException: " + sqlEx);
                        logger.error("Exception getSQLState: "
                                + sqlEx.getSQLState());
                        throw new RuntimeException(sqlEx);
                    } catch (NullPointerException e) {
                        logger.error(e);
                        throw new RuntimeException(e);
                    }

                }
            }

            try {

                rowCount = statement.executeUpdate();

                rs = statement.getGeneratedKeys();

                result = ResultsetToHashMapArray.getHashMapArray(rs);

            } catch (SQLException sqlEx) {
                logger.error("SQL statement: \n" + preparedStatement);
                logger.error("SQLException: " + sqlEx);
                logger.error("Exception getSQLState: " + sqlEx.getSQLState());
                throw new RuntimeException(sqlEx);
            } catch (ArrayIndexOutOfBoundsException e) {
                logger.error("SQL statement: \n" + preparedStatement);
                logger.error("ArrayIndexOutOfBoundsException: " + e);
                throw new RuntimeException(e);
            } catch (NullPointerException e) {
                logger.error("SQL statement: \n" + preparedStatement);
                logger.error("NullPointerException: " + e);
                logger.warn("No generated key!");

                // although no generated key was returned
                // the rows affected variable can be
                // ROWS_AFFECTED=1 single insert
                // ROWS_AFFECTED=2 multiple inserts
                result = new LinkedHashMap[1];
                result[0] = new LinkedHashMap<String, Object>();

            }

        } finally {

            // it is a good idea to release
            // resources in a finally{} block
            // in reverse-order of their creation
            // if they are no-longer needed

            if (rs != null) {

                try {
                    rs.close();
                } catch (SQLException sqlEx) {
                    logger.error(sqlEx);
                }

                rs = null;
            }

            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException sqlEx) {
                    logger.error(sqlEx);
                }

                statement = null;
            }

            if (con != null) {
                try {
                    con.close();
                } catch (SQLException sqlEx) {
                    // ignore -- as we can't do anything about it here
                    logger.error(sqlEx);
                }

                con = null;
            }

        }

        // add rows updated
        if (result != null && result[0] != null)
            result[0].put("ROWS_AFFECTED", new Integer(rowCount));

        return result;

    }

}// class
