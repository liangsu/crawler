package com.ls.download.m3u8;

import com.ls.http.FileUtils;

public class M3u8Log {
    private String savePath;

    public void writeM3u8(String content){
        FileUtils.writeToFile(savePath + "\\m3u8.txt", content);
    }

    public void writeKey(byte[] key){
        FileUtils.writeToFile(savePath + "\\key.txt", new String(key));
    }

    public void writeMainInfo(M3u8DownloadTask m3u8DownloadTask){
        FileUtils.writeToFile(savePath + "\\main.txt", m3u8DownloadTask.getUrl());
    }

}
