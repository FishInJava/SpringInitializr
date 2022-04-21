package com.happyzombie.springinitializr.quartz.latestblock;

import com.happyzombie.springinitializr.common.util.DateUtil;
import com.happyzombie.springinitializr.dao.TransactionsAnalyzeEntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * transaction_analyze表定期清除Job
 *
 * @author admin
 */
@Slf4j
@Component
public class TransactionAnalyzeCleanJob extends QuartzJobBean {
    /**
     * 保留天数
     */
    private static final long DAY_TO_MILLIS = TimeUnit.DAYS.toMillis(1);

    @Resource
    TransactionsAnalyzeEntityMapper transactionsAnalyzeEntityMapper;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        try {
            // 获取当前时间戳，获取3天前时间戳，清除数据库
            final Long currentTimestampMilli = DateUtil.getCurrentTimestampMilli();
            Long endTime = currentTimestampMilli - DAY_TO_MILLIS;
            final int i = transactionsAnalyzeEntityMapper.deleteByTime(endTime);
            log.info("TransactionAnalyzeCleanJob 清除：{} 条数据", i);
        } catch (Exception e) {
            log.error("TransactionAnalyzeCleanJob 异常", e);
        }
    }
}
