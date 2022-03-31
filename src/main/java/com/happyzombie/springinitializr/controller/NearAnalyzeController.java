package com.happyzombie.springinitializr.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.happyzombie.springinitializr.common.bean.Result;
import com.happyzombie.springinitializr.service.HotTransactionsFindService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/nearAnalyzeController")
public class NearAnalyzeController {
    @Autowired
    HotTransactionsFindService hotTransactionsFindService;

    @CrossOrigin
    @RequestMapping(value = "/getHotAccountId", method = RequestMethod.POST)
    public Result<Object> getHotAccountId(@RequestBody PageInfo pageInfo) {
        Page page = PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        final Set<ZSetOperations.TypedTuple<String>> hotAccountId = hotTransactionsFindService.getHotAccountId(page.getStartRow(), page.getEndRow() - 1);
        final Long hotAccountIdTotalCount = hotTransactionsFindService.getHotAccountIdTotalCount();
        final HashMap<String, Object> result = new HashMap<>();
        result.put("list", hotAccountId);
        result.put("total", hotAccountIdTotalCount);
        return Result.successResult(result);
    }

    @CrossOrigin
    @RequestMapping(value = "/getHotMethodByAccountId/{accountId}/{start}/{end}", method = RequestMethod.GET)
    public Result<Object> getHotMethodByAccountId(@PathVariable("accountId") String accountId, @PathVariable("start") Long start, @PathVariable("end") Long end) {
        final Set<ZSetOperations.TypedTuple<String>> hotAccountId = hotTransactionsFindService.getHotMethodByAccountId(accountId, start, end);
        return Result.successResult(hotAccountId);
    }
}
