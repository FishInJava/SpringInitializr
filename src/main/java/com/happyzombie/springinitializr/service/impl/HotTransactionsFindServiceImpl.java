package com.happyzombie.springinitializr.service.impl;

import com.happyzombie.springinitializr.bean.RedisKey;
import com.happyzombie.springinitializr.service.HotTransactionsFindService;
import com.happyzombie.springinitializr.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class HotTransactionsFindServiceImpl implements HotTransactionsFindService {
    @Autowired
    RedisService redisService;

    @Override
    public Set<ZSetOperations.TypedTuple<String>> getHotAccountId(long start, long end) {
        final Set<ZSetOperations.TypedTuple<String>> typedTuples = redisService.zReverseRangeWithScores(RedisKey.HOT_TRANSACTIONS_FIND, start, end);
        return typedTuples;
    }
}
