package ac.elements.sdb;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.apache.catalina.Container;
import org.apache.catalina.Loader;
import org.apache.catalina.Session;
import org.apache.catalina.Store;
import org.apache.catalina.session.StandardSession;
import org.apache.catalina.session.StoreBase;
import org.apache.catalina.util.CustomObjectInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.codec.binary.Base64;

//import ac.elements.conf.Base64Decoder;
//import ac.elements.conf.Base64Encoder;
//import ac.elements.conf.Base64FormatException;
import ac.elements.conf.Configuration;

/**
 * Implementation of the <code>Store</code> interface that stores serialized
 * session objects in Amazon's SimpleDBDatabase. Sessions that are saved are
 * still subject to being expired based on inactivity.
 * 
 * @author Eddie Moojen
 * @version $Revision$, $Date$
 */

public class SimpleDBStore extends StoreBase implements Store {

    /** The descriptive information about this implementation. */
    protected static String info = "SimpleDBStore/1.0";

    /** The Constant log. */
    private final static Log log = LogFactory.getLog(SimpleDBStore.class);

    /** Name to register for this Store, used for logging. */
    protected static String storeName = "SimpleDBStore";

    /**
     * The Access Key ID is associated with your AWS account. You include it in
     * AWS service requests to identify yourself as the sender of the request.
     * 
     * The Access Key ID is not a secret, and anyone could use your Access Key
     * ID in requests to AWS.
     */
    private final String accessKeyId =
            Configuration.getInstance().getValue("aws", "AWSAccessKeyId");

    /** The Constant DEFAULT_ENCODING. */
    private static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * To provide proof that you truly are the sender of the request, you also
     * include a digital signature calculated using your Secret Access Key.
     */
    private final String secretAccessKey =
            Configuration.getInstance().getValue("aws", "SecretAccessKey");

    /** Context name associated with this Store. */
    private String name = null;

    private final SimpleDBCollection exampleDB =
            new SimpleDBCollection(accessKeyId, secretAccessKey);

    /** Column to use for /Engine/Host/Context name. */
    protected String sessionAppCol = "app";

    /** Data column to use. */
    protected String sessionDataCol = "data";

    /** Id column to use. */
    protected String sessionIdCol = "id";

    /** Last Accessed column to use. */
    protected String sessionLastAccessedCol = "lastaccess";

    /** Max Inactive column to use. */
    protected String sessionMaxInactiveCol = "maxinactive";

    /** Table to use. */
    protected String sessionTable = "tomcatSessions";

    /** Is Valid column to use. */
    protected String sessionValidCol = "valid";

    /** Name to register for the background thread. */
    protected String threadName = "SimpleDBStore";

    /**
     * Remove all of the Sessions in this Store.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * 
     * @exception IOException
     *                if an input/output error occurs
     */
    public void clear() throws IOException {

        synchronized (this) {
            String clearSql =
                    "DELETE FROM " + sessionTable + " WHERE " + sessionAppCol
                            + " = '" + getName() + "'";

            exampleDB.setDelete(clearSql);
            log.error("Cleared all sessions");
        }
    }

    /**
     * Return the info for this Store.
     * 
     * @return the info
     */
    public String getInfo() {
        return (info);
    }

    /**
     * Return the name for this instance (built from container name).
     * 
     * @return the name
     */
    public String getName() {
        if (name == null) {
            Container container = manager.getContainer();
            String contextName = container.getName();
            String hostName = "";
            String engineName = "";

            if (container.getParent() != null) {
                Container host = container.getParent();
                hostName = host.getName();
                if (host.getParent() != null) {
                    engineName = host.getParent().getName();
                }
            }
            name = "/" + engineName + "/" + hostName + contextName;
        }
        return name;
    }

    /**
     * Return an integer containing a count of all Sessions currently saved in
     * this Store. If there are no Sessions, <code>0</code> is returned.
     * 
     * @return the size
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * 
     * @exception IOException
     *                if an input/output error occurred
     */
    public int getSize() throws IOException {
        int size = 0;

        synchronized (this) {

            String keysSql =
                    "SELECT COUNT(*) FROM " + sessionTable + " WHERE `"
                            + sessionAppCol + "` = '"
                            + ExtendedFunctions.escapeSql(getName()) + "'";

            SimpleDBDataList result = exampleDB.getSelect(keysSql, null);

            size =
                    new Integer(
                            (String) result.get(0).get("Count").toArray()[0]);
        }

        log.error("getSize(): " + size);
        return (size);
    }

    //
    // /**
    // * Set the driver for this Store.
    // *
    // * @param driverName
    // * The new driver
    // */
    // public void setDriverName(String driverName) {
    // String oldDriverName = this.driverName;
    // this.driverName = driverName;
    // support
    // .firePropertyChange("driverName", oldDriverName,
    // this.driverName);
    // this.driverName = driverName;
    // }
    //
    // /**
    // * Return the driver for this Store.
    // */
    // public String getDriverName() {
    // return (this.driverName);
    // }
    //
    // /**
    // * Return the username to use to connect to the database.
    // *
    // */
    // public String getConnectionName() {
    // return connectionName;
    // }
    //
    // /**
    // * Set the username to use to connect to the database.
    // *
    // * @param connectionName
    // * Username
    // */
    // public void setConnectionName(String connectionName) {
    // this.connectionName = connectionName;
    // }
    //
    // /**
    // * Return the password to use to connect to the database.
    // *
    // */
    // public String getConnectionPassword() {
    // return connectionPassword;
    // }
    //
    // /**
    // * Set the password to use to connect to the database.
    // *
    // * @param connectionPassword
    // * User password
    // */
    // public void setConnectionPassword(String connectionPassword) {
    // this.connectionPassword = connectionPassword;
    // }
    //
    // /**
    // * Set the Connection URL for this Store.
    // *
    // * @param connectionURL
    // * The new Connection URL
    // */
    // public void setConnectionURL(String connectionURL) {
    // String oldConnString = this.connectionURL;
    // this.connectionURL = connectionURL;
    // support.firePropertyChange("connectionURL", oldConnString,
    // this.connectionURL);
    // }
    //
    // /**
    // * Return the Connection URL for this Store.
    // */
    // public String getConnectionURL() {
    // return (this.connectionURL);
    // }
    //
    // /**
    // * Set the table for this Store.
    // *
    // * @param sessionTable
    // * The new table
    // */
    // public void setSessionTable(String sessionTable) {
    // String oldSessionTable = this.sessionTable;
    // this.sessionTable = sessionTable;
    // support.firePropertyChange("sessionTable", oldSessionTable,
    // this.sessionTable);
    // }
    //
    // /**
    // * Return the table for this Store.
    // */
    // public String getSessionTable() {
    // return (this.sessionTable);
    // }
    //
    // /**
    // * Set the App column for the table.
    // *
    // * @param sessionAppCol
    // * the column name
    // */
    // public void setSessionAppCol(String sessionAppCol) {
    // String oldSessionAppCol = this.sessionAppCol;
    // this.sessionAppCol = sessionAppCol;
    // support.firePropertyChange("sessionAppCol", oldSessionAppCol,
    // this.sessionAppCol);
    // }
    //
    // /**
    // * Return the web application name column for the table.
    // */
    // public String getSessionAppCol() {
    // return (this.sessionAppCol);
    // }
    //
    // /**
    // * Set the Id column for the table.
    // *
    // * @param sessionIdCol
    // * the column name
    // */
    // public void setSessionIdCol(String sessionIdCol) {
    // String oldSessionIdCol = this.sessionIdCol;
    // this.sessionIdCol = sessionIdCol;
    // support.firePropertyChange("sessionIdCol", oldSessionIdCol,
    // this.sessionIdCol);
    // }
    //
    // /**
    // * Return the Id column for the table.
    // */
    // public String getSessionIdCol() {
    // return (this.sessionIdCol);
    // }
    //
    // /**
    // * Set the Data column for the table
    // *
    // * @param sessionDataCol
    // * the column name
    // */
    // public void setSessionDataCol(String sessionDataCol) {
    // String oldSessionDataCol = this.sessionDataCol;
    // this.sessionDataCol = sessionDataCol;
    // support.firePropertyChange("sessionDataCol", oldSessionDataCol,
    // this.sessionDataCol);
    // }
    //
    // /**
    // * Return the data column for the table
    // */
    // public String getSessionDataCol() {
    // return (this.sessionDataCol);
    // }
    //
    // /**
    // * Set the Is Valid column for the table
    // *
    // * @param sessionValidCol
    // * The column name
    // */
    // public void setSessionValidCol(String sessionValidCol) {
    // String oldSessionValidCol = this.sessionValidCol;
    // this.sessionValidCol = sessionValidCol;
    // support.firePropertyChange("sessionValidCol", oldSessionValidCol,
    // this.sessionValidCol);
    // }
    //
    // /**
    // * Return the Is Valid column
    // */
    // public String getSessionValidCol() {
    // return (this.sessionValidCol);
    // }
    //
    // /**
    // * Set the Max Inactive column for the table
    // *
    // * @param sessionMaxInactiveCol
    // * The column name
    // */
    // public void setSessionMaxInactiveCol(String sessionMaxInactiveCol) {
    // String oldSessionMaxInactiveCol = this.sessionMaxInactiveCol;
    // this.sessionMaxInactiveCol = sessionMaxInactiveCol;
    // support.firePropertyChange("sessionMaxInactiveCol",
    // oldSessionMaxInactiveCol, this.sessionMaxInactiveCol);
    // }
    //
    // /**
    // * Return the Max Inactive column
    // */
    // public String getSessionMaxInactiveCol() {
    // return (this.sessionMaxInactiveCol);
    // }
    //
    // /**
    // * Set the Last Accessed column for the table
    // *
    // * @param sessionLastAccessedCol
    // * The column name
    // */
    // public void setSessionLastAccessedCol(String sessionLastAccessedCol) {
    // String oldSessionLastAccessedCol = this.sessionLastAccessedCol;
    // this.sessionLastAccessedCol = sessionLastAccessedCol;
    // support.firePropertyChange("sessionLastAccessedCol",
    // oldSessionLastAccessedCol, this.sessionLastAccessedCol);
    // }
    //
    // /**
    // * Return the Last Accessed column
    // */
    // public String getSessionLastAccessedCol() {
    // return (this.sessionLastAccessedCol);
    // }
    //
    // --------------------------------------------------------- Public
    // Methods

    /**
     * Return the name for this Store, used for logging.
     * 
     * @return the store name
     */
    public String getStoreName() {
        return (storeName);
    }

    /**
     * Return the thread name for this Store.
     * 
     * @return the thread name
     */
    public String getThreadName() {
        return (threadName);
    }

    /**
     * Return an array containing the session identifiers of all Sessions
     * currently saved in this Store. If there are no such Sessions, a
     * zero-length array is returned.
     * 
     * @return the string[]
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * 
     * @exception IOException
     *                if an input/output error occurred
     */
    public String[] keys() throws IOException {

        String keys[] = null;
        synchronized (this) {

            String keysSql =
                    "SELECT " + sessionIdCol + " FROM " + sessionTable
                            + " WHERE `" + sessionAppCol + "` = '"
                            + ExtendedFunctions.escapeSql(getName()) + "'";

            SimpleDBDataList result = exampleDB.getSelect(keysSql, null);
            ArrayList<String> tmpkeys = new ArrayList<String>();

            for (int i = 0; i < result.size(); i++) {
                tmpkeys
                        .add((String) result.get(i).get(sessionIdCol).toArray()[0]);
            }
            keys = (String[]) tmpkeys.toArray(new String[tmpkeys.size()]);
        }

        log.error("Listing sessions found as saved: " + keys.length);
        String keyString = "";
        for (int i = 0; i < keys.length; i++) {
            keyString += keys[i] + ", ";
        }
        log.error(keyString);
        return (keys);
    }

    /**
     * Load the Session associated with the id <code>id</code>. If no such
     * session is found <code>null</code> is returned.
     * 
     * @param id
     *            a value of type <code>String</code>
     * 
     * @return the stored <code>Session</code>
     * 
     * @throws ClassNotFoundException
     *             the class not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * 
     * @exception ClassNotFoundException
     *                if an error occurs
     * @exception IOException
     *                if an input/output error occurred
     */
    public Session load(String id) throws ClassNotFoundException, IOException {

        Session[] activeSessions = manager.findSessions();
        log.error("Entering load with number of sessions: "
                + activeSessions.length);

        // no need to load an already existing session
        if (activeSessions != null && activeSessions.length > 0)
            for (int i = 0; i < activeSessions.length; i++) {

                log.error("looking at session: "
                        + activeSessions[i].getIdInternal());
                if (activeSessions[i].getIdInternal().equals(id)) {
                    log.error("returning active session: "
                            + activeSessions[i].getIdInternal());
                    return activeSessions[i];
                }
            }

        StandardSession session = null;
        Loader loader = null;
        ClassLoader classLoader = null;
        ObjectInputStream ois = null;
        BufferedInputStream bis = null;
        Container container = manager.getContainer();
        SimpleDBDataList result = new SimpleDBDataList();

        synchronized (this) {

            try {
                String keysSql =
                        "SELECT " + sessionIdCol + ", " + sessionDataCol
                                + " FROM " + sessionTable + " WHERE `"
                                + sessionIdCol + "` = '" + id + "' AND `"
                                + sessionAppCol + "` = '" + getName() + "'";
                log.error(keysSql);
                result = exampleDB.getSelect(keysSql, null);
            } catch (Exception e) {
                log.fatal("not loading: " + e);
            }

            if (result.size() == 0) {
                log.fatal("no session found");
                return null;
            }

            // Base64Decoder decoder =
            // new Base64Decoder((String) result.get(0)
            // .get(sessionDataCol).toArray()[0]);
            //
            // byte[] enc = null;
            // try {
            // enc = decoder.processBytes();
            // } catch (Base64FormatException e1) {
            // log.error("Bytes could not be decode");
            // e1.printStackTrace();
            // }

            byte[] enc =
                    Base64.decodeBase64(((String) result.get(0).get(
                            sessionDataCol).toArray()[0])
                            .getBytes(DEFAULT_ENCODING));

            if (enc != null) {
                log.error("Bytes to decode: " + enc.length);
            }

            bis = new BufferedInputStream(new ByteArrayInputStream(enc));

            if (container != null) {
                loader = container.getLoader();
            }
            if (loader != null) {
                classLoader = loader.getClassLoader();
            }
            if (classLoader != null) {
                ois = new CustomObjectInputStream(bis, classLoader);
            } else {
                ois = new ObjectInputStream(bis);
            }

            try {
                session = (StandardSession) manager.createEmptySession();
                session.readObjectData(ois);
                session.setManager(manager);
                log.error("loaded: " + id);
            } catch (Exception e) {
                session = null;
                e.printStackTrace();
                log.fatal("not readObjectData: " + e);
            }

        }

        return (session);
    }

    /**
     * Remove the Session with the specified session identifier from this Store,
     * if present. If no such Session is present, this method takes no action.
     * 
     * @param id
     *            Session identifier of the Session to be removed
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * 
     * @exception IOException
     *                if an input/output error occurs
     */
    public void remove(String id) throws IOException {

        synchronized (this) {
            String removeSql =
                    "DELETE FROM " + sessionTable + " WHERE " + sessionIdCol
                            + " = '" + id + "'  AND " + sessionAppCol + " = '"
                            + getName() + "'";

            exampleDB.setDelete(removeSql);
            log.error("removed: " + id);
        }

        // if (manager.getContainer().getLogger().isDebugEnabled()) {
        // manager.getContainer().getLogger().debug(sm.getString(getStoreName()
        // +
        // ".removing", id, sessionTable));
        // }
    }

    /**
     * Save a session to the Store.
     * 
     * @param session
     *            the session to be stored
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * 
     * @exception IOException
     *                if an input/output error occurs
     */
    public void save(Session session) throws IOException {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream bos = null;

        synchronized (this) {
            try {
                bos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(new BufferedOutputStream(bos));

                ((StandardSession) session).writeObjectData(oos);
                oos.close();
                oos = null;
                byte[] obs = bos.toByteArray();

                // Base64Encoder encoder = new Base64Encoder(obs);
                //
                // String encodedBytes = encoder.processString();

                String encodedBytes = new String(Base64.encodeBase64(obs));

                String saveSql =
                        "REPLACE INTO " + sessionTable + " (" + sessionIdCol
                                + ", " + sessionAppCol + ", " + sessionDataCol
                                + ", " + sessionValidCol + ", "
                                + sessionMaxInactiveCol + ", "
                                + sessionLastAccessedCol + ") VALUES "
                                + session.getIdInternal() + "('"
                                + session.getIdInternal() + "', '" + getName()
                                + "', '" + encodedBytes + "', '"
                                + (session.isValid() ? "1" : "0") + "', '"
                                + session.getMaxInactiveInterval() + "', '"
                                + session.getLastAccessedTime() + "')";

                exampleDB.setReplace(saveSql);

                log.fatal("saved: " + session.getIdInternal());

            } catch (Exception e) {
                log.fatal("not saving: " + e);
            }
        }

    }
}
