package com.happyzombie.springinitializr.bean.request.statistics;

import lombok.Data;

/**
 * @author admin
 */
@Data
public class GetHotAccountIdByTimeRequest {

    /**
     * 时间 单位毫秒
     */
    private Long milliTime;
}
