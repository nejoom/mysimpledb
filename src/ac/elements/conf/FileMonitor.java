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
package ac.elements.conf;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Date;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

/**
 * Objects that implement the FileChangeListener can register themselves with
 * the FileMonitor singleton to get notified of changes in the fileChanged
 * method.
 * 
 * @author <a href="mailto:eddie@tinyelements.com">Eddie Moojen</a>
 */
public class FileMonitor {

    /**
     * Inner class to do the task of monitoring. created with the
     * addFileChangeListener method The Class FileMonitorTask.
     */
    class FileMonitorTask extends TimerTask {

        /** The file name. */
        String fileName;

        /** The last modified. */
        long lastModified;

        /** The listener. */
        FileChangeListener listener;

        /** The monitored file. */
        File monitoredFile;

        /**
         * Instantiates a new file monitor task.
         * 
         * @param listener
         *            the listener
         * @param fileName
         *            the file name
         * 
         * @throws FileNotFoundException
         *             the file not found exception
         */
        public FileMonitorTask(FileChangeListener listener, String fileName)
                throws FileNotFoundException {

            this.listener = listener;
            this.fileName = fileName;
            this.lastModified = 0;

            monitoredFile = new File(fileName);

            if (!monitoredFile.exists()) {

                // but is it on CLASSPATH?
                URL fileURL =
                        listener.getClass().getClassLoader().getResource(
                                fileName);

                if (fileURL != null)
                    monitoredFile = new File(fileURL.getFile());

                else
                    throw new FileNotFoundException("File Not Found: "
                            .concat(fileName));

            }
            this.lastModified = monitoredFile.lastModified();

            if (log.isDebugEnabled())
                log.debug("Starting monitor for file: " + fileName);
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.TimerTask#run()
         */
        public void run() {

            long lastModified = monitoredFile.lastModified();

            if (log.isDebugEnabled())
                log.debug("Checking file: " + this.fileName
                        + ", lastModified(): " + new Date(lastModified));

            if (lastModified != this.lastModified) {

                this.lastModified = lastModified;
                fireFileChangeEvent(this.listener, this.fileName);

            }
        }// end run
    }// end inner class

    /** The Constant instance. */
    private static final FileMonitor instance = new FileMonitor();

    /** The Constant log. */
    private final static Logger log = Logger.getLogger(FileMonitor.class);

    /**
     * Gets the single instance of FileMonitor.
     * 
     * @return single instance of FileMonitor
     */
    public static synchronized FileMonitor getInstance() {

        return instance;

    }

    /** The timer. */
    private Timer timer;

    /** The timer entries. */
    private Hashtable<String, FileMonitorTask> timerEntries;

    /**
     * Instantiates a new file monitor.
     */
    private FileMonitor() {

        // Create timer, run timer thread as daemon.
        timer = new Timer(true);
        timerEntries = new Hashtable<String, FileMonitorTask>();

    }

    /**
     * Add a monitored file with a FileChangeListener.
     * 
     * @param listener
     *            listener to notify when the file changed.
     * @param fileName
     *            name of the file to monitor.
     * @param period
     *            polling period in milliseconds.
     * 
     * @throws FileNotFoundException
     *             the file not found exception
     */
    public void addFileChangeListener(FileChangeListener listener,
            String fileName, long period) throws FileNotFoundException {

        removeFileChangeListener(listener, fileName);

        if (log.isDebugEnabled())
            log.debug("Registering listener: " + listener.getClass().getName()
                    + ", " + fileName);

        FileMonitorTask task = new FileMonitorTask(listener, fileName);
        timerEntries.put(fileName + listener.hashCode(), task);
        timer.schedule(task, period, period);

    }

    /**
     * Notify the of data changed.
     * 
     * @param listener
     *            the listener to be notified.
     * @param fileName
     *            the file name notified.
     */
    protected void fireFileChangeEvent(FileChangeListener listener,
            String fileName) {

        if (log.isDebugEnabled())
            log.debug("Firing event for file: " + fileName);

        listener.fileChanged(fileName);

    }

    /**
     * Remove the listener from the notification list.
     * 
     * @param listener
     *            the listener to be removed.
     * @param fileName
     *            the file name
     */
    public void removeFileChangeListener(FileChangeListener listener,
            String fileName) {

        FileMonitorTask task =
                (FileMonitorTask) timerEntries.remove(fileName
                        + listener.hashCode());

        if (task != null)
            task.cancel();

    }
}
