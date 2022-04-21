package com.happyzombie.springinitializr.config;


import com.happyzombie.springinitializr.quartz.latestblock.NearLatestBlockJob;
import com.happyzombie.springinitializr.quartz.latestblock.TransactionAnalyzeCleanJob;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hbz
 */
@Configuration
public class QuartzConfig {

    @Bean
    @ConditionalOnExpression("#{'open'.equals(environment['near.latest.block.job'])}")
    public JobDetail getNearLatestBlockJob() {
        // near最新区块处理任务
        return JobBuilder.newJob(NearLatestBlockJob.class).withIdentity("getNearLatestBlockJob").storeDurably().build();
    }

    @Bean
    @ConditionalOnExpression("#{'open'.equals(environment['near.latest.block.job'])}")
    public Trigger nearLatestBlockJobTrigger() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
                .simpleSchedule()
                // 设置时间周期(秒):1s
                .withIntervalInSeconds(1)
                .repeatForever();
        return TriggerBuilder.newTrigger().forJob(getNearLatestBlockJob())
                .withIdentity("nearLatestBlockJobTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }

    @Bean
    @ConditionalOnExpression("#{'open'.equals(environment['transaction.analyze.cleanJob.job'])}")
    public JobDetail getTransactionAnalyzeCleanJob() {
        // transaction_analyze表定时定理任务
        return JobBuilder.newJob(TransactionAnalyzeCleanJob.class).withIdentity("getTransactionAnalyzeCleanJob").storeDurably().build();
    }

    @Bean
    @ConditionalOnExpression("#{'open'.equals(environment['transaction.analyze.cleanJob.job'])}")
    public Trigger transactionAnalyzeCleanJobTrigger() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
                .simpleSchedule()
                // 设置时间周期(秒)：1h
                .withIntervalInSeconds(3600)
                .repeatForever();
        return TriggerBuilder.newTrigger().forJob(getTransactionAnalyzeCleanJob())
                .withIdentity("transactionAnalyzeCleanJobTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }
}
