package com.ls.http.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

public class HttpProxy implements InvocationHandler {

    private Map<Method, HttpMethodHandler> methodMap;
    private HttpRequestContext httpRequestContext;

    public HttpProxy(Map<Method, HttpMethodHandler> methodMap) {
        this.methodMap = methodMap;
        this.httpRequestContext = new HttpRequestContext();
    }

    public HttpProxy(Map<Method, HttpMethodHandler> methodMap, HttpRequestContext httpRequestContext) {
        this.methodMap = methodMap;
        this.httpRequestContext = httpRequestContext;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        HttpMethodHandler httpMethodHandler = methodMap.get(method);
        return httpMethodHandler.request(args, httpRequestContext);
    }







}
