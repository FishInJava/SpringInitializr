package com.happyzombie.springinitializr.bean.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionActionsEntity {
    private String transactionHash;

    private String actionKind;

    private String args;
}