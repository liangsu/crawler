package com.ls.http;

import org.apache.commons.io.IOUtils;
import ws.schild.jave.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class VideoUtils {

    public static void concatVideo(String path, String fileListName, String mp4Name){
        // ffmpeg -f concat -safe 0 -i filelist.txt -c copy output.mp4
        String cmd = "ffmpeg -f concat -safe 0 -i fileList.txt -c copy output.mp4 -y";
        cmd = cmd.replace("fileList.txt", path + fileListName);
        cmd = cmd.replace("output.mp4", path + mp4Name+".mp4");

        cmd = cmd.replaceAll("\\\\", "/");
        System.out.println(cmd);

        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(cmd);

            InputStream is = process.getErrorStream();
            IOUtils.copy(is, System.out);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String path = "G:\\video\\贵在真实年轻大学生情侣周日校外开房啪啪啪小妹子叫的很给力表情销魂\\";
        concatVideo(path, "fileList.txt", "aa.mp4");
    }

//    public static File convertFlvToMP4(String inputPath, String outDir){
//        int outFileNamePos = inputPath.lastIndexOf(File.separator);
//        String fileName = inputPath.substring(outFileNamePos + 1); // 文件名称：aa.flv
//
////        ffmpeg -f concat -safe 0 -i filelist.txt -c copy output.mp4
//
//        if(!outDir.endsWith(File.separator)){
//            outDir += File.separator;
//        }
//
//        File source = new File(inputPath);
////        File target = new File(outDir + fileNamePrefix + ".mp4");
//
//        // 视频设置
//        VideoAttributes video = new VideoAttributes();
//        video.setCodec("copy"); // 复制视频
//        // 音频设置
//        AudioAttributes audio = new AudioAttributes();
//        audio.setCodec("copy"); // 复制音频
//
//        EncodingAttributes attrs = new EncodingAttributes();
//        attrs.setFormat("concat");
//        attrs.setVideoAttributes(video);
//        attrs.setAudioAttributes(audio);
//        try {
//            Encoder encoder = new Encoder();
//            encoder.encode(new MultimediaObject(source), target, attrs);
//        } catch (EncoderException e) {
//            target = null;
//        }
//
//        return target;
//    }

}
