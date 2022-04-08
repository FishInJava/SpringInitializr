package com.happyzombie.springinitializr.common.component;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.happyzombie.springinitializr.common.bean.WebBrowserConstant;
import com.happyzombie.springinitializr.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.InterruptedIOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author admin
 */
@Component
@Slf4j
public class ApacheHttpClientComponent {
    @Value("${proxy.host}")
    private String proxyHost;
    @Value("${proxy.port}")
    private Integer proxyPort;

    @Value("#{'${near.server.ssl.protocol}'.split(',')}")
    private String[] nearSupportProtocols;
    @Value("#{'${tls13.spport.ciphersuites}'.split(',')}")
    private String[] cipherSuites;

    /**
     * 连接管理器
     */
    public static final PoolingHttpClientConnectionManager CONN_MANAGER = new PoolingHttpClientConnectionManager();

    static {
        // 设置最大连接数
        CONN_MANAGER.setMaxTotal(200);
        // 设置每个连接的路由数
        CONN_MANAGER.setDefaultMaxPerRoute(20);
    }

    /**
     * 重试处理器
     * 测出超时重试机制为了防止超时不生效而设置
     * 如果直接放回false,不重试,这里会根据情况进行判断是否重试
     *
     * @param retryTime 重试次数
     * @return HttpRequestRetryHandler
     */
    public static HttpRequestRetryHandler getRetryHandler(int retryTime) {
        return (exception, executionCount, context) -> {
            if (executionCount >= retryTime) {// 如果已经重试了3次，就放弃
                return false;
            }
            if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                log.error("服务器丢掉了连接，重试");
                return true;
            }
            if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                return false;
            }
            if (exception instanceof InterruptedIOException) {// 超时
                log.error("======超时");
                return true;
            }
            if (exception instanceof UnknownHostException) {// 目标服务器不可达
                log.error("目标服务器不可达");
                return false;
            }
            if (exception instanceof SSLException) {// ssl握手异常
                log.error("ssl握手异常");
                return false;
            }
            HttpClientContext clientContext = HttpClientContext.adapt(context);
            HttpRequest request = clientContext.getRequest();
            // 如果请求是幂等的，就再次尝试
            return !(request instanceof HttpEntityEnclosingRequest);
        };
    }

    /**
     * http客户端
     */
    private volatile CloseableHttpClient onlySsl13Client;
    private volatile CloseableHttpClient defaultClient;

    /**
     * 通用请求配置
     */
    private RequestConfig getProxyRequestConfig(HttpHost proxy) {
        return RequestConfig.custom().setProxy(proxy)
                .setConnectTimeout(10000)
                .setSocketTimeout(10000)
                .setConnectionRequestTimeout(10000)
                .build();
    }

    private Header[] getBasicHeaders(String host, String origin, String referer) {
        // url
        final BasicHeader hostUrl = new BasicHeader("Host", host);
        final BasicHeader originUrl = new BasicHeader("Origin", origin);
        final BasicHeader refererUrl = new BasicHeader("Referer", referer);
        //
        final BasicHeader connection = new BasicHeader("Connection", WebBrowserConstant.KEEP_ALIVE);
        final BasicHeader contentType = new BasicHeader("Content-Type", WebBrowserConstant.JSON_CONTENT_TYPE);
        final BasicHeader acceptEncoding = new BasicHeader("Accept-Encoding", WebBrowserConstant.ACCEPT_ENCODING);
        final BasicHeader accept = new BasicHeader("Accept", WebBrowserConstant.ACCEPT_ALL);
        // 模拟浏览器
        final BasicHeader userAgent = new BasicHeader("User-Agent", WebBrowserConstant.USER_AGENT);
        // 浏览器缓存
        final BasicHeader pragma = new BasicHeader("Pragma", WebBrowserConstant.NO_CACHE);
        final BasicHeader cacheControl = new BasicHeader("Cache-Control", WebBrowserConstant.NO_CACHE);
        // sec
        final BasicHeader secChUa = new BasicHeader("sec-ch-ua", WebBrowserConstant.SEC_CH_UA);
        final BasicHeader secChUaMobile = new BasicHeader("sec-ch-ua-mobile", WebBrowserConstant.SEC_CH_UA_MOBILE);
        final BasicHeader secChUaPlatform = new BasicHeader("sec-ch-ua-platform", WebBrowserConstant.SEC_CH_UA_PLATFORM);
        final BasicHeader secFetchDest = new BasicHeader("Sec-Fetch-Dest", WebBrowserConstant.SEC_FETCH_DEST);
        final BasicHeader secFetchMode = new BasicHeader("Sec-Fetch-Mode", WebBrowserConstant.SEC_FETCH_MODE);
        final BasicHeader secFetchSite = new BasicHeader("Sec-Fetch-Site", WebBrowserConstant.SEC_FETCH_SITE);
        return new Header[]{hostUrl, originUrl, refererUrl, connection, contentType, acceptEncoding, accept, userAgent, pragma, cacheControl, secChUa, secChUaMobile, secChUaPlatform, secFetchDest, secFetchMode, secFetchSite};
    }

    public HttpGet getBrowserProxyHttpGet(String url, String host, String origin, String referer, Map<String, String> param) {
        // 设置代理
        final HttpHost proxy = new HttpHost(proxyHost, proxyPort);
        final RequestConfig config = getProxyRequestConfig(proxy);

        final Supplier<URI> supplier = () -> {
            final URI uri = URI.create(url);
            if (param != null && !param.isEmpty()) {
                final URIBuilder builder = new URIBuilder(uri);
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key));
                }
                try {
                    return builder.build();
                } catch (Exception e) {
                    log.error("getBrowserProxyHttpGet error", e);
                    throw new RuntimeException("getBrowserProxyHttpGet error", e);
                }
            }
            return uri;
        };

        HttpGet httpGet = new HttpGet(supplier.get());

        httpGet.setConfig(config);
        // 设置通用头
        httpGet.setHeaders(getBasicHeaders(host, origin, referer));
        return httpGet;
    }

    /**
     * post 请求
     */
    public HttpPost getBrowserProxyHttpPost(String url, String host, String origin, String referer) {
        // 设置代理
        final HttpHost proxy = new HttpHost(proxyHost, proxyPort);
        final RequestConfig requestConfig = getProxyRequestConfig(proxy);
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        // url
        httpPost.setHeader("Host", host);
        httpPost.setHeader("Origin", origin);
        httpPost.setHeader("Referer", referer);
        //
        httpPost.setHeader("Connection", WebBrowserConstant.KEEP_ALIVE);
        httpPost.setHeader("Content-Type", WebBrowserConstant.JSON_CONTENT_TYPE);
        httpPost.setHeader("Accept-Encoding", WebBrowserConstant.ACCEPT_ENCODING);
        httpPost.setHeader("Accept", WebBrowserConstant.ACCEPT_ALL);
        // 模拟浏览器
        httpPost.setHeader("User-Agent", WebBrowserConstant.USER_AGENT);
        // 浏览器缓存
        httpPost.setHeader("Pragma", WebBrowserConstant.NO_CACHE);
        httpPost.setHeader("Cache-Control", WebBrowserConstant.NO_CACHE);
        // sec
        httpPost.setHeader("sec-ch-ua", WebBrowserConstant.SEC_CH_UA);
        httpPost.setHeader("sec-ch-ua-mobile", WebBrowserConstant.SEC_CH_UA_MOBILE);
        httpPost.setHeader("sec-ch-ua-platform", WebBrowserConstant.SEC_CH_UA_PLATFORM);
        httpPost.setHeader("Sec-Fetch-Dest", WebBrowserConstant.SEC_FETCH_DEST);
        httpPost.setHeader("Sec-Fetch-Mode", WebBrowserConstant.SEC_FETCH_MODE);
        httpPost.setHeader("Sec-Fetch-Site", WebBrowserConstant.SEC_FETCH_SITE);
        return httpPost;
    }

    /**
     * 获取Ssl13 Http客户端
     */
    public CloseableHttpClient getOnlySsl13Client() {
        if (onlySsl13Client == null) {
            synchronized (ApacheHttpClientComponent.class) {
                if (onlySsl13Client == null) {
                    // 设置ssl，目前只支持TLSv1.3
                    SSLConnectionSocketFactory onlySsl13 = new SSLConnectionSocketFactory(
                            SSLContexts.createDefault(),
                            nearSupportProtocols,
                            cipherSuites,
                            SSLConnectionSocketFactory.getDefaultHostnameVerifier());
                    //
                    onlySsl13Client = HttpClients.custom().
                            setSSLSocketFactory(onlySsl13).
                            setConnectionManager(CONN_MANAGER).
                            setRetryHandler(getRetryHandler(3)).
                            build();
                }
            }
        }
        return onlySsl13Client;
    }

    /**
     * 通用client
     */
    public CloseableHttpClient getDefaultClient() {
        if (defaultClient == null) {
            synchronized (ApacheHttpClientComponent.class) {
                if (defaultClient == null) {
                    defaultClient = HttpClients.custom().
                            setConnectionManager(CONN_MANAGER).
                            setRetryHandler(getRetryHandler(3)).
                            build();
                }
            }
        }
        return defaultClient;
    }

    private StringEntity getGeneralRequest(ObjectNode request) {
        return new StringEntity(JsonUtil.objectToString(request), "UTF-8");
    }

    public <T> T postRequest(HttpPost httpPost, CloseableHttpClient client, ObjectNode request, Class<T> clazz) {
        CloseableHttpResponse response = null;
        try {
            // 设置Entity
            httpPost.setEntity(getGeneralRequest(request));
            // 发送请求
            response = client.execute(httpPost);
            // 检查响应码
            checkResponseCode(response);
            return JsonUtil.jsonStringToObject(EntityUtils.toString(response.getEntity()), clazz);
        } catch (Exception e) {
            log.error("=== apache httpUtil post error,{}", response, e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                    response.close();
                } catch (Exception e) {
                    log.error("close error", e);
                }
            }
        }
        throw new RuntimeException("=== post result is null");
    }

    /**
     * get请求
     */
    public <T> T sendGetRequest(HttpGet httpGet, CloseableHttpClient client, Class<T> clazz) {
        CloseableHttpResponse response = null;
        try {
            // 发送请求
            response = client.execute(httpGet);
            // 检查响应码
            checkResponseCode(response);
            return JsonUtil.jsonStringToObject(EntityUtils.toString(response.getEntity()), clazz);
        } catch (Exception e) {
            log.error("=== apache httpUtil get error,{}", response, e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                    response.close();
                } catch (Exception e) {
                    log.error("close error", e);
                }
            }
        }
        throw new RuntimeException("=== get result is null");
    }

    /**
     * get请求，返回的结果是一个字符串数组
     */
    public <T> List<T> sendGetRequest2(HttpGet httpGet, CloseableHttpClient client, Class<T> clazz) {
        CloseableHttpResponse response = null;
        try {
            // 发送请求
            response = client.execute(httpGet);
            // 检查响应码
            checkResponseCode(response);
            return JsonUtil.jsonStringToList(EntityUtils.toString(response.getEntity()), clazz);
        } catch (Exception e) {
            log.error("http with near error,{}", response, e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                    response.close();
                } catch (Exception e) {
                    log.error("close error", e);
                }
            }
        }
        throw new RuntimeException("nearRequest result is null");
    }

    private void checkResponseCode(HttpResponse response) {
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new RuntimeException("============ response is not 200 ===============");
        }
    }

}
