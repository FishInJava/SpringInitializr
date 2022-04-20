package com.happyzombie.springinitializr.service.user;

import com.happyzombie.springinitializr.bean.ActionEnum;
import com.happyzombie.springinitializr.bean.GeneralConstant;
import com.happyzombie.springinitializr.bean.RedisKey;
import com.happyzombie.springinitializr.bean.dto.GetUserTransactionsDTO;
import com.happyzombie.springinitializr.bean.dto.TransferTransactionDTO;
import com.happyzombie.springinitializr.bean.entity.TransactionsEntity;
import com.happyzombie.springinitializr.common.util.CollectionUtil;
import com.happyzombie.springinitializr.common.util.JsonUtil;
import com.happyzombie.springinitializr.dao.TransactionsEntityMapper;
import com.happyzombie.springinitializr.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 查询用户Transaction
 *
 * @author admin
 */
@Service
@Slf4j
public class UserLinkAccountService {
    @Resource
    RedisService redisService;

    @Resource
    TransactionsEntityMapper transactionsEntityMapper;

    /**
     * 1.账号有效性校验并更新数据，查询该用户最新交易
     * 2.查询redis上次更新时间lastUpdateTime
     * 3.lastUpdateTime >= 用户最新交易 ，直接返回redis中结果
     * 4.lastUpdateTime < 用户最新交易 ，更新redis
     */
    public HashMap getUserLinkAccount(String userAccountId) {
        // 查数据库最新数据
        final TransactionsEntity entity = transactionsEntityMapper.selectOneNewestTransaction(userAccountId);
        final Long dbLatestUpdateTime = entity.getBlockTimestamp();

        // 获取redis记录的最新更新时间
        final String key = String.format(RedisKey.USER_TRANSFER_ACCOUNT_LATEST_UPDATE_TIME, userAccountId);
        final Long redisLatestUpdateTime = redisService.<Long>get(key);

        // 判断是否需要更新redis
        if (redisLatestUpdateTime == null) {
            // 全量更新redis
            log.info("全量更新redis中UserLinkAccount数据");
            updateRedis(userAccountId, null);
        } else if (dbLatestUpdateTime > redisLatestUpdateTime) {
            // 增量更新redis
            log.info("增量更新redis中UserLinkAccount数据,dbLatestUpdateTime：{}， redisLatestUpdateTime:{}", dbLatestUpdateTime, redisLatestUpdateTime);
            updateRedis(userAccountId, redisLatestUpdateTime);
        } else {
            log.info("无需更新redis中UserLinkAccount数据");
        }

        // 查询redis返回结果
        final HashMap result = getResult(userAccountId);
        return result;
    }

    private HashMap getResult(String userAccountId) {
        // 转出排行
        final String outKey = String.format(RedisKey.USER_TRANSFER_ACCOUNT_OUT, userAccountId);
        final Set<ZSetOperations.TypedTuple<String>> out = redisService.zReverseRangeWithScores(outKey, 0, -1);
        // 转入排行
        final String inKey = String.format(RedisKey.USER_TRANSFER_ACCOUNT_IN, userAccountId);
        final Set<ZSetOperations.TypedTuple<String>> in = redisService.zReverseRangeWithScores(inKey, 0, -1);
        final HashMap<String, Object> result = new HashMap<>();
        result.put("in", in);
        result.put("out", out);
        return result;
    }

    private void updateRedis(String userAccountId, Long endTime) {
        // 查询action是Transfer和method是add_request_and_confirm的交易
        final List<GetUserTransactionsDTO> transactions = transactionsEntityMapper.getTransferTransaction(userAccountId, endTime);
        // 处理并过滤出所有Transfer的交易
        final List<TransferTransactionDTO> transfer = transactions.stream().filter(transactionActionsDTO -> {
                    final boolean equals = transactionActionsDTO.getActionKind().equals(ActionEnum.TRANSFER.getValue());
                    if (equals) {
                        return true;
                    }
                    UserTransactionService.handlerAction(transactionActionsDTO);
                    return transactionActionsDTO.getFirstAction().getType().equals(ActionEnum.TRANSFER.getValue());
                }).map(transactionsDTO -> {
                    final TransferTransactionDTO transferTransactionDTO = new TransferTransactionDTO();
                    // 处理action是Transfer的交易
                    if (transactionsDTO.getActionKind().equals(ActionEnum.TRANSFER.getValue())) {
                        transferTransactionDTO.setOut(transactionsDTO.getSignerAccountId().equals(userAccountId));
                        transferTransactionDTO.setAccountId(transactionsDTO.getSignerAccountId().equals(userAccountId) ? transactionsDTO.getReceiverAccountId() : transactionsDTO.getSignerAccountId());
                        final ActionEnum.Transfer transfer1 = JsonUtil.jsonStringToObject(transactionsDTO.getArgs(), ActionEnum.Transfer.class);
                        transferTransactionDTO.setAmount(transfer1.getDeposit());
                    } else {
                        // 处理add_request_and_confirm中type是Transfer的交易
                        final ActionEnum.BigAction firstAction = transactionsDTO.getFirstAction();
                        transferTransactionDTO.setOut(!firstAction.getReceiverId().equals(userAccountId));
                        // 目前从wallet网页发起的转出都是add_request_and_confirm，可能和设置了邮件二次确认有关
                        transferTransactionDTO.setAccountId(firstAction.getReceiverId());
                        // 有deposit和amount两个参数，不知道有何区别
                        transferTransactionDTO.setAmount(firstAction.getDeposit());
                    }
                    return transferTransactionDTO;
                })
                .collect(Collectors.toList());

        transfer.forEach(transactionsDTO -> {
            final Double nearCount = getNearCount(transactionsDTO.getAmount());
            // 判断转出还是转入
            final String key;
            if (transactionsDTO.getOut()) {
                key = String.format(RedisKey.USER_TRANSFER_ACCOUNT_OUT, userAccountId);
            } else {
                key = String.format(RedisKey.USER_TRANSFER_ACCOUNT_IN, userAccountId);
            }
            redisService.zInitOrIncrement(key, transactionsDTO.getAccountId(), nearCount, nearCount);
        });

        /**
         * 更新redisLatestUpdateTime,第一个就是最新时间,使用transactions，因为有可能filter后没有Transfer数据，但是时间还是需要更新
         * todo 这里不严谨，如果有相同的时间，查询时未完全返回，会漏掉数据
         */
        if (CollectionUtil.isNotEmpty(transactions)) {
            final GetUserTransactionsDTO getUserTransactionsDTO = transactions.get(0);
            final String key = String.format(RedisKey.USER_TRANSFER_ACCOUNT_LATEST_UPDATE_TIME, userAccountId);
            redisService.set(key, getUserTransactionsDTO.getBlockTimestamp());
        }
    }

    private Double getNearCount(String count) {
        final BigDecimal deposit = new BigDecimal(count).divide(GeneralConstant.NEAR_PRECISION, 4, RoundingMode.HALF_UP);
        return deposit.doubleValue();
    }

}
