package com.raymond.httpclient;

import com.raymond.httpclient.enums.ErrorCode;
import com.raymond.httpclient.enums.HttpMethod;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Map;

/**
 * Xml请求
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-05 14:58
 */
public class XmlHttpClient implements MyHttpClient {
    private final static Logger logger = Logger.getLogger(HttpPollManager.class);

    private HttpClient httpClient;
    @Override
    public String execute(String url) {
        return execute(url, null);
    }

    @Override
    public String execute(String url, Object obj) {
        return execute(url, obj, String.class);
    }

    @Override
    public <T> T execute(String url, Object obj, Class<T> clazz) {
        return execute(url, obj, null, clazz);
    }

    @Override
    public <T> T execute(String url, Object obj, HttpMethod httpMethod, Class<T> clazz) {
        return execute(url, obj, httpMethod, null, clazz);
    }

    @Override
    public <T> T execute(String url, Object obj, HttpMethod httpMethod, Map<String, String> header, Class<T> clazz){
        HttpUriRequest httpRequest = HttpClientFactory.getHttpPost(url, obj, HttpClientFactory.XML);
        HttpClientFactory.setHeader(httpRequest, header, HttpClientFactory.XML);
        String result;
        try {
            if (httpClient == null) {
                httpClient = HttpClientFactory.getHttpClient();
            }
            result = HttpClientFactory.execute(httpClient, httpRequest);
        } catch (IOException e) {
            logger.error("请求异常:", e);
            result = ErrorCode.ERROR_CODE_310099.getStrErrorCode();
        }
        if (String.class.isAssignableFrom(clazz)) {
            return clazz.cast(result);
        }
        if (ErrorCode.ERROR_CODE_310099.getStrErrorCode().equals(result)) {
            if (clazz.isAssignableFrom(Result.class)) {
                return clazz.cast(new Result(ErrorCode.ERROR_CODE_310099));
            }
            return null;
        }
        return HttpClientFactory.resultStr(result, clazz, HttpClientFactory.XML);
    }

    @Override
    public Result executeResult(String url) {
        return executeResult(url, null);
    }

    @Override
    public Result executeResult(String url, Object obj) {
        return executeResult(url, obj, null);
    }

    @Override
    public Result executeResult(String url, Object obj, HttpMethod httpMethod) {
        return executeResult(url, obj, httpMethod, null);
    }

    @Override
    public Result executeResult(String url, Object obj, HttpMethod httpMethod, Map<String, String> header) {
        return execute(url, obj, httpMethod, header, Result.class);
    }
}
