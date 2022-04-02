package com.happyzombie.springinitializr.service;

import com.happyzombie.springinitializr.bean.dto.SelectStatisticsDTO;
import com.happyzombie.springinitializr.bean.request.statistics.GetStatisticsTransactionsRequest;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Set;

public interface HotTransactionsFindService {
    Set<ZSetOperations.TypedTuple<String>> getHotAccountId(long start, long end);

    Long getHotAccountIdTotalCount();

    Set<ZSetOperations.TypedTuple<String>> getHotMethodByAccountId(String accountId, long start, long end);

    List<SelectStatisticsDTO> getStatisticsTransactions(GetStatisticsTransactionsRequest request);

    Long getStatisticsTransactionsTotalCount(GetStatisticsTransactionsRequest request);
}
