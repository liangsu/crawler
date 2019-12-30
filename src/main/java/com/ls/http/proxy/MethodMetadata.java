package com.ls.http.proxy;

import com.ls.http.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liangsu
 * @version v1.0
 * @Description
 * @Date 2019/12/30 17:35
 * @since
 */
public class MethodMetadata {
    private String url;
    private RequestMethod method;

    private List<String> urlParams = new ArrayList<>();
    private List<String> formParams = new ArrayList<>();

    private Map<Integer, String> indexNameMap = new HashMap<>();

    public List<String> getUrlParams() {
        return urlParams;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUrlParams(List<String> urlParams) {
        this.urlParams = urlParams;
    }

    public List<String> getFormParams() {
        return formParams;
    }

    public void setFormParams(List<String> formParams) {
        this.formParams = formParams;
    }

    public Map<Integer, String> getIndexNameMap() {
        return indexNameMap;
    }

    public void setIndexNameMap(Map<Integer, String> indexNameMap) {
        this.indexNameMap = indexNameMap;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public void setMethod(RequestMethod method) {
        this.method = method;
    }
}
