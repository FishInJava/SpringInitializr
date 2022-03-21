package com.happyzombie.springinitializr.api;

public interface SocketResponseHandler {
    boolean isMatch(String response);

    void handler(String response);
}
