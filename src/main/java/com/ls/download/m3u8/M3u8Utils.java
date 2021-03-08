package com.ls.download.m3u8;

import com.ls.download.URLUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class M3u8Utils {

    /**
     *
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
            return url.substring(0, end);

        }else{
            HashMap<String, String> params = URLUtils.getParams(url);
            return params.get("end") + ".ts";
        }
    }

    /**
     * 校验ts文件格式
     * @param data
     * @return
     */
    public static boolean validTs(byte[] data) {
        return true;
//        return data[0] == 0x47 && data[1] == 0x40;
    }

    /**
     * 创建m3u8合并文件
     * @param path
     * @param fileListName
     * @param m3u8
     * @throws IOException
     */
    public void createFileList(String path, String fileListName, M3u8 m3u8) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(path + fileListName));
        String pathbak = path.replaceAll("\\\\", "\\\\\\\\");
        for (int i = 0; i < m3u8.getSubUrls().size(); i++) {
            String fileName = M3u8Utils.getTsNameFromM3u8Url(m3u8.getSubUrls().get(i));
            bw.write("file '" + fileName +"'");
            bw.newLine();
        }
        bw.flush();
    }

}
