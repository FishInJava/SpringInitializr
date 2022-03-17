package com.happyzombie.springinitializr.dao;


import com.happyzombie.springinitializr.bean.Do.NftInfoDO;

public interface NftInfoMapper {

    void insertOne(NftInfoDO nftInfoDo);

    NftInfoDO selectOne(NftInfoDO nftInfoDo);

}
