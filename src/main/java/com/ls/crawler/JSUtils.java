package com.ls.crawler;

import org.apache.commons.io.IOUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

public class JSUtils {

    public static ScriptEngine runJs(String js){
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        try{
            engine.eval(js);
            return engine;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
