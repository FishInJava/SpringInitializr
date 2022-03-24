package com.happyzombie.springinitializr.api.socket.handler;

import com.happyzombie.springinitializr.api.TransactionBaseInfo;
import com.happyzombie.springinitializr.api.WampMessageCodes;
import com.happyzombie.springinitializr.common.util.JsonUtil;
import com.happyzombie.springinitializr.service.GetAllTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TransactionsListByAccountIdResponseHandler implements SocketResponseHandler {
    /**
     * 自定义接口id
     */
    public static Integer ID = 1;

    @Autowired
    GetAllTransactionService getAllTransactionService;

    @Override
    public boolean isMatch(String response) {
        // 如果第一个参数是50，第二个参数是 TransactionsListByAccountIdHandler.ID
        final LinkedList<Object> objectList = JsonUtil.jsonStringToList(response);
        final Object o = objectList.get(0);
        final Object o1 = objectList.get(1);
        // 丑陋
        return o instanceof Integer && ((Integer) o).intValue() == WampMessageCodes.RESULT && o1 instanceof Integer && ((Integer) o1).intValue() == ID;
    }

    @Override
    public void handler(String response) {
        // 提取第四个参数
        final LinkedList<Object> objectList = JsonUtil.jsonStringToList(response);
        final Object o = objectList.get(3);
        if (o instanceof List) {
            // List<Map>
            final Object mapList = ((List) o).get(0);
            final LinkedList<TransactionBaseInfo> transactionBaseInfos = JsonUtil.mapListToObjectList((List<Map>) mapList, TransactionBaseInfo.class);
            log.info("transactionBaseInfos  {}", transactionBaseInfos);
            getAllTransactionService.addTask(response);
        }
    }
}
