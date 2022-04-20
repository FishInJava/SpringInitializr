package com.happyzombie.springinitializr.service.user;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.happyzombie.springinitializr.bean.ActionEnum;
import com.happyzombie.springinitializr.bean.GeneralConstant;
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

    private static void createFirstAction(GetUserTransactionsDTO transaction) {
        final String actionKind = transaction.getActionKind();
        final String args = transaction.getArgs();
        final ActionEnum.BigAction firstAction = createAction(actionKind, args);
        // 设置默认ReceiverId
        firstAction.setReceiverId(transaction.getReceiverAccountId());
        transaction.pushAction(firstAction);
    }

    private static ActionEnum.BigAction createAction(String actionKind, String args) {
        final ActionEnum action = ActionEnum.getAction(actionKind);
        switch (action) {
            case FUNCTION_CALL:
            case ADD_KEY: {
                final ActionEnum.BigAction bigAction = JsonUtil.jsonStringToObject(args, ActionEnum.BigAction.class);
                bigAction.setType(action.getValue());
                return bigAction;
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
    public static void handlerAction(GetUserTransactionsDTO transaction) {
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
    private static void handlerFunctionCallMethod(GetUserTransactionsDTO transaction) {
        final ActionEnum.BigAction firstAction = transaction.getFirstAction();
        final String args = firstAction.getArgs();
        switch (firstAction.getMethodName()) {
            case GeneralConstant.FUNCTION_CALL_ADD_REQUEST_AND_CONFIRM: {
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
            case GeneralConstant.FUNCTION_CALL_CONFIRM: {
                /**
                 * confirm识别出request_id
                 */
                final ActionEnum.FunctionCall.ConfirmRequest confirmRequest = JsonUtil.jsonStringToObject(args, ActionEnum.FunctionCall.ConfirmRequest.class);
                firstAction.setRequestId(confirmRequest.getRequestId());
            }
            break;
            case GeneralConstant.FUNCTION_CALL_DELETE_REQUEST: {
                /**
                 * delete_request识别出request_id
                 */
                final ActionEnum.FunctionCall.DeleteRequest deleteRequest = JsonUtil.jsonStringToObject(args, ActionEnum.FunctionCall.DeleteRequest.class);
                firstAction.setRequestId(deleteRequest.getRequestId());
            }
            break;
            default:
                log.info("对该方法不处理：{}, hash:{}", firstAction.getMethodName(), transaction.getHash());
        }
    }

    /**
     * 查询总数，分页使用
     */
    public Long getUserTransactionsCount(String userAccountId) {
        return transactionsEntityMapper.getUserTransactionsCount(userAccountId);
    }

    /**
     * 获取用户交易分类信息：按照action_kind分类
     */
    public List<TransactionActionsDTO> getTransactionActions(String userAccountId) {
        return transactionsEntityMapper.getTransactionActions(userAccountId);
    }

}
