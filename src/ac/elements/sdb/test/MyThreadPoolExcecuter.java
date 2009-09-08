package ac.elements.sdb.test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class MyThreadPoolExecutor {
    int poolSize = 6;

    int maxPoolSize = 8;

    long keepAliveTime = 10;

    ThreadPoolExecutor threadPool = null;

    final BlockingQueue queue = new ArrayBlockingQueue(1000);

    public MyThreadPoolExecutor() {
        threadPool =
                new ThreadPoolExecutor(poolSize, maxPoolSize, keepAliveTime,
                        TimeUnit.MILLISECONDS, queue);

    }

    public void runTask(Runnable task) {
        // System.out.println("Task count.."+threadPool.getTaskCount() );
        // System.out.println("Queue Size before assigning the
        // task.."+queue.size() );
        threadPool.execute(task);
        // System.out.println("Queue Size after assigning the
        // task.."+queue.size() );
        // System.out.println("Pool Size after assigning the
        // task.."+threadPool.getActiveCount() );
        // System.out.println("Task count.."+threadPool.getTaskCount() );
        System.out.println("Task count.." + queue.size());

    }

    public void shutDown() {
        threadPool.shutdown();
    }

    public static void main(String args[]) {
        MyThreadPoolExecutor mtpe = new MyThreadPoolExecutor();
        // start first one
        mtpe.runTask(new Runnable() {
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        System.out.println("First Task");
                        System.out.println(Thread.currentThread().getId());
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException ie) {
                    }
                }
            }
        });
        // start second one
        /*
         * try{ Thread.currentThread().sleep(500); }catch(InterruptedException
         * ie){}
         */
        mtpe.runTask(new Runnable() {
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        System.out.println("Second Task");
                        System.out.println(Thread.currentThread().getId());
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException ie) {
                    }
                }
            }
        });
        // start third one
        /*
         * try{ Thread.currentThread().sleep(500); }catch(InterruptedException
         * ie){}
         */
        mtpe.runTask(new Runnable() {
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        System.out.println("Third Task");
                        System.out.println(Thread.currentThread().getId());
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException ie) {
                    }
                }
            }
        });
        // start fourth one
        /*
         * try{ Thread.currentThread().sleep(500); }catch(InterruptedException
         * ie){}
         */
        mtpe.runTask(new Runnable() {
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        System.out.println("Fourth Task");
                        System.out.println(Thread.currentThread().getId());
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException ie) {
                    }
                }
            }
        });
        // start fifth one
        /*
         * try{ Thread.currentThread().sleep(500); }catch(InterruptedException
         * ie){}
         */
        mtpe.runTask(new Runnable() {
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        System.out.println("Fifth Task");
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException ie) {
                    }
                }
            }
        });
        // start Sixth one
        /*
         * try{ Thread.currentThread().sleep(500); }catch(InterruptedException
         * ie){}
         */
        mtpe.runTask(new Runnable() {
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        System.out.println("Sixth Task");
                        System.out.println(Thread.currentThread().getId());
                        Thread.currentThread().sleep(1000);
                        if (i==1) throw new RuntimeException("Felt like it");
                        
                    } catch (InterruptedException ie) {
                    }
                }
            }
        });
        mtpe.shutDown();
        System.out.println("done");
    }

}
