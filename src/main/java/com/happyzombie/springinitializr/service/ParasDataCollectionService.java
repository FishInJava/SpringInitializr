package com.happyzombie.springinitializr.service;

import com.happyzombie.springinitializr.bean.Do.NftInfoDO;
import com.happyzombie.springinitializr.bean.Do.NftRarityDO;
import com.happyzombie.springinitializr.bean.ParasAttributesDTO;
import com.happyzombie.springinitializr.bean.ParasRarityRequestDTO;
import com.happyzombie.springinitializr.bean.ParasRarityResponseDTO;
import com.happyzombie.springinitializr.common.util.AssertUtil;
import com.happyzombie.springinitializr.common.util.CollectionUtil;
import com.happyzombie.springinitializr.common.util.CompressAndDecompressUtil;
import com.happyzombie.springinitializr.common.util.JsonUtil;
import com.happyzombie.springinitializr.dao.NftInfoMapper;
import com.happyzombie.springinitializr.dao.NftRarityMapper;
import com.happyzombie.springinitializr.feignclient.ParasClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class ParasDataCollectionService {

    @Autowired
    public ExcelCreateService excelCreateService;

    @Autowired
    public ParasClient parasClient;

    @Resource
    public NftInfoMapper nftInfoMapper;

    @Resource
    public NftRarityMapper nftRarityMapper;

    public NftInfoDO getNftInfoByTokenSeriesId(String tokenSeriesId) {
        // todo 少一层dao和dto
        final NftInfoDO nftInfoDo = new NftInfoDO();
        nftInfoDo.setTokenSeriesId(tokenSeriesId);
        return nftInfoMapper.selectOne(nftInfoDo);
    }

    public void insertNftInfo(NftInfoDO nftInfoDo) {
        nftInfoMapper.insertOne(nftInfoDo);
    }

    public NftRarityDO getNftRarityByTokenSeriesId(String tokenSeriesId) {
        // todo 少一层dao和dto
        final NftRarityDO nftRarityDo = new NftRarityDO();
        nftRarityDo.setTokenSeriesId(tokenSeriesId);
        return nftRarityMapper.selectOne(nftRarityDo);
    }

    public void insertNftRarity(NftRarityDO nftRarityDo) {
        nftRarityMapper.insertOne(nftRarityDo);
    }

    // 获取nft属性信息
    public List<ParasAttributesDTO.DataDTO.ResultsDTO.MetadataDTO.AttributesDTO> getNFTAttributes(String contractId, String tokenSeriesId) {
        final ParasAttributesDTO parasAttributesDto = getNFTInfo(contractId, tokenSeriesId);
        // todo 最好加上对状态码的判断
        AssertUtil.shouldBeTrue(parasAttributesDto != null &&
                        parasAttributesDto.getData() != null &&
                        CollectionUtil.isNotEmpty(parasAttributesDto.getData().getResults()) &&
                        parasAttributesDto.getData().getResults().get(0).getMetadata() != null &&
                        parasAttributesDto.getData().getResults().get(0).getMetadata().getAttributes() != null,
                "相应参数为空" + parasAttributesDto);
        return getNFTAttributes(parasAttributesDto);
    }

    // 获取nft详细信息
    public ParasAttributesDTO getNFTInfo(String contractId, String tokenSeriesId){
        ResponseEntity<byte[]> nftAttributes = parasClient.getNFTAttributes(contractId, tokenSeriesId);
        byte[] body = nftAttributes.getBody();
        // todo 这种不灵活可以考虑取相应头的 Content-Encoding: br 然后根据结果解码
        return JsonUtil.jsonStringToObject(CompressAndDecompressUtil.brDecompress(body), ParasAttributesDTO.class);
    }

    // 提取nft属性信息
    public List<ParasAttributesDTO.DataDTO.ResultsDTO.MetadataDTO.AttributesDTO> getNFTAttributes(ParasAttributesDTO parasAttributesDto) {
        return parasAttributesDto.getData().getResults().get(0).getMetadata().getAttributes();
    }

    // 获取nft属性稀有度
    public ParasRarityResponseDTO getNFTAttributesRarity(List<ParasAttributesDTO.DataDTO.ResultsDTO.MetadataDTO.AttributesDTO> attributes) {
        AssertUtil.shouldBeTrue(CollectionUtil.isNotEmpty(attributes), "nft属性不能为空");
        // 构建请求对象
        ParasRarityRequestDTO parasRarityRequestDto = new ParasRarityRequestDTO();
        parasRarityRequestDto.setCollectionId("asac.near");
        parasRarityRequestDto.setTtl(120);
        LinkedList<ParasRarityRequestDTO.AttributesDTO> attributesDTOS = new LinkedList<>();
        parasRarityRequestDto.setAttributes(attributesDTOS);
        attributes.forEach(attribute -> {
            ParasRarityRequestDTO.AttributesDTO attributesDto = new ParasRarityRequestDTO.AttributesDTO();
            attributesDto.setTraitType(attribute.getTraitType());
            attributesDto.setValue(attribute.getValue());
            attributesDTOS.add(attributesDto);
        });

        // 对象转换成post请求的form格式
        // Map map = JsonUtil.beanToMap(parasRarityRequestDto);
        ResponseEntity<byte[]> nftAttributesRarity = parasClient.getNFTAttributesRarity(JsonUtil.objectToString(parasRarityRequestDto));
        final String result = CompressAndDecompressUtil.brDecompress(nftAttributesRarity.getBody());
        return JsonUtil.jsonStringToObject(result, ParasRarityResponseDTO.class);
    }

}
