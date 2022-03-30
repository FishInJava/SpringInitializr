package com.happyzombie.springinitializr.config;


import com.happyzombie.springinitializr.quartz.NearLatestBlockJob;
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
    public JobDetail getTradeJobBean() {
        // 交易量统计
        return JobBuilder.newJob(NearLatestBlockJob.class).withIdentity("nearLatestBlockJob").storeDurably().build();
    }

    @Bean
    @ConditionalOnExpression("#{'open'.equals(environment['near.latest.block.job'])}")
    public Trigger tradeJobQuartzTrigger() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
                .simpleSchedule()
                //设置时间周期单位秒
                .withIntervalInSeconds(1)
                .repeatForever();
        return TriggerBuilder.newTrigger().forJob(getTradeJobBean())
                .withIdentity("nearLatestBlockJob")
                .withSchedule(scheduleBuilder)
                .build();
    }

}
