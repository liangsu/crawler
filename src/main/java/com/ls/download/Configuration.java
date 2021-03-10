package com.ls.download;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局配置类
 */
public class Configuration {
    /**
     * 单例使用
     */
    public static Configuration instance = new Configuration();

    /**
     * 默认保存路径
     */
    private String defaultSaveDir;

    /**
     * 代理ip、端口、登录名称、密码
     */
    private String proxyIp;
    private String proxyPort;
    private String proxyUserName;
    private String proxyPwd;

    private Configuration(){
    }



}
