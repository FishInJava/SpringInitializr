package com.happyzombie.springinitializr.controller;

import com.google.common.collect.Sets;
import com.happyzombie.springinitializr.bean.Do.NftInfoDO;
import com.happyzombie.springinitializr.bean.Do.NftRarityDO;
import com.happyzombie.springinitializr.bean.ParasAttributesDTO;
import com.happyzombie.springinitializr.bean.ParasRarityResponseDTO;
import com.happyzombie.springinitializr.common.bean.Result;
import com.happyzombie.springinitializr.common.util.AssertUtil;
import com.happyzombie.springinitializr.common.util.JsonUtil;
import com.happyzombie.springinitializr.service.ParasDataCollectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Slf4j
@RequestMapping("/parasController")
public class ParasController {

    static final String ANTISOCIAL_APE_CLUB = "Antisocial Ape Club";
    static final String SKIN = "Skin";
    static final String MOUTH = "Mouth";
    static final String EYES = "Eyes";
    static final String HEAD = "Head";
    static final String NECK = "Neck";
    static final String EARRINGS = "Earrings";
    static final HashSet<String> traitTypeSet = Sets.newHashSet(ANTISOCIAL_APE_CLUB, SKIN, MOUTH, EYES, HEAD, NECK, EARRINGS);

    @Autowired
    ParasDataCollectionService parasDataCollectionService;

    @CrossOrigin
    @RequestMapping(value = "/getParasData/{start}", method = RequestMethod.GET)
    public Result<Object> getParasData(@PathVariable(value = "start") Integer start) {
        // final List<ParasAttributesDTO.DataDTO.ResultsDTO.MetadataDTO.AttributesDTO> nftAttributes = parasDataCollectionService.getNFTAttributes("asac.near", "2694");
        final List<ParasAttributesDTO.DataDTO.ResultsDTO.MetadataDTO.AttributesDTO> nftAttributes = parasDataCollectionService.getNFTAttributes("asac.near", start.toString());
        final ParasRarityResponseDTO nftAttributesRarity = parasDataCollectionService.getNFTAttributesRarity(nftAttributes);
        log.info("getParasData + " + new Date());
        return Result.successResult(nftAttributesRarity);
    }

    static Integer beginTokenSeriesId = 0;
    static Integer endTokenSeriesId = 3333;

    @CrossOrigin
    @RequestMapping(value = "/getParasDataStart/{begin}", method = RequestMethod.GET)
    public Result<Object> getParasDataStart(@PathVariable(value = "begin") Integer begin) {
        AssertUtil.shouldBeTrue(begin >= 0, "begin参数不合法");
        beginTokenSeriesId = begin;
        // todo 事务
        while (endTokenSeriesId > beginTokenSeriesId) {
            log.info("getParasDataStart ,begin:{} ", beginTokenSeriesId);
            // 从数据库中判断是否存在该笔信息
            NftInfoDO nftInfoDo = parasDataCollectionService.getNftInfoByTokenSeriesId(beginTokenSeriesId.toString());
            List<ParasAttributesDTO.DataDTO.ResultsDTO.MetadataDTO.AttributesDTO> nftAttributes;
            if (nftInfoDo == null) {
                // 查询nft信息
                final ParasAttributesDTO parasAttributesDto = parasDataCollectionService.getNFTInfo("asac.near", beginTokenSeriesId.toString());
                // 获取nft属性信息
                nftAttributes = parasDataCollectionService.getNFTAttributes(parasAttributesDto);
                // 入库
                final NftInfoDO nftInfo = new NftInfoDO();
                // todo 增加判断
                final ParasAttributesDTO.DataDTO.ResultsDTO resultsDTO = parasAttributesDto.getData().getResults().get(0);
                nftInfo.setContractId(resultsDTO.getContractId());
                nftInfo.setNftId(resultsDTO.getId());
                nftInfo.setAttributes(JsonUtil.objectToString(parasAttributesDto));// 相应的json格式
                nftInfo.setTokenSeriesId(resultsDTO.getTokenSeriesId());
                nftInfo.setName(resultsDTO.getMetadata().getName());
                // ipfs图片网址
                nftInfo.setMedia(resultsDTO.getMetadata().getMedia());
                nftInfoDo = nftInfo;// 这个写法我看着很烦
                parasDataCollectionService.insertNftInfo(nftInfo);
            } else {
                // 将NftInfoDo转换成Json
                log.info("从库中成功获取信息");
                final ParasAttributesDTO parasAttributesDTO = JsonUtil.jsonStringToObject(nftInfoDo.getAttributes(), ParasAttributesDTO.class);
                nftAttributes = parasDataCollectionService.getNFTAttributes(parasAttributesDTO);
            }

            // 从数据库查询稀有度
            final NftRarityDO nftRarityDo = parasDataCollectionService.getNftRarityByTokenSeriesId(beginTokenSeriesId.toString());
            if (nftRarityDo == null) {
                // 查询稀有度信息
                final ParasRarityResponseDTO nftAttributesRarity = parasDataCollectionService.getNFTAttributesRarity(nftAttributes);
                // todo 校验
                // 入库
                final NftRarityDO nftRarityDO = new NftRarityDO();
                nftRarityDO.setNftId(nftInfoDo.getNftId());
                nftRarityDO.setTokenSeriesId(nftInfoDo.getTokenSeriesId());
                nftRarityDO.setRarity(JsonUtil.objectToString(nftAttributesRarity));
                final List<ParasRarityResponseDTO.DataDTO> data = nftAttributesRarity.getData();
                // 转换成map方便赋值
                final Map<String, ParasRarityResponseDTO.DataDTO> map = rarityDataToMap(data);
                nftRarityDO.setAntisocialApeClub(map.get(ANTISOCIAL_APE_CLUB).getValue());
                nftRarityDO.setAntisocialApeClubCount(map.get(ANTISOCIAL_APE_CLUB).getRarity().getCount());
                nftRarityDO.setAntisocialApeClubRarity(map.get(ANTISOCIAL_APE_CLUB).getRarity().getRarity());
                nftRarityDO.setSkin(map.get(SKIN).getValue());
                nftRarityDO.setSkinCount(map.get(SKIN).getRarity().getCount());
                nftRarityDO.setSkinRarity(map.get(SKIN).getRarity().getRarity());
                nftRarityDO.setMouth(map.get(MOUTH).getValue());
                nftRarityDO.setMouthCount(map.get(MOUTH).getRarity().getCount());
                nftRarityDO.setMouthRarity(map.get(MOUTH).getRarity().getRarity());
                nftRarityDO.setEyes(map.get(EYES).getValue());
                nftRarityDO.setEyesCount(map.get(EYES).getRarity().getCount());
                nftRarityDO.setEyesRarity(map.get(EYES).getRarity().getRarity());
                nftRarityDO.setHead(map.get(HEAD).getValue());
                nftRarityDO.setHeadCount(map.get(HEAD).getRarity().getCount());
                nftRarityDO.setHeadRarity(map.get(HEAD).getRarity().getRarity());
                nftRarityDO.setNeck(map.get(NECK).getValue());
                nftRarityDO.setNeckCount(map.get(NECK).getRarity().getCount());
                nftRarityDO.setNeckRarity(map.get(NECK).getRarity().getRarity());
                nftRarityDO.setEarrings(map.get(EARRINGS).getValue());
                nftRarityDO.setEarringsCount(map.get(EARRINGS).getRarity().getCount());
                nftRarityDO.setEarringsRarity(map.get(EARRINGS).getRarity().getRarity());
                parasDataCollectionService.insertNftRarity(nftRarityDO);
            }
            // 下一个nft信息查询
            beginTokenSeriesId++;
        }
        return Result.successResult(true);
    }

    private Map<String, ParasRarityResponseDTO.DataDTO> rarityDataToMap(List<ParasRarityResponseDTO.DataDTO> data) {
        final HashMap<String, ParasRarityResponseDTO.DataDTO> map = new HashMap<>();
        data.forEach(dataDTO -> {
            final String traitType = dataDTO.getTraitType();
            if (traitTypeSet.contains(traitType)) {
                map.put(traitType, dataDTO);
            } else {
                log.error("未出现种类 : {}", data);
            }
        });
        return map;
    }

}
