package com.ls.http.proxy;

import com.ls.http.annotation.*;
import com.ls.http.test.BoxnovelBaidu;
import com.ls.http.test.Story;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpProxyBuilder {

    public static<T> T  createProxy(Class<T> source){
        Class[] itfs = new Class[]{source};

        Feign feign = source.getAnnotation(Feign.class);
        if(feign == null){
            throw new IllegalArgumentException("没有注解Feign");
        }
        String url =  feign.url();

        // 解析header
        HttpRequestContext httpRequestContext = new HttpRequestContext();
        Header[] headers = feign.headers();
        if(headers != null && headers.length > 0){
            for (Header header : headers) {
                httpRequestContext.addHeader(header.key(), header.value());
            }
        }

        // ssl
        boolean ssl = feign.ssl();
        httpRequestContext.setSsl(ssl);

        // 解析方法
        Map<Method, HttpMethodHandler> methodMap = new HashMap<>();
        Method[] methods = source.getDeclaredMethods();
        for (Method method : methods) {
            MethodMetadata methodMetadata = resoveMethod(method, url); // 解析方法
            HttpMethodHandler httpMethodHandler = new HttpMethodHandler(methodMetadata);
            methodMap.put(method, httpMethodHandler);
        }

        HttpProxy httpProxy = new HttpProxy(methodMap, httpRequestContext);

        return (T) Proxy.newProxyInstance(HttpProxy.class.getClassLoader(), itfs, httpProxy);
    }

    /**
     * 解析方法
     * @param method
     * @param urlPrefix
     * @return
     */
    public static MethodMetadata resoveMethod(Method method, String urlPrefix){
        MethodMetadata methodMetadata = new MethodMetadata();

        // url
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        String value = requestMapping.value();
        String url = urlPrefix + value;
        methodMetadata.setUrl(url);

        // 解析url上的参数
        methodMetadata.setUrlParams(resovleURLParamterNames(url));

        // 请求类型：get、post etc.
        RequestMethod requestMethod = requestMapping.method();
        methodMetadata.setMethod(requestMethod);

        // 解析参数名
        Parameter[] parameters = method.getParameters();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; parameters != null && i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class parameterType = parameter.getType();
            String name = null;
            if(parameter.isNamePresent()){
                name = parameter.getName();
            }
            RequestParam requestParam = findParamterAnnotation(parameterAnnotations, i);
            if(requestParam != null){
                name = requestParam.value();
            }
            methodMetadata.getIndexNameMap().put(i, name); // 增加参数

            // 表单参数
            if(!methodMetadata.getUrlParams().contains(name)){
                methodMetadata.getFormParams().add(i, name);
            }
        }

        return methodMetadata;
    }

    /**
     * 解析url上面的参数
     * @param url
     * @return
     */
    private static List<String> resovleURLParamterNames(String url) {
        List<String> names = new ArrayList<>();

        StringBuilder name = null;
        for(int i = 0; i < url.length(); i++){
            char ch = url.charAt(i);
            if(ch == '{'){
                name = new StringBuilder();
            }else if(ch == '}'){
                name.toString();
                names.add(name.toString());
                name = null;
            }else if(name != null){
                name.append(ch);
            }
        }
        return names;
    }

    public static<T> T findParamterAnnotation(Annotation[][] parameterAnnotations, int i){
        Annotation[] annotations = parameterAnnotations[i];
        if(annotations != null){
            for (Annotation annotation : annotations) {
                annotation.getClass().getTypeName();
                if(annotation instanceof RequestParam){
                    return (T) annotation;
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
//        BoxnovelBaidu baidu = createProxy(BoxnovelBaidu.class);
////        String reslut = story.getDirectories("4315647406", 1, "asc", "");
//        String reslut = baidu.getContent("4315647406", "10147867");
//        System.out.println(reslut);

        Story story = createProxy(Story.class);
        System.out.println(story.get("1", 1, 1, 1));
    }

}
