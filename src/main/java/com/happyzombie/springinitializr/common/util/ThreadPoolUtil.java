package com.happyzombie.springinitializr.common.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ThreadPoolUtil {
    private static final LinkedBlockingQueue<Runnable> BLOCKING_QUEUE = new LinkedBlockingQueue<>(200);

    private static final RejectedExecutionHandler LOG_REJECTED_HANDLER = (r, executor) -> log.error("reject");

    private static final ThreadPoolExecutor POOL = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors(),
            0,
            TimeUnit.MINUTES,
            BLOCKING_QUEUE,
            new ThreadFactoryBuilder().setNameFormat("======General-Pool========-%d").build(),
            LOG_REJECTED_HANDLER);

    public static ThreadPoolExecutor getGeneralPool() {
        return POOL;
    }
}
