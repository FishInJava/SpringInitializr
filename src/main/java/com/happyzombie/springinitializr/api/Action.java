package com.happyzombie.springinitializr.api;

import lombok.Data;

@Data
public class Action {
    /**
     * DeleteAccount
     * DeployContract
     * FunctionCall
     * Transfer
     * Stake
     * AddKey
     * DeleteKey
     * CreateAccount
     */
    private String kind;
    // 如果args结构不固定，如该场景下，与kind的取值相关，这种在java中如何定义
    private Object args;
}
