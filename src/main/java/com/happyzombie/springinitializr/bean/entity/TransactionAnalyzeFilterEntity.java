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
public class TransactionAnalyzeFilterEntity {
    private Long id;

    private String signerId;

    private String receiverId;

    private String actions;

    private Byte isSimpleAction;

    private String methodName;

    private String chunkId;

    private Long createTime;
}