package com.happyzombie.springinitializr.api;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
@Slf4j
public class NearExplorerBackendService {

    @Autowired
    private NearExplorerBackendSocket socket;

    @Value("${getTransactionsListByAccountId}")
    String getTransactionsListByAccountId;

    public void getTransactionsListByAccountId(String accountId, BigInteger endTimestamp, Integer transactionIndex) {
        final WebSocketClient webSocket = socket.getWebSocket();
        final NearBackendRequest nearBackendRequest = new NearBackendRequest();
        nearBackendRequest.setMessageType(WampMessageCodes.CALL);
        nearBackendRequest.setRequestId(TransactionsListByAccountIdResponseHandler.ID);
        nearBackendRequest.setOptions(null);
        nearBackendRequest.setApiName(getTransactionsListByAccountId);
        nearBackendRequest.setArguments(accountId, 15, new TransactionPagination(endTimestamp, transactionIndex));
        webSocket.send(nearBackendRequest.toRequestJson());
    }

}
