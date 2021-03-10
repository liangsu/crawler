package com.ls.download.m3u8;

import com.ls.download.URLUtils;
import com.ls.http.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class M3u8Utils {

    /**
     * 从m3u8的url中，获取ts的文件名称
     * @param m3u8Url
     * @return
     */
    public static String getTsNameFromM3u8Url(M3u8Url m3u8Url){
        if(StringUtils.isNotBlank(m3u8Url.getTitle())){
            return m3u8Url.getTitle();
        }

        String url = m3u8Url.getUrl();

        if(!m3u8Url.getM3u8().isHasSameTsName()){
            int end = url.indexOf("?");
            if(end > -1){
                return url.substring(0, end);
            }else{
                return url;
            }

        }else{
            List<String> paramKeys = m3u8Url.getM3u8().getSubUrlNotRepeatParamKey();

            HashMap<String, String> params = URLUtils.getParams(url);
            return params.get("end") + ".ts";
        }
    }

    /**
     * 从m3u8的url中，获取ts的解密后的文件名称
     * @param m3u8Url
     * @return
     */
    public static String getDecryptTsNameFromM3u8Url(M3u8Url m3u8Url){
        String fileName = getTsNameFromM3u8Url(m3u8Url);
        return FileUtils.getFileName(fileName) + "_dec.ts";
    }

    /**
     * 校验ts文件格式
     * @param data
     * @return
     */
    public static boolean validTs(byte[] data) {
        return data[0] == 0x47 && data[1] == 0x40;
    }

    /**
     * 创建m3u8合并文件
     * @param fileListNamePath
     * @param m3u8
     * @throws IOException
     */
    public static void createFileList(String fileListNamePath, M3u8 m3u8) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileListNamePath));
        for (int i = 0; i < m3u8.getSubUrls().size(); i++) {
            M3u8Url m3u8Url = m3u8.getSubUrls().get(i);
            String fileName;
            if(m3u8Url.getKey() != null){
                fileName = getDecryptTsNameFromM3u8Url(m3u8Url);
            }else{
                fileName = getTsNameFromM3u8Url(m3u8Url);
            }
            bw.write("file '" + fileName +"'");
            bw.newLine();
        }
        bw.flush();
    }

}
