package com.happyzombie.springinitializr.service.latestblock;

import com.happyzombie.springinitializr.bean.entity.TransactionsAnalyzeEntity;
import com.happyzombie.springinitializr.bean.response.nearcore.BlockDetailsResponse;
import com.happyzombie.springinitializr.bean.response.nearcore.ChunkDetailsResponse;
import com.happyzombie.springinitializr.common.util.ThreadPoolUtil;
import com.happyzombie.springinitializr.dao.TransactionsAnalyzeEntityMapper;
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
public class DbLatestBlockService {
    @Resource
    TransactionsAnalyzeEntityMapper transactionsAnalyzeEntityMapper;

    /**
     * 持久化交易信息到transaction_analyze
     */
    public void saveTransactions(BlockDetailsResponse latestBlockDetail, ChunkDetailsResponse.ResultDTO.TransactionsDTO transactionsDTO, BlockDetailsResponse.ResultDTO.ChunksDTO chunksDTO) {
        ThreadPoolUtil.getGeneralPool().submit(() -> {
            try {
                save(latestBlockDetail, transactionsDTO, chunksDTO);
            } catch (Exception e) {
                log.error("saveTransactions error", e);
            }
        });
    }

    private void save(BlockDetailsResponse latestBlockDetail, ChunkDetailsResponse.ResultDTO.TransactionsDTO transactionsDTO, BlockDetailsResponse.ResultDTO.ChunksDTO chunksDTO) {
        final List<ChunkDetailsResponse.ResultDTO.TransactionsDTO.ActionsDTO> actions = transactionsDTO.getActions();
        final TransactionsAnalyzeEntity transactionsAnalyzeEntity = new TransactionsAnalyzeEntity();
        transactionsAnalyzeEntity.setSignerId(transactionsDTO.getSignerId());
        transactionsAnalyzeEntity.setReceiverId(transactionsDTO.getReceiverId());
        transactionsAnalyzeEntity.setChunkId(chunksDTO.getChunkHash());
        final Long timestamp = latestBlockDetail.getResult().getHeader().getTimestamp();
        // 处理时间戳，服务端返回的是纳秒，转换成毫秒
        transactionsAnalyzeEntity.setCreateTime(timestamp / 1000000);
        // 简单类型的action
        if (actions.size() == 1) {
            final ChunkDetailsResponse.ResultDTO.TransactionsDTO.ActionsDTO actionsDTO = actions.get(0);
            final ChunkDetailsResponse.ResultDTO.TransactionsDTO.ActionsDTO.FunctionCallDTO functionCall = actionsDTO.getFunctionCall();
            if (functionCall != null) {
                transactionsAnalyzeEntity.setMethodName(functionCall.getMethodName());
            }
            transactionsAnalyzeEntity.setActions(ChunkDetailsResponse.getActionType(actionsDTO));
            transactionsAnalyzeEntity.setIsSimpleAction(1);
            transactionsAnalyzeEntityMapper.insert(transactionsAnalyzeEntity);
        } else {
            // 多个action
            transactionsAnalyzeEntity.setIsSimpleAction(0);
            transactionsAnalyzeEntityMapper.insert(transactionsAnalyzeEntity);
        }
    }
}
