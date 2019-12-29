package com.ls.http.test;

import com.ls.http.annotation.Feign;
import com.ls.http.annotation.RequestMapping;
import com.ls.http.annotation.RequestMethod;
import com.ls.http.annotation.RequestParam;

@Feign(url = "")
public interface Story {

    @RequestMapping(value = "http://www.qstheory.cn/wp/2019-12/29/c_1125399878.htm", method = RequestMethod.GET)
    String get(@RequestParam("name") String name, @RequestParam("key")Integer key, @RequestParam("page") int page, @RequestParam("size") int size);
}
