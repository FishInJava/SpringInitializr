package com.happyzombie.springinitializr.service.user;

import com.happyzombie.springinitializr.bean.GeneralStr;
import com.happyzombie.springinitializr.bean.dto.GetUserTransactionsDTO;
import com.happyzombie.springinitializr.bean.request.user.GetUserTransactionsRequest;
import com.happyzombie.springinitializr.common.util.CollectionUtil;
import com.happyzombie.springinitializr.common.util.DateUtil;
import com.happyzombie.springinitializr.common.util.JsonUtil;
import com.happyzombie.springinitializr.dao.TransactionsEntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 查询用户Transaction
 *
 * @author admin
 */
@Service
@Slf4j
public class UserTransactionService {
    @Resource
    TransactionsEntityMapper transactionsEntityMapper;

    public List<GetUserTransactionsDTO> getUserTransactions(GetUserTransactionsRequest request) {
        final List<GetUserTransactionsDTO> userTransactions = transactionsEntityMapper.getUserTransactions(request);
        // add_request_and_confirm方法转换
        if (CollectionUtil.isNotEmpty(userTransactions)) {
            userTransactions.forEach(transaction -> {
                // 时间转换
                transaction.setBlockTimestampStr(DateUtil.timestampMilliToString(transaction.getBlockTimestamp()));
                // 转换FunctionCall的arg参数
                if (GeneralStr.FUNCTION_CALL.equals(transaction.getActionKind())) {
                    final GetUserTransactionsDTO.Args args = JsonUtil.jsonStringToObject(transaction.getArgs(), GetUserTransactionsDTO.Args.class);
                    transaction.setArgsDTO(args);
                    transaction.setMethodName(args.getMethodName());

                    // 如果是add_request_and_confirm，找到真正的方法和参数
                    if (GeneralStr.ADD_REQUEST_AND_CONFIRM.equals(transaction.getMethodName())) {
                        // 解析真正的请求参数
                        final String realArgStr = args.getArgs();
                        final GetUserTransactionsDTO.Args.RealArg realArg = JsonUtil.jsonStringToObject(realArgStr, GetUserTransactionsDTO.Args.RealArg.class);
                        args.setRealArg(realArg);
                        final List<GetUserTransactionsDTO.Args.RealArg.RequestDTO.ActionsDTO> actions = realArg.getRequest().getActions();
                        if (CollectionUtil.isNotEmpty(actions) && actions.size() == 1) {
                            final GetUserTransactionsDTO.Args.RealArg.RequestDTO.ActionsDTO actionsDTO = actions.get(0);
                            final String type = actionsDTO.getType();
                            transaction.setRealAction(type);
                            // 这里可能要区分具体操作
                        } else {
                            log.error("actions 需要再分析！ hash:{}", transaction.getHash());
                        }

                    }

                }
            });
        }
        return userTransactions;
    }

    public Long getUserTransactionsCount(String userAccountId) {
        return transactionsEntityMapper.getUserTransactionsCount(userAccountId);
    }
}
