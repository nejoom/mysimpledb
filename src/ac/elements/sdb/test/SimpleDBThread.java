package ac.elements.sdb.test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ac.elements.parser.CsvReader;
import ac.elements.sdb.ASimpleDBCollection;
import ac.elements.sdb.SimpleDBMap;

public class SimpleDBThread implements Runnable {

    /** The Constant log. */
    private final static Log log = LogFactory.getLog(SimpleDBThread.class);

    private static volatile Thread thread;

    private String method;

    private ASimpleDBCollection simpleDBCollection;

    private SimpleDBThread() {
    }

    public void stop() {
        Thread moribund = thread;
        thread = null;
        moribund.interrupt();
    }

    public static void deleteAttributes(String domain, String item,
            SimpleDBMap sdbMap) {
        SimpleDBThread st = new SimpleDBThread();
        thread = new Thread(st);
        thread.start();
    }

    public void run() {

        if (method.equals("one")) {
            simpleDBCollection.deleteAttributes("domain", 
                    new SimpleDBMap());
        }
        stop();

    }
//
//    public void runMethod() {
//        String id = "aid";
//        String key = "akey";
//        SimpleDBThread st = new SimpleDBThread(id, key);
//        thread = new Thread(st);
//        thread.start();
//
//    }

}
