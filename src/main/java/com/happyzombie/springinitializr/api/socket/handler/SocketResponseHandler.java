package com.happyzombie.springinitializr.api.socket.handler;

/**
 * @author admin
 */
public interface SocketResponseHandler {
    boolean isMatch(String response);

    void handler(String response);
}
