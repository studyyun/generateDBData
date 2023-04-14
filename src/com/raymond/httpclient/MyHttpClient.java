package com.raymond.httpclient;

import com.raymond.httpclient.enums.HttpMethod;

import java.util.Map;

/**
 * Http请求类
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-05 12:44
 */
public interface MyHttpClient {
    /**
     * 请求
     * @param url 请求url
     * @return String
     */
    String execute(String url);

    /**
     * 请求
     * @param url  请求url
     * @param obj 请求数据
     * @return 结果集
     */
    String execute(String url, Object obj);
    /**
     * 请求
     * @param url 请求url
     * @param obj 发送的数据
     * @param clazz 类型
     * @return 结果集
     */
    <T> T execute(String url, Object obj, Class<T> clazz);
    /**
     * 请求
     * @param url 请求url
     * @param obj 发送的数据
     * @param httpMethod 请求类型
     * @param clazz 类型
     * @return 结果集
     */
    <T> T execute(String url, Object obj, HttpMethod httpMethod, Class<T> clazz);
    /**
     * 请求
     * @param url 请求url
     * @param httpMethod 请求类型
     * @param obj 发送的数据
     * @param header 请求头
     * @param clazz 类型
     * @return 结果集
     */
    <T> T execute(String url, Object obj, HttpMethod httpMethod, Map<String, String> header, Class<T> clazz);
    /**
     * 请求
     * @param url 请求url
     * @return String
     */
    Result executeResult(String url);

    /**
     * 请求
     * @param url  请求url
     * @param obj 请求数据
     * @return 结果集
     */
    Result executeResult(String url, Object obj);
    /**
     * 请求
     * @param url 请求url
     * @param obj 发送的数据
     * @param httpMethod 请求类型
     * @return 结果集
     */
    Result executeResult(String url, Object obj, HttpMethod httpMethod);
    /**
     * 请求
     * @param url 请求url
     * @param httpMethod 请求类型
     * @param obj 发送的数据
     * @param header 请求头
     * @return 结果集
     */
    Result executeResult(String url, Object obj, HttpMethod httpMethod, Map<String, String> header);
}
