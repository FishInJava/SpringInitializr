package com.happyzombie.springinitializr.dao;

import com.happyzombie.springinitializr.bean.entity.HotTransactionDailyEntity;

/**
 * @author admin
 */
public interface HotTransactionDailyEntityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(HotTransactionDailyEntity row);

    int insertSelective(HotTransactionDailyEntity row);

    HotTransactionDailyEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(HotTransactionDailyEntity row);

    int updateByPrimaryKeyWithBLOBs(HotTransactionDailyEntity row);

    int updateByPrimaryKey(HotTransactionDailyEntity row);
}