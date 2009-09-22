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
