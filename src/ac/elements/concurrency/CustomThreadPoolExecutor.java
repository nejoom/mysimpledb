package ac.elements.concurrency;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * CustomThreadPoolExecutor.
 */
public class CustomThreadPoolExecutor extends ThreadPoolExecutor {

  /**
   * CustomThreadPoolExecutor Constructor.
   */
  public CustomThreadPoolExecutor(int corePoolSize, int maxPoolSize,
      long keepAliveTime, TimeUnit unit,
      BlockingQueue<Runnable> workQueue) {
    super(corePoolSize, maxPoolSize, keepAliveTime, unit, workQueue);
  }

  @Override
  public void beforeExecute(Thread t, Runnable r) {
    super.beforeExecute(t, r);
    Logger.log("After calling beforeExecute() method for a thread "
        + r);
  }

  @Override
  public void afterExecute(Runnable r, Throwable t) {
    super.afterExecute(r, t);
    Logger.log("After calling afterExecute() method for a thread "
        + r);
  }

  @Override
  public void terminated() {
    super.terminated();
    Logger.log("Threadpool terminated");
  }

}
