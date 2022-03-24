package com.happyzombie.springinitializr.service;

import com.happyzombie.springinitializr.api.NearBackendRequest;
import com.happyzombie.springinitializr.api.TransactionPagination;
import com.happyzombie.springinitializr.api.WampMessageCodes;
import com.happyzombie.springinitializr.api.socket.NearExplorerBackendSocket;
import com.happyzombie.springinitializr.api.socket.handler.TransactionsListByAccountIdResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NearExplorerBackendService {

    @Autowired
    private NearExplorerBackendSocket socket;

    @Value("${getTransactionsListByAccountId}")
    String getTransactionsListByAccountId;

    /**
     * 间接查询backend项目的DS_INDEXER_BACKEND数据库，获取用户Transaction记录
     *
     * @param accountId        accountId
     * @param endTimestamp     endTimestamp
     * @param transactionIndex transactionIndex
     */
    public void getTransactionsListByAccountId(String accountId, Long endTimestamp, Integer transactionIndex) {
        final WebSocketClient webSocket = socket.getWebSocket();
        final NearBackendRequest nearBackendRequest = new NearBackendRequest();
        nearBackendRequest.setMessageType(WampMessageCodes.CALL);
        nearBackendRequest.setRequestId(TransactionsListByAccountIdResponseHandler.ID);
        nearBackendRequest.setOptions(null);
        nearBackendRequest.setApiName(getTransactionsListByAccountId);
        nearBackendRequest.setArguments(accountId, 15, endTimestamp == null ? null : new TransactionPagination(endTimestamp, transactionIndex));
        webSocket.send(nearBackendRequest.toRequestJson());
    }

    /**
     * 如使用transactions-list-by-account-id何查询所有数据(参考frontend中的做法，这里不太理解chunk模型和transactionIndex参数的意义)
     * 分析请求参数
     * [48,3,{},"com.nearprotocol.mainnet.explorer.transactions-list-by-account-id",["witt.near",10,null]]
     * [48,24,{},"com.nearprotocol.mainnet.explorer.transactions-list-by-account-id",["witt.near",10,{"endTimestamp":1647617270775,"transactionIndex":2}]]
     * [48,35,{},"com.nearprotocol.mainnet.explorer.transactions-list-by-account-id",["witt.near",10,{"endTimestamp":1646908114944,"transactionIndex":6}]]
     * 第一个请求参数，endTimestamp参数给null,会返回最新的10条数据
     * 第二个请求参数中，endTimestamp和transactionIndex取第一次请求中最后一条Transaction中的相应参数
     * 同理，每次请求参数都取上个请求最后一条Transaction中的endTimestamp和transactionIndex
     * <p>
     * 设计需要注意
     * -后一次请求依赖前一次请求结果
     * -处理器中添加通知处理器
     * -本地存储数据,注意停止条件的判断：1.已经找到用户第一次Transaction 2.本地数据库中已经存在数据
     * -每个用户数据查询是串行，不同用户间的数据同步要并行，所以使用TransactionsListByAccountIdResponseHandler.ID的设计方式不合理
     */
    public void updateTransactionInfoByAccountId(String accountId) {
        //
    }

}
