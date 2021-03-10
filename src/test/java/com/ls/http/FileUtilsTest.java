package com.ls.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author liangsu
 * @version v1.0
 * @Description
 * @Date 2021/3/10 11:05
 * @since
 */
class FileUtilsTest {

    @Test
    void getFileNameFromPath() {
        System.out.println(FileUtils.getFileNameFromPath("C:\\Users\\Administrator\\Desktop\\aa"));
        System.out.println(FileUtils.getFileNameFromPath("C:\\Users\\Administrator\\Desktop/aa"));
        System.out.println(FileUtils.getFileNameFromPath("C:\\Users\\Administrator\\Desktop/aa.mp4"));
    }

    @Test
    void getDirectoryFromPath() {
        System.out.println(FileUtils.getDirectoryFromPath("C:\\Users\\Administrator\\Desktop\\aa"));
        System.out.println(FileUtils.getDirectoryFromPath("C:\\Users\\Administrator\\Desktop/aa"));
        System.out.println(FileUtils.getDirectoryFromPath("C:\\Users\\Administrator\\Desktop/aa.mp4"));
    }
}