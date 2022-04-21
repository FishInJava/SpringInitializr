package com.happyzombie.springinitializr.common.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author admin
 */
@Component
@Slf4j
public class ThreadPoolUtil {
    private static final LinkedBlockingQueue<Runnable> BLOCKING_QUEUE = new LinkedBlockingQueue<>(400);

    private static final RejectedExecutionHandler LOG_REJECTED_HANDLER = (r, executor) -> log.error("reject");

    /**
     * 这个maximumPoolSize还是有用的,虽然同时最多8个线程并行，但是可以开20个线程去处理，这20个线程总会抢到时间片，变相的增大了线程池阻塞队列的出口。
     */
    private static final ThreadPoolExecutor POOL = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            30,
            0,
            TimeUnit.MINUTES,
            BLOCKING_QUEUE,
            new ThreadFactoryBuilder().setNameFormat("======General-Pool========-%d").build(),
            LOG_REJECTED_HANDLER);

    public static ThreadPoolExecutor getGeneralPool() {
        return POOL;
    }
}
