package com.happyzombie.springinitializr.controller;

import com.happyzombie.springinitializr.common.bean.Result;
import com.happyzombie.springinitializr.service.HotTransactionsFindService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/nearAnalyzeController")
public class NearAnalyzeController {
    @Autowired
    HotTransactionsFindService hotTransactionsFindService;

    @CrossOrigin
    @RequestMapping(value = "/getBlockDetailByBlockId/{start}/{end}", method = RequestMethod.GET)
    public Result<Object> getBlockDetailByBlockId(@PathVariable("start") Long start, @PathVariable("end") Long end) {
        final Set<ZSetOperations.TypedTuple<String>> hotAccountId = hotTransactionsFindService.getHotAccountId(start, end);
        return Result.successResult(hotAccountId);
    }
}
