package com.happyzombie.springinitializr.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.happyzombie.springinitializr.dao.TransactionActionsEntityMapper;
import com.happyzombie.springinitializr.dao.TransactionsEntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 获取用户所有Transaction的服务
 */
@Service
@Slf4j
public class GetAllTransactionService {
    private static final LinkedBlockingQueue<Runnable> BLOCKING_QUEUE = new LinkedBlockingQueue<>(200);

    private static final RejectedExecutionHandler LOG_REJECTED_HANDLER = (r, executor) -> log.error("reject");

    private static final ThreadPoolExecutor POOL = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors(),
            0,
            TimeUnit.MINUTES,
            BLOCKING_QUEUE,
            new ThreadFactoryBuilder().setNameFormat("==========GetAllTransactionService-%d").build(),
            LOG_REJECTED_HANDLER);

    @Resource
    public TransactionActionsEntityMapper transactionActionsEntityMapper;

    @Resource
    public TransactionsEntityMapper transactionsEntityMapper;


    /**
     * 提交查询TransactionTask
     *
     * @param response response
     */
    public void addTask(String response) {
        POOL.submit(() -> {
            /**
             * Step1:查询DB，查询最新的一条数据（Timestamp最大），获得Timestamp_DB_Newest和Hash_DB_Newest
             * Step2:判断是否写入（从response中获取第一条数据和最后一条数据的Timestamp和hash，Timestamp_Backend_Newest,Hash_Backend_Newest,Timestamp_Backend_End,Hash_Backend_End）
             * - 数据库已是最新数据，不需要更新DB:Timestamp_DB_Newest == Timestamp_DB_Max && Hash_DB_Newest == Hash_Backend_Newest
             * - 从backend查询的数据全写入DB:(Timestamp_Backend_End > Timestamp_DB_Newest) || (Timestamp_Backend_End == Timestamp_DB_Newest &&  Hash_Backend_End != Hash_DB_Newest)
             * - 部分写入：用hash匹配，匹配上后前面的数据全写入
             * Step3:发送通知，同步receipts相关信息,这里调用backed的nearcore-tx，本质是和访问nearcore的EXPERIMENTAL_tx_status
             * 参考https://docs.near.org/docs/api/rpc
             */
            // transactionsEntityMapper.selectByPrimaryKey()
        });
    }

}
