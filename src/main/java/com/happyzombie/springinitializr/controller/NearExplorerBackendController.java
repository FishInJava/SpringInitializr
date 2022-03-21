package com.happyzombie.springinitializr.controller;

import com.happyzombie.springinitializr.common.bean.Result;
import com.happyzombie.springinitializr.common.util.DateUtil;
import com.happyzombie.springinitializr.service.NearExplorerBackendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * https://infura.io/
 * account:hibabyzombie@yahoo.com
 */
@RestController
@Slf4j
@RequestMapping("/nearExplorerBackendController")
public class NearExplorerBackendController {
    @Autowired
    NearExplorerBackendService nearExplorerBackendService;

    @CrossOrigin
    @RequestMapping(value = "/getTransactionsListByAccountId/{accountId}/{endDate}/{page}", method = RequestMethod.GET)
    public Result<Object> getTransactionsListByAccountId(@PathVariable("accountId") String accountId, @PathVariable("endDate") String endDate, @PathVariable("page") Integer page) {
        // 输入天(年月日)，按天分页
        final Long aLong = DateUtil.dataStrToMilli(endDate);
        log.info("当前日期：{}，对应时间戳：{}", endDate, aLong);
        nearExplorerBackendService.getTransactionsListByAccountId(accountId, aLong, page);
        return Result.successResult(true);
    }

}
