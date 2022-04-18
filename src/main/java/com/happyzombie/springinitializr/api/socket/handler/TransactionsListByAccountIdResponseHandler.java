package com.happyzombie.springinitializr.api.socket.handler;

import com.happyzombie.springinitializr.api.TransactionBaseInfo;
import com.happyzombie.springinitializr.api.WampMessageCodes;
import com.happyzombie.springinitializr.common.util.JsonUtil;
import com.happyzombie.springinitializr.service.GetAllTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * @author admin
 */
@Slf4j
@Service
public class TransactionsListByAccountIdResponseHandler implements SocketResponseHandler {
    /**
     * 自定义接口id
     */
    public static Integer ID_PREFIX = 100;

    @Autowired
    GetAllTransactionService getAllTransactionService;

    // 访问时要保证线程安全
    public static final HashMap<Integer, String> ID_ACCOUNT_ID = new HashMap<>();
    public static final Random RANDOM = new Random();

    /**
     * 应该设计成全局的，但是目前全局暂不明确
     *
     * @param accountId accountId
     * @return ID
     */
    public synchronized static Integer getRequestId(String accountId) {
        // [100,300)
        Integer a = 100 + RANDOM.nextInt(200);
        String strResult = ID_PREFIX + String.valueOf(a);
        final Integer result = Integer.valueOf(strResult);
        if (!ID_ACCOUNT_ID.containsKey(result)) {
            ID_ACCOUNT_ID.put(result, accountId);
            return result;
        }
        return getRequestId(accountId);
    }

    /**
     * 获取后清除
     *
     * @return Account ID
     */
    public synchronized static String getAccountByRequestId(Integer requestId) {
        final String result = Optional.ofNullable(ID_ACCOUNT_ID.get(requestId)).orElseThrow(() -> new RuntimeException("getAccountByRequestId失败，requestId ：" + requestId));
        ID_ACCOUNT_ID.remove(requestId);
        return result;
    }

    private synchronized static boolean containRequest(Integer requestId) {
        return ID_ACCOUNT_ID.containsKey(requestId);
    }

    @Override
    public boolean isMatch(String response) {
        // 如果第一个参数是50，第二个参数是 TransactionsListByAccountIdHandler.ID
        final LinkedList<Object> objectList = JsonUtil.jsonStringToList(response);
        final Object o = objectList.get(0);
        final Object o1 = objectList.get(1);
        // 丑陋
        return o instanceof Integer && ((Integer) o).intValue() == WampMessageCodes.RESULT && o1 instanceof Integer && containRequest(((Integer) o1));
    }

    @Override
    public void handler(String response) {
        // 提取第四个参数
        final LinkedList<Object> objectList = JsonUtil.jsonStringToList(response);
        final Object o = objectList.get(3);
        if (o instanceof List) {
            // 结果是List<Map>
            final Object mapList = ((List) o).get(0);
            final LinkedList<TransactionBaseInfo> transactionBaseInfos = JsonUtil.mapListToObjectList((List<Map>) mapList, TransactionBaseInfo.class);
            if (log.isDebugEnabled()) {
                log.debug("transactionBaseInfos  {}", transactionBaseInfos);
            }
            getAllTransactionService.addTask((Integer) objectList.get(1), transactionBaseInfos);
        } else {
            log.error("o don't instanceof List,{}", response);
        }
    }
}
