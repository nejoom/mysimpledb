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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

/**
 * ResultsetToHashMapArray is a static object that accepts resultSets and
 * converts them to {@link java.util.LinkedHashMap} arrays.<br>
 * <br>
 * Each array can be thought of as a row, containing a HashMap which maps each
 * "column name" (key) to the value of that "column name". (read
 * {@link java.util.HashMap}).
 * 
 * @author <a href="mailto:eddie@tinyelements.com">Eddie Moojen</a>
 * @version 1.51, 29/04/04
 * @since org.tinyelements.util v1.0
 */

public class ResultsetToHashMapArray {

    /** The Constant log. */
    private final static Logger logger =
        Logger.getLogger(ResultsetToHashMapArray.class);

    /**
     * Get the (Linked)HashMap representation of the resultset. <br>
     * Null objects from in the reultset are represented with null value.
     * 
     * @param resultSet
     *            the _resultset
     * 
     * @return the hash map array
     * 
     * @throws java.sql.SQLException
     *             Thrown if the <code>_resultset</code> forces any sql
     *             exceptions.
     * @throws java.lang.NullPointerException
     *             Thrown if the <code>_resultset</code> is null.
     * @throws SQLException
     *             the SQL exception
     * @throws NullPointerException
     *             the null pointer exception
     */
    @SuppressWarnings("unchecked")
    public static LinkedHashMap<String, Object>[] getHashMapArray(
            ResultSet resultSet) throws SQLException, NullPointerException {

        if (resultSet == null)
            return null;

        // initialize array
        resultSet.last();

        int numberOfRows = resultSet.getRow();
        if (numberOfRows == 0)
            return null;

        LinkedHashMap<String, Object>[] results =
                new LinkedHashMap[numberOfRows];

        // set up columnNames
        ResultSetMetaData _resultsetMega = resultSet.getMetaData();
        int numberOfColumns = _resultsetMega.getColumnCount();

        String[] columnNames = new String[numberOfColumns];

        // fill column string for later use
        for (int i = 1; i <= numberOfColumns; i++) {
            columnNames[i - 1] = _resultsetMega.getColumnLabel(i);
        }

        // setup resultset to first row
        resultSet.first();

        // initialize row counter
        int row = 0;

        // fill HashMap with put(columnName, data)
        do {

            // Get warnings on ResultSet object
            SQLWarning warning = resultSet.getWarnings();

            while (warning != null) {

                // Process connection warning
                // For information on these values, see e241 Handling
                // a SQL Exception
                String message = warning.getMessage();
                String sqlState = warning.getSQLState();
                int errorCode = warning.getErrorCode();

                logger.warn("message: " + message + "\nsqlState: " + sqlState
                        + "\nerrorCode :" + errorCode);

                warning = warning.getNextWarning();

            }

            results[row] = new LinkedHashMap<String, Object>();

            // loop through resultset columns
            for (int i = 0; i < numberOfColumns; i++) {

                Object myObject = null;
                try {

                    myObject = resultSet.getObject(columnNames[i]);
                    results[row].put(columnNames[i], myObject);

                } catch (NullPointerException e) {

                    // just use null
                    results[row].put(columnNames[i], null);

                    logger.error("Inserting null for column " + columnNames[i]
                            + ", row " + row);

                }
            }

            // set row count
            row++;

        } while (resultSet.next());// do while

        return results;

    }// getHashMap

    /*
     * testing
     */
    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        // String sql;
        // java.sql.Connection conn = null;
        // ResultSet rset = null;
        // java.sql.Statement stmt = null;
        // String url = "jdbc:mysql://localhost:3306/mysql";
        // String id = "root";
        // String pass = "";

        LinkedHashMap<String, String> lm = new LinkedHashMap<String, String>();
        // HashMap lm = new HashMap();

        lm.put("1", "one");
        lm.put("2", "two");
        lm.put("3", "three");
        lm.put("4", "four");
        lm.put("4", "one");
        lm.put("3", "two");
        lm.put("2", "three");
        lm.put("1", "four");

        lm.put("5", "five");
        lm.put("6", "six");
        lm.put("7", "seven");
        lm.put("8", "eight");
        lm.put("5", "five");
        lm.put("6", "six");
        lm.put("7", "seven");
        lm.put("8", "eight");
        lm.put("1", "one");
        lm.put("2", "two");
        lm.put("3", "three");
        lm.put("4", "four");

        HashMap<String, String> hm = lm;

        java.util.Iterator<String> myIt = hm.keySet().iterator();
        while (myIt.hasNext()) {
            Object myObject = myIt.next();
            System.out.print(myObject);
            System.out.println(lm.get(myObject));
        }
    }

    /**
     * Empty private constructor for ResultsetToHashMapArray(). Should use
     * static syntax.
     */
    private ResultsetToHashMapArray() {
    }

}// class

