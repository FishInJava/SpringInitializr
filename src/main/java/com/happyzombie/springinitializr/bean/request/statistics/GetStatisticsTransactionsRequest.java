package com.happyzombie.springinitializr.bean.request.statistics;

import com.github.pagehelper.PageInfo;
import lombok.Data;

/**
 * @author admin
 */
@Data
public class GetStatisticsTransactionsRequest extends PageInfo {
    /**
     * 1以signer_id分组 2以receiver_id分组
     */
    private Integer flag;

    /**
     * 时间 单位毫秒
     */
    private Long milliTime;

    private Long beginTime;
    private Long endTime;
}
