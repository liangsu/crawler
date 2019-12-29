package com.ls.http.proxy;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestContext {
    private boolean ssl;
    private Map<String, String> headers = new HashMap<>();


    public Map<String, String> getHeaders() {
        return headers;
    }
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
    public void addHeader(String key, String value){
        headers.put(key, value);
    }
    public boolean isSsl() {
        return ssl;
    }
    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }
}
