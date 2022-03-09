package com.happyzombie.springinitializr.feignclient;


import feign.RequestInterceptor;
import feign.RequestTemplate;

public abstract class ClientInterceptor implements RequestInterceptor {

    public abstract void setRequestHeader(RequestTemplate template);

    @Override
    public void apply(RequestTemplate template) {
        // 设置请求头
        setRequestHeader(template);
    }

}
