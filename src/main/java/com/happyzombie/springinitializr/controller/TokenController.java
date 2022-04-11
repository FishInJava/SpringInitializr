package com.happyzombie.springinitializr.controller;

import com.happyzombie.springinitializr.bean.entity.FungibleTokensEntityWithBLOBs;
import com.happyzombie.springinitializr.bean.entity.NonFungibleTokensEntityWithBLOBs;
import com.happyzombie.springinitializr.common.bean.Result;
import com.happyzombie.springinitializr.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author admin
 */
@RestController
@Slf4j
@Validated
@RequestMapping("/tokenController")
public class TokenController {
    @Resource
    TokenService tokenService;

    @CrossOrigin
    @RequestMapping(value = "/getFTMetadata/{contractName}", method = RequestMethod.GET)
    public Result<Object> getFTMetadata(@PathVariable("contractName") String contractName) {
        final FungibleTokensEntityWithBLOBs ftMetadata = tokenService.getFTMetadata(contractName);
        return Result.successResult(ftMetadata);
    }

    @CrossOrigin
    @RequestMapping(value = "/getNFTMetadata/{contractName}", method = RequestMethod.GET)
    public Result<Object> getNFTMetadata(@PathVariable("contractName") String contractName) {
        final NonFungibleTokensEntityWithBLOBs nftMetadata = tokenService.getNFTMetadata(contractName);
        return Result.successResult(nftMetadata);
    }
}
