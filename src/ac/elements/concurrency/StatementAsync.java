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
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Logger;

import ac.elements.sdb.SimpleDBImplementation;
import ac.elements.sdb.collection.SimpleDBDataList;

public class StatementAsync {

    // ExecutorService executor = Executors.newFixedThreadPool(100);

    /** The Constant log. */
    private final static Logger log =
        Logger.getLogger(StatementAsync.class);

    public static final StatementAsync SINGLETON = new StatementAsync();

    private StatementAsync() {
        log.error("Constructing StatementAsync");
    }

    public Future<SimpleDBDataList> setStaticExecuteAsync(
            final String preparedStatement) {

        ThreadPoolExecutor executor =
                ThreadPoolExecutorFactory.getExecuter(preparedStatement);

        Future<SimpleDBDataList> response =
                executor.submit(new Callable<SimpleDBDataList>() {

                    public SimpleDBDataList call() {

                        SimpleDBDataList sdbl =
                                SimpleDBImplementation
                                        .setStaticExecute(preparedStatement);
                        return sdbl;
                    }

                });
        return response;
    }

    // public void getInfo() {
    //
    // log.error("In get info");
    // if (responses == null)
    // return;
    // log.error("Responses sized: " + responses.size());
    // log.error("Requests sized: " + processingRequests.size());
    // for (Future<SimpleDBDataList> future : responses) {
    // if (future == null) {
    // log.error("null future");
    // }
    // String originalRequest =
    // processingRequests.get(responses.indexOf(future));
    //
    // log.error("Got originalRequest: " + originalRequest);
    //
    // if (!future.isDone()) {
    // log.error("This future is not done, moving on: "
    // + originalRequest);
    // continue;
    // } else {
    // log.error("This future is done: " + originalRequest);
    // }
    // try {
    //
    // if (future != null) {
    // log.error("Removing request/ response: " + future.get());
    // processingRequests.remove(responses.indexOf(future));
    // responses.remove(future);
    // }
    // } catch (CancellationException ce) {
    //
    // log.error("Is the task cancelled: " + future.isCancelled());
    // ce.printStackTrace();
    //
    // if (N_THREADS > executor.getActiveCount())
    // N_THREADS = executor.getActiveCount();
    // else {
    // N_THREADS--;
    // }
    // executor.setMaximumPoolSize(N_THREADS);
    // executor.setCorePoolSize(N_THREADS);
    //
    // // todo increase que
    // log.error("Decreased N_THREADS to " + N_THREADS);
    // log.error("invoking statement again for request" + N_THREADS);
    // invokeStatement(originalRequest);
    //
    // } catch (InterruptedException e) {
    // log.error("The thread was interrupted");
    // // if the thread was interrupted.
    // e.printStackTrace();
    // } catch (ExecutionException e) {
    // // if the call method threw an exception.
    // log.error("e.getMessage()");
    // System.err.println(e.getMessage());
    // e.printStackTrace();
    // } catch (Exception e) {
    // log.error("Caught Exception: " + e.getMessage());
    // e.printStackTrace();
    // /*
    // * System.out.println("Response Status Code: " +
    // * exception.getStatusCode()); System.out.println("Error Code: "
    // * + exception.getErrorCode());
    // * System.out.println("Error Type: " +
    // * exception.getErrorType()); System.out.println("Request ID: "
    // * + exception.getRequestId()); System.out.print("XML: " +
    // * exception.getXML());
    // */
    // }
    // }
    //
    // }

    // public Future<SimpleDBDataList> invokeStatement(String request) {
    // Future<SimpleDBDataList> response =
    // SimpleDBImplementation.setStaticExecuteAsync(request);
    //
    // if (response.isCancelled()) {
    // if (N_THREADS > executor.getActiveCount())
    // N_THREADS = executor.getActiveCount();
    // else {
    // N_THREADS--;
    // }
    // executor.setMaximumPoolSize(N_THREADS);
    // executor.setCorePoolSize(N_THREADS);
    //
    // // todo increase que
    //
    // // retry the request
    // return invokeStatement(request);
    // }
    // return response;
    //
    // }

    public static void main(String arg[]) {
        String sql =
                "INSERT INTO test1 (`itemName()`, `A–o`, `Chinese`, `Km`, `Marca`, `Modelo`, `Precio`, `Version`, `key1`, `key2`, `werwer`, `test`) VALUES ('Aviso', '2000', '???', '50000', 'Ford', 'Ka', '28000', '1,8', NULL, NULL, NULL, NULL), ('item', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0zik0zn?', '0zik0zq?', NULL, NULL), ('item', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0zik0zq?', NULL, NULL, NULL), ('Auto', '2000', NULL, NULL, 'Ford', 'Fiesta', '35000', '1,6', NULL, NULL, NULL, NULL), ('qwerw', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'werwre', NULL), ('werwer', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'werwre', NULL), ('qwre', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'werwre', NULL), ('12342134234', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'werwre', NULL), ('qwe123', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'werwre', NULL), ('qwerwqer', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'werwre', NULL), ('1234', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'werwre', NULL), ('r1234', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'werwre', NULL), ('qwreqwre', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'werwre', NULL), ('2342314', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'werwre', NULL), ('12132134', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'werwre', NULL), ('e1243', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'werwre', NULL), ('1242134234', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'werwre', NULL), ('w234', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'werwre', NULL), ('1234234', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'werwre', NULL), ('21342342314', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'werwre', NULL), ('wer', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'werwre', NULL), ('234r', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'werwre', NULL), ('qwerqwer', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'werwre', NULL), ('qwerwerewr', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'werwre', NULL), ('qrwer', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'werwre', NULL);";

        log.error("running");
        StatementAsync.SINGLETON.setStaticExecuteAsync(sql);

        log.error("done");
    }

}
