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
