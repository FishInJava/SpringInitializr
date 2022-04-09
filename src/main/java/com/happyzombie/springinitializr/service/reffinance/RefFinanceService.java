package com.happyzombie.springinitializr.service.reffinance;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.happyzombie.springinitializr.bean.response.reffinance.GetPoolResponse;
import com.happyzombie.springinitializr.bean.response.reffinance.GetPoolSharesResponse;
import com.happyzombie.springinitializr.bean.response.reffinance.LiquidityPoolResponse;
import com.happyzombie.springinitializr.bean.response.reffinance.ListUserSeedsResponse;
import com.happyzombie.springinitializr.bean.response.reffinance.RefFinancePoolInfo;
import com.happyzombie.springinitializr.common.component.ApacheHttpClientComponent;
import com.happyzombie.springinitializr.common.util.CompressAndDecompressUtil;
import com.happyzombie.springinitializr.common.util.JsonUtil;
import com.happyzombie.springinitializr.service.NearRpcServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * https://app.ref.finance/
 * 分析用户存在ref的tokens
 *
 * @author admin
 */
@Service
@Slf4j
public class RefFinanceService {
    @Value("${app.ref.finance.url}")
    private String refFinanceUrl;
    private static final String HOST = "indexer.ref-finance.net";
    private static final String ORIGIN = "https://app.ref.finance";
    private static final String REFERER = "https://app.ref.finance/";

    @Resource
    private ApacheHttpClientComponent apacheHttpClientComponent;

    @Resource
    NearRpcServiceImpl nearRpcService;

    /**
     * 获取用户liquidity-pools信息，该接口目前没有使用场景
     * 注意：具体的份额还是要通过getListUserSeeds这个接口去链上查询
     */
    public List<LiquidityPoolResponse> getUserLiquidityPool(String accountId) {
        final CloseableHttpClient defaultClient = apacheHttpClientComponent.getDefaultClient();
        final HttpGet get = apacheHttpClientComponent.getBrowserProxyHttpGet(refFinanceUrl + "/liquidity-pools/" + accountId, HOST, ORIGIN, REFERER, null);
        final List<LiquidityPoolResponse> response = apacheHttpClientComponent.sendGetRequest2(get, defaultClient, LiquidityPoolResponse.class);
        // todo 数值转换下
        return response;
    }

    /**
     * 获取用户份额
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> getListUserSeeds(String accountId) {
        final ObjectNode request = JsonUtil.getObjectNode();
        request.put("request_type", "call_function");
        request.put("account_id", "v2.ref-farming.near");
        request.put("method_name", "list_user_seeds");
        final ObjectNode json = JsonUtil.getObjectNode();
        json.put("account_id", accountId);
        request.put("args_base64", CompressAndDecompressUtil.base64(json.toString()));
        request.put("finality", "optimistic");
        final ListUserSeedsResponse nearGeneralResponse = nearRpcService.generalQuery(request, ListUserSeedsResponse.class);
        final Map<String, String> resultMap = nearGeneralResponse.getResult().getResultMap();
        return resultMap;
    }

    /**
     * 从链上获取池子信息
     */
    public GetPoolResponse getPoolInfoFromChain(Integer poolId) {
        final ObjectNode request = JsonUtil.getObjectNode();
        request.put("request_type", "call_function");
        request.put("account_id", "v2.ref-finance.near");
        request.put("method_name", "get_pool");
        final ObjectNode json = JsonUtil.getObjectNode();
        json.put("pool_id", poolId);
        request.put("args_base64", CompressAndDecompressUtil.base64(json.toString()));
        request.put("finality", "optimistic");
        final GetPoolResponse response = nearRpcService.generalQuery(request, GetPoolResponse.class);
        return response;
    }

    /**
     * 用户在池子中share量
     */
    public GetPoolSharesResponse getPoolShares(String accountId, Integer poolId) {
        final ObjectNode request = JsonUtil.getObjectNode();
        request.put("request_type", "call_function");
        request.put("account_id", "v2.ref-finance.near");
        request.put("method_name", "get_pool_shares");
        final ObjectNode json = JsonUtil.getObjectNode();
        json.put("pool_id", poolId);
        json.put("account_id", accountId);
        request.put("args_base64", CompressAndDecompressUtil.base64(json.toString()));
        request.put("finality", "optimistic");
        final GetPoolSharesResponse response = nearRpcService.generalQuery(request, GetPoolSharesResponse.class);
        return response;
    }

    /**
     * 从ref后台获取池子信息
     */
    public RefFinancePoolInfo getPoolInfoFromRef(Integer poolId) {
        final CloseableHttpClient defaultClient = apacheHttpClientComponent.getDefaultClient();
        final HttpGet get = apacheHttpClientComponent.getBrowserProxyHttpGet(refFinanceUrl + "/get-pool?pool_id=" + poolId, HOST, ORIGIN, REFERER, null);
        final RefFinancePoolInfo info = apacheHttpClientComponent.sendGetRequest(get, defaultClient, RefFinancePoolInfo.class);
        return info;
    }
    
}
