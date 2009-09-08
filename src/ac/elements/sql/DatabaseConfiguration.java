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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.WeakHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ac.elements.conf.Configuration;
import ac.elements.conf.TypeConverter;

/**
 * DatabaseConfiguration: object to get the database configuration given a
 * database name. <br>
 * 
 * @author <a href="mailto:eddie@tinyelements.com">Eddie Moojen</a>
 * @version 1.0, 30/04/02
 * @since org.tinyelements.util v1.51
 */
public class DatabaseConfiguration {

    // cached set of database names
    /** The cache. */
    private static WeakHashMap<String, DatabaseConfiguration> cache =
            new WeakHashMap<String, DatabaseConfiguration>();

    /** The Constant createObject. */
    private static final Object createObject = new Object();

    /** The Constant DATASOURCE_DATABASE_CONFIGURATION. */
    private static final String DATASOURCE_DATABASE_CONFIGURATION =
            TypeConverter.getString(Configuration.getInstance().getValue(
                    "tweaking",
                    "DatabaseConfiguration.DATASOURCE_DATABASE_CONFIGURATION"),
                    "jdbc/default");

    // the milliseconds this instance is expired after
    // five minutes = 5*60*1000
    /** The Constant expireTime. */
    private static final long expireTime =
            TypeConverter.getInt(Configuration.getInstance().getValue(
                    "tweaking", "DatabaseConfiguration.expireTime"),
                    5 * 60 * 1000);

    /** The Constant log. */
    private final static Log logger = LogFactory.getLog(DatabaseConfiguration.class);


    /**
     * Gets the by database name.
     * 
     * @param databaseName
     *            the database name
     * 
     * @return the by database name
     */
    public static DatabaseConfiguration getByDatabaseName(String databaseName) {

        long t0 = System.currentTimeMillis();

        DatabaseConfiguration cachedDatabaseConfiguration;
        synchronized (createObject) {
            cachedDatabaseConfiguration = cache.get(databaseName);
        }

        if (cachedDatabaseConfiguration != null
                && cachedDatabaseConfiguration.creationTimestamp + expireTime > System
                        .currentTimeMillis()) {
            return cachedDatabaseConfiguration;
        }

        synchronized (createObject) {

            DatabaseConfiguration dc = new DatabaseConfiguration();
            dc.setDatabaseName(databaseName);

            String dataSource = loadDataSource(databaseName);
            if (dataSource == null && cachedDatabaseConfiguration != null) {
                dataSource = cachedDatabaseConfiguration.getDataSource();
                logger
                        .error("Cant find dataSource, using cache,  databaseName: "
                                + databaseName + ", " + dataSource);
            }

            if (dataSource == null) {
                logger
                        .error("Cant find dataSource, using 'jdbc/default', databaseName: "
                                + databaseName);
                dataSource = "jdbc/default";
            }

            dc.setDataSource(dataSource);

            cache.put(databaseName, dc);

            long ms = System.currentTimeMillis() - t0;

            if (logger.isDebugEnabled()) {
                logger.debug("[" + ms
                        + "ms] Created DatabaseConfiguration for "
                        + databaseName);
                logger.debug(dc.toStringF());
            }
            return dc;
        }
    }

    /**
     * Gets the search string.
     * 
     * @return the search string
     */
    protected static String getSearchString() {

        return "";

    }

    /**
     * Load data source.
     * 
     * @param databaseName
     *            the database name
     * 
     * @return the string
     */
    private static String loadDataSource(String databaseName) {

        logger.info("loading datasource");
        String sql =
                Configuration.getInstance().getValue("sqlStatements",
                        "DatabaseConfiguration.loadDataSource");

        // used for making prepare statements
        ArrayList<Object> arrayList = new ArrayList<Object>();
        arrayList.add(databaseName);

        logger.info("Using: " + DATASOURCE_DATABASE_CONFIGURATION);

        HashMap<String, Object>[] resultArray =
                DatabaseStatementsManager.getHashMapArray(sql, arrayList,
                        DATASOURCE_DATABASE_CONFIGURATION);

        if (resultArray == null)
            return null;

        return (String) resultArray[0].get("dataSource");

    }

    /** The creation timestamp. */
    private final long creationTimestamp = System.currentTimeMillis();

    /** The internal database name. */
    private String internalDatabaseName;

    /** The internal data source. */
    private String internalDataSource;

    /**
     * Private constructor for DatabaseConfigurator(), use factory methods.
     */
    private DatabaseConfiguration() {
    }

    /**
     * Clear cache.
     * 
     * @param databaseName
     *            the database name
     */
    public void clearCache(String databaseName) {
        synchronized (createObject) {

            cache.remove(databaseName);

        }
    }

    /**
     * Gets the database name.
     * 
     * @return the databaseName
     */
    public String getDatabaseName() {
        return internalDatabaseName;
    }

    /**
     * Gets the data source.
     * 
     * @return the dataSource for the databaseName as a string.
     */
    public String getDataSource() {
        return internalDataSource;
    }

    /**
     * set the databaseName.
     * 
     * @param databaseName
     *            the database name
     */
    private void setDatabaseName(String databaseName) {
        internalDatabaseName = databaseName;
    }

    /**
     * set the dataSource for the databaseName.
     * 
     * @param dataSource
     *            the data source
     */
    private void setDataSource(String dataSource) {
        internalDataSource = dataSource;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        StringBuffer result = new StringBuffer();

        final String newLine = System.getProperty("line.separator");

        result.append(this.getClass().getName() + " Object {");

        result.append(newLine);

        result.append(" cache (static): ");

        Iterator<String> myIterator = cache.keySet().iterator();
        while (myIterator.hasNext()) {
            Object key = myIterator.next();
            result.append(key);
            result.append("=");
            result.append(cache.get(key).getDatabaseName());
            result.append(newLine);
        }
        result.append(newLine);

        result.append("DATASOURCE_DATABASE_CONFIGURATION");
        result.append("=");
        result.append(DATASOURCE_DATABASE_CONFIGURATION);
        result.append(newLine);

        result.append("}");
        return result.toString();

    }

    /**
     * To string f.
     * 
     * @return the string
     */
    public String toStringF() {

        StringBuffer result = new StringBuffer();

        final String newLine = System.getProperty("line.separator");

        result.append(this.getClass().getName() + " Object {");

        result.append(newLine);

        result.append(" internalDataSource: ");
        result.append(internalDataSource);
        result.append(internalDataSource);
        result.append(" internalDatabaseName: ");
        result.append(internalDatabaseName);
        result.append(newLine);

        result.append(" creationTimestamp (internal): ");
        result.append(creationTimestamp);
        result.append(newLine);

        result.append(" creationDate (formatted): ");
        result.append(new Date(creationTimestamp));
        result.append(newLine);

        result.append("DATASOURCE_DATABASE_CONFIGURATION");
        result.append("=");
        result.append(DATASOURCE_DATABASE_CONFIGURATION);
        result.append(newLine);

        result.append("}");
        return result.toString();

    }
}// class
