package com.ls.crawler;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

/**
 * @author ceshi
 * @Title: JunitTestJS
 * @ProjectName ceshi
 * @Description: java 运行js
 * @date 2018/7/1016:35
 */
public class JunitTestJS {

    @Test
    public void test(){
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        try{
            engine.eval("function add(a,b){" +
                    "return a+b;" +
                    "}");
            if (engine instanceof Invocable) {
                Invocable in = (Invocable) engine;
                System.out.println(in.invokeFunction("add",1,1));
            }
            }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void test2(){
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        try{
            FileInputStream fis = new FileInputStream("F:\\dev\\workspace_idea\\crawler\\src\\main\\java\\com\\ls\\crawler\\a.js");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            IOUtils.copy(fis, bos);

            String js = bos.toString();

            engine.eval("var playerObjList = {}; " + js);

            System.out.println(engine.get("media_8"));

//            if (engine instanceof Invocable) {
//                System.out.println(engine.get("media_7"));
//                Invocable in = (Invocable) engine;
////                System.out.println(in.in("media_8"));
//            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
