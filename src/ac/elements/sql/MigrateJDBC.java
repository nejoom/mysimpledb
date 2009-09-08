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
