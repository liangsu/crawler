package com.ls.download.m3u8;

import com.ls.crawler.HttpClientUtil;
import com.ls.download.AesUtils;
import com.ls.download.http.HttpClient;
import com.ls.http.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

class TsDownloadTask implements Runnable{
    private final static Logger logger = LoggerFactory.getLogger(TsDownloadTask.class);

    private M3u8DownloadTask mainTask;
    private M3u8Url m3u8Url;
    private String saveDir;
    private String prefixUrl;
    private HttpClient httpClient;

    private String tsPath;
    private String keyPath;
    private String decryptFilePath;

    public void setMainTask(M3u8DownloadTask mainTask) {
        this.mainTask = mainTask;
    }
    public void setM3u8Url(M3u8Url m3u8Url) {
        this.m3u8Url = m3u8Url;
    }
    public M3u8Url getM3u8Url() {
        return m3u8Url;
    }
    public void setSaveDir(String saveDir) {
        this.saveDir = saveDir;
    }
    public void setPrefixUrl(String prefixUrl) {
        this.prefixUrl = prefixUrl;
    }
    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public void run() {
        logger.info("线程[{}]开始下载：{}", Thread.currentThread().getName(), prefixUrl + m3u8Url.getUrl());
        boolean success = false; // 下载是否成功
        // 下载文件
        try{

            String fileName = M3u8Utils.getTsNameFromM3u8Url(m3u8Url);
            String fileNameNoExt = FileUtils.getFileName(fileName); // 没有后缀名的文件
            tsPath = saveDir + fileName;
            keyPath = saveDir + "key.txt";
            decryptFilePath = saveDir + fileNameNoExt + "_dec.ts";

            downloadKeyIfNecessary();

            success = downloadTs();

            if(success){
                decryptIfNecessary();
            }

            // 日志
            if(success){
                logger.info("{} success", fileName);
            }else{
                logger.info("{} error", fileName);
            }

        }catch (Exception e){
            logger.error("download ts file error!", e);
        }

        // 如果下载失败，写入失败文件列表
        if(success){
            mainTask.completedTask(this);
        }else{
            mainTask.errorTask(this);
        }

        logger.info("线程[{}]结束下载：{}", Thread.currentThread().getName(), m3u8Url.getUrl());
    }

    /**
     * 下载解密key
     */
    private void downloadKeyIfNecessary() {
        // 下载解码文件
        M3u8Key key = m3u8Url.getKey();
        if(key == null){
            return;
        }

        byte[] aesKey = null;
        if(StringUtils.equals(key.getMethod(), "AES-128")){
            File file = new File(keyPath);
            if(!file.exists()){
                aesKey = httpClient.getBytes(key.getUri(), null);
                FileUtils.writeToFile(aesKey, keyPath);
            }
        }
    }

    /**
     * 下载ts文件
     * @return
     * @throws IOException
     */
    protected boolean downloadTs() throws IOException {
        boolean success = false;

        File file = new File(tsPath);
        if(!file.exists() || file.length() == 0){
            byte[] data = httpClient.getBytes(prefixUrl + m3u8Url.getUrl(), null);
            if(data != null){
                if((m3u8Url.getKey() == null && M3u8Utils.validTs(data)) || m3u8Url.getKey() != null){
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(data);
                    fos.flush();
                    fos.close();
                    success = true;
                }
            }
        }else{
            success = true;
        }

        return success;
    }

    /**
     * 解密ts文件
     * @throws IOException
     */
    protected void decryptIfNecessary() throws IOException {
        M3u8Key key = m3u8Url.getKey();
        if(key == null){
            return;
        }

        if(StringUtils.equals(key.getMethod(), "AES-128")){
            File file = new File(decryptFilePath);
            if(!file.exists() || file.length() == 0){
                byte[] aesKey = FileUtils.readFileToByteArray(keyPath);
                byte[] content = FileUtils.readFileToByteArray(tsPath);
                byte[] iv = key.getIvBytes();

                // 解密
                byte[] decryptData = AesUtils.decrypt(content, aesKey, iv);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(decryptData);
                fos.flush();
                fos.close();
            }
        }
    }

}
