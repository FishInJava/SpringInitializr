package com.happyzombie.springinitializr.dao;

import com.happyzombie.springinitializr.bean.entity.TransactionsEntity;

public interface TransactionsEntityMapper {
    int deleteByPrimaryKey(String hash);

    int insert(TransactionsEntity row);

    int insertSelective(TransactionsEntity row);

    TransactionsEntity selectByPrimaryKey(String hash);

    TransactionsEntity selectOne(TransactionsEntity transactionsEntity);

    int updateByPrimaryKeySelective(TransactionsEntity row);

    int updateByPrimaryKey(TransactionsEntity row);
}