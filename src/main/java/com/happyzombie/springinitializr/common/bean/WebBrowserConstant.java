package com.happyzombie.springinitializr.common.bean;


import com.happyzombie.springinitializr.common.util.CompressAndDecompressUtil;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author admin
 */
public interface WebBrowserConstant {
    String NO_CACHE = "no-cache";
    String KEEP_ALIVE = "keep-alive";
    String JSON_CONTENT_TYPE = "application/json; charset=UTF-8";
    String CHINA_ACCEPT_LANGUAGE = "zh-CN,zh;q=0.9,en;q=0.8";
    String ACCEPT_ENCODING = "gzip, deflate, br";
    String ACCEPT_ALL = "*/*";
    String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.74 Safari/537.36";

    /**
     * SEC相关设置
     */
    String SEC_CH_UA = "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"100\", \"Google Chrome\";v=\"100\"";
    String SEC_CH_UA_MOBILE = "?0";
    String SEC_CH_UA_PLATFORM = "windows";
    String SEC_FETCH_DEST = "empty";
    String SEC_FETCH_MODE = "cors";
    String SEC_FETCH_SITE = "cross-site";


    /**
     * general Sec-WebSocket-Key:a 16byte,base64 encoded String
     *
     * @return admin
     */
    static String getSecWebSocketKey() {
        final String str = RandomStringUtils.random(16, true, true);
        return CompressAndDecompressUtil.base64Encode(str);
    }
}
