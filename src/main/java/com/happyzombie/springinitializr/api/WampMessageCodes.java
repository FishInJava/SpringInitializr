package com.happyzombie.springinitializr.api;

/**
 * 参考文档
 * https://wamp-proto.org/_static/gen/wamp_latest.html
 *
 * WAMP的全称是:The Web Application Messaging Protocol
 */
public interface WampMessageCodes {
    Integer HELLO = 1;
    Integer WELCOME = 2;
    Integer ABORT = 3;
    Integer GOODBYE = 6;

    Integer ERROR = 8;

    Integer SUBSCRIBE = 32;
    Integer SUBSCRIBED = 33;
    Integer UNSUBSCRIBE = 34;
    Integer UNSUBSCRIBED = 35;

    Integer CALL = 48;
    Integer RESULT = 50;
}
