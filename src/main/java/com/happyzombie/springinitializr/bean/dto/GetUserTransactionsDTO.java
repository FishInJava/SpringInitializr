package com.happyzombie.springinitializr.bean.dto;

import lombok.Data;

/**
 * @author admin
 */
@Data
public class GetUserTransactionsDTO {
    private String hash;

    private String signerAccountId;

    private String receiverAccountId;

    private Long blockTimestamp;

    private String includedInBlockHash;

    private Integer indexInChunk;

    private String actionKind;

    private String args;
}
