package com.happyzombie.springinitializr.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.happyzombie.springinitializr.bean.dto.SelectStatisticsDTO;
import com.happyzombie.springinitializr.bean.entity.TransactionAnalyzeFilterEntity;
import com.happyzombie.springinitializr.bean.request.statistics.GetStatisticsTransactionsRequest;
import com.happyzombie.springinitializr.common.bean.Result;
import com.happyzombie.springinitializr.service.impl.HotTransactionsFindServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author admin
 */
@RestController
@Slf4j
@Validated
@RequestMapping("/nearAnalyzeController")
public class NearAnalyzeController {
    @Resource
    HotTransactionsFindServiceImpl hotTransactionsFindService;

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

    @CrossOrigin
    @RequestMapping(value = "/getStatisticsTransactions", method = RequestMethod.POST)
    public Result<Object> getStatisticsTransactions(@Valid @RequestBody GetStatisticsTransactionsRequest request) {
        Page page = PageHelper.startPage(request.getPageNum(), request.getPageSize());
        request.setStartRow(page.getStartRow());
        request.setEndRow(page.getEndRow() - 1);
        final List<SelectStatisticsDTO> statisticsTransactions = hotTransactionsFindService.getStatisticsTransactions(request);
        final Long statisticsTransactionsTotalCount = hotTransactionsFindService.getStatisticsTransactionsTotalCount(request);
        final HashMap<String, Object> result = new HashMap<>();
        result.put("list", statisticsTransactions);
        result.put("total", statisticsTransactionsTotalCount);
        return Result.successResult(result);
    }

    @CrossOrigin
    @RequestMapping(value = "/getStatisticsFilterTransactions", method = RequestMethod.POST)
    public Result<Object> getStatisticsFilterTransactions(@Valid @RequestBody GetStatisticsTransactionsRequest request) {
        Page page = PageHelper.startPage(request.getPageNum(), request.getPageSize());
        request.setStartRow(page.getStartRow());
        request.setEndRow(page.getEndRow() - 1);
        final List<TransactionAnalyzeFilterEntity> statisticsFilterTransactions = hotTransactionsFindService.getStatisticsFilterTransactions(request);
        final Long statisticsTransactionsTotalCount = hotTransactionsFindService.getStatisticsFilterTransactionsTotalCount(request);
        final HashMap<String, Object> result = new HashMap<>();
        result.put("list", statisticsFilterTransactions);
        result.put("total", statisticsTransactionsTotalCount);
        return Result.successResult(result);
    }
}
