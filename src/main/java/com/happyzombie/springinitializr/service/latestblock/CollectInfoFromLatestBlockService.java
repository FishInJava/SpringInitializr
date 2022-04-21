package com.happyzombie.springinitializr.service.latestblock;

import com.happyzombie.springinitializr.bean.RedisKey;
import com.happyzombie.springinitializr.bean.response.nearcore.BlockDetailsResponse;
import com.happyzombie.springinitializr.bean.response.nearcore.ChunkDetailsResponse;
import com.happyzombie.springinitializr.common.util.StringUtil;
import com.happyzombie.springinitializr.common.util.ThreadPoolUtil;
import com.happyzombie.springinitializr.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 持久化读取的每个区块服务
 *
 * @author admin
 */
@Service
@Slf4j
public class CollectInfoFromLatestBlockService {
    @Resource
    RedisService redisService;

    /**
     * 持久化交易信息到transaction_analyze
     */
    public void collectTopReceiverAndMethod(ChunkDetailsResponse.ResultDTO.TransactionsDTO transactionsDTO, BlockDetailsResponse.ResultDTO.ChunksDTO chunksDTO) {
        ThreadPoolUtil.getGeneralPool().submit(() -> {
            try {
                doCollectTopReceiverAndMethod(transactionsDTO, chunksDTO);
            } catch (Exception e) {
                log.error("collectTopReceiverAndMethod error", e);
            }
        });
    }

    /**
     * 统计合约和及该合约中方法调用量排行榜
     */
    public void doCollectTopReceiverAndMethod(ChunkDetailsResponse.ResultDTO.TransactionsDTO transactionsDTO, BlockDetailsResponse.ResultDTO.ChunksDTO chunksDTO) {
        // 合约发布方（包括转账方，后面用action直接过滤掉）
        final String receiverId = transactionsDTO.getReceiverId();
        // todo ActionsDTO 这种返回格式不确定的结构，java怎么处理好点？
        final List<ChunkDetailsResponse.ResultDTO.TransactionsDTO.ActionsDTO> actions = transactionsDTO.getActions();
        actions.forEach(actionsDTO -> {
            final ChunkDetailsResponse.ResultDTO.TransactionsDTO.ActionsDTO.FunctionCallDTO functionCall = actionsDTO.getFunctionCall();
            // 合约方法名
            if (functionCall != null) {
                final String methodName = functionCall.getMethodName();
                // 写入redis
                saveInRedis(receiverId, methodName);
                // 忽略的场景：1.transfer 2.addKey操作 3.receiverId和signerId相同（自己处理自己的交易） 4.CreateAccount动作
            } else if (actionsDTO.getTransfer() == null &&
                    actionsDTO.getAddKey() == null &&
                    !receiverId.equals(transactionsDTO.getSignerId()) &&
                    StringUtil.isEmpty(actionsDTO.getCreateAccount())) {
                log.error("actions  信息格式不对，chunk_hash:{}", chunksDTO.getChunkHash());
            }
        });
    }

    /**
     * 写入Redis
     * 火热合约（账户）
     * key设计：near.analyze:hot.transactions.find:hot.account:zset
     * value设计：value是accountId ， score 每次加1
     * <p>
     * 火热合约方法
     * key设计：near.analyze:hot.transactions.find:hot.account.method:{hot.account}:zset
     * value设计：value是method name，score是次数
     */
    private void saveInRedis(String receiverId, String methodName) {
        /**
         * 统计火热合约
         * 判断key是否存在，存在+1，不存在初始化0
         */
        redisService.zInitOrIncrement(RedisKey.HOT_TRANSACTIONS_FIND, receiverId, 1, 1);

        /**
         * 统计合约方法
         */
        final String hotMethodKey = String.format(RedisKey.HOT_TRANSACTIONS_METHOD_FIND, receiverId);
        redisService.zInitOrIncrement(hotMethodKey, methodName, 1, 1);
    }

}
