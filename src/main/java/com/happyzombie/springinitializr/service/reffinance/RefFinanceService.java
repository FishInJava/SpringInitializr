package com.happyzombie.springinitializr.service.reffinance;

import com.happyzombie.springinitializr.bean.response.reffinance.LiquidityPoolResponse;
import com.happyzombie.springinitializr.common.component.ApacheHttpClientComponent;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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

    public List<LiquidityPoolResponse> getUserLiquidityPool(String accountId) {
        final CloseableHttpClient defaultClient = apacheHttpClientComponent.getDefaultClient();
        final HttpGet get = apacheHttpClientComponent.getBrowserProxyHttpGet(refFinanceUrl + "/liquidity-pools/" + accountId, HOST, ORIGIN, REFERER, null);
        final List<LiquidityPoolResponse> response = apacheHttpClientComponent.sendGetRequest2(get, defaultClient, LiquidityPoolResponse.class);
        // todo 数值转换下
        return response;
    }

}
