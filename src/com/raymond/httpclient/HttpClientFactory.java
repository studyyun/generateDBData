package com.raymond.httpclient;


import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSONObject;
import com.raymond.httpclient.enums.ErrorCode;
import com.raymond.httpclient.enums.HttpMethod;
import com.raymond.xml.utils.MyXmlUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * httpClient工厂
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-05 15:05
 */
public class HttpClientFactory {

    private final static Logger logger = Logger.getLogger(HttpClientFactory.class);
    /**
     * xml
     */
    static final String XML = "XML";
    /**
     * json
     */
    private static final String JSON = "JSON";

    /**
     * 请求和响应编码格式
     */
    private static String CHARSET = "UTF-8";



    /**
     * 获取json请求
     *
     * @return JsonHttpClient
     */
    public static MyHttpClient getMyHttpClient(Type type) {
        if (Type.XML == type) {
            return new XmlHttpClient();
        }
        if (Type.JSON == type) {
            return new JsonHttpClient();
        }
        return new JsonHttpClient();
    }



    /**
     * 获取 HttpClient
     *
     * @return HttpClient
     */
    static HttpClient getHttpClient() {
        return HttpPollManager.getHttpClient();
    }

    /**
     * 获取HttpPost
     *
     * @param url         请求url
     * @param obj         请求数据
     * @param contentType 请求类型
     * @return HttpPost
     */
    static HttpPost getHttpPost(String url, Object obj, String contentType) {
        HttpPost httpPost = new HttpPost(url);
        StringEntity stringEntity;
        if (obj != null) {
            String entityValue;
            if (XML.equals(contentType)) {
                entityValue = MyXmlUtil.convertToXml(obj);
            } else {
                entityValue = JSONObject.toJSONString(obj);
            }
            stringEntity = new StringEntity(entityValue, CHARSET);
            httpPost.setEntity(stringEntity);
        }
        return httpPost;
    }

    /**
     * 获取HttpGet
     *
     * @param url url
     * @param obj 数据
     * @return HttpGet
     */
    private static HttpGet getHttpGet(String url, Object obj) {
        URIBuilder builder;
        try {
            builder = new URIBuilder(url);
            if (obj != null) {
                Map beanMap = Convert.convert(Map.class, obj);
                for (Object key : beanMap.keySet()) {
                    builder.setParameter(key + "", beanMap.get(key).toString());
                }
            }
            return new HttpGet(builder.build());
        } catch (URISyntaxException e) {
            logger.error("获取HttpGet请求异常", e);
            return new HttpGet();
        }

    }


    /**
     * 获取 HttpUriRequest
     *
     * @param url 请求地址
     * @return HttpUriRequest
     */
    public static HttpUriRequest getHttpUriRequest(String url) {
        return getHttpUriRequest(url, null, HttpMethod.POST);
    }


    /**
     * 获取 HttpUriRequest
     *
     * @param url        请求地址
     * @param obj        请求数据
     * @param httpMethod 请求类型
     * @return HttpUriRequest
     */
    static HttpUriRequest getHttpUriRequest(String url, Object obj, HttpMethod httpMethod) {
        if (httpMethod == HttpMethod.GET) {
            return getHttpGet(url, obj);
        } else {
            return getHttpPost(url, obj, JSON);
        }
    }

    /**
     * 设置请求头,默认 Content-Type:application/json
     *
     * @param httpRequest httpRequest
     * @param headers     headers
     */
    static void setHeader(HttpRequest httpRequest, Map<String, String> headers) {
        setHeader(httpRequest, headers, JSON);
    }

    /**
     * 设置头部信息
     *
     * @param httpRequest httpRequest
     * @param headers     headers
     */
    static void setHeader(HttpRequest httpRequest, Map<String, String> headers, String contentType) {
        if (XML.equals(contentType)) {
            httpRequest.setHeader("Content-Type", "text/xml");
        } else {
            httpRequest.setHeader("Content-Type", "application/json");
        }
        if (null == headers || headers.isEmpty()) {
            return;
        }
        for (String key : headers.keySet()) {
            httpRequest.setHeader(key, headers.get(key));
        }
    }

    /**
     * 将结果集转换成对象
     *
     * @param result 结果集
     * @param clazz  对象类型
     * @return 对象
     */
    static <T> T resultStr(String result, Class<T> clazz) {
        return resultStr(result, clazz, JSON);
    }

    /**
     * 将结果集转换成对象
     *
     * @param result      结果集
     * @param clazz       对象类型
     * @param contentType 结果集的类型,仅支持xml和json
     * @return 对象
     */
    static <T> T resultStr(String result, Class<T> clazz, String contentType) {
        if (clazz.isAssignableFrom(String.class)) {
            return clazz.cast(result);
        }
        if (ErrorCode.ERROR_CODE_310099.getStrErrorCode().equals(result)) {
            if (clazz.isAssignableFrom(Result.class)) {
                return clazz.cast(new Result<T>(ErrorCode.ERROR_CODE_310099));
            }
            return null;
        }
        if (XML.equals(contentType)) {
            return MyXmlUtil.convertXmlStrToObject(clazz, result);
        }
        return JSONObject.parseObject(result, clazz);
    }

    /**
     * http请求
     *
     * @param httpClient  HttpClient
     * @param httpRequest HttpUriRequest
     * @return 请求结果
     * @throws IOException io异常
     */
    static String execute(HttpClient httpClient, HttpUriRequest httpRequest) throws IOException {
        String result = ErrorCode.ERROR_CODE_310099.getStrErrorCode();
        HttpEntity entity;
        HttpResponse httpResponse = httpClient.execute(httpRequest);
        int isOk = 200;
        if ( httpResponse.getStatusLine().getStatusCode() == isOk) {
            entity = httpResponse.getEntity();
            if (entity != null) {
                try {
                    //请求成功，能获取到响应内容
                    result = EntityUtils.toString(entity, CHARSET);
                } catch (Exception e) {
                    logger.error("请求获取不到响应内容", e);
                }
            }
        } else {
            logger.error("请求错误:" + httpResponse.getStatusLine());
        }
        return result;
    }
    public enum Type {
        /**
         * xml格式
         */
        XML,
        /**
         * json格式
         */
        JSON;
    }
}
