package com.ls.download;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Downloader {

    private final static Logger logger = LoggerFactory.getLogger(Downloader.class);

    private ExecutorService executorService;
    private String defaultSaveDirector = "C:\\Users\\warho\\Desktop\\imei";

    private Downloader() {
        executorService = new ThreadPoolExecutor(40, 80,
                60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue(10000));
    }

    public void submitTask(DownloadTask downloadTask){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                downloadTask.download();
            }
        });
    }

    public String getDefaultSaveDirector() {
        return defaultSaveDirector;
    }

    private static Downloader instance = new Downloader();
    public static Downloader getInstance(){
        return instance;
    }
}
