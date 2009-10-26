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
package ac.elements.concurrency;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ac.elements.parser.SimpleDBParser;

public class ThreadPoolExecutorFactory {

    private static ConcurrentHashMap<String, ThreadPoolExecutor> threadPoolExecutors =
            new ConcurrentHashMap<String, ThreadPoolExecutor>();

    /** The Constant log. */
    private final static Log log =
            LogFactory.getLog(ThreadPoolExecutorFactory.class);

    private static final int CAPACITY = 1000;

    private static ThreadPoolExecutor getExcecutor(String key, int N_THREADS) {
        ThreadPoolExecutor executor = threadPoolExecutors.get(key);
        if (executor == null) {
            return createExecutor(key, N_THREADS);
        }
        return executor;
    }

    private static ThreadPoolExecutor createExecutor(String key, int N_THREADS) {

        LinkedBlockingQueue<Runnable> lbq =
                new LinkedBlockingQueue<Runnable>(CAPACITY);

        /*
         * http://book.javanb.com/java-concurrency-in-Practice/ch08lev1sec3.html
         * 
         * The caller-runs policy implements a form of throttling that neither
         * discards tasks nor throws an exception, but instead tries to slow
         * down the flow of new tasks by pushing some of the work back to the
         * caller.
         * 
         * As the server becomes overloaded, the overload is gradually pushed
         * outward from the pool threads to the work queue to the application to
         * the Source (eg. TCP layer/ OS layer), and eventually to the client
         * enabling more graceful degradation under load.
         */
        ThreadPoolExecutor executor =
                new ThreadPoolExecutor(N_THREADS, N_THREADS, 0L,
                        TimeUnit.MILLISECONDS, lbq);
        threadPoolExecutors.put(key, executor);
        return executor;
    }

    public static ThreadPoolExecutor getExecuter(String sql) {
        // parse the sql to set up the key which maps to the correct executer

        // /*
        // * BatchPutAttributes -> domain.batchput
        // * PutAttributes -> domain.put
        // * Select -> domain.select
        // * DeleteAttributes -> domain.delete
        // * GetAttributes
        // * ListDomains
        // * CreateDomain -> domain.create
        // * DeleteDomain
        // * DomainMetadata
        // */

        // first find the domain
        String domain = SimpleDBParser.getDomain(sql);
        String key = domain;

//        log.error("Creating thread for domain: " + key);

        // there are x for thread pools for each domain, figure out which one to
        // get
        if (SimpleDBParser.getOperation(sql).equals("INSERT")
                || SimpleDBParser.getOperation(sql).equals("REPLACE")) {

            if (SimpleDBParser.isBatchOperation(sql)) {
                // is it a batch put operation? get a thread pool with 2
                // concurrent threads, this can actually be more thread, but 2
                // is optimal for a batch of 25 items being inserted
                key = key + ".batchput";
                log.error("Creating thread for domain: " + key);
                return getExcecutor(key, 2);
            } else {

                // is it a single batch put operation? then get a thread pool
                // with 100
                // threads
                key = key + ".put";
                log.error("Creating thread for domain: " + key);
                return getExcecutor(key, 60);
            }
        } else if (SimpleDBParser.getOperation(sql).equals("CREATE")) {

            // is it a create operation
            key = key + ".create";
            log.error("Creating thread for domain: " + key);
            return getExcecutor(key, 2);

        } else if (SimpleDBParser.getOperation(sql).equals("SELECT")) {

            // is it a create operation
            key = key + ".select";
            log.error("Creating thread for domain: " + key);
            return getExcecutor(key, 80);

        } else if (SimpleDBParser.getOperation(sql).equals("DELETE")) {

            // is it a create operation
            key = key + ".select";
            log.error("Creating thread for domain: " + key);
            return getExcecutor(key, 60);

        } else {

            // is it a single select operation? then get a thread pool with 100
            // threads

            // is it a single batch put operation? then get a thread pool
            // with 100
            // threads
            key = key + ".other";
            log.error("Creating thread for domain: " + key);
            return getExcecutor(key, 50);
        }
        // else {
        //
        // key = key + ".single.other";
        // return getExcecutor(key, 65);

        // is it a priority statement?

    }
}

/*
 * Benchmarks with a table that has the following structure: <pre> Domain: Fips
 * AttributeNameCount: 4 AttributeNamesSizeBytes: 36 bytes AttributeValueCount:
 * 64590 AttributeValuesSizeBytes: 277.3 Kb BoxUsage: 0.0000071759 ItemCount:
 * 21529 ItemNamesSizeBytes: 756.8 Kb Timestamp: Thu Oct 08 02:15:05 CEST 2009
 * 
 * import single rows single threads 12:03:05 - > 12:13:05 without logging (at
 * 4255 of 21500 too long) 50 minutes import multiple rows/ batch single threads
 * 12:18:05 - > 12:19:58 without logging 1m 53s (> factor 25 improvement) import
 * multiple rows/ batch 40 threads 21:07:00 - > 12:07:50 without logging 50s (>
 * factor 2 improvement 40 threads) import multiple rows/ batch 55s (10 threads,
 * capacity 1000, short wait, missing inserts) import multiple rows/ batch 85s
 * (8 threads, capacity 1000, show wait, missing inserts) import multiple rows/
 * batch 85s (8 threads, capacity 1000, show wait, missing inserts) import
 * multiple rows/ batch 72s (6 threads, capacity 1000, short wait, missing
 * inserts) import multiple rows/ batch 60s (5 threads, capacity 1000, missing
 * inserts) import multiple rows/ batch 1m (2 threads, capacity 100, no missing,
 * some warnings) import multiple rows/ batch 55s (2 threads, capacity 100, no
 * missing, some warnings) import multiple rows/ batch 50s (4 threads, capacity
 * 1000) import multiple rows/ batch 1m15s(3 threads, no missing, lots of
 * warnings) import multiple rows/ batch 1m10s(2 threads, no missing, no
 * warnings) _BEST_ import multiple rows/ batch 58s (2 threads, no missing, no
 * warnings) _BEST_ import multiple rows/ batch single row 05:20 -> 06:59 1m39s
 * (100 threads, capacity 1000, no missing, some warnings, http connections 100)
 * single row 1m35s (200 threads, capacity 1000, no missing, some warnings, http
 * connections 500) single row 4m 05s (400 threads, capacity 1000, no missing,
 * some warnings, http connections 500, lots of warnings) single row 30:30 3m
 * 10s (300 threads, capacity 1000, no missing, some warnings, http connections
 * 500, lots of warnings) single row 3m (250 threads, capacity 1000, no missing,
 * some warnings, http connections 500, lots of warnings) single row 1m51s (225
 * threads, capacity 1000, no missing, http connections 500) single row 2m (230
 * threads, capacity 10000, no missing, some warnings, http connections 500)
 * ============ best performance 307 inserts per second per domain export 37s
 * for 21528 581 rows per second per domain
 * 
 * 
 * import mysql single rows 2.047s import mysql multiple rows/ batch 1.481s
 * </pre>
 */
