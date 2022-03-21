package com.happyzombie.springinitializr.api.socket.handler;

public interface SocketResponseHandler {
    boolean isMatch(String response);

    void handler(String response);
}
