//package com.happyzombie.springinitializr.config;
//
//import org.apache.http.HttpHost;
//import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.ssl.SSLContexts;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.Set;
//
///**
// * feign代理设置
// */
//@Configuration
//public class FeignProxyConfig {
//    @Value("${proxy.host}")
//    private String proxyHost;
//    @Value("${proxy.port}")
//    private Integer proxyPort;
//    @Value("#{'${proxy.domains}'.split(',')}")
//    private Set<String> domainList;
//
//    @Bean
//    public CloseableHttpClient feignClient() {
//
//        // 代理对象
//        final HttpHost proxy = new HttpHost(proxyHost, proxyPort);
//        // Socket工厂
//        final SSLConnectionSocketFactory onlySsl13 = new SSLConnectionSocketFactory(
//                SSLContexts.createDefault(),
//                new String[]{"TLSv1.3"},
//                null,
//                SSLConnectionSocketFactory.getDefaultHostnameVerifier());
//        // httpClient
//        return HttpClients.custom().setSSLSocketFactory(onlySsl13).setProxy(proxy).build();
//
////        return HttpClientBuilder.create().setProxy(proxy).setSSLSocketFactory(onlySsl13).build();
//
//    }
//
//}
