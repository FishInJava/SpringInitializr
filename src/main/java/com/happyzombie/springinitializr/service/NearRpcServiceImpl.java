package com.happyzombie.springinitializr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.happyzombie.springinitializr.bean.response.NearGeneralResponse;
import com.happyzombie.springinitializr.bean.response.nearcore.BlockDetailsResponse;
import com.happyzombie.springinitializr.bean.response.nearcore.ChunkDetailsResponse;
import com.happyzombie.springinitializr.bean.response.nearcore.FTMetadataResponse;
import com.happyzombie.springinitializr.bean.response.nearcore.NFTMetadataResponse;
import com.happyzombie.springinitializr.bean.response.nearcore.ReceiptDetailsResponse;
import com.happyzombie.springinitializr.bean.response.nearcore.TxStatusResponse;
import com.happyzombie.springinitializr.bean.response.nearcore.ViewAccountResponse;
import com.happyzombie.springinitializr.common.bean.WebBrowserConstant;
import com.happyzombie.springinitializr.common.component.ApacheHttpClientComponent;
import com.happyzombie.springinitializr.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
 *
 * @author admin
 */
@Service
@Slf4j
public class NearRpcServiceImpl implements NearRpcService {
    @Value("${nearcore.rpc.mainnet}")
    private String mainNet;
    @Value("${nearcore.rpc.historical.mainnet}")
    private String historicalMainNet;
    @Value("${proxy.host}")
    private String proxyHost;
    @Value("${proxy.port}")
    private Integer proxyPort;

    @Resource
    ApacheHttpClientComponent apacheHttpClientComponent;

    /**
     * 获取near格式的通用HttpPost
     *
     * @return HttpPost
     */
    private HttpPost getGeneralProxyHttpPost() {
        // 设置代理
        final HttpHost proxy = new HttpHost(proxyHost, proxyPort);
        RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy)
                .setConnectTimeout(10000)
                .setSocketTimeout(10000)
                .setConnectionRequestTimeout(10000)
                .build();
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
    public TxStatusResponse getTransactionStatus(String transactionHash, String senderAccountId, Boolean history) {
        final ArrayNode params = JsonUtil.getArrayNode();
        params.add(transactionHash);
        params.add(senderAccountId);
        if (history) {
            return historicalNearRequest("EXPERIMENTAL_tx_status", params, TxStatusResponse.class);
        } else {
            return generalNearRequest("EXPERIMENTAL_tx_status", params, TxStatusResponse.class);
        }
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

    @Override
    public FTMetadataResponse getFTMetadata(String contractName) {
        final ObjectNode request = JsonUtil.getObjectNode();
        request.put("request_type", "call_function");
        request.put("account_id", contractName);
        request.put("method_name", "ft_metadata");
        request.put("args_base64", "e30=");
        request.put("finality", "optimistic");
        return generalNearRequest("query", request, FTMetadataResponse.class);
    }

    @Override
    public NFTMetadataResponse getNFTMetadata(String contractName) {
        final ObjectNode request = JsonUtil.getObjectNode();
        request.put("request_type", "call_function");
        request.put("account_id", contractName);
        request.put("method_name", "nft_metadata");
        request.put("args_base64", "e30=");
        request.put("finality", "optimistic");
        return generalNearRequest("query", request, NFTMetadataResponse.class);
    }

    @Override
    public String generalQuery(ObjectNode params) {
        return nearRequest(getGeneralProxyHttpPost(), "query", params);
    }

    @Override
    public <T extends NearGeneralResponse> T generalQuery(ObjectNode params, Class<T> clazz) {
        return nearRequest(getGeneralProxyHttpPost(), "query", params, clazz);
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
            CloseableHttpClient onlySsl13Client = apacheHttpClientComponent.getOnlySsl13Client();
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
            final CloseableHttpClient onlySsl13Client = apacheHttpClientComponent.getOnlySsl13Client();
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
}
