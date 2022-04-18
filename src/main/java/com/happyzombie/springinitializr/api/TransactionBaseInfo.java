package com.happyzombie.springinitializr.api;

import lombok.Data;

import java.util.List;

/**
 * @author admin
 */
@Data
public class TransactionBaseInfo {
    private String hash;
    private String signerId;
    private String receiverId;
    private String blockHash;
    private Long blockTimestamp;
    private Integer transactionIndex;
    private List<Action> actions;
}
