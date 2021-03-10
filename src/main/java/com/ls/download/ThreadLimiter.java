package com.ls.download;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 线程限制器
 * 目的：可以用于限制一个网站的连接，最多有多少个线程执行下载任务
 */
public class ThreadLimiter implements Runnable {
    private Queue<Node> queue = new LinkedBlockingQueue<>();
    private ConcurrentHashMap<String,Semaphore> semaphoreMap = new ConcurrentHashMap<>();

    public void limit(String key, int num){
        Semaphore semaphore = semaphoreMap.get(key);
        if(semaphore == null){
            semaphoreMap.putIfAbsent(key, new Semaphore(num));
        }
    }

    public void offer(String key, Runnable r){
        Semaphore semaphore = semaphoreMap.get(key);
        if(semaphore == null){
            throw new IllegalStateException("not invoke limit method to set limit");
        }

        queue.offer(new Node(key, r));
    }

    class Node{
        String key;
        Runnable r;

        public Node(String key, Runnable r) {
            this.key = key;
            this.r = r;
        }
    }


    @Override
    public void run() {
        Node pre = null;
        Node node = null;
        int repeat = 0;

        boolean isLock = false;
        while((node = queue.poll()) != null){
            Semaphore semaphore = semaphoreMap.get(node.key);

            if(semaphore.tryAcquire()){
                isLock = true;

            }else{

                if(pre == node){
                    repeat++;
                }else{
                    repeat = 0;
                }

                if(repeat >= 5){
                    try {
                        if(semaphore.tryAcquire(10, TimeUnit.SECONDS)){
                            isLock = true;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            if(isLock){
                final Runnable task = node.r;
                Downloader.getInstance().execute(() ->{
                    try{
                        task.run();
                    }finally {
                        semaphore.release();
                    }
                });
            }else{
                queue.offer(node);
            }
            isLock = false;
            pre = node;
        }
    }


}
