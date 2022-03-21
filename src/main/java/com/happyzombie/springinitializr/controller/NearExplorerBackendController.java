package com.happyzombie.springinitializr.controller;

import com.happyzombie.springinitializr.api.NearExplorerBackendService;
import com.happyzombie.springinitializr.common.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

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
    @RequestMapping(value = "/getTransactionsListByAccountId/{accountId}", method = RequestMethod.GET)
    public Result<Object> getTransactionsListByAccountId(@PathVariable("accountId") String accountId) {
        final BigInteger bigInteger = new BigInteger("1646907653388");
        nearExplorerBackendService.getTransactionsListByAccountId(accountId, bigInteger, 0);
        return Result.successResult(true);
    }

}
