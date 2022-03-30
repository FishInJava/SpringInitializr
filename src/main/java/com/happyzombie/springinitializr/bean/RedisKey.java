package com.happyzombie.springinitializr.bean;

public interface RedisKey {
    /**
     * 火热交易发现：火热合约
     */
    String HOT_TRANSACTIONS_FIND = "near.analyze:hot.transactions.find:hot.account:zset";
    /**
     * 火热交易发现：火热合约方法
     * %s 替换成account-id（account-id代表合约）
     */
    String HOT_TRANSACTIONS_METHOD_FIND = "near.analyze:hot.transactions.find:hot.account.method:%s:zset";
}
