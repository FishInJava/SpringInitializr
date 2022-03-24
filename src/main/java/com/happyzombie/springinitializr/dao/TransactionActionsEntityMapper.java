package com.happyzombie.springinitializr.dao;

import com.happyzombie.springinitializr.bean.entity.TransactionActionsEntity;

public interface TransactionActionsEntityMapper {
    int deleteByPrimaryKey(String transactionHash);

    int insert(TransactionActionsEntity row);

    int insertSelective(TransactionActionsEntity row);

    TransactionActionsEntity selectByPrimaryKey(String transactionHash);

    int updateByPrimaryKeySelective(TransactionActionsEntity row);

    int updateByPrimaryKeyWithBLOBs(TransactionActionsEntity row);

    int updateByPrimaryKey(TransactionActionsEntity row);
}