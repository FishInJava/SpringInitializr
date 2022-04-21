package com.happyzombie.springinitializr.service.latestblock;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.happyzombie.springinitializr.bean.entity.TransactionsAnalyzeEntity;
import com.happyzombie.springinitializr.bean.response.nearcore.BlockDetailsResponse;
import com.happyzombie.springinitializr.bean.response.nearcore.ChunkDetailsResponse;
import com.happyzombie.springinitializr.common.util.CollectionUtil;
import com.happyzombie.springinitializr.common.util.ThreadPoolUtil;
import com.happyzombie.springinitializr.dao.TransactionAnalyzeFilterEntityMapper;
import com.happyzombie.springinitializr.dao.TransactionsAnalyzeEntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.HashSet;
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

    @Resource
    TransactionAnalyzeFilterEntityMapper transactionAnalyzeFilterEntityMapper;

    private static final HashSet<String> SIGNER_ID = Sets.newHashSet("relay.aurora", "app.nearcrowd.near");
    private static final HashSet<String> RECEIVER_ID = Sets.newHashSet("aurora", "app.nearcrowd.near", "priceoracle.near");
    private static final HashMap<String, HashSet<String>> RECEIVER_ID_METHOD = Maps.newHashMap();

    static {
        // aurora
        final HashSet<String> auroraMethods = Sets.newHashSet("submit", "deposit", "ft_transfer_call");
        RECEIVER_ID_METHOD.put("aurora", auroraMethods);
        // app.nearcrowd.near
        final HashSet<String> nearcrowdMethod = Sets.newHashSet("add_tasks", "finalize_task", "approve_solution", "finalize_challenged_task", "return_assignment_admin", "challenge", "starfish_reward2", "starfish_reward3", "starfish_reward4");
        RECEIVER_ID_METHOD.put("app.nearcrowd.near", nearcrowdMethod);
        // priceoracle.near
        final HashSet<String> priceoracleMethod = Sets.newHashSet("report_prices", "oracle_call");
        RECEIVER_ID_METHOD.put("priceoracle.near", priceoracleMethod);
    }

    private static boolean isInReceiverMethod(String receiverId, String method) {
        final boolean b = RECEIVER_ID_METHOD.containsKey(receiverId);
        final HashSet<String> methods = RECEIVER_ID_METHOD.get(receiverId);
        final boolean contains = methods.contains(method);
        return b && contains;
    }

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

    /**
     * 判断是否过滤
     */
    private boolean isFilter(ChunkDetailsResponse.ResultDTO.TransactionsDTO transactionsDTO) {
        final String receiverId = transactionsDTO.getReceiverId();
        final String signerId = transactionsDTO.getSignerId();
        final List<ChunkDetailsResponse.ResultDTO.TransactionsDTO.ActionsDTO> actions = transactionsDTO.getActions();
        ChunkDetailsResponse.ResultDTO.TransactionsDTO.ActionsDTO firstAction = new ChunkDetailsResponse.ResultDTO.TransactionsDTO.ActionsDTO();
        final ChunkDetailsResponse.ResultDTO.TransactionsDTO.ActionsDTO.FunctionCallDTO functionCallDTO = new ChunkDetailsResponse.ResultDTO.TransactionsDTO.ActionsDTO.FunctionCallDTO();
        functionCallDTO.setMethodName("unFilter");
        firstAction.setFunctionCall(functionCallDTO);
        /**
         * 简单Action，且是functionCall
         */
        if (CollectionUtil.isNotEmpty(actions) && actions.size() == 1) {
            final ChunkDetailsResponse.ResultDTO.TransactionsDTO.ActionsDTO actionsDTO = actions.get(0);
            if (actionsDTO.getFunctionCall() != null) {
                firstAction = actionsDTO;
            }
        }
        return RECEIVER_ID.contains(receiverId) && SIGNER_ID.contains(signerId) && isInReceiverMethod(receiverId, firstAction.getFunctionCall().getMethodName());
    }

    private void save(BlockDetailsResponse latestBlockDetail, ChunkDetailsResponse.ResultDTO.TransactionsDTO transactionsDTO, BlockDetailsResponse.ResultDTO.ChunksDTO chunksDTO) {
        final boolean filter = isFilter(transactionsDTO);

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
            saveInDb(filter, transactionsAnalyzeEntity);
        } else {
            // 多个action
            transactionsAnalyzeEntity.setIsSimpleAction(0);
            saveInDb(filter, transactionsAnalyzeEntity);
        }
    }

    private void saveInDb(boolean filter, TransactionsAnalyzeEntity transactionsAnalyzeEntity) {
        if (filter) {
            transactionAnalyzeFilterEntityMapper.insert(transactionsAnalyzeEntity);
        } else {
            transactionsAnalyzeEntityMapper.insert(transactionsAnalyzeEntity);
        }
    }
}
