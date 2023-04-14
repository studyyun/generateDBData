package com.raymond.utils;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 配置文件信息
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-19 17:28
 */
public class PropUtils {
    private static Logger logger = Logger.getLogger(PropUtils.class);
    private static Properties properties;
    static {
        InputStream in;
        try {
            properties = new Properties();
            in = new FileInputStream("SystemGlobals.properties");
            properties.load(in);
        } catch (IOException e) {
            logger.error("获取配置文件异常", e);
        }
    }
    public static String getProp(String key){
        return getProp(key, "");
    }
    public static String getProp(String key, String defaultVal){
        return properties.getProperty(key, defaultVal);
    }
}
