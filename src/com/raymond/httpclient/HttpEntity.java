package com.raymond.httpclient;

import org.apache.log4j.Logger;

/**
 * http属性
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-05 11:38
 */
public class HttpEntity {
    private final static Logger logger = Logger.getLogger(HttpEntity.class);
    /**
     * 从连接池中取连接的超时时间默认2秒
     */
    public static int connectionRequestTimeout = 30 * 1000;

    /**
     * 连接超时时间默认5秒
     */
    public static int connectTimeout = 20 * 1000;

    /**
     * 响应超时时间默认5秒
     */
    public static int socketTimeout = 60 * 1000;

    /**
     * 连接池最大连接数默认10个
     */
    public static int maxTotal = 20;

    /**
     * 每个ip最大连接数默认2个
     */
    public static int maxPerRoute = 10;
    /**
     * 自动清理时间
     */
    public static long clearTime = 15;
    /**
     * 设置从连接池获取连接超时时间
     * @param time 超时间
     * @return void
     * */
    public static void setRequestTimeout(int time) {
        if (time > 0) {
            connectionRequestTimeout = time;
        } else {
            logger.info("连接池获取连接超时时间设置失败默认设置为30秒");
        }
    }
    /**
     * 设置连接超时时间
     * @param time 超时间
     * @return void
     * */
    public static void setConnectTimeout(int time) {
        if (time > 0) {
            connectTimeout = time;
        } else {
            logger.info("连接超时时间设置失败默认设置为20秒");
        }

    }
    /**
     * 设置响应超时时间
     * @param time 超时间
     * @return void
     * */
    public static void setSocketTimeout(int time) {
        if (time > 0) {
            socketTimeout = time;
        } else {
            logger.info("响应超时时间设置失败默认设置为60秒");
        }
    }
    /**
     * 设置连接最大数和每个ip连接最大数
     * @param total 连接最大数
     * @param route 每个ip连接最大数
     * @return void
     * */
    public static void setMaxTotal(int total, int route) {
        if (route > 0 && total >= route) {
            maxTotal = total;
            maxPerRoute = route;
        } else {
            logger.info("连接池最大连接数和ip最大连接数设置不符合规定按照默认值设置");
        }
    }
    /**
     * 设置自动清理空余连接时间
     * @param time 时间秒
     * @return void
     * */
    public static void setClearTime(int time){
        if(time>0){
            clearTime = time;
        }else{
            logger.info("自动清理空余连接时间失败默认设置为15秒");
        }
    }
}
