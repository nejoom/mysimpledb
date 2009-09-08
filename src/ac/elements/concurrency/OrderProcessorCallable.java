package ac.elements.concurrency;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * OrderProcessorCallable.
 */
public class OrderProcessorCallable implements Callable<Integer> {
    private BlockingQueue<OrderVO> orderVOQueue;

    private String threadName;

    private boolean running = true;

    private int processedCount;

    /**
     * OrderProcessorCallable Constructor.
     */
    public OrderProcessorCallable(String name, BlockingQueue<OrderVO> msgQueue) {
        this.threadName = name;
        this.orderVOQueue = msgQueue;
    }

    @Override
    public Integer call() throws OrderProcessingException {
        while (running) {
            // check if current Thread is interrupted
            checkInterruptStatus();

            try {
                // get message from message queue with timeout of 10ms
                OrderVO order = orderVOQueue.poll(10, TimeUnit.MILLISECONDS);

                if (order != null) {
                    // do message processing here
                    Logger.log(threadName + ": processed message "
                            + order.toString() + ", " + processedCount);

                    // increment processed message count
                    processedCount++;
                } else {
                    Logger.log(threadName
                            + ": waiting for message in the blocking queue");
                }

                // for demo purpose sleep current thread for 500ms otherwise
                // program will complete very quickly
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new OrderProcessingException(threadName
                        + " thread interrupted while waiting for message", e);
            }
        }

        // return result
        return processedCount;
    }

    private void checkInterruptStatus() throws OrderProcessingException {
        if (Thread.interrupted()) {
            throw new OrderProcessingException("Thread was interrupted");
        }
    }

    /**
     * Getter for the threadName.
     * 
     * @return The threadName
     */
    public String getThreadName() {
        return threadName;
    }

    /**
     * Getter for the running.
     * 
     * @return The running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Sets the value of running.
     * 
     * @param running
     *            Sets the running to running
     */
    public void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * Getter for the processedCount.
     * 
     * @return The processedCount
     */
    public int getProcessedCount() {
        return processedCount;
    }

}
