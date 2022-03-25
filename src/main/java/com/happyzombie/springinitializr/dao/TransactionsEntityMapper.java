package com.happyzombie.springinitializr.dao;

import com.happyzombie.springinitializr.bean.entity.TransactionsEntity;

import java.util.List;

public interface TransactionsEntityMapper {
    int deleteByPrimaryKey(String hash);

    int insert(TransactionsEntity row);

    int insertList(List<TransactionsEntity> list);

    int insertSelective(TransactionsEntity row);

    TransactionsEntity selectByPrimaryKey(String hash);

    /**
     * backend返回的最新的顺序，则插入数据库中block_timestamp是最小的，id是最大的
     *
     * @return TransactionsEntity
     */
    TransactionsEntity selectOneOldestTransaction();

    TransactionsEntity selectOneNewestTransaction();

    int updateByPrimaryKeySelective(TransactionsEntity row);

    int updateByPrimaryKey(TransactionsEntity row);
}