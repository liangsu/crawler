package com.ls.download.m3u8;

import com.ls.crawler.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;

class TsDownloadTask implements Runnable{
    private final static Logger logger = LoggerFactory.getLogger(TsDownloadTask.class);

    private M3u8DownloadTask mainTask;
    private M3u8Url m3u8Url;
    private String saveDir;
    private String prefixUrl;

    public void setMainTask(M3u8DownloadTask mainTask) {
        this.mainTask = mainTask;
    }
    public void setM3u8Url(M3u8Url m3u8Url) {
        this.m3u8Url = m3u8Url;
    }
    public void setSaveDir(String saveDir) {
        this.saveDir = saveDir;
    }
    public void setPrefixUrl(String prefixUrl) {
        this.prefixUrl = prefixUrl;
    }

    @Override
    public void run() {
        boolean success = false; // 下载是否成功
        // 下载文件
        try{
            String fileName = M3u8Utils.getTsNameFromM3u8Url(m3u8Url);
            String filePath = saveDir + fileName;
            File file = new File(filePath);

            if(!file.exists() || file.length() == 0){
                byte[] data = HttpClientUtil.get(prefixUrl + m3u8Url.getUrl(), null, "utf-8");
                if(data != null){
                    if(M3u8Utils.validTs(data)){
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
        if(!success){
            mainTask.completedTask(this);
        }else{
            mainTask.errorTask(this);
        }
    }

}
