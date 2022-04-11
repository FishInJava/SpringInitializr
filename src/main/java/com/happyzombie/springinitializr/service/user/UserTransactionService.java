package com.happyzombie.springinitializr.service.user;

import com.happyzombie.springinitializr.bean.dto.GetUserTransactionsDTO;
import com.happyzombie.springinitializr.bean.request.user.GetUserTransactionsRequest;
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
        // 时间转换，add_request_and_confirm方法转换
        return userTransactions;
    }

    public Long getUserTransactionsCount(String userAccountId) {
        return transactionsEntityMapper.getUserTransactionsCount(userAccountId);
    }
}
