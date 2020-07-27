package com.ls.http;

import java.io.File;

public class FileUtils {

    public static void mkdirs(String path){
        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
    }

}
