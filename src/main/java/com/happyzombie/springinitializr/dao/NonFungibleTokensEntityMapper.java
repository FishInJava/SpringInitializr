package com.happyzombie.springinitializr.dao;

import com.happyzombie.springinitializr.bean.entity.NonFungibleTokensEntity;
import com.happyzombie.springinitializr.bean.entity.NonFungibleTokensEntityWithBLOBs;

public interface NonFungibleTokensEntityMapper {
    int deleteByPrimaryKey(String accountId);

    int insert(NonFungibleTokensEntityWithBLOBs row);

    int insertSelective(NonFungibleTokensEntityWithBLOBs row);

    NonFungibleTokensEntityWithBLOBs selectByPrimaryKey(String accountId);

    int updateByPrimaryKeySelective(NonFungibleTokensEntityWithBLOBs row);

    int updateByPrimaryKeyWithBLOBs(NonFungibleTokensEntityWithBLOBs row);

    int updateByPrimaryKey(NonFungibleTokensEntity row);
}