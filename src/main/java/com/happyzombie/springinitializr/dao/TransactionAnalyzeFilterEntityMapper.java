package com.happyzombie.springinitializr.dao;

import com.happyzombie.springinitializr.bean.entity.TransactionAnalyzeFilterEntity;
import com.happyzombie.springinitializr.bean.entity.TransactionsAnalyzeEntity;
import com.happyzombie.springinitializr.bean.request.statistics.GetStatisticsTransactionsRequest;

import java.util.List;

/**
 * @author admin
 */
public interface TransactionAnalyzeFilterEntityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TransactionsAnalyzeEntity row);

    int insertSelective(TransactionAnalyzeFilterEntity row);

    TransactionAnalyzeFilterEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TransactionAnalyzeFilterEntity row);

    int updateByPrimaryKey(TransactionAnalyzeFilterEntity row);

    int deleteByTime(Long endTime);

    List<TransactionAnalyzeFilterEntity> selectStatistics(GetStatisticsTransactionsRequest request);

    Long getStatisticsTransactionsTotalCount(GetStatisticsTransactionsRequest row);
}