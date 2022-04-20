package com.happyzombie.springinitializr.bean.dto;

import lombok.Data;

/**
 * @author admin
 */
@Data
public class TransferTransactionDTO {
    /**
     * 是否是转出
     */
    Boolean out;
    String accountId;
    /**
     * 转账数量
     */
    String amount;
}
