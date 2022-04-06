package com.happyzombie.springinitializr.dao;

import com.happyzombie.springinitializr.bean.entity.FungibleTokensEntity;
import com.happyzombie.springinitializr.bean.entity.FungibleTokensEntityWithBLOBs;

public interface FungibleTokensEntityMapper {
    int deleteByPrimaryKey(String accountId);

    int insert(FungibleTokensEntityWithBLOBs row);

    int insertSelective(FungibleTokensEntityWithBLOBs row);

    FungibleTokensEntityWithBLOBs selectByPrimaryKey(String accountId);

    int updateByPrimaryKeySelective(FungibleTokensEntityWithBLOBs row);

    int updateByPrimaryKeyWithBLOBs(FungibleTokensEntityWithBLOBs row);

    int updateByPrimaryKey(FungibleTokensEntity row);
}