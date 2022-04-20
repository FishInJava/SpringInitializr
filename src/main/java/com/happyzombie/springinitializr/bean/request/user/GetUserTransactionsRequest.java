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
    private String action;
    /**
     * 过滤掉的方法名称
     */
    private List<String> filterMethodName;
    /**
     * 过滤掉的action
     */
    private List<String> filterAction;
}
