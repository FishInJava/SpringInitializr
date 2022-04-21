package com.happyzombie.springinitializr.quartz.latestblock;

import com.happyzombie.springinitializr.bean.response.nearcore.BlockDetailsResponse;
import com.happyzombie.springinitializr.bean.response.nearcore.ChunkDetailsResponse;
import com.happyzombie.springinitializr.common.util.CollectionUtil;
import com.happyzombie.springinitializr.common.util.ThreadPoolUtil;
import com.happyzombie.springinitializr.service.NearRpcService;
import com.happyzombie.springinitializr.service.latestblock.CollectInfoFromLatestBlockService;
import com.happyzombie.springinitializr.service.latestblock.DbLatestBlockService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;

/**
 * 实时区块获取Job
 *
 * @author admin
 */
@Slf4j
@Component
public class NearLatestBlockJob extends QuartzJobBean {
    @Resource
    NearRpcService nearRpcService;

    @Resource
    DbLatestBlockService dbLatestBlockService;

    @Resource
    CollectInfoFromLatestBlockService collectInfoFromLatestBlockService;

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

    private void countAndLog() {
        if (count > 360) {
            count = 0;
            log.info("NearLatestBlockJob 运行360次!");
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
            countAndLog();
            blockHash = latestBlockDetail.getResult().getHeader().getHash();
            // 定期清除Map，没必要引入guava，每次简单判断下就好
            cleanTransactionMap();
            // 防止重复处理block todo 这里还需要进行防漏设计
            if (TRAN_HASH.contains(blockHash)) {
                return;
            } else {
                TRAN_HASH.add(blockHash);
            }
            // 获取区块中chunks
            final List<BlockDetailsResponse.ResultDTO.ChunksDTO> chunks = latestBlockDetail.getResult().getChunks();
            // 多线程处理每个chunk
            chunks.forEach(chunksDTO -> submitToPool(latestBlockDetail, chunksDTO));
        } catch (Exception e) {
            log.error("NearLatestBlockJob 异常,,blockHash:{}", blockHash, e);
        }
    }

    private void submitToPool(BlockDetailsResponse latestBlockDetail, BlockDetailsResponse.ResultDTO.ChunksDTO chunksDTO) {
        ThreadPoolUtil.getGeneralPool().submit(() -> {
            try {
                handlerChunk(latestBlockDetail, chunksDTO);
            } catch (Exception e) {
                log.error("==========NearLatestBlockJob error", e);
            }
        });
    }

    private void handlerChunk(BlockDetailsResponse latestBlockDetail, BlockDetailsResponse.ResultDTO.ChunksDTO chunksDTO) {
        // 这里api直接访问主网就好
        final ChunkDetailsResponse chunkDetailsById = nearRpcService.getChunkDetailsById(chunksDTO.getChunkHash());
        // 只需要关注用户发起的交易信息，不需要关注Receipt
        final List<ChunkDetailsResponse.ResultDTO.TransactionsDTO> transactions = chunkDetailsById.getResult().getTransactions();
        transactions.forEach(transactionsDTO -> {
            if (CollectionUtil.isEmpty(transactionsDTO.getActions())) {
                log.error("actions  为空:{}", chunksDTO.getChunkHash());
                return;
            }
            // 统计合约和及该合约中方法调用量排行榜
            collectInfoFromLatestBlockService.collectTopReceiverAndMethod(transactionsDTO, chunksDTO);
            // 记录所有交易到transaction_analyze
            dbLatestBlockService.saveTransactions(latestBlockDetail, transactionsDTO, chunksDTO);
        });
    }
}
