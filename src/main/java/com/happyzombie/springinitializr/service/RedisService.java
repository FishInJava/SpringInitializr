package com.happyzombie.springinitializr.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author admin
 */
@Service
@Slf4j
public class RedisService {
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 写入缓存
     */
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            log.error("RedisService # set error ", e);
        }
        return result;
    }

    /**
     * 写入缓存设置时效时间
     */
    public boolean set(final String key, Object value, Long expireTime, TimeUnit timeUnit) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, timeUnit);
            result = true;
        } catch (Exception e) {
            log.error("RedisService # set error ", e);
        }
        return result;
    }

    /**
     * 批量删除对应的value
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 批量删除key
     */
    @SuppressWarnings("unchecked")
    public void removePattern(final String pattern) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (keys != null && keys.size() > 0) {
            redisTemplate.delete(keys);
        } else {
            log.error("批量删除失败，key:{}", pattern);
        }
    }

    /**
     * 删除对应的value
     *
     * @param key
     */
    @SuppressWarnings("unchecked")
    public void remove(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }

    /**
     * 判断缓存中是否有对应的value
     */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 读取缓存
     */
    @SuppressWarnings("unchecked")
    public <T> T get(final String key) {
        Object result;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return (T) result;
    }

    /**
     * 哈希 添加
     */
    public void hmSet(String key, Object hashKey, Object value) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        hash.put(key, hashKey, value);
    }

    /**
     * 哈希获取数据
     */
    public Object hmGet(String key, Object hashKey) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        return hash.get(key, hashKey);
    }

    /**
     * 列表添加
     */
    public void lPush(String k, Object v) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.rightPush(k, v);
    }

    /**
     * 列表获取
     */
    public List<Object> lRange(String k, long l, long l1) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return list.range(k, l, l1);
    }

    /**
     * 集合添加
     */
    public void add(String key, Object value) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        set.add(key, value);
    }

    /**
     * 集合获取
     */
    public Set<Object> setMembers(String key) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        return set.members(key);
    }

    /**
     * 有序集合添加
     */
    public <T> void zAdd(String key, T value, double score) {
        ZSetOperations<String, T> zSet = redisTemplate.opsForZSet();
        zSet.add(key, value, score);
    }

    /**
     * 获取zset中某个value的socre,可以用来判断该元素是否存在
     */
    public <T> Double zScore(String key, T value) {
        ZSetOperations<String, T> zSet = redisTemplate.opsForZSet();
        return zSet.score(key, value);
    }

    public <T> void zIncrementScore(String key, T value, double score) {
        ZSetOperations<String, T> zSet = redisTemplate.opsForZSet();
        zSet.incrementScore(key, value, score);
    }

    /**
     * 如果没有则初始化一个score为1的值，有则+1
     * todo 这里应该有事务
     */
    public <T> void zInitOrIncrement(String key, T value, double init, double delta) {
        ZSetOperations<String, T> zSet = redisTemplate.opsForZSet();
        final Double aDouble = zSet.score(key, value);
        if (aDouble != null) {
            zSet.incrementScore(key, value, delta);
        } else {
            zSet.add(key, value, init);
        }
    }

    /**
     * 排序
     * 参考指令
     * ZREVRANGE near.analyze:hot.transactions.find:hot.account:zset 0 10 WITHSCORES
     */
    public <T> Set<ZSetOperations.TypedTuple<T>> zReverseRangeWithScores(String key, long start, long end) {
        ZSetOperations<String, T> zSet = redisTemplate.opsForZSet();
        final Set<ZSetOperations.TypedTuple<T>> typedTuples = zSet.reverseRangeWithScores(key, start, end);
        return typedTuples;
    }

    public Long zSize(String key) {
        ZSetOperations zSet = redisTemplate.opsForZSet();
        return zSet.size(key);
    }

    /**
     * 有序集合获取
     */
    public Set<Object> rangeByScore(String key, double score, double scoure1) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        return zset.rangeByScore(key, score, scoure1);
    }
}
