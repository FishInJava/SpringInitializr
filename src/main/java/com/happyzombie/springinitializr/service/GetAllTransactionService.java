package com.happyzombie.springinitializr.service;

import com.happyzombie.springinitializr.api.Action;
import com.happyzombie.springinitializr.api.TransactionBaseInfo;
import com.happyzombie.springinitializr.api.socket.handler.TransactionsListByAccountIdResponseHandler;
import com.happyzombie.springinitializr.bean.entity.TransactionActionsEntity;
import com.happyzombie.springinitializr.bean.entity.TransactionsEntity;
import com.happyzombie.springinitializr.common.util.CollectionUtil;
import com.happyzombie.springinitializr.common.util.JsonUtil;
import com.happyzombie.springinitializr.common.util.ThreadPoolUtil;
import com.happyzombie.springinitializr.dao.TransactionActionsEntityMapper;
import com.happyzombie.springinitializr.dao.TransactionsEntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 获取用户所有Transaction的服务
 */
@Service
@Slf4j
public class GetAllTransactionService {
    @Resource
    public TransactionActionsEntityMapper transactionActionsMapper;

    @Resource
    public TransactionsEntityMapper transactionsMapper;

    @Autowired
    public NearExplorerBackendService nearExplorerBackendService;

    @Autowired
    public DataSourceTransactionManager dtsManager;

    private final static DefaultTransactionDefinition DEFINITION = new DefaultTransactionDefinition();

    static {
        DEFINITION.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    /**
     * 提交查询TransactionTask
     */
    public void addTask(Integer requestId, LinkedList<TransactionBaseInfo> trans) {
        if (CollectionUtil.isEmpty(trans)) {
            log.error("transactionBaseInfos is null");
            return;
        }

        /**
         * 问题1
         * 如果有重复消息怎么办？应该问题不大，库中有的也不会进行操作
         * todo 问题2
         * 最新的Transaction信息可能是中间态的，比如说执行中，是否要考虑后续更新？终态如何判断？
         */
        ThreadPoolUtil.getGeneralPool().submit(() -> {
            try {
                /**
                 * Step1:查询DB，查询最新的一条数据（Timestamp最大），获得Timestamp_DB_Newest和Hash_DB_Newest
                 * Step2:判断是否写入（从response中获取第一条数据和最后一条数据的Timestamp和hash，Timestamp_Backend_Newest,Hash_Backend_Newest,Timestamp_Backend_End,Hash_Backend_End）
                 * - 数据库已是最新数据，不需要更新DB:Timestamp_DB_Newest == Timestamp_DB_Max && Hash_DB_Newest == Hash_Backend_Newest
                 * - 从backend查询的数据全写入DB:(Timestamp_Backend_End > Timestamp_DB_Newest) || (Timestamp_Backend_End == Timestamp_DB_Newest &&  Hash_Backend_End != Hash_DB_Newest)
                 * - 部分写入：用hash匹配，匹配上后前面的数据全写入
                 * todo 我的建议是暂时不处理收据信息，一笔Transaction会产生多必收据信息，每条收据信息又对应一条收据结果。但是收据信息中包含了FunctionCall具体的方法等信息，对写快速调用合约接口有很大帮助
                 * Step3:发送通知，同步receipts相关信息,这里调用backed的nearcore-tx，本质是和访问nearcore的EXPERIMENTAL_tx_status
                 * 参考https://docs.near.org/docs/api/rpc
                 */
                // todo 最后一个场景，backed返回账号的第一条交易
                final String signerId = TransactionsListByAccountIdResponseHandler.getAccountByRequestId(requestId);
                // 查询DB最新数据
                final TransactionsEntity dbOldest = transactionsMapper.selectOneOldestTransaction(signerId);
                final TransactionsEntity dbNewest = transactionsMapper.selectOneNewestTransaction(signerId);

                // 如果数据库为空
                if (dbOldest == null) {
                    log.info("=============新库，写入操作");
                    // 全量写入
                    insertTransAndActions(trans, trans.size() - 1);
                    final TransactionBaseInfo last = trans.getLast();
                    // 发送webSocket，继续查询 如果发生异常
                    nearExplorerBackendService.getTransactionsListByAccountId(last.getSignerId(), last.getBlockTimestamp(), last.getTransactionIndex());
                    return;
                }

                final TransactionBaseInfo first = trans.getFirst();
                final TransactionBaseInfo last = trans.getLast();

                // 数据库已是最新数据，不需要更新DB
                if (dbNewest.getBlockTimestamp().equals(first.getBlockTimestamp()) && Objects.equals(dbNewest.getHash(), first.getHash())) {
                    log.info("=============数据库已是最新数据，结束更新");
                    return;
                }

                // 全量写入
                if (last.getBlockTimestamp().compareTo(dbOldest.getBlockTimestamp()) > 0 || (first.getBlockTimestamp().compareTo(dbOldest.getBlockTimestamp()) == 0 && !first.getHash().equals(dbOldest.getHash()))) {
                    log.info("=============全量写入");
                    insertTransAndActions(trans, trans.size() - 1);
                    // 发送webSocket，继续查询 如果发生异常
                    nearExplorerBackendService.getTransactionsListByAccountId(last.getSignerId(), last.getBlockTimestamp(), last.getTransactionIndex());
                } else {
                    // 部分写入
                    log.info("=============部分写入");
                    AtomicReference<Integer> index = new AtomicReference<>();
                    final boolean anyMatch = trans.stream().anyMatch(transactionBaseInfo -> {
                        final boolean equals = transactionBaseInfo.getHash().equals(dbOldest.getHash());
                        if (equals) {
                            index.set(trans.indexOf(transactionBaseInfo));
                        }
                        return equals;
                    });
                    if (!anyMatch) {
                        log.error("=============没有匹配的数据，检查逻辑！");
                        return;
                    }

                    // 部分写入
                    insertTransAndActions(trans, index.get() - 1);

                    // 发送webSocket，继续查询
                    final TransactionBaseInfo transactionBaseInfo = trans.get(index.get());
                    nearExplorerBackendService.getTransactionsListByAccountId(transactionBaseInfo.getHash(), transactionBaseInfo.getBlockTimestamp(), transactionBaseInfo.getTransactionIndex());
                }
            } catch (Exception e) {
                log.error("==========GetAllTransactionService error", e);
            }


        });
    }

    /**
     * 持久化
     *
     * @param trans    交易信息
     * @param endIndex 最后一条写入数据的索引
     */
    private void insertTransAndActions(LinkedList<TransactionBaseInfo> trans, int endIndex) {
        final List<TransactionsEntity> insertTrans = getTransInsert(trans, endIndex);
        final List<TransactionActionsEntity> insertAction = getTransActionInsert(trans, endIndex);
        insertTransAndActions(insertTrans, insertAction);
    }

    private void insertTransAndActions(List<TransactionsEntity> insertTrans, List<TransactionActionsEntity> insertAction) {
        // 编程事务
        final TransactionStatus status = startTransaction();
        try {
            transactionsMapper.insertList(insertTrans);
            transactionActionsMapper.insertList(insertAction);
            commitTransaction(status);
        } catch (Exception e) {
            log.error("执行sql异常", e);
            rollBackTransaction(status);
            throw new RuntimeException("线程停止运行", e);
        }
    }

    private TransactionStatus startTransaction() {
        return dtsManager.getTransaction(DEFINITION);
    }

    private void commitTransaction(TransactionStatus status) {
        dtsManager.commit(status);
    }

    private void rollBackTransaction(TransactionStatus status) {
        dtsManager.rollback(status);
    }

    private List<TransactionsEntity> getTransInsert(LinkedList<TransactionBaseInfo> trans, int endIndex) {
        final List<TransactionsEntity> transactionsEntity = new LinkedList<>();
        for (int i = 0; i <= endIndex; i++) {
            final TransactionBaseInfo transactionBaseInfo = trans.get(i);
            final TransactionsEntity entity = new TransactionsEntity();
            entity.setHash(transactionBaseInfo.getHash());
            entity.setSignerAccountId(transactionBaseInfo.getSignerId());
            entity.setReceiverAccountId(transactionBaseInfo.getReceiverId());
            entity.setBlockTimestamp(transactionBaseInfo.getBlockTimestamp());
            entity.setIncludedInBlockHash(transactionBaseInfo.getBlockHash());
            entity.setIndexInChunk(transactionBaseInfo.getTransactionIndex());
            transactionsEntity.add(entity);
        }
        return transactionsEntity;
    }

    private List<TransactionActionsEntity> getTransActionInsert(LinkedList<TransactionBaseInfo> trans, int endIndex) {
        final List<TransactionActionsEntity> actions = new LinkedList<>();
        for (int i = 0; i <= endIndex; i++) {
            final String hash = trans.get(i).getHash();
            final List<Action> actionsList = trans.get(i).getActions();
            for (int j = 0; j < actionsList.size(); j++) {
                final Action action = actionsList.get(j);
                final TransactionActionsEntity actionsEntity = new TransactionActionsEntity();
                actionsEntity.setTransactionHash(hash);
                actionsEntity.setActionKind(action.getKind());
                actionsEntity.setArgs(JsonUtil.objectToString(action.getArgs()));
                actions.add(actionsEntity);
            }

        }
        return actions;
    }

}
