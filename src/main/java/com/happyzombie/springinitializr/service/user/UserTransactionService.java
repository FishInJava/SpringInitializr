package com.happyzombie.springinitializr.service.user;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import com.happyzombie.springinitializr.bean.ActionEnum;
import com.happyzombie.springinitializr.bean.GeneralStr;
import com.happyzombie.springinitializr.bean.dto.GetUserTransactionsDTO;
import com.happyzombie.springinitializr.bean.dto.TransactionActionsDTO;
import com.happyzombie.springinitializr.bean.request.user.GetUserTransactionsRequest;
import com.happyzombie.springinitializr.common.util.CollectionUtil;
import com.happyzombie.springinitializr.common.util.DateUtil;
import com.happyzombie.springinitializr.common.util.JsonUtil;
import com.happyzombie.springinitializr.dao.TransactionsEntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 分页查询用户进行的交易
     */
    public List<GetUserTransactionsDTO> getUserTransactions(GetUserTransactionsRequest request) {
        final List<GetUserTransactionsDTO> userTransactions = transactionsEntityMapper.getUserTransactions(request);
        if (CollectionUtil.isEmpty(userTransactions)) {
            return userTransactions;
        }
        // 数据处理
        userTransactions.forEach(transaction -> {
            // 时间转换
            transaction.setBlockTimestampStr(DateUtil.timestampMilliToString(transaction.getBlockTimestamp()));
            // 处理Action(生成action链)
            handlerAction(transaction);
        });
        return userTransactions;
    }

    /**
     * 根据action查询用户操作
     */
    public HashMap<String, Map> getTransactionsByAction(GetUserTransactionsRequest request) {
        final List<GetUserTransactionsDTO> userTransactions = transactionsEntityMapper.getTransactionsByAction(request);
        if (CollectionUtil.isEmpty(userTransactions)) {
            return Maps.newHashMap();
        }
        final HashMap<String, BigDecimal> in = new HashMap<>();
        final HashMap<String, BigDecimal> out = new HashMap<>();
        final String userAccountId = request.getUserAccountId();
        // 数据处理
        userTransactions.forEach(transaction -> {
            // 时间转换
            transaction.setBlockTimestampStr(DateUtil.timestampMilliToString(transaction.getBlockTimestamp()));
            // 解析args获取转账数量
            final ActionEnum.Transfer transfer = JsonUtil.jsonStringToObject(transaction.getArgs(), ActionEnum.Transfer.class);
            // 转换单位
            final BigDecimal deposit = new BigDecimal(transfer.getDeposit()).divide(new BigDecimal("1000000000000000000000000"), 4, RoundingMode.HALF_UP);
            // 签名账户是输入账户，说明转出
            if (transaction.getSignerAccountId().equals(userAccountId)) {
                if (out.containsKey(transaction.getReceiverAccountId())) {
                    final BigDecimal bigInteger = out.get(transaction.getReceiverAccountId());
                    out.put(transaction.getReceiverAccountId(), deposit.add(bigInteger));
                } else {
                    out.put(transaction.getReceiverAccountId(), deposit);
                }
                // 接收账户是输入账户，说明转入
            } else if (transaction.getReceiverAccountId().equals(userAccountId)) {
                if (in.containsKey(transaction.getSignerAccountId())) {
                    final BigDecimal bigInteger = in.get(transaction.getSignerAccountId());
                    in.put(transaction.getSignerAccountId(), deposit.add(bigInteger));
                } else {
                    in.put(transaction.getSignerAccountId(), deposit);
                }
            }
        });
        final HashMap<String, Map> stringMapHashMap = new HashMap<>();
        stringMapHashMap.put("in", in);
        stringMapHashMap.put("out", out);
        return stringMapHashMap;
    }

    private void createFirstAction(GetUserTransactionsDTO transaction) {
        final String actionKind = transaction.getActionKind();
        final String args = transaction.getArgs();
        final ActionEnum.BigAction firstAction = createAction(actionKind, args);
        transaction.pushAction(firstAction);
    }

    private ActionEnum.BigAction createAction(String actionKind, String args) {
        final ActionEnum action = ActionEnum.getAction(actionKind);
        switch (action) {
            case FUNCTION_CALL:
            case ADD_KEY: {
                final ActionEnum.BigAction functionCall = JsonUtil.jsonStringToObject(args, ActionEnum.BigAction.class);
                functionCall.setType(action.getValue());
                return functionCall;
            }
            default:
                log.info("Action 未匹配");
                final ActionEnum.BigAction functionCall = JsonUtil.jsonStringToObject(args, ActionEnum.BigAction.class);
                return functionCall;
        }
    }

    /**
     * 处理Action
     */
    private void handlerAction(GetUserTransactionsDTO transaction) {
        // 如果actionList是空，说明是从数据库查出来的第一条数据，处理后生成头Action
        if (CollectionUtil.isEmpty(transaction.getActionList())) {
            createFirstAction(transaction);
        }
        final ActionEnum.BigAction firstAction = transaction.getFirstAction();
        if (firstAction.getType().equals(ActionEnum.FUNCTION_CALL.getValue())) {
            // 处理FUNCTION_CALL中的方法
            handlerFunctionCallMethod(transaction);
        } else if (firstAction.getType().equals(ActionEnum.ADD_KEY.getValue())) {
            // 将receiverId从Permission参数中提取到外层（前端可以不用判断，直接使用）
            final String receiverId = firstAction.getPermission().getReceiverId();
            firstAction.setReceiverId(receiverId);
        } else if (firstAction.getType().equals(ActionEnum.TRANSFER.getValue())) {
            final ObjectNode objectNode = JsonUtil.getObjectNode();
            objectNode.put("deposit", firstAction.getDeposit());
            objectNode.put("amount", firstAction.getAmount());
            firstAction.setArgsDirect(JsonUtil.objectToString(objectNode));
        } else {
            log.info("对该Action不处理, 待观察,hash:{}", transaction.getHash());
        }
    }

    /**
     * 处理FUNCTION_CALL中的方法
     * 这里只有自己合约中的方法吗？
     * 自带方法
     */
    private void handlerFunctionCallMethod(GetUserTransactionsDTO transaction) {
        final ActionEnum.BigAction action = transaction.getFirstAction();
        final String args = action.getArgs();
        switch (action.getMethodName()) {
            case GeneralStr.FUNCTION_CALL_ADD_REQUEST_AND_CONFIRM: {
                /**
                 * 本账号的合约中自带方法add_request_and_confirm，该方法并不是最终方法
                 * 真正的请求的合约和方法在args中
                 */
                final ActionEnum.FunctionCall.AddRequestAndConfirmRequest request = JsonUtil.jsonStringToObject(args, ActionEnum.FunctionCall.AddRequestAndConfirmRequest.class);
                final String receiverId = request.getRequest().getReceiverId();
                final List<ActionEnum.BigAction> actions = request.getRequest().getActions();
                // 处理所有action
                actions.forEach(actionsDTO -> {
                    // 将action入栈
                    actionsDTO.setReceiverId(receiverId);
                    transaction.pushAction(actionsDTO);
                    // 处理
                    handlerAction(transaction);
                });
            }
            break;
            case GeneralStr.FUNCTION_CALL_CONFIRM: {
                /**
                 * confirm识别出request_id
                 */
                final ActionEnum.FunctionCall.ConfirmRequest confirmRequest = JsonUtil.jsonStringToObject(args, ActionEnum.FunctionCall.ConfirmRequest.class);
                action.setRequestId(confirmRequest.getRequestId());
            }
            break;
            case GeneralStr.FUNCTION_CALL_DELETE_REQUEST: {
                /**
                 * delete_request识别出request_id
                 */
                final ActionEnum.FunctionCall.DeleteRequest deleteRequest = JsonUtil.jsonStringToObject(args, ActionEnum.FunctionCall.DeleteRequest.class);
                action.setRequestId(deleteRequest.getRequestId());
            }
            break;
            default:
                log.info("对该方法不处理：{}, hash:{}", action.getMethodName(), transaction.getHash());
        }
    }

    /**
     * 查询总数，分页使用
     */
    public Long getUserTransactionsCount(String userAccountId) {
        return transactionsEntityMapper.getUserTransactionsCount(userAccountId);
    }

    /**
     * 获取用户交易分类信息
     */
    public void getTransactionsType(String userAccountId) {
        // 总交易数目
        final Long userSynchronizedCount = transactionsEntityMapper.getUserSynchronizedCount(userAccountId);
        // Transfer数目（出/入）
        // signer_account_id和receiver_account_id都不是userAccountId数目
    }

    /**
     * 获取用户交易分类信息：按照action_kind分类
     */
    public List<TransactionActionsDTO> getTransactionActions(String userAccountId) {
        return transactionsEntityMapper.getTransactionActions(userAccountId);
    }

}
