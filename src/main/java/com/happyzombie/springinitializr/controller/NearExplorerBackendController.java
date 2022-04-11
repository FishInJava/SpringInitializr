package com.happyzombie.springinitializr.controller;

import com.happyzombie.springinitializr.bean.response.nearcore.BlockDetailsResponse;
import com.happyzombie.springinitializr.bean.response.nearcore.ChunkDetailsResponse;
import com.happyzombie.springinitializr.bean.response.nearcore.ReceiptDetailsResponse;
import com.happyzombie.springinitializr.bean.response.nearcore.TxStatusResponse;
import com.happyzombie.springinitializr.bean.response.nearcore.ViewAccountResponse;
import com.happyzombie.springinitializr.common.bean.Result;
import com.happyzombie.springinitializr.common.util.DateUtil;
import com.happyzombie.springinitializr.common.util.StringUtil;
import com.happyzombie.springinitializr.service.NearExplorerBackendService;
import com.happyzombie.springinitializr.service.NearRpcServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping("/nearExplorerBackendController")
public class NearExplorerBackendController {
    @Resource
    NearExplorerBackendService nearExplorerBackendService;

    @Resource
    NearRpcServiceImpl nearRpcService;

    // 数据采集完成后，查询接口走数据库
    @Deprecated
    @CrossOrigin
    @RequestMapping(value = "/getTransactionsListByAccountId/{accountId}/{endDate}/{page}", method = RequestMethod.GET)
    public Result<Object> getTransactionsListByAccountId(@PathVariable("accountId") String accountId, @PathVariable("endDate") String endDate, @PathVariable("page") Integer page) {
        if (StringUtil.isEmpty(endDate) && page == null) {
            nearExplorerBackendService.getNewestTransactionsListByAccountId(accountId);
        } else {
            // 输入天(年月日)，按天分页
            final Long aLong = DateUtil.dataStrToMilli(endDate);
            log.info("当前日期：{}，对应时间戳：{}", endDate, aLong);
            nearExplorerBackendService.getTransactionsListByAccountId(accountId, aLong, page);
        }
        return Result.successResult(true);
    }

    @CrossOrigin
    @RequestMapping(value = "/updateTransactionByAccountId/{accountId}", method = RequestMethod.GET)
    public Result<Object> updateTransactionByAccountId(@PathVariable("accountId") String accountId) {
        // 账号有效性校验
        final ViewAccountResponse viewAccountResponse = nearRpcService.viewAccount(accountId);
        if (viewAccountResponse.getResult() != null) {
            nearExplorerBackendService.getNewestTransactionsListByAccountId(accountId);
        }
        return Result.successResult(true);
    }

    @CrossOrigin
    @RequestMapping(value = "/getTransactionStatus/{history}/{transactionHash}/{senderAccountId}", method = RequestMethod.GET)
    public Result<Object> getTransactionStatus(@PathVariable("history") Boolean history, @PathVariable("transactionHash") String transactionHash, @PathVariable("senderAccountId") String senderAccountId) {
        final TxStatusResponse transactionStatus = nearRpcService.getTransactionStatus(transactionHash, senderAccountId, history);
        return Result.successResult(transactionStatus);
    }

    @CrossOrigin
    @RequestMapping(value = "/getLatestBlockDetail", method = RequestMethod.GET)
    public Result<Object> getLatestBlockDetail() {
        final BlockDetailsResponse latestBlockDetail = nearRpcService.getLatestBlockDetail();
        return Result.successResult(latestBlockDetail);
    }

    @CrossOrigin
    @RequestMapping(value = "/getChunkDetailsById/{chunkId}", method = RequestMethod.GET)
    public Result<Object> getChunkDetailsById(@PathVariable("chunkId") String chunkId) {
        final ChunkDetailsResponse latestBlockDetail = nearRpcService.getChunkDetailsById(chunkId);
        return Result.successResult(latestBlockDetail);
    }

    @CrossOrigin
    @RequestMapping(value = "/getHistoricalChunkDetailsById/{chunkId}", method = RequestMethod.GET)
    public Result<Object> getHistoricalChunkDetailsById(@PathVariable("chunkId") String chunkId) {
        final ChunkDetailsResponse latestBlockDetail = nearRpcService.getHistoricalChunkDetailsById(chunkId);
        return Result.successResult(latestBlockDetail);
    }

    @CrossOrigin
    @RequestMapping(value = "/getReceiptById/{receiptId}", method = RequestMethod.GET)
    public Result<Object> getReceiptById(@PathVariable("receiptId") String receiptId) {
        final ReceiptDetailsResponse latestBlockDetail = nearRpcService.getReceiptById(receiptId);
        return Result.successResult(latestBlockDetail);
    }

    @CrossOrigin
    @RequestMapping(value = "/getBlockDetailByBlockHash/{blockHash}", method = RequestMethod.GET)
    public Result<Object> getBlockDetailByBlockHash(@PathVariable("blockHash") String blockHash) {
        final BlockDetailsResponse latestBlockDetail = nearRpcService.getBlockDetailByBlockHash(blockHash);
        return Result.successResult(latestBlockDetail);
    }

    @CrossOrigin
    @RequestMapping(value = "/getHistoricalBlockDetailByBlockHash/{blockHash}", method = RequestMethod.GET)
    public Result<Object> getHistoricalBlockDetailByBlockHash(@PathVariable("blockHash") String blockHash) {
        final BlockDetailsResponse latestBlockDetail = nearRpcService.getHistoricalBlockDetailByBlockHash(blockHash);
        return Result.successResult(latestBlockDetail);
    }

    @CrossOrigin
    @RequestMapping(value = "/getBlockDetailByBlockId/{height}", method = RequestMethod.GET)
    public Result<Object> getBlockDetailByBlockId(@PathVariable("height") Long height) {
        final BlockDetailsResponse latestBlockDetail = nearRpcService.getBlockDetailByBlockId(height);
        return Result.successResult(latestBlockDetail);
    }
}
