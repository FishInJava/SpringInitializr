package com.happyzombie.springinitializr.bean.entity;

import lombok.Data;

@Data
public class TransactionsEntity {
    private Long id;

    private String hash;

    private String signerAccountId;

    private String receiverAccountId;

    private Long blockTimestamp;

    private String includedInBlockHash;

    private Integer indexInChunk;
}