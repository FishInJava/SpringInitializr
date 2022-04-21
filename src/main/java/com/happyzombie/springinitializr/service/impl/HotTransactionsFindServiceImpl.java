package com.happyzombie.springinitializr.service.impl;

import com.happyzombie.springinitializr.bean.RedisKey;
import com.happyzombie.springinitializr.bean.dto.SelectStatisticsDTO;
import com.happyzombie.springinitializr.bean.entity.TransactionAnalyzeFilterEntity;
import com.happyzombie.springinitializr.bean.request.statistics.GetStatisticsTransactionsRequest;
import com.happyzombie.springinitializr.common.util.DateUtil;
import com.happyzombie.springinitializr.dao.TransactionAnalyzeFilterEntityMapper;
import com.happyzombie.springinitializr.dao.TransactionsAnalyzeEntityMapper;
import com.happyzombie.springinitializr.service.HotTransactionsFindService;
import com.happyzombie.springinitializr.service.RedisService;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @author admin
 */
@Service
public class HotTransactionsFindServiceImpl implements HotTransactionsFindService {
    @Resource
    RedisService redisService;

    @Resource
    TransactionsAnalyzeEntityMapper transactionsAnalyzeEntityMapper;

    @Resource
    TransactionAnalyzeFilterEntityMapper transactionAnalyzeFilterEntityMapper;

    @Override
    public Set<ZSetOperations.TypedTuple<String>> getHotAccountId(long start, long end) {
        final Set<ZSetOperations.TypedTuple<String>> typedTuples = redisService.zReverseRangeWithScores(RedisKey.HOT_TRANSACTIONS_FIND, start, end);
        return typedTuples;
    }

    @Override
    public Long getHotAccountIdTotalCount() {
        return redisService.zSize(RedisKey.HOT_TRANSACTIONS_FIND);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> getHotMethodByAccountId(String accountId, long start, long end) {
        final String hotMethodKey = String.format(RedisKey.HOT_TRANSACTIONS_METHOD_FIND, accountId);
        final Set<ZSetOperations.TypedTuple<String>> typedTuples = redisService.zReverseRangeWithScores(hotMethodKey, start, end);
        return typedTuples;
    }

    @Override
    public List<SelectStatisticsDTO> getStatisticsTransactions(GetStatisticsTransactionsRequest request) {
        final Long time = request.getMilliTime();
        final Long currentTimestampMilli = DateUtil.getCurrentTimestampMilli();
        request.setBeginTime(currentTimestampMilli - time);
        request.setEndTime(currentTimestampMilli);
        final List<SelectStatisticsDTO> transactionsAnalyzeEntities = transactionsAnalyzeEntityMapper.selectStatistics(request);
        return transactionsAnalyzeEntities;
    }

    @Override
    public Long getStatisticsTransactionsTotalCount(GetStatisticsTransactionsRequest request) {
        final Long time = request.getMilliTime();
        final Long currentTimestampMilli = DateUtil.getCurrentTimestampMilli();
        request.setBeginTime(currentTimestampMilli - time);
        request.setEndTime(currentTimestampMilli);
        final Long statisticsTransactionsTotalCount = transactionsAnalyzeEntityMapper.getStatisticsTransactionsTotalCount(request);
        return statisticsTransactionsTotalCount;
    }

    public List<TransactionAnalyzeFilterEntity> getStatisticsFilterTransactions(GetStatisticsTransactionsRequest request) {
        final Long time = request.getMilliTime();
        final Long currentTimestampMilli = DateUtil.getCurrentTimestampMilli();
        request.setBeginTime(currentTimestampMilli - time);
        request.setEndTime(currentTimestampMilli);
        final List<TransactionAnalyzeFilterEntity> transactionAnalyzeFilterEntities = transactionAnalyzeFilterEntityMapper.selectStatistics(request);
        return transactionAnalyzeFilterEntities;
    }

    public Long getStatisticsFilterTransactionsTotalCount(GetStatisticsTransactionsRequest request) {
        final Long time = request.getMilliTime();
        final Long currentTimestampMilli = DateUtil.getCurrentTimestampMilli();
        request.setBeginTime(currentTimestampMilli - time);
        request.setEndTime(currentTimestampMilli);
        return transactionAnalyzeFilterEntityMapper.getStatisticsTransactionsTotalCount(request);
    }
}
