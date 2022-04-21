package com.happyzombie.springinitializr.quartz.hottransaction;

import com.happyzombie.springinitializr.bean.RedisKey;
import com.happyzombie.springinitializr.bean.entity.HotTransactionDailyEntity;
import com.happyzombie.springinitializr.common.util.DateUtil;
import com.happyzombie.springinitializr.common.util.JsonUtil;
import com.happyzombie.springinitializr.dao.HotTransactionDailyEntityMapper;
import com.happyzombie.springinitializr.service.RedisService;
import com.happyzombie.springinitializr.service.impl.HotTransactionsFindServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

/**
 * 对Redis中统计的每日热门合约进行清理和持久化
 * 每日20点执行
 *
 * @author admin
 */
@Slf4j
@Component
public class RedisCleanAndSaveInDbJob extends QuartzJobBean {
    @Resource
    RedisService redisService;

    @Resource
    HotTransactionsFindServiceImpl hotTransactionsFindService;

    @Resource
    HotTransactionDailyEntityMapper hotTransactionDailyEntityMapper;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        try {
            log.info("RedisCleanAndSaveInDbJob开始执行！");
            // 获取排名前20的数据
            final Set<ZSetOperations.TypedTuple<String>> hotAccountId = hotTransactionsFindService.getHotAccountId(0, 20);
            // 暂时不记录合约中的方法
            final String arg = JsonUtil.objectToString(hotAccountId);
            saveInDb(arg);
            // 删除合约排名
            redisService.remove(RedisKey.HOT_TRANSACTIONS_FIND);
            // 删除合约方法
            redisService.removePattern("near.analyze:hot.transactions.find:hot.account.method*");
            log.info("RedisCleanAndSaveInDbJob执行结束！");
        } catch (Exception e) {
            log.error("RedisCleanAndSaveInDbJob 异常", e);
        }
    }

    private void saveInDb(String arg) {
        final HotTransactionDailyEntity hotTransactionDailyEntity = new HotTransactionDailyEntity();
        hotTransactionDailyEntity.setCreateTime(DateUtil.dayMilli());
        hotTransactionDailyEntity.setArgs(arg);
        hotTransactionDailyEntityMapper.insert(hotTransactionDailyEntity);
    }
}
