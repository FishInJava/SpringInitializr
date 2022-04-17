package com.happyzombie.springinitializr.bean.request.user;

import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.util.List;

/**
 * @author admin
 */
@Data
public class GetUserTransactionsRequest extends PageInfo {
    private String userAccountId;
    /**
     * 过滤掉的方法名称
     */
    private List<String> filterMethodName;
}
