package ac.elements.sql;

/*
 * Copyright 1999-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.jsp.jstl.sql.Result;

/**
 * <p>
 * This class creates a cached version of a <tt>ResultSet</tt>. It's
 * represented as a <tt>Result</tt> implementation, capable of returing an
 * array of <tt>Row</tt> objects containing a <tt>Column</tt> instance for
 * each column in the row. It is not part of the JSTL API; it serves merely as a
 * back-end to ResultSupport's static methods. Thus, we scope its access to the
 * file://localhost/Users/eddie/Downloads/jakarta-taglibs-standard-1.1.2-src/standard/src/javax/servlet/jsp/jstl/sql/SQLExecutionTag.javapackage.
 * 
 * @author Hans Bergsten
 * @author Justyna Horwat
 */

public class ResultImpl implements Result, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    private final List rowMap;

    @SuppressWarnings("unchecked")
    private final List rowByIndex;

    private final String[] columnNames;

    private final boolean isLimited = false;

    private final String id;

    private final String legend;

    /**
     * This constructor reads the hashMapArray and saves a cached copy. It's
     * important to note that this object will be serializable only if the
     * objects returned by the hashMapArray are serializable too.
     * 
     */
    @SuppressWarnings("unchecked")
    public ResultImpl(HashMap[] hashMapArray, String id, String legend) {

        if (hashMapArray == null)
            throw new IllegalArgumentException("hashmap array cannot be null");
        if (hashMapArray[0] == null)
            throw new IllegalArgumentException(
                    "hashmap array [0] cannot be null");

        this.id = id;
        this.legend = legend;
        rowMap = new ArrayList();
        rowByIndex = new ArrayList();

        Set keys = hashMapArray[0].keySet();
        int noOfColumns = hashMapArray[0].keySet().size();

        // Create the column name array
        columnNames = new String[noOfColumns];

        int row = 0;
        for (Iterator j = keys.iterator(); j.hasNext();)
            columnNames[row++] = (String) j.next();

        // Process the remaining rows upto maxRows
        int processedRows = 0;
        while (processedRows < hashMapArray.length) {

            Object[] columns = new Object[noOfColumns];
            SortedMap columnMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);

            // JDBC uses 1 as the lowest index!
            for (int i = 0; i < noOfColumns; i++) {
                columns[i] = hashMapArray[processedRows].get(columnNames[i]);
                columnMap.put(columnNames[i], columns[i]);
            }
            rowMap.add(columnMap);
            rowByIndex.add(columns);
            processedRows++;
        }
    }

    /**
     */
    public String getId() {
        return id;
    }

    /**
     */
    public String getLegend() {
        return legend;
    }

    /**
     * Returns an array of SortedMap objects. The SortedMap object key is the
     * ColumnName and the value is the ColumnValue. SortedMap was created using
     * the CASE_INSENSITIVE_ORDER Comparator so the key is the case insensitive
     * representation of the ColumnName.
     * 
     * @return an array of Map, or null if there are no rows
     */
    @SuppressWarnings( { "unchecked", "unchecked" })
    public SortedMap[] getRows() {
        if (rowMap == null) {
            return null;
        }

        // should just be able to return SortedMap[] object
        return (SortedMap[]) rowMap.toArray(new SortedMap[0]);
    }

    /**
     * Returns an array of SortedMap objects. The SortedMap object key is the
     * ColumnName and the value is the ColumnValue. SortedMap was created using
     * the CASE_INSENSITIVE_ORDER Comparator so the key is the case insensitive
     * representation of the ColumnName.
     * 
     * @return an array of Map, or null if there are no rows
     */
    @SuppressWarnings( { "unchecked", "unchecked" })
    public ArrayList getRowsAsList() {
        if (rowMap == null) {
            return null;
        }

        // should just be able to return SortedMap[] object
        return (ArrayList) rowMap;
    }

    /**
     * Returns an array of Object[] objects. The first index designates the Row,
     * the second the Column. The array stores the value at the specified row
     * and column.
     * 
     * @return an array of Object[], or null if there are no rows
     */
    @SuppressWarnings("unchecked")
    public Object[][] getRowsByIndex() {
        if (rowByIndex == null) {
            return null;
        }

        // should just be able to return Object[][] object
        return (Object[][]) rowByIndex.toArray(new Object[0][0]);
    }

    /**
     * Returns an array of String objects. The array represents the names of the
     * columns arranged in the same order as in the getRowsByIndex() method.
     * 
     * @return an array of String[]
     */
    public String[] getColumnNames() {
        return columnNames;
    }

    /**
     * Returns the number of rows in the cached ResultSet
     * 
     * @return the number of cached rows, or -1 if the Result could not be
     *         initialized due to SQLExceptions
     */
    public int getRowCount() {
        if (rowMap == null) {
            return -1;
        }
        return rowMap.size();
    }

    /**
     * Returns true if the query was limited by a maximum row setting
     * 
     * @return true if the query was limited by a MaxRows attribute
     */
    public boolean isLimitedByMaxRows() {
        return isLimited;
    }

}
