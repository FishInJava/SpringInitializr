package com.happyzombie.springinitializr.controller;

import com.happyzombie.springinitializr.bean.response.reffinance.GetPoolResponse;
import com.happyzombie.springinitializr.bean.response.reffinance.LiquidityPoolResponse;
import com.happyzombie.springinitializr.bean.response.reffinance.RefFinancePoolInfo;
import com.happyzombie.springinitializr.common.bean.Result;
import com.happyzombie.springinitializr.common.util.BigDecimalUtil;
import com.happyzombie.springinitializr.service.reffinance.RefFinanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ref相关合约介绍
 * https://guide.ref.finance/developers/contracts
 *
 * @author admin
 */
@RestController
@Slf4j
@Validated
@RequestMapping("/refFinanceController")
public class RefFinanceController {

    @Resource
    RefFinanceService refFinanceService;

    @CrossOrigin
    @RequestMapping(value = "/getUserLiquidityPool/{accountId}", method = RequestMethod.GET)
    public Result<Object> getHotMethodByAccountId(@PathVariable("accountId") String accountId) {
        final List<LiquidityPoolResponse> userLiquidityPool = refFinanceService.getUserLiquidityPool(accountId);
        final HashMap<String, Object> result = new HashMap<>();
        result.put("accountId", accountId);
        result.put("list", userLiquidityPool);
        return Result.successResult(result);
    }

    /**
     * 获取用户在ref播种的农场，数值是份额
     */
    @CrossOrigin
    @RequestMapping(value = "/getListUserSeeds/{accountId}", method = RequestMethod.GET)
    public Result<Object> getListUserSeeds(@PathVariable("accountId") String accountId) {
        // 用户份额信息(farmStake)
        final Map<String, String> listUserSeeds = refFinanceService.getListUserSeeds(accountId);
        // 通过用户份额信息查找池子信息
        final List<GetPoolResponse.Pool> pools = listUserSeeds.keySet().stream().map(key -> {
                    // 以下是ref-ui项目中js的处理写法
                    final String[] split = key.split("@");
                    final Optional<Integer> poolId = Optional.of(Integer.parseInt(split[1]));

                    // 从链上获取池子信息
                    final GetPoolResponse.Pool poolInfo = refFinanceService.getPoolInfoFromChain(poolId.get()).getCallFunctionResult();
                    poolInfo.setPoolId(poolId.get());

                    // 从ref后台获取池子信息
                    final RefFinancePoolInfo poolInfoFromRef = refFinanceService.getPoolInfoFromRef(poolId.get());
                    poolInfo.setRefFinancePoolInfo(poolInfoFromRef);
                    final String join = String.join("/", poolInfoFromRef.getTokenSymbols());
                    poolInfo.setTokenPairs(join);

                    // 用户在该池子中质押farm量
                    final String userFarmStake = listUserSeeds.get(key);
                    poolInfo.setUserFarmStake(userFarmStake);

                    // 用户在该池子中shares量
                    final String userPoolShares = refFinanceService.getPoolShares(accountId, poolId.get()).getCallFunctionResult();
                    poolInfo.setUserPoolShares(userPoolShares);

                    // 计算用户在该池子中的价值（单位$）:(userFarmStake + userPoolShares) * (池子TVL / 池子shares_total_supply)
                    final BigDecimal userTotalShare = BigDecimalUtil.safePlus(new BigDecimal(userFarmStake), new BigDecimal(userPoolShares));
                    // 保留4位小数,4舍5入
                    final BigDecimal userValue = userTotalShare.multiply(new BigDecimal(poolInfoFromRef.getTvl())).divide(new BigDecimal(poolInfo.getSharesTotalSupply()), 4, RoundingMode.HALF_UP);
                    poolInfo.setUserValue(userValue.toString());

                    return poolInfo;
                }
        ).collect(Collectors.toList());
        final HashMap<String, Object> result = new HashMap<>();
        result.put("accountId", accountId);
        result.put("userSeeds", listUserSeeds);
        result.put("pools", pools);
        return Result.successResult(result);
    }

    /**
     * 查询池子信息
     */
    @CrossOrigin
    @RequestMapping(value = "/getPool/{poolId}", method = RequestMethod.GET)
    public Result<Object> getPool(@PathVariable("poolId") Integer poolId) {
        final GetPoolResponse pool = refFinanceService.getPoolInfoFromChain(poolId);
        final HashMap<String, Object> result = new HashMap<>();
        result.put("poolId", poolId);
        result.put("pool", pool.getCallFunctionResult());
        return Result.successResult(result);
    }
}
