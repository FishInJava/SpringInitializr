package com.happyzombie.springinitializr.dao;

import com.happyzombie.springinitializr.bean.entity.TransactionActionsEntity;

import java.util.List;

public interface TransactionActionsEntityMapper {
    int deleteByPrimaryKey(String transactionHash);

    int insert(TransactionActionsEntity row);

    int insertList(List<TransactionActionsEntity> rows);

    int insertSelective(TransactionActionsEntity row);

    TransactionActionsEntity selectByPrimaryKey(String transactionHash);

    int updateByPrimaryKeySelective(TransactionActionsEntity row);

    int updateByPrimaryKeyWithBLOBs(TransactionActionsEntity row);

    int updateByPrimaryKey(TransactionActionsEntity row);
}