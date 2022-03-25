package com.happyzombie.springinitializr.dao;

import com.happyzombie.springinitializr.bean.entity.TransactionsEntity;

import java.util.List;

public interface TransactionsEntityMapper {
    int deleteByPrimaryKey(String hash);

    int insert(TransactionsEntity row);

    int insertList(List<TransactionsEntity> list);

    int insertSelective(TransactionsEntity row);

    TransactionsEntity selectByPrimaryKey(String hash);

    TransactionsEntity selectOneNewestTransaction();

    int updateByPrimaryKeySelective(TransactionsEntity row);

    int updateByPrimaryKey(TransactionsEntity row);
}