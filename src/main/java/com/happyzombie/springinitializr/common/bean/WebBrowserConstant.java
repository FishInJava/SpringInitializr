package com.happyzombie.springinitializr.common.bean;


import org.apache.commons.lang3.RandomStringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author admin
 */
public interface WebBrowserConstant {
    String NO_CACHE = "no-cache";
    String CHINA_ACCEPT_LANGUAGE = "zh-CN,zh;q=0.9,en;q=0.8";
    String ACCEPT_ENCODING = "gzip, deflate, br";
    String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.74 Safari/537.36";

    /**
     * general Sec-WebSocket-Key:a 16byte,base64 encoded String
     *
     * @return admin
     */
    static String getSecWebSocketKey() {
        final String str = RandomStringUtils.random(16, true, true);
        return Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }
}
