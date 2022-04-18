package com.happyzombie.springinitializr.bean.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author admin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionsEntity {
    private Long id;

    private String hash;

    private String synchronizedAccountId;
    
    private String signerAccountId;

    private String receiverAccountId;

    private Long blockTimestamp;

    private String includedInBlockHash;

    private Integer indexInChunk;
}