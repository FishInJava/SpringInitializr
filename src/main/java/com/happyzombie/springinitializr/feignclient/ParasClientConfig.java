package com.happyzombie.springinitializr.feignclient;

import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParasClientConfig extends ClientInterceptor {

    @Override
    public void setRequestHeader(RequestTemplate template){
        String url = template.url();

        // todo 看下这个method是什么，最好直接用方法名判断
        String method = template.method();
        System.out.println(method);
        if (url.contains("/token-series")){
            doApply(template);
        }else if (url.contains("/rarity")){
            // todo
        }
    }

    public void doApply(RequestTemplate template) {
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
}
