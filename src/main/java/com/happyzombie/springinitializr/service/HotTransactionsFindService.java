package com.happyzombie.springinitializr.service;

import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

public interface HotTransactionsFindService {
    Set<ZSetOperations.TypedTuple<String>> getHotAccountId(long start, long end);
}
