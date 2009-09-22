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
