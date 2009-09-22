/**
 *
 * Copyright 2008-2009 Elements. All Rights Reserved.
 *
 * License version: CPAL 1.0
 *
 * The Original Code is mysimpledb.com code. Please visit mysimpledb.com to see how
 * you can contribute and improve this software.
 *
 * The contents of this file are licensed under the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *    http://mysimpledb.com/license.
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
 * Based on commercial needs the contents of this file may be used under the
 * terms of the Elements End-User License Agreement (the Elements License), in
 * which case the provisions of the Elements License are applicable instead of
 * those above.
 *
 * You may wish to allow use of your version of this file under the terms of
 * the Elements License please visit http://mysimpledb.com/license for details.
 *
 */
package ac.elements.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import ac.elements.sql.debug.DebuggableConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MigrateJDBC {

    /** The Constant log. */
    private final static Log logger = LogFactory.getLog(MigrateJDBC.class);

    /**
     * Get the resultSet for the sql expression and dataSource passed as a
     * parameter. It is a good idea to release resources in a finally{} block 
     * if they are no-longer needed. The below code demonstrates this.
     * 
     * <pre>
     *         finally {
     *             if (rs.getStatement().getConnection() != null) {
     *                 try {
     *                     rs.getStatement().getConnection().close();
     *                 } catch (SQLException sqlEx) {
     *                     // ignore -- as we can't do anything about it here
     *                     logger.error(sqlEx);
     *                 }
     * 
     *                 rs.getStatement().getConnection() = null;
     *             }
     * 
     *             if (rs.getStatement() != null) {
     *                 try {
     *                     rs.getStatement().close();
     *                 } catch (SQLException sqlEx) {
     *                     logger.error(sqlEx);
     *                 }
     * 
     *                 rs.getStatement() = null;
     *             }
     * 
     *             if (rs != null) {
     * 
     *                 try {
     *                     rs.close();
     *                 } catch (SQLException sqlEx) {
     *                     logger.error(sqlEx);
     *                 }
     * 
     *                 rs = null;
     *             }
     *         }
     * </pre>
     * 
     * @param preparedStatement
     *            the prepared statement
     * @param arrayList
     *            the array list
     * @param dataSource
     *            the dataSource
     * 
     * @return the ResultSet
     */
    public static ResultSet getResultSet(String preparedStatement,
            ArrayList<Object> arrayList, String dataSource) {

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
            
            statement = con.prepareStatement(preparedStatement,
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            statement.setFetchSize(Integer.MIN_VALUE);
//      
//            statement =
//                    con.prepareStatement(preparedStatement,
//                            ResultSet.TYPE_SCROLL_SENSITIVE,
//                            ResultSet.CONCUR_READ_ONLY);

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

        return rs;
        
    }
    
    
}
