package com.ls.download;

public interface DownloadTask {
    /**
     * 下载文件的名称
     * @return
     */
    String getName();

    /**
     * 下载方法
     */
    void download();

    /**
     * 完成进度
     * @return
     */
    Integer progress();
}
