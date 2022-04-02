package com.happyzombie.springinitializr.bean.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionsAnalyzeEntity {
    private Long id;

    private String signerId;

    private String receiverId;

    private String actions;

    // 1是简单类型 0否
    private Integer isSimpleAction;

    private String methodName;

    private String chunkId;

    private Long createTime;

}