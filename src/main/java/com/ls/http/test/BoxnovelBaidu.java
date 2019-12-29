package com.ls.http.test;

import com.ls.http.annotation.*;

@Feign(url = "https://boxnovel.baidu.com",
    headers = {
            @Header(key = "Cookie", value = "BAIDUID=1AC69084B3E5500F2C675C4734519EFA:FG=1; PSTM=1551422894; BIDUPSID=422811C2830CB8ECFAE8B267030EC98F; BDUSS=lNM2JyOUxHTjFrTnFNaE5Ob0p4ekU3czJycHQ5WkthWXFGZ3RtQW5CUFdhUjFlRVFBQUFBJCQAAAAAAAAAAAEAAACjeS5By9W458rHusPIywAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAANbc9V3W3PVdT1; delPer=0; BDORZ=B490B5EBF6F3CD402E515D22BCDA1598; BDRCVFR[feWj1Vr5u3D]=I67x6TjHwwYf0; PSINO=6; H_PS_PSSID=1441_21122_30211_30284_30509_30481"),
            @Header(key = "Referer", value = "https://boxnovel.baidu.com/boxnovel/content")
    })
public interface BoxnovelBaidu {

    /**
     * 获取目录列表
     * @param bookid 4315647406
     * @param pageNum 1
     * @param order asc
     * @param site ""
     * @return
     */
    @RequestMapping(value = "/boxnovel/wiseapi/chapterList",
            method = RequestMethod.GET)
//    ?bookid=4315647406&pageNum=1&order=asc&site=
    String getDirectories(@RequestParam(value = "bookid") String bookid,
                                 @RequestParam(value = "pageNum")int pageNum,
                                 @RequestParam(value = "order")String order,
                                 @RequestParam(value = "site")String site);

    /**
     * 获取内容
     * @param bookid
     * @param cid
     * @return
     */
    @RequestMapping(value = "/boxnovel/wiseapi/chapterContent", method = RequestMethod.GET)
    String getContent(@RequestParam(value = "bookid") String bookid, @RequestParam(value = "cid") String cid);

}
