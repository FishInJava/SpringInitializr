package com.happyzombie.springinitializr.service;

import com.happyzombie.springinitializr.bean.response.nearcore.BlockDetailsResponse;
import com.happyzombie.springinitializr.bean.response.nearcore.ChunkDetailsResponse;
import com.happyzombie.springinitializr.bean.response.nearcore.TxStatusResponse;
import com.happyzombie.springinitializr.bean.response.nearcore.ViewAccountResponse;

public interface NearRpcService {

    TxStatusResponse getTransactionStatus(String transactionHash, String senderAccountId);

    ViewAccountResponse viewAccount(String accountId);

    BlockDetailsResponse getLatestBlockDetail();

    BlockDetailsResponse getBlockDetailByBlockId(Long blockId);

    BlockDetailsResponse getBlockDetailByBlockHash(String blockHash);

    ChunkDetailsResponse getChunkDetailsById(String chunkId);
}
