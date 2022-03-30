package com.happyzombie.springinitializr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.happyzombie.springinitializr.bean.response.nearcore.BlockDetailsResponse;
import com.happyzombie.springinitializr.bean.response.nearcore.ChunkDetailsResponse;
import com.happyzombie.springinitializr.bean.response.nearcore.NearGeneralResponse;
import com.happyzombie.springinitializr.bean.response.nearcore.ReceiptDetailsResponse;
import com.happyzombie.springinitializr.bean.response.nearcore.TxStatusResponse;
import com.happyzombie.springinitializr.bean.response.nearcore.ViewAccountResponse;
import com.happyzombie.springinitializr.common.bean.WebBrowserConstant;
import com.happyzombie.springinitializr.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

/**
 * 参考文档
 * https://docs.near.org/docs/api/rpc
 * 接口定义
 * https://github.com/near/nearcore/blob/bf9ae4ce8c680d3408db1935ebd0ca24c4960884/chain/jsonrpc/client/src/lib.rs#L181
 * 说明
 * POST for all methods
 * JSON RPC 2.0:基于http请求的一种约定的报文格式
 * id: "dontcare"
 * 注意事项
 * 服务端只接受TLSv1.3
 * <p>
 * todo jsonrpc4j研究下怎么配置可以生效 , 如何在Feign中生效代理和配置TLSv1.3
 */
@Service
@Slf4j
public class NearRpcServiceImpl implements NearRpcService {
    @Value("#{'${near.server.ssl.protocol}'.split(',')}")
    private String[] nearSupportProtocols;
    @Value("${nearcore.rpc.mainnet}")
    private String mainNet;
    @Value("${nearcore.rpc.historical.mainnet}")
    private String historicalMainNet;
    @Value("${proxy.host}")
    private String proxyHost;
    @Value("${proxy.port}")
    private Integer proxyPort;
    @Value("#{'${tls13.spport.ciphersuites}'.split(',')}")
    private String[] cipherSuites;

    // http客户端
    private volatile CloseableHttpClient onlySsl13Client;

    // 连接管理器
    private static final PoolingHttpClientConnectionManager CONN_MANAGER = new PoolingHttpClientConnectionManager();

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
    private HttpRequestRetryHandler getRetryHandler(int retryTime) {
        return (exception, executionCount, context) -> {
            if (executionCount >= retryTime) {// 如果已经重试了3次，就放弃
                return false;
            }
            if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                return true;
            }
            if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                return false;
            }
            if (exception instanceof InterruptedIOException) {// 超时
                return true;
            }
            if (exception instanceof UnknownHostException) {// 目标服务器不可达
                return false;
            }
            if (exception instanceof SSLException) {// ssl握手异常
                return false;
            }
            HttpClientContext clientContext = HttpClientContext.adapt(context);
            HttpRequest request = clientContext.getRequest();
            // 如果请求是幂等的，就再次尝试
            return !(request instanceof HttpEntityEnclosingRequest);
        };
    }

    // 获取Http客户端
    private CloseableHttpClient getOnlySsl13Client() {
        if (onlySsl13Client == null) {
            synchronized (NearRpcServiceImpl.class) {
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
     * 获取near格式的通用HttpPost
     *
     * @return HttpPost
     */
    private HttpPost getGeneralProxyHttpPost() {
        // 设置代理
        final HttpHost proxy = new HttpHost(proxyHost, proxyPort);
        RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy).build();
        HttpPost httpPost = new HttpPost(mainNet);
        httpPost.setConfig(requestConfig);
        // setHeader
        httpPost.setHeader("host", "rpc.mainnet.near.org:443");
        httpPost.setHeader("Connection", "keep-alive");
        httpPost.setHeader("Content-Type", "application/json");
        // 模拟postMan
        httpPost.setHeader("User-Agent", "PostmanRuntime/7.29.0");
        httpPost.setHeader("Accept-Encoding", WebBrowserConstant.ACCEPT_ENCODING);
        return httpPost;
    }

    private HttpPost getHistoricalProxyHttpPost() {
        // 设置代理
        final HttpHost proxy = new HttpHost(proxyHost, proxyPort);
        RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy).build();
        HttpPost httpPost = new HttpPost(historicalMainNet);
        httpPost.setConfig(requestConfig);
        // setHeader
        httpPost.setHeader("host", "archival-rpc.mainnet.near.org:443");
        httpPost.setHeader("Connection", "keep-alive");
        httpPost.setHeader("Content-Type", "application/json");
        // 模拟postMan
        httpPost.setHeader("User-Agent", "PostmanRuntime/7.29.0");
        httpPost.setHeader("Accept-Encoding", WebBrowserConstant.ACCEPT_ENCODING);
        return httpPost;
    }

    /**
     * 通用的near request对象
     *
     * @param method 方法名
     * @param params 参数
     * @return StringEntity
     */
    private StringEntity getGeneralRequest(String method, JsonNode params) {
        // 设置请求参数
        final ObjectNode request = JsonUtil.getObjectNode();
        request.put("jsonrpc", "2.0");
        request.put("id", "dontcare");
        request.put("method", method);
        request.set("params", params);
        return new StringEntity(JsonUtil.objectToString(request), "UTF-8");
    }

    /**
     * 获取交易状态
     *
     * @param transactionHash transactionHash
     * @param senderAccountId senderAccountId
     */
    @Override
    public TxStatusResponse getTransactionStatus(String transactionHash, String senderAccountId) {
        final ArrayNode params = JsonUtil.getArrayNode();
        params.add(transactionHash);
        params.add(senderAccountId);
        return generalNearRequest("EXPERIMENTAL_tx_status", params, TxStatusResponse.class);
    }

    /**
     * 获取Account信息
     * {
     * "jsonrpc": "2.0",
     * "id": "dontcare",
     * "method": "query",
     * "params": {
     * "request_type": "view_account",
     * "finality": "final",
     * "account_id": "nearkat.testnet"
     * }
     * }
     *
     * @param accountId accountId
     */
    @Override
    public ViewAccountResponse viewAccount(String accountId) {
        final ObjectNode request = JsonUtil.getObjectNode();
        request.put("request_type", "view_account");
        request.put("finality", "final");
        request.put("account_id", accountId);
        return generalNearRequest("query", request, ViewAccountResponse.class);
    }

    @Override
    public BlockDetailsResponse getLatestBlockDetail() {
        final ObjectNode request = JsonUtil.getObjectNode();
        request.put("finality", "final");
        return generalNearRequest("block", request, BlockDetailsResponse.class);
    }

    @Override
    public BlockDetailsResponse getBlockDetailByBlockId(Long blockId) {
        final ObjectNode request = JsonUtil.getObjectNode();
        request.put("block_id", blockId);
        return generalNearRequest("block", request, BlockDetailsResponse.class);
    }

    @Override
    public BlockDetailsResponse getBlockDetailByBlockHash(String blockHash) {
        final ObjectNode request = JsonUtil.getObjectNode();
        request.put("block_id", blockHash);
        return generalNearRequest("block", request, BlockDetailsResponse.class);
    }

    @Override
    public BlockDetailsResponse getHistoricalBlockDetailByBlockHash(String blockHash) {
        final ObjectNode request = JsonUtil.getObjectNode();
        request.put("block_id", blockHash);
        return historicalNearRequest("block", request, BlockDetailsResponse.class);
    }

    @Override
    public ChunkDetailsResponse getChunkDetailsById(String chunkId) {
        final ObjectNode request = JsonUtil.getObjectNode();
        request.put("chunk_id", chunkId);
        try {
            final String chunkString = nearRequest(getGeneralProxyHttpPost(), "chunk", request);
            // 返回的结果里面List<Action>是不规则的，有字符串，又有对象；如果有字符串，替换成对象
            ChunkDetailsResponse chunkDetailsResponse;
            if (chunkString.contains("CreateAccount")) {
                final String result = JsonUtil.stringReplaceToJsonObject(chunkString, "CreateAccount", "{\"CreateAccount\": \"CreateAccount\"}");
                chunkDetailsResponse = JsonUtil.jsonStringToObject(result, ChunkDetailsResponse.class);
            } else {
                chunkDetailsResponse = JsonUtil.jsonStringToObject(chunkString, ChunkDetailsResponse.class);
            }
            if (chunkDetailsResponse.getError() != null) {
                throw new RuntimeException("near rpc return error message," + chunkDetailsResponse.getError());
            }
            return chunkDetailsResponse;
        } catch (Exception e) {
            log.error("getChunkDetailsById error", e);
            throw e;
        }
    }

    @Override
    public ChunkDetailsResponse getHistoricalChunkDetailsById(String chunkId) {
        final ObjectNode request = JsonUtil.getObjectNode();
        request.put("chunk_id", chunkId);
        return historicalNearRequest("chunk", request, ChunkDetailsResponse.class);
    }

    @Override
    public ReceiptDetailsResponse getReceiptById(String receiptId) {
        final ObjectNode request = JsonUtil.getObjectNode();
        request.put("receipt_id", receiptId);
        return generalNearRequest("EXPERIMENTAL_receipt", request, ReceiptDetailsResponse.class);
    }

    private <T extends NearGeneralResponse> T generalNearRequest(String methodName, JsonNode params, Class<T> clazz) {
        return nearRequest(getGeneralProxyHttpPost(), methodName, params, clazz);
    }

    /**
     * Querying historical data (older than 5 epochs or ~2.5 days)
     */
    private <T extends NearGeneralResponse> T historicalNearRequest(String methodName, JsonNode params, Class<T> clazz) {
        return nearRequest(getHistoricalProxyHttpPost(), methodName, params, clazz);
    }

    private String nearRequest(HttpPost httpPost, String methodName, JsonNode params) {
        CloseableHttpResponse response = null;
        try {
            // 获取httpClient
            final CloseableHttpClient onlySsl13Client = getOnlySsl13Client();
            // 设置Entity
            httpPost.setEntity(getGeneralRequest(methodName, params));
            // 发送请求
            response = onlySsl13Client.execute(httpPost);
            // 检查响应码
            checkResponseCode(response);
            return EntityUtils.toString(response.getEntity());
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

    private <T extends NearGeneralResponse> T nearRequest(HttpPost httpPost, String methodName, JsonNode params, Class<T> clazz) {
        CloseableHttpResponse response = null;
        try {
            // 获取httpClient
            final CloseableHttpClient onlySsl13Client = getOnlySsl13Client();
            // 设置Entity
            httpPost.setEntity(getGeneralRequest(methodName, params));
            // 发送请求
            response = onlySsl13Client.execute(httpPost);
            // 检查响应码
            checkResponseCode(response);
            final T nearGeneralResponse = JsonUtil.jsonStringToObject(EntityUtils.toString(response.getEntity()), clazz);
            if (nearGeneralResponse.getError() != null) {
                throw new RuntimeException("near rpc return error message," + nearGeneralResponse.getError());
            }
            return nearGeneralResponse;
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

//    private Map<String, String> getHeaders() {
//        final HashMap<String, String> map = new HashMap<>();
//        map.put("Accept-Encoding", WebBrowserConstant.ACCEPT_ENCODING);
//        map.put("User-Agent", "PostmanRuntime/7.29.0");
//        map.put("Content-Type", "application/json");
//        map.put("Accept", "*/*");
//        map.put("Connection", "keep-alive");
//        map.put("host", "rpc.mainnet.near.org:443");
//        return map;
//    }

//    public void getTransactionStatus(String transactionHash, String senderAccountId) {
//        try {
//            /**
//             * 猜想
//             * jsonrpc4j主要是java平台下配套使用的，Client也是针对JavaServer的
//             */
//            JsonRpcHttpClient client = new JsonRpcHttpClient(new URL(mainnet));
//
//            // 设置代理
//            final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(InetAddress.getLocalHost(), 58591));
//            client.setConnectionProxy(proxy);
//
//            // 请求参数
//            final ObjectNode request = JsonUtil.getObjectNode();
//            request.put("jsonrpc", "2.0");
//            request.put("id", "dontcare");
//            request.put("method", "EXPERIMENTAL_tx_status");
//            final ArrayNode arrayNode = JsonUtil.getArrayNode();
//            arrayNode.add(transactionHash);
//            arrayNode.add(senderAccountId);
//            request.set("params", arrayNode);
//            TxStatusResponse res = client.invoke("EXPERIMENTAL_tx_status", request, TxStatusResponse.class, getHeaders());
//            log.info("getTransactionStatus {}", res);
//        } catch (Throwable e) {
//            log.error("getTransactionStatus err !", e);
//        }
//    }

//    @Autowired
//    NearRpcClient nearRpcClient;
//
//    public void postFeign(String transactionHash, String senderAccountId) {
//        final ObjectNode request = JsonUtil.getObjectNode();
//        request.put("jsonrpc", "2.0");
//        request.put("id", "dontcare");
//        request.put("method", "EXPERIMENTAL_tx_status");
//        final ArrayNode arrayNode = JsonUtil.getArrayNode();
//        arrayNode.add(transactionHash);
//        arrayNode.add(senderAccountId);
//        request.set("params", arrayNode);
//        final ResponseEntity<byte[]> nftAttributesRarity = nearRpcClient.getNFTAttributesRarity(JsonUtil.objectToString(request));
//        System.out.println(1);
//    }


}
