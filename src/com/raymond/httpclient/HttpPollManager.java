package com.raymond.httpclient;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.concurrent.TimeUnit;

/**
 * Http连接池
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-05 11:36
 */
public class HttpPollManager {



    public static HttpClient getHttpClient() {
        return HttpConnectManager.getInstance().buildHttpClient();
    }

    private static class HttpConnectManager {
        /**
         * 连接配置对象
         */
        private HttpClientBuilder builder;


        private HttpConnectManager() {
            builder = httpClientBuilder();
            RequestConfig.Builder configBuilder = RequestConfig.custom();
            //从连接池获取连接超时时间
            configBuilder.setConnectionRequestTimeout(HttpEntity.connectionRequestTimeout);
            //连接超时时间
            configBuilder.setConnectTimeout(HttpEntity.connectTimeout);
            //响应超时时间
            configBuilder.setSocketTimeout(HttpEntity.socketTimeout);
            RequestConfig config = configBuilder.build();
            builder.setDefaultRequestConfig(config);
        }


        private HttpClientBuilder httpClientBuilder() {
            HttpClientBuilder builder;
            ConnectionSocketFactory socketFactory = PlainConnectionSocketFactory.getSocketFactory();
            LayeredConnectionSocketFactory sslConnectionSocketFactory = SSLConnectionSocketFactory
                    .getSocketFactory();
            Registry<ConnectionSocketFactory> registry = RegistryBuilder
                    .<ConnectionSocketFactory> create().register("http", socketFactory)
                    .register("https", sslConnectionSocketFactory).build();
            PoolingHttpClientConnectionManager clientConnectionManager = new PoolingHttpClientConnectionManager(
                    registry);
            //设置最大连接数
            clientConnectionManager.setMaxTotal(HttpEntity.maxTotal);
            //设置每个ip最大连接数
            clientConnectionManager.setDefaultMaxPerRoute(HttpEntity.maxPerRoute);
            builder = HttpClients.custom();
            builder.setConnectionManager(clientConnectionManager);
            //25秒没用连接会自动清理
            builder.evictIdleConnections(HttpEntity.clearTime, TimeUnit.SECONDS);
            return builder;
        }

        /**
         * 从连接池中获取一个连接对象
         * @return HttpClient
         * */
        HttpClient buildHttpClient() {
            return builder.build();
        }

        private static class ConnectorHolder {
            private static final HttpConnectManager INSTANCE = new HttpConnectManager();
        }

        /**
         * 获取连接池管理类
         *
         * @return HttpConnectManager
         */
        static HttpConnectManager getInstance() {
            return ConnectorHolder.INSTANCE;
        }

    }

    /**
     * 初始化配置连接池对象
     * */
    private HttpPollManager() {

    }

}
