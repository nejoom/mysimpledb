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
package ac.elements.conf;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/*
 * Singleton to load configuration files.<br> <br> The getValue(String
 * categoryName, String key) method will return the value for the key.<br> <br>
 * The key will be sought for in the properties file named
 * propertiesFile.properties.<br> <br> The properties file should be located in
 * the class path, preferably the root class (/WEB-INF/classes in a java webapp.<br>
 * <br> If the properties file or key is not found the key will be looked for in
 * the database ot_properties.<br> <br> Any changes to the key value pair will
 * be reloaded after a default of 15 seconds for files, and a set database field
 * ('expire') for database variables.<br> <br> This class does the look up
 * process quite intelligently and is tuned for performance.
 * 
 * todo: Missing properties file throws error.
 * 
 * Caching of values through hashtable means memory should be considered but
 * performance measuremnt show it to be near or better than dynamic generation
 * of String value = "" + i, in for loop.<br> <br> Database structure follows:
 * <pre> CREATE DATABASE ot_properties; USE ot_properties; CREATE TABLE
 * GLOBAL_KEY_VALUES ( category_name CHAR(100) NOT NULL, key_name CHAR(255) NOT
 * NULL, value TEXT NOT NULL, expire INT UNSIGNED NOT NULL, PRIMARY
 * KEY(category_name, key_name) );
 * 
 * Column Name Column Type ----------- ----------- category_name CHAR(255)
 * key_name CHAR(255) value TEXT expire INT
 * 
 * INSERT INTO GLOBAL_KEY_VALUES VALUES ('tweaking',
 * 'AutoRefreshMap.get.requests', '100', 60000); </pre>
 */
// @SuppressWarnings("unchecked")
/**
 * The Class Configuration.
 * 
 * @author <a href="mailto:eddie@tinyelements.com">Eddie Moojen</a>
 */
public class Configuration implements FileChangeListener {

    // private String propertiesFile;

    // =
    // "Configuration"; //works in class path "/classes"
    // "org.tinyelements.conf/Configuration"; //works
    // "Configuration.properties"; //works in class path "/classes"
    // "org/tinyelements/conf/Configuration.properties"; //works
    //        
    // "C:\\development\\tinyelements.net\\src\\" +
    // "org\\tinyelements\\conf\\Configuration.properties"; //works by
    // automatically setting absolutePath marker

    /*
     * CREATE DATABASE ot_properties; USE ot_properties; CREATE TABLE
     * GLOBAL_KEY_VALUES ( category_name CHAR(100) NOT NULL, key_name CHAR(255)
     * NOT NULL, value TEXT NOT NULL, expire INT UNSIGNED NOT NULL, PRIMARY
     * KEY(category_name, key_name) );
     * 
     * Column Name Column Type ----------- ----------- category_name CHAR(255)
     * key_name CHAR(255) value TEXT expire INT
     * 
     * INSERT INTO GLOBAL_KEY_VALUES VALUES ('tweaking',
     * 'AutoRefreshMap.get.requests', '100', 60000);
     * 
     */

    /**
     * The Class Value.
     */
    static class Value {
        /** The expires on. */
        private final long expiresOn;

        // private long time;
        /** The value. */
        private final String value;

        /**
         * Instantiates a new value.
         * 
         * @param value
         *            the value
         * @param expire
         *            the expire
         */
        Value(String value, long expire) {
            /*
             * Do not allow rapid reloads on database. One must wait at least 15
             * secs
             */
            if (expire < 15000)
                expire = 15000;

            this.value = value;
            this.expiresOn = System.currentTimeMillis() + expire;
        }

        /**
         * Checks for expired.
         * 
         * @return true, if successful
         */
        boolean hasExpired() {
            if (System.currentTimeMillis() > expiresOn) {
                return true;
            } else {
                return false;
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return value;
        }
    }

    /* default is one hour before we check back at database */
    // private static final long DEFAULT_DB_EXPIRE_TIME_MS = 600000;
    /* The singleton instance */
    /** The Constant _instance. */
    static final private Configuration _instance = new Configuration();

    /* used for mapping lastAccessTime for database */
    // private HashMap lastAccessTime = new HashMap();
    /* default properties reload for files */
    /** The Constant DEFAULT_FILE_EXPIRE_TIME_MS. */
    private static final long DEFAULT_FILE_EXPIRE_TIME_MS = 15000;

    /** The Constant log. */
    private final static Log log = LogFactory.getLog(Configuration.class);

    /** The Constant SUFFIX. */
    private static final String SUFFIX = ".properties";

    // private static long lastReload = 0;

    /**
     * Gets the instance.
     * 
     * @return The unique instance of this class.
     */
    static public synchronized Configuration getInstance() {
        return _instance;
    }

    // private static final
    // String PROPERTIES_CONFIGURATION_DATABASE =
    // TypeConverter.getString(
    // Configuration.getInstance().
    // getValue("tweaking",
    // "Configuration.PROPERTIES_CONFIGURATION_DATABASE")
    // , "ot_properties"
    // );

    /**
     * Gets the list.
     * 
     * @param list
     *            the list
     * 
     * @return the list
     */
    private static ArrayList<String> getList(String list) {

        String[] result = list.split("\n");

        ArrayList<String> al =
                new ArrayList<String>((int) (result.length * 1.10d));

        for (int i = 0; i < result.length; i++) {
            al.add(result[i].trim());
        }
        return al;

    }

    /* used for mapping categoryName to properties file */
    /** The prop file table. */
    private final HashMap<String, String> propFileTable =
            new HashMap<String, String>();

    /* used for mapping categoryName to properties table */
    /** The prop table. */
    private final HashMap<String, ExtendedProperties> propTable =
            new HashMap<String, ExtendedProperties>();

    /**
     * Instantiates a new configuration.
     */
    private Configuration() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see glowaxes.util.FileChangeListener#fileChanged(java.lang.String)
     */
    public void fileChanged(String fileName) {

        String _originalFile = propFileTable.get(fileName).toString();

        // log.info("fileChanged issued for file [" +
        // fileName + "]");

        reloadPropertiesFile(_originalFile);

    }

    /*
     * Get a ArrayList of values. <p> The the values of the ArrayList be sought
     * for in the properties file named propertiesFile.properties. <p> The
     * properties file should be located in the class path, preferably the root
     * class (/WEB-INF/classes in a java webapp. <p> Values should be separated
     * by a new line. <p> Any changes to the values of the list will be reloaded
     * after a default of 15 seconds for files.
     */
    /**
     * Gets the array list.
     * 
     * @param propertiesFile
     *            the properties file
     * @param key
     *            the key
     * 
     * @return the array list
     */
    public ArrayList<String> getArrayList(String propertiesFile, String key) {

        if (propertiesFile == null || key == null)
            return null;

        String list = getValue(propertiesFile, key);

        if (list != null) {
            return getList(list);
        } else {
            log.error("Cant load arrayList: " + propertiesFile + ":" + key);
            return null;

        }
    }

    /**
     * Internalized routines.
     * 
     * @param fileName
     *            the file name
     * 
     * @return the file name
     */
    private String getFileName(String fileName) {

        // windows absolute path
        if (fileName.indexOf("\\") != -1) {

            return fileName;

        }

        // Unix absolute path
        if (fileName.startsWith("/")) {

            return fileName;

        }

        // java style reference
        // org.tinyelements.conf/Configuration.properties
        if ((fileName.indexOf(".") < fileName.indexOf("/")
                && fileName.indexOf("/") != -1 && fileName.indexOf(".") != -1)) {

            fileName = fileName.replace('.', '/');

        }

        // type org.tinyelements.conf.Configuration
        if (fileName.indexOf(".") != fileName.lastIndexOf(".")) {

            fileName = fileName.replace('.', '/');

        }

        if (!fileName.endsWith(SUFFIX))
            fileName = fileName.concat(SUFFIX);

        return fileName;

    }

    /*
     * Get the value key pairs for the propertiesFile as a hashtable. <p> The
     * the value key pairs will be sought for in the properties file named
     * propertiesFile.properties. <p> The properties file should be located in
     * the class path, preferably the root class (/WEB-INF/classes in a java
     * webapp. <p> Any changes to the key value pair will be reloaded after a
     * default of 15 seconds for files.
     */
    /**
     * Gets the hashtable.
     * 
     * @param propertiesFile
     *            the properties file
     * 
     * @return the hashtable
     */
    public Hashtable<Object, Object> getHashtable(String propertiesFile) {

        if (propertiesFile == null)
            return null;

        Hashtable<Object, Object> tempTable = propTable.get(propertiesFile);

        if (tempTable == null) {

            reloadPropertiesFile(propertiesFile);

            tempTable = propTable.get(propertiesFile);

        }

        if (tempTable != null) {

            return tempTable;

        } else {
            log.error("Cant load hashtable: " + propertiesFile);
            return null;

        }
    }

    /*
     * Get the value for the specified key. <br> The key will be sought for in
     * the properties file named propertiesFile.properties.<br> <br> The
     * properties file should be located in the class path, preferably the root
     * class (/WEB-INF/classes in a java webapp.<br> <br> If the properties
     * file *or* key is not found the key will be looked for in the
     * ot_properties database.<br> <br> Any changes to the key value pair will
     * be reloaded after a default of 15 seconds for files, and a set database
     * field ('expire') for database variables.<br>
     */
    /**
     * Gets the value.
     * 
     * @param propertiesFile
     *            the properties file
     * @param key
     *            the key
     * 
     * @return the value
     */
    public String getValue(String propertiesFile, String key) {

        if (key == null) {
            log.warn("Returning null for null key in " + propertiesFile);
            return null;
        }

        Hashtable<Object, Object> tempTable = propTable.get(propertiesFile);

        if (tempTable == null) {

            reloadPropertiesFile(propertiesFile);

            tempTable = propTable.get(propertiesFile);

        }

        Object tempVal = null;

        if (tempTable != null && tempTable.get(key) != null) {

            tempVal = tempTable.get(key);

            /*
             * if this is a database variable then check if its still ok (valid
             * till millis)
             */
            if (tempVal instanceof Value && ((Value) tempVal).hasExpired()) {
                // if (log.isDebugEnabled())
                // log.debug("Looping returning value for db");

                // invalid value so: remove it
                tempTable.remove(key);

                // do this routine again
                // ie check properties or get it from database
                return this.getValue(propertiesFile, key);

            }
            // if (log.isDebugEnabled())
            // log.debug("Returning property [" +
            // key + "] to [" + tempVal +"]");

        }

        if (tempVal == null)
            log.error("Returning null for: " + propertiesFile + ", " + key);
        else
            tempVal = tempVal.toString().trim();

        if (tempVal == null || tempVal.equals(""))
            return null;

        return ((String) tempVal);
    }

    /**
     * Gets the xml.
     * 
     * @param propertiesFile
     *            the properties file
     * 
     * @return the xml
     */
    public Element getXml(String propertiesFile) {

        String tempFile = propertiesFile;
        InputStream in = null;
        Element element = null;

        try {

            boolean absolutePath = isAbsolutePath(propertiesFile);

            if (!absolutePath) {

                URL fileURL =
                        this.getClass().getClassLoader().getResource(tempFile);

                if (fileURL != null)
                    in = new FileInputStream(fileURL.getFile());
                else {
                    log.error("Cant load (in=null): " + tempFile);
                    throw new RuntimeException("Cant load (in=null): "
                            + tempFile);
                }

            } else {

                in = new FileInputStream(tempFile);

            }

            // Turn off validation
            SAXBuilder builder = new SAXBuilder(false);
            builder.setIgnoringBoundaryWhitespace(true);
            builder.setIgnoringElementContentWhitespace(true);

            // command line should offer URIs or file names
            try {
                element = (Element) builder.build(in).getRootElement().detach();
            }
            // indicates a well-formedness or validity error
            catch (JDOMException e) {
                System.out.println(propertiesFile + " is not valid.");
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println("Could not check " + propertiesFile);
                System.out.println(" because " + e.getMessage());
            }

            in.close();

        } catch (IOException e) {

            log.error(e + ": IOException");

        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                log.error(e + ": can't close stream");
            }
        }

        return element;

    }

    /**
     * Checks if is absolute path.
     * 
     * @param fileName
     *            the file name
     * 
     * @return true, if is absolute path
     */
    private boolean isAbsolutePath(String fileName) {
        // windows absolute path
        if (fileName.indexOf("\\") != -1) {

            return true;

        }

        // Unix absolute path
        if (fileName.startsWith("/")) {

            return true;

        }

        return false;
    }

    /**
     * Reload properties file.
     * 
     * @param _propertiesFile
     *            the _properties file
     */
    private void reloadPropertiesFile(String _propertiesFile) {

        ExtendedProperties keyValueTable = new ExtendedProperties();
        InputStream in = null;

        // _propertiesFile =
        // "C:\\development\\glowaxes\\glowaxes.properties";
        String tempFile = _propertiesFile;
        try {

            tempFile = getFileName(_propertiesFile);
            propFileTable.put(tempFile, _propertiesFile);

            boolean absolutePath = isAbsolutePath(_propertiesFile);

            if (!absolutePath) {

                URL fileURL =
                        this.getClass().getClassLoader().getResource(tempFile);

                if (fileURL != null)
                    in = new FileInputStream(fileURL.getFile());
                else {
                    log.error("Cant load (in=null): " + tempFile);
                    return;
                }

                /*
                 * dirty Unix seems to cache its resource streams so this wont
                 * work on unix... one must load as file
                 */
                // in =
                // this.getClass().getClassLoader().getResourceAsStream(tempFile);
            } else {

                in = new FileInputStream(tempFile);
                // System.out.println(in.);

            }

            try {

                keyValueTable.load(in);

                // if(log.isDebugEnabled())
                // log.debug("Loaded: [" +
                // _propertiesFile + "], [" + keyValueTable +"]");

            } catch (NullPointerException npe) {
                log.error("Cant load: [" + tempFile + "], " + npe);
                return;
            }

            in.close();

        } catch (IOException e) {

            log.error(e + ": IOException");

        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                log.error(e + ": can't close stream");
            }
        }

        if (keyValueTable != null)
            propTable.put(_propertiesFile, keyValueTable);
        else {
            // check file in a minute
            setCheckPeriodFile(60000, tempFile);
            return;
        }

        String period = (String) keyValueTable.get("checkPeriodMs");
        long longPeriod;

        if (period instanceof String)
            try {
                longPeriod = Long.parseLong(period.trim());
            } catch (NumberFormatException e) {
                longPeriod = DEFAULT_FILE_EXPIRE_TIME_MS;
                // log.warn("checkPeriodMs: unrecognized format, " +
                // "setting to default " + DEFAULT_FILE_EXPIRE_TIME_MS +
                // "[ms]");
            }
        else
            longPeriod = DEFAULT_FILE_EXPIRE_TIME_MS;

        setCheckPeriodFile(longPeriod, tempFile);

    }

    // private synchronized void setLastAccessTime(
    // String categoryName,
    // String key,
    // long expire) {
    //
    // if (log.isDebugEnabled())
    // log.debug("setLastAccessTime: [" +
    // categoryName + "], [" + key + "] expires in " +
    // expire + "[ms]");
    //
    // HashMap hm = new HashMap();
    // hm.put("expire", new Long(expire));
    // hm.put("lastAccessTime",
    // new Long(System.currentTimeMillis()));
    //        
    // lastAccessTime.put(categoryName + key, hm);
    //
    // }

    /**
     * Sets the check period file.
     * 
     * @param period
     *            the period
     * @param _propertiesFile
     *            the _properties file
     */
    private synchronized void setCheckPeriodFile(long period,
            String _propertiesFile) {

        // if (log.isDebugEnabled())
        // log.debug("Checking: [" + _propertiesFile +
        // "] in " + period + "[ms]");

        try {

            FileMonitor.getInstance().addFileChangeListener(
                    Configuration.getInstance(), _propertiesFile, period);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
