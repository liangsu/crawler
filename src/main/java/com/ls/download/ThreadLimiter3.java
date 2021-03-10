package com.ls.download;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;

/**
 * 线程限制器
 * 目的：可以用于限制一个网站的连接，最多有多少个线程执行下载任务
 */
public class ThreadLimiter3 implements Executor {
    private LinkedBlockingDeque<Runnable> queue = new LinkedBlockingDeque<>();
    private Semaphore semaphore;

    public ThreadLimiter3(int limit){
        semaphore = new Semaphore(limit);
    }

    @Override
    public void execute(Runnable command) {
        offer(command);
    }

    public void offer(Runnable r){
        queue.offer(r);
        deliveryTask();
    }

    /**
     * 投递任务到线程池
     */
    public void deliveryTask(){
        LinkedBlockingDeque<Runnable> queue = this.queue;
        Runnable task;
        while((task = queue.poll()) != null){
            if(semaphore.tryAcquire()){
                ReleaseSemphoreTask releaseSemphoreTask = new ReleaseSemphoreTask(task);
                Downloader.getInstance().execute(releaseSemphoreTask);
            }else{
                queue.addFirst(task);
                break;
            }
        }
    }

    private void release() {
        semaphore.release();
        deliveryTask();
    }

    class ReleaseSemphoreTask implements Runnable{
        Runnable task;

        public ReleaseSemphoreTask(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            try{
                task.run();
            }finally {
                release();
            }
        }
    }

}
