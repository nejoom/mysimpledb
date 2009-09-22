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

import java.util.concurrent.*;

import ac.elements.sdb.ISimpleDBStatement;
import ac.elements.sdb.SimpleDBDataList;

public class ThreadPoolExecutor implements ISimpleDBStatement {
    ExecutorService exec = Executors.newCachedThreadPool();

    private void runServer() {
            exec.execute(new ConnectionRunnable());
    }

    public void runTask(Runnable task) {
        // System.out.println("Task count.."+threadPool.getTaskCount() );
        // System.out.println("Queue Size before assigning the
        // task.."+queue.size() );
        exec.execute(task);
        // System.out.println("Queue Size after assigning the
        // task.."+queue.size() );
        // System.out.println("Pool Size after assigning the
        // task.."+threadPool.getActiveCount() );
        // System.out.println("Task count.."+threadPool.getTaskCount() );
    }
    
    private static class ConnectionRunnable implements Runnable {

        ConnectionRunnable() {
        }

        public void run() {
            // handle connection
        }
    }

    public SimpleDBDataList setDelete(String deleteExpression) {
        // TODO Auto-generated method stub
        return null;
    }

    public SimpleDBDataList setDeleteAttributeWhere(String deleteExpression) {
        // TODO Auto-generated method stub
        return null;
    }

    public SimpleDBDataList setInsert(String insertExpression) {
        // TODO Auto-generated method stub
        return null;
        
    }

    public SimpleDBDataList setInsertWhere(String insertExpression) {
        // TODO Auto-generated method stub
        return null;
    }

    public SimpleDBDataList setReplace(String replaceExpression) {
        // TODO Auto-generated method stub
        return null;
        
    }

    public SimpleDBDataList setReplaceWhere(String replaceExpression) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SimpleDBDataList setDeleteAttribute(String deleteExpression) {
        // TODO Auto-generated method stub
        return null;
    }
}
