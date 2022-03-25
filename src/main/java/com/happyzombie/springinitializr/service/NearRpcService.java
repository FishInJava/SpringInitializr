package com.happyzombie.springinitializr.service;

import com.happyzombie.springinitializr.bean.response.nearcore.TxStatusResponse;
import com.happyzombie.springinitializr.bean.response.nearcore.ViewAccountResponse;

public interface NearRpcService {

    TxStatusResponse getTransactionStatus(String transactionHash, String senderAccountId);

    ViewAccountResponse viewAccount(String accountId);
}
