package com.happyzombie.springinitializr.feignclient.config;

import com.happyzombie.springinitializr.feignclient.ClientInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

@Component
public class NearRcpClientConfig extends ClientInterceptor {
    // 之前看过一个写法在这里创建FeignBuilder，从而对使用该配置类的类进行代理和TSL版本配置，我写了没实现，有空研究
    @Override
    public void setRequestHeader(RequestTemplate template) {
        final String url = template.url();
        final String url1 = template.feignTarget().url();
        if (url.contains("near.org") || url1.contains("near.org")) {
            doTokenSeries(template);
        }
    }

    public void doTokenSeries(RequestTemplate template) {
        template.header("host", "rpc.mainnet.near.org");
        template.header("Connection", "keep-alive");
        template.header("User-Agent", "PostmanRuntime/7.29.0");
        template.header("Content-Type", "application/json");
    }
}
