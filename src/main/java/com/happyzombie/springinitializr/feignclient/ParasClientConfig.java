package com.happyzombie.springinitializr.feignclient;

import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParasClientConfig extends ClientInterceptor {

    @Override
    public void setRequestHeader(RequestTemplate template){
        String url = template.url();
        if (url.contains("/token-series")){
            doTokenSeries(template);
        }else if (url.contains("/rarity")){
            doRarity(template);
        }
    }

    public void doTokenSeries(RequestTemplate template) {
        template.header("Host", "api-v2-mainnet.paras.id");
        template.header("Connection", "keep-alive");
        template.header("Pragma", "no-cache");
        template.header("Cache-Control", "no-cache");
        template.header("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"99\", \"Google Chrome\";v=\"99\"");
        template.header("Accept", "application/json, text/plain, */*");
        template.header("sec-ch-ua-mobile", "?0");
        template.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.51 Safari/537.36");
        template.header("sec-ch-ua-platform", "Windows");
        template.header("Origin", "https://stake.paras.id");
        template.header("Sec-Fetch-Site", "same-site");
        template.header("Sec-Fetch-Mode", "cors");
        template.header("Sec-Fetch-Dest", "empty");
        template.header("Referer", "https://stake.paras.id/");
        template.header("Accept-Encoding", "gzip, deflate, br");
        template.header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
    }

    public void doRarity(RequestTemplate template) {
        template.header("Host", "api-v2-mainnet.paras.id");
        template.header("Connection", "keep-alive");
        template.header("Content-Length", "365");
        template.header("Pragma", "no-cache");
        template.header("Cache-Control", "no-cache");
        template.header("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"99\", \"Google Chrome\";v=\"99\"");
        template.header("Accept", "application/json, text/plain, */*");
        template.header("Content-Type", "application/json");
        template.header("sec-ch-ua-mobile", "?0");
        template.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.51 Safari/537.36");
        template.header("sec-ch-ua-platform", "Windows");
        template.header("Origin", "https://paras.id");
        template.header("Sec-Fetch-Site", "same-site");
        template.header("Sec-Fetch-Mode", "cors");
        template.header("Sec-Fetch-Dest", "empty");
        template.header("Referer", "https://paras.id/");
        template.header("Accept-Encoding", "gzip, deflate, br");
        template.header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
    }
}
