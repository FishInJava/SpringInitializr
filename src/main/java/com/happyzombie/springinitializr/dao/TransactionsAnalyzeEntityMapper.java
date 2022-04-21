package com.happyzombie.springinitializr.dao;

import com.happyzombie.springinitializr.bean.dto.SelectStatisticsDTO;
import com.happyzombie.springinitializr.bean.entity.TransactionsAnalyzeEntity;
import com.happyzombie.springinitializr.bean.request.statistics.GetStatisticsTransactionsRequest;

import java.util.List;

public interface TransactionsAnalyzeEntityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TransactionsAnalyzeEntity row);

    int insertSelective(TransactionsAnalyzeEntity row);

    TransactionsAnalyzeEntity selectByPrimaryKey(Long id);

    List<SelectStatisticsDTO> selectStatistics(GetStatisticsTransactionsRequest request);

    int updateByPrimaryKeySelective(TransactionsAnalyzeEntity row);

    int updateByPrimaryKey(TransactionsAnalyzeEntity row);

    Long getStatisticsTransactionsTotalCount(GetStatisticsTransactionsRequest row);

    int deleteByTime(Long endTime);
}