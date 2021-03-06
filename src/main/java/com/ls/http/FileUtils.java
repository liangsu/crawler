package com.ls.http;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;

public class FileUtils {
    /**
     * 创建文件夹
     * @param path
     */
    public static void mkdirs(String path){
        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
    }

    public static void writeToFile(String content, String path){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToFile(byte[] data, String path){
        try {
            BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(path));
            writer.write(data);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFromFile(String path){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            IOUtils.copy(reader, bos);
            return bos.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] readFileToByteArray(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOUtils.copy(fis, bos);
        return bos.toByteArray();
    }

    public static String readFileToString(String filePath) throws IOException {
        return new String(readFileToByteArray(filePath));
    }

    /**
     * 获取文件名称，不带后缀
     * @param fileName 如：aa.mp3
     * @return
     *         aa.mp3 -> aa
     *         aa -> aa
     *         null -> aa
     */
    public static String getFileName(String fileName) {
        if(StringUtils.isBlank(fileName)){
            return fileName;
        }
        int pos = fileName.lastIndexOf(".");
        if(pos < 0){
            return fileName;
        }
        return fileName.substring(0, pos);
    }

    public static String formatPath(String path){
        return path.replaceAll("/", "\\\\");
    }

    /**
     * 从路径中获取文件的名称
     * @param path
     * @return
     *          C:\Users\Administrator\Desktop\aa 返回 null
     *          C:\Users\Administrator\Desktop\aa.mp4 返回 aa.mp4
     */
    public static String getFileNameFromPath(String path) {
        path = formatPath(path);
        int pos = path.lastIndexOf("\\");
        if(pos > -1){
            String lastName = path.substring(pos + 1);
            if(lastName.indexOf(".") > -1){
                return lastName;
            }
        }
        return null;
    }

    public static String getDirectoryFromPath(String path) {
        String dir;
        dir = path = formatPath(path);
        int pos = path.lastIndexOf("\\");
        if(pos > -1){
            String lastName = path.substring(pos + 1);
            if(lastName.indexOf(".") > -1){ // 有文件名
                dir = path.substring(0, pos + 1);
            }
        }

        if(!dir.endsWith("\\")){
            dir += "\\";
        }

        return dir;
    }

}
