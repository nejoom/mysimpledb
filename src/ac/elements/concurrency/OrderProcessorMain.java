package ac.elements.concurrency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * http://www.devx.com/Java/Article/41377/1954: SampleMain.
 */
public class OrderProcessorMain {

    private static final int MSG_QUEUE_SIZE = 100;

    private static final int THREAD_POOL_SIZE = 2;

    // create BlockingQueue to put fund transfer objects
    private BlockingQueue<OrderVO> orderVOQueue =
            new ArrayBlockingQueue<OrderVO>(MSG_QUEUE_SIZE);

    private final static ExecutorService executor =
            Executors.newCachedThreadPool();

    private HashMap<String, OrderProcessorCallable> callableMap;

    private ArrayList<Future<Integer>> futurList;

    public OrderProcessorMain() {

        // create a thread pool with fixed no of threads
        // executor =
        // new CustomThreadPoolExecutor(THREAD_POOL_SIZE,
        // THREAD_POOL_SIZE, 0L, TimeUnit.MILLISECONDS,
        // new LinkedBlockingQueue<Runnable>());

        callableMap = new HashMap<String, OrderProcessorCallable>();

        // create list to store reference to Future objects
        futurList = new ArrayList<Future<Integer>>();
    }

    private void createAndSubmitTasks() {
        // create Callables
        OrderProcessorCallable callable1 =
                new OrderProcessorCallable("OrderProcessor_1", orderVOQueue);
        callableMap.put(callable1.getThreadName(), callable1);

        OrderProcessorCallable callable2 =
                new OrderProcessorCallable("OrderProcessor_2", orderVOQueue);
        callableMap.put(callable2.getThreadName(), callable2);

        // submit callable tasks
        Future<Integer> future;
        future = executor.submit(callable1);
        futurList.add(future);

        future = executor.submit(callable2);
        futurList.add(future);
    }

    private void populateOrderVOQueue() throws InterruptedException {
        // put orderVO objects in BlockingQueue
        for (int i = 0; i < 10; i++) {
            // this method will put OrderVO object in the order queue
            orderVOQueue.put(new OrderVO(i, "XYZ"));
        }
    }

    private void printProcessorStatus() throws InterruptedException {
        // print processor status until all orders are processed
        while (!orderVOQueue.isEmpty()) {
            for (Map.Entry<String, OrderProcessorCallable> e : callableMap
                    .entrySet()) {
                Logger.log(e.getKey() + " processed order count: "
                        + e.getValue().getProcessedCount());
            }
            Thread.sleep(1000);
        }
    }

    private void shutDown(boolean forceShutdown) {
        if (!forceShutdown) {
            // shutdown() method will mark the thread pool shutdown to true
            executor.shutdown();
            Logger.log("Executor shutdown status " + executor.isShutdown());
            Logger.log("Executor terninated status " + executor.isTerminated());

            // Mark threads to return threads gracefully.
            for (Map.Entry<String, OrderProcessorCallable> orderProcessor : callableMap
                    .entrySet()) {
                orderProcessor.getValue().setRunning(false);
            }
        } else {

            for (Future<Integer> f : futurList) {
                f.cancel(true);
            }

            // shutdown() method will mark the thread pool shutdown to true
            executor.shutdownNow();
        }
    }

    private void printWorkersResult() {
        for (Future<Integer> f : futurList) {
            try {
                Integer result = f.get(1000, TimeUnit.MILLISECONDS);
                Logger.log(f + " result. Processed orders " + result);
            } catch (InterruptedException e) {
                Logger.error(e.getMessage(), e);
            } catch (ExecutionException e) {
                Logger.error(e.getCause().getMessage(), e);
            } catch (TimeoutException e) {
                Logger.error(e.getMessage(), e);
            } catch (CancellationException e) {
                Logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Main method
     */
    public static void main(String[] args) throws Exception {

        // flag for if thread pool should be shutdown forcefully or gracefully
        final boolean FORCE_SHUTDOWN = false;

        OrderProcessorMain mainProcessor = new OrderProcessorMain();
        // create the callable tasks and submit to executor
        mainProcessor.createAndSubmitTasks();

        // sleeping to demonstrate threads in the thread pool are running
        // and are waiting for messages to process
        Thread.sleep(2000);
        Logger.log("Main thread awaken. "
                + "Putting OrderVO objects in blocking queue");

        // populate orderVO in blocking queue
        mainProcessor.populateOrderVOQueue();

        // print processor status
        mainProcessor.printProcessorStatus();

        // shutdown thread pool
        mainProcessor.shutDown(FORCE_SHUTDOWN);

        // print final statistics
        mainProcessor.printWorkersResult();

        Logger.log("Executor terminated status "
                + mainProcessor.executor.isTerminated());

    }

}
