package com.happyzombie.springinitializr.quartz;

import com.happyzombie.springinitializr.bean.RedisKey;
import com.happyzombie.springinitializr.bean.response.nearcore.BlockDetailsResponse;
import com.happyzombie.springinitializr.bean.response.nearcore.ChunkDetailsResponse;
import com.happyzombie.springinitializr.common.util.CollectionUtil;
import com.happyzombie.springinitializr.common.util.StringUtil;
import com.happyzombie.springinitializr.common.util.ThreadPoolUtil;
import com.happyzombie.springinitializr.service.NearRpcService;
import com.happyzombie.springinitializr.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

/**
 * get latest block
 */
@Slf4j
@Component
public class NearLatestBlockJob extends QuartzJobBean {
    @Autowired
    NearRpcService nearRpcService;

    @Autowired
    RedisService redisService;

    private static int count = 0;

    /**
     * 防止重复
     */
    private static final HashSet<String> TRAN_HASH = new HashSet<>(4096);

    private void cleanTransactionMap() {
        if (TRAN_HASH.size() > 10000) {
            log.info("定期清理 TRAN_HASH");
            TRAN_HASH.clear();
        }
    }

    private void countAndLog(BlockDetailsResponse latestBlockDetail) {
        if (count > 3600) {
            count = 0;
            log.info("NearLatestBlockJob 运行360次:{}", latestBlockDetail);
        } else {
            count++;
        }
    }

    @Override
    protected void executeInternal(JobExecutionContext context) {
        String blockHash = null;
        try {
            // 获取最新区块
            final BlockDetailsResponse latestBlockDetail = nearRpcService.getLatestBlockDetail();
            countAndLog(latestBlockDetail);
            blockHash = latestBlockDetail.getResult().getHeader().getHash();
            // 定期清除Map，没必要引入guava，每次简单判断下就好
            cleanTransactionMap();
            // 防止重复处理block
            if (TRAN_HASH.contains(blockHash)) {
                return;
            } else {
                TRAN_HASH.add(blockHash);
            }
            // 获取区块中chunks
            final List<BlockDetailsResponse.ResultDTO.ChunksDTO> chunks = latestBlockDetail.getResult().getChunks();
            // 多线程处理每个chunk
            chunks.forEach(this::submitToPool);
        } catch (Exception e) {
            log.error("NearLatestBlockJob 异常,,blockHash:{}", blockHash, e);
        }
    }

    private void submitToPool(BlockDetailsResponse.ResultDTO.ChunksDTO chunksDTO) {
        ThreadPoolUtil.getGeneralPool().submit(() -> {
            try {
                handlerChunk(chunksDTO);
            } catch (Exception e) {
                log.error("==========NearLatestBlockJob error", e);
            }
        });
    }

    private void handlerChunk(BlockDetailsResponse.ResultDTO.ChunksDTO chunksDTO) {
        // 这里api直接访问主网就好
        final ChunkDetailsResponse chunkDetailsById = nearRpcService.getChunkDetailsById(chunksDTO.getChunkHash());
        // 只需要关注用户发起的交易信息，不需要关注Receipt
        final List<ChunkDetailsResponse.ResultDTO.TransactionsDTO> transactions = chunkDetailsById.getResult().getTransactions();
        transactions.forEach(transactionsDTO -> {
            // 合约发布方（包括转账方，后面用action直接过滤掉）
            final String receiverId = transactionsDTO.getReceiverId();
            // todo ActionsDTO 这种返回格式不确定的结构，java怎么处理好点？
            final List<ChunkDetailsResponse.ResultDTO.TransactionsDTO.ActionsDTO> actions = transactionsDTO.getActions();
            if (CollectionUtil.isEmpty(actions)) {
                log.error("actions  为空:{}", chunksDTO.getChunkHash());
                return;
            }

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
        redisService.zInitOrIncrement(RedisKey.HOT_TRANSACTIONS_FIND, receiverId);

        /**
         * 统计合约方法
         */
        final String hotMethodKey = String.format(RedisKey.HOT_TRANSACTIONS_METHOD_FIND, receiverId);
        redisService.zInitOrIncrement(hotMethodKey, methodName);
    }

}
