package com.happyzombie.springinitializr.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.happyzombie.springinitializr.bean.dto.GetUserTransactionsDTO;
import com.happyzombie.springinitializr.bean.dto.TransactionActionsDTO;
import com.happyzombie.springinitializr.bean.request.user.GetUserTransactionsRequest;
import com.happyzombie.springinitializr.bean.response.nearcore.ViewAccountResponse;
import com.happyzombie.springinitializr.common.bean.Result;
import com.happyzombie.springinitializr.common.util.AssertUtil;
import com.happyzombie.springinitializr.service.NearExplorerBackendService;
import com.happyzombie.springinitializr.service.NearRpcServiceImpl;
import com.happyzombie.springinitializr.service.user.UserLinkAccountService;
import com.happyzombie.springinitializr.service.user.UserTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * @author admin
 */
@RestController
@Slf4j
@Validated
@RequestMapping("/userTransactionController")
public class UserTransactionController {
    @Resource
    UserTransactionService userTransactionService;

    @Resource
    NearExplorerBackendService nearExplorerBackendService;

    @Resource
    NearRpcServiceImpl nearRpcService;

    @Resource
    UserLinkAccountService userLinkAccountService;

    @CrossOrigin
    @RequestMapping(value = "/getUserTransactions", method = RequestMethod.POST)
    public Result<Object> getUserTransactions(@RequestBody GetUserTransactionsRequest request) {
        // 账号有效性校验
        checkAccountAndUpdate(request.getUserAccountId());
        Page page = PageHelper.startPage(request.getPageNum(), request.getPageSize());
        request.setStartRow(page.getStartRow());
        request.setEndRow(page.getEndRow() - 1);
        // 无论是否更新成功都查询数据
        final List<GetUserTransactionsDTO> userTransactions = userTransactionService.getUserTransactions(request);
        // 查询用户actions
        final List<TransactionActionsDTO> actions = userTransactionService.getTransactionActions(request.getUserAccountId());
        // 查询总数
        final Long total = userTransactionService.getUserTransactionsCount(request.getUserAccountId());
        final HashMap<String, Object> result = new HashMap<>();
        result.put("list", userTransactions);
        result.put("total", total);
        result.put("actions", actions);
        return Result.successResult(result);
    }

    /**
     * 用户关联账户统计
     * 查询FunctionCall是Transfer的交易（成功状态）
     * add_request_and_confirm中type是Transfer的交易（成功状态）
     * todo 需要account-id维度的锁，网页上不同的可能同时查询相同的账号
     */
    @CrossOrigin
    @RequestMapping(value = "/getTransferTransactions", method = RequestMethod.POST)
    public Result<Object> getTransferTransactions(@RequestBody GetUserTransactionsRequest request) {
        checkAccountAndUpdate(request.getUserAccountId());

        final HashMap userLinkAccount = userLinkAccountService.getUserLinkAccount(request.getUserAccountId());

        return Result.successResult(userLinkAccount);
    }

    private void checkAccountAndUpdate(String accountId) {
        final ViewAccountResponse viewAccountResponse = nearRpcService.viewAccount(accountId);
        Optional.of(viewAccountResponse).ifPresent(viewAccount -> {
            // 更新数据库
            AssertUtil.shouldBeTrue(viewAccountResponse.getResult() != null, "账号无效：" + accountId);
            nearExplorerBackendService.getNewestTransactionsListByAccountId(accountId);
        });
    }

}
