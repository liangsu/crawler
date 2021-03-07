package com.ls.http;

import org.apache.commons.io.IOUtils;

import java.io.*;

public class FileUtils {

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

    public static byte[] readFileToByteArray(String filePath) throws Exception {
        FileInputStream fis = new FileInputStream(filePath);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOUtils.copy(fis, bos);
        return bos.toByteArray();
    }

}
