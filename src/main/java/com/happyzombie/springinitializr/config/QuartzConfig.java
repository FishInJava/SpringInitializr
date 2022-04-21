package com.happyzombie.springinitializr.config;


import com.happyzombie.springinitializr.quartz.hottransaction.RedisCleanAndSaveInDbJob;
import com.happyzombie.springinitializr.quartz.latestblock.NearLatestBlockJob;
import com.happyzombie.springinitializr.quartz.latestblock.TransactionAnalyzeCleanJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hbz
 */
@Configuration
public class QuartzConfig {
    @Value("${hot.transaction.redis.clean.and.save.indb.job.corn}")
    String redisCleanCorn;

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

    @Bean
    @ConditionalOnExpression("#{'open'.equals(environment['hot.transaction.redis.clean.and.save.indb.job'])}")
    public JobDetail getRedisCleanAndSaveInDbJob() {
        // 火爆合约redis定期清理并持久化任务
        return JobBuilder.newJob(RedisCleanAndSaveInDbJob.class).withIdentity("getRedisCleanAndSaveInDbJob").storeDurably().build();
    }

    @Bean
    @ConditionalOnExpression("#{'open'.equals(environment['hot.transaction.redis.clean.and.save.indb.job'])}")
    public Trigger redisCleanAndSaveInDbJobTrigger() {
        // 每天20点执行
        final CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(redisCleanCorn);
        return TriggerBuilder.newTrigger().forJob(getRedisCleanAndSaveInDbJob())
                .withIdentity("redisCleanAndSaveInDbJobTrigger")
                .withSchedule(cronScheduleBuilder)
                .build();
    }

}
