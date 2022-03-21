package com.happyzombie.springinitializr.api.socket.handler;

import com.happyzombie.springinitializr.api.WampMessageCodes;
import com.happyzombie.springinitializr.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

@Slf4j
public class WelComeHandler implements SocketResponseHandler {
    private Lock lock;
    private Condition con;

    public WelComeHandler(Lock lock, Condition con) {
        this.lock = lock;
        this.con = con;
    }

    @Override
    public boolean isMatch(String response) {
        // 如果第一个参数是50，第二个参数是 TransactionsListByAccountIdHandler.ID
        final LinkedList<Object> objectList = JsonUtil.jsonStringToList(response);
        final Object o = objectList.get(0);
        // 丑陋
        return o instanceof Integer && ((Integer) o).intValue() == WampMessageCodes.WELCOME;
    }

    @Override
    public void handler(String response) {
        lock.lock();
        try {
            con.signal();
        } catch (Exception e) {
            log.error("wait wampServer welcome error", e);
        } finally {
            lock.unlock();
            log.info("already get welcome msg！~~");
        }
    }
}
