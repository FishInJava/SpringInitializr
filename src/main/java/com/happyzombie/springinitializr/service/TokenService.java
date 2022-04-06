package com.happyzombie.springinitializr.service;

import com.happyzombie.springinitializr.bean.entity.FungibleTokensEntityWithBLOBs;
import com.happyzombie.springinitializr.bean.entity.NonFungibleTokensEntityWithBLOBs;
import com.happyzombie.springinitializr.bean.response.nearcore.FTMetadataResponse;
import com.happyzombie.springinitializr.bean.response.nearcore.NFTMetadataResponse;
import com.happyzombie.springinitializr.dao.FungibleTokensEntityMapper;
import com.happyzombie.springinitializr.dao.NonFungibleTokensEntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author admin
 */
@Service
@Slf4j
public class TokenService {

    @Autowired
    NearRpcServiceImpl nearRpcService;

    @Resource
    FungibleTokensEntityMapper fungibleTokensEntityMapper;

    @Resource
    NonFungibleTokensEntityMapper nonFungibleTokensEntityMapper;

    /**
     * 查询同质化代币信息
     */
    public FungibleTokensEntityWithBLOBs getFTMetadata(String contractName) {
        // 查数据库
        final FungibleTokensEntityWithBLOBs fungibleTokensEntity = fungibleTokensEntityMapper.selectByPrimaryKey(contractName);
        if (fungibleTokensEntity != null) {
            return fungibleTokensEntity;
        }

        // 查询链上数据
        final FTMetadataResponse ftMetadata = nearRpcService.getFTMetadata(contractName);
        final FungibleTokensEntityWithBLOBs insert = new FungibleTokensEntityWithBLOBs();
        Optional.of(ftMetadata).ifPresent(ftMetadataResponse -> {
            final FTMetadataResponse.ResultDTO.FTMetadata metadata = ftMetadataResponse.getResult().getMetadata();
            insert.setAccountId(contractName);
            insert.setSymbol(metadata.getSymbol());
            insert.setName(metadata.getName());
            insert.setDecimals(metadata.getDecimals());
            insert.setSpec(metadata.getSpec());
            insert.setIcon(metadata.getIcon());
            insert.setReference(metadata.getReference());
            insert.setReferenceHash(metadata.getReferenceHash());
            fungibleTokensEntityMapper.insert(insert);
        });
        return insert;
    }

    /**
     * 查询nft信息
     */
    public NonFungibleTokensEntityWithBLOBs getNFTMetadata(String contractName) {
        // 查数据库
        final NonFungibleTokensEntityWithBLOBs nonFungibleTokensEntity = nonFungibleTokensEntityMapper.selectByPrimaryKey(contractName);
        if (nonFungibleTokensEntity != null) {
            return nonFungibleTokensEntity;
        }

        // 查询链上数据
        final NFTMetadataResponse nftMetadata = nearRpcService.getNFTMetadata(contractName);
        final NonFungibleTokensEntityWithBLOBs insert = new NonFungibleTokensEntityWithBLOBs();
        Optional.of(nftMetadata).ifPresent(nftMetadataResponse -> {
            final NFTMetadataResponse.ResultDTO.NFTMetadata metadata = nftMetadataResponse.getResult().getMetadata();
            insert.setAccountId(contractName);
            insert.setSymbol(metadata.getSymbol());
            insert.setName(metadata.getName());
            insert.setSpec(metadata.getSpec());
            insert.setIcon(metadata.getIcon());
            insert.setBaseUri(metadata.getBaseUri());
            insert.setReference(metadata.getReference());
            insert.setReferenceHash(metadata.getReferenceHash());
            nonFungibleTokensEntityMapper.insert(insert);
        });
        return insert;
    }

}
