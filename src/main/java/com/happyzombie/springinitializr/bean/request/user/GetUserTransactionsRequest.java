package com.happyzombie.springinitializr.bean.request.user;

import com.github.pagehelper.PageInfo;
import lombok.Data;

/**
 * @author admin
 */
@Data
public class GetUserTransactionsRequest extends PageInfo {
    private String userAccountId;
}
