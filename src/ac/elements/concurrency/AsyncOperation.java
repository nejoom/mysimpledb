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

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import ac.elements.conf.Configuration;
import ac.elements.parser.SimpleDBParser;
import ac.elements.sdb.SimpleDBImplementation;
import ac.elements.sdb.collection.SimpleDBMap;
import ac.elements.sdb.collection.SimpleDBDataList;

public class AsyncOperation {

    private static ConcurrentHashMap<String, ThreadPoolExecutor> threadPoolExecutors =
            new ConcurrentHashMap<String, ThreadPoolExecutor>();

    /** The Constant log. */
    private final static Logger log =
        Logger.getLogger(ThreadPoolExecutorFactory.class);

    private static final int CAPACITY = 1000;

    /** The access key id. */
    private final static String staticAccessKeyId =
            Configuration.getInstance().getValue("aws", "AWSAccessKeyId");

    /** The secret access key. */
    private final static String staticSecretAccessKey =
            Configuration.getInstance().getValue("aws", "SecretAccessKey");

    private final static SimpleDBImplementation sdbimpl =
            new SimpleDBImplementation(staticAccessKeyId, staticSecretAccessKey);

    private static ThreadPoolExecutor getStaticExcecutor(String key,
            int N_THREADS) {
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

    // todo async
    public static Future<String> deleteItem(final String domain,
            final String itemName) {

        String key = domain;
        key = key + ".deleteAttributes";

        ThreadPoolExecutor executor =
                AsyncOperation.getStaticExcecutor(key, 50);

        Future<String> response = executor.submit(new Callable<String>() {

            public String call() {
                return sdbimpl.deleteItem(domain, itemName);
            }

        });
        return response;
    }

    // todo async
    public static Future<String> deleteAttributes(final String domain,
            final SimpleDBMap sdbMap) {

        String key = domain;
        key = key + ".deleteAttributes";

        ThreadPoolExecutor executor =
                AsyncOperation.getStaticExcecutor(key, 50);

        Future<String> response = executor.submit(new Callable<String>() {

            public String call() {
                return sdbimpl.deleteAttributes(domain, sdbMap);
            }

        });
        return response;
    }

    // todo async
    public static Future<String> batchPutAttributes(
            final SimpleDBDataList dataList) {

        if (log.isDebugEnabled())
            log.debug("Entering batchPutAttributes");

        String domain = dataList.getDomainName();
        String key = domain;
        key = key + ".batchPutAttributes";

        ThreadPoolExecutor executor = AsyncOperation.getStaticExcecutor(key, 2);

        Future<String> response = executor.submit(new Callable<String>() {

            public String call() {
                return sdbimpl.batchPutAttributes(dataList);
            }

        });
        return response;
    }

    // todo async
    public static Future<String> batchPutReplaceAttributes(
            final SimpleDBDataList dataList) {

        String domain = dataList.getDomainName();
        String key = domain;
        key = key + ".batchPutAttributes";

        ThreadPoolExecutor executor = AsyncOperation.getStaticExcecutor(key, 2);

        Future<String> response = executor.submit(new Callable<String>() {

            public String call() {
                return sdbimpl.batchPutReplaceAttributes(dataList);
            }

        });
        return response;
    }

    // todo async
    public static Future<String> putAttributes(final String domain,
            final SimpleDBMap sdbMap) {

        String key = domain;
        key = key + ".deleteAttributes";

        ThreadPoolExecutor executor =
                AsyncOperation.getStaticExcecutor(key, 50);

        Future<String> response = executor.submit(new Callable<String>() {

            public String call() {
                return sdbimpl.putAttributes(domain, sdbMap);
            }

        });
        return response;
    }

    // todo async
    public static Future<String> select(final String selectExpression,
            final String nextToken) {

        String key = SimpleDBParser.getDomain(selectExpression);
        key = key + ".getSelect";

        ThreadPoolExecutor executor =
                AsyncOperation.getStaticExcecutor(key, 100);

        Future<String> response = executor.submit(new Callable<String>() {

            public String call() {
                return sdbimpl.select(selectExpression, nextToken);
            }

        });
        return response;
    }

}