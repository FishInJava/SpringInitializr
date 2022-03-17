package com.happyzombie.springinitializr.dao;


import com.happyzombie.springinitializr.bean.Do.NftRarityDO;

public interface NftRarityMapper {

    void insertOne(NftRarityDO nftRarityDo);

    NftRarityDO selectOne(NftRarityDO nftInfoDo);

}
