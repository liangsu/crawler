package com.ls.download;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * 线程限制器
 * 目的：可以用于限制一个网站的连接，最多有多少个线程执行下载任务
 */
public class ThreadLimiter2{
    private ConcurrentHashMap<String,Node> nodeMap = new ConcurrentHashMap<>();

    public void limit(String key, int num){
        Node node = nodeMap.get(key);
        if(node == null){
            node = new Node(key, new LinkedBlockingQueue(), new Semaphore(num));
            nodeMap.putIfAbsent(key, node);
        }
    }

    public void offer(String key, Runnable r){
        Node node = nodeMap.get(key);
        if(node == null){
            throw new IllegalStateException("not invoke limit method to set limit");
        }
        node.queue.offer(r);

        deliveryTask(node);
    }

    /**
     * 投递任务到线程池
     * @param node
     */
    public void deliveryTask(Node node){
        Queue<Runnable> queue = node.queue;
        Runnable task;
        while((task = queue.poll()) != null){
            if(node.semaphore.tryAcquire()){
                ReleaseSemphoreTask releaseSemphoreTask = new ReleaseSemphoreTask(node, task);
                Downloader.getInstance().execute(releaseSemphoreTask);
            }
        }
    }

    private void release(Node node) {
        node.semaphore.release();
        deliveryTask(node);
    }

    class Node{
        String key;
        Queue<Runnable> queue;
        Semaphore semaphore;

        public Node(String key, Queue queue, Semaphore semaphore) {
            this.key = key;
            this.queue = queue;
            this.semaphore = semaphore;
        }
    }

    class ReleaseSemphoreTask implements Runnable{
        Node node;
        Runnable task;

        public ReleaseSemphoreTask(Node node, Runnable task) {
            this.node = node;
            this.task = task;
        }

        @Override
        public void run() {
            try{
                task.run();
            }finally {
                release(node);
            }
        }
    }


}
