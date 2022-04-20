package com.happyzombie.springinitializr.bean;

/**
 * @author admin
 */
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

    /**
     * 用户关联账号查询Transfer操作:记录最新更新时间
     * 是否失效：否
     * %s 替换成account-id
     */
    String USER_TRANSFER_ACCOUNT_LATEST_UPDATE_TIME = "near.analyze:user.transfer.accounts:latest.update.time:%s:string";

    /**
     * 用户关联账号查询Transfer转出操作: 存储的是交互账户名称，得分表示转账总金额
     * 是否失效：否
     * %s 替换成account-id（account-id代表交互账户）
     */
    String USER_TRANSFER_ACCOUNT_OUT = "near.analyze:user.transfer.accounts:out:%s:zset";

    /**
     * 转入操作: 存储的是交互账户名称，得分表示转账总金额
     * 是否失效：否
     * %s 替换成account-id（account-id代表交互账户）
     */
    String USER_TRANSFER_ACCOUNT_IN = "near.analyze:user.transfer.accounts:in:%s:zset";

}
