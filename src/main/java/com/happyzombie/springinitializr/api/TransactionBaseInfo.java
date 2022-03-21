package com.happyzombie.springinitializr.api;

import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
public class TransactionBaseInfo {
    private String hash;
    private String signerId;
    private String receiverId;
    private String blockHash;
    private BigInteger blockTimestamp;
    private Integer transactionIndex;
    private List<Action> actions;
}
