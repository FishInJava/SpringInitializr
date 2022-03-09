package com.happyzombie.springinitializr.service;

import com.happyzombie.springinitializr.bean.ParasAttributesDto;
import com.happyzombie.springinitializr.bean.ParasRarityRequestDto;
import com.happyzombie.springinitializr.common.util.AssertUtil;
import com.happyzombie.springinitializr.common.util.CollectionUtil;
import com.happyzombie.springinitializr.common.util.CompressAndDecompressUtil;
import com.happyzombie.springinitializr.common.util.JsonUtil;
import com.happyzombie.springinitializr.feignclient.ParasClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class ParasDataCollectionService {

    @Autowired
    public ExcelCreateService excelCreateService;

    @Autowired
    public ParasClient parasClient;

    /**
     * 观察请求，发送请求（确定引入http框架）
     * 观察数据格式
     * 整理重要数据到Excel
     */
    // 获取nft属性信息
    public List<ParasAttributesDto.DataDTO.ResultsDTO.MetadataDTO.AttributesDTO> getNFTAttributes(String contractId, String tokenSeriesId) {
        ResponseEntity<byte[]> nftAttributes = parasClient.getNFTAttributes(contractId, tokenSeriesId);
        byte[] body = nftAttributes.getBody();
        // todo 这种不灵活可以考虑取相应头的 Content-Encoding: br 然后根据结果解码
        ParasAttributesDto parasAttributesDto = JsonUtil.jsonStringToObject(CompressAndDecompressUtil.brDecompress(body), ParasAttributesDto.class);
        AssertUtil.shouldBeTrue(parasAttributesDto != null &&
                        parasAttributesDto.getData() != null &&
                        CollectionUtil.isNotEmpty(parasAttributesDto.getData().getResults()) &&
                        parasAttributesDto.getData().getResults().get(0).getMetadata() != null &&
                        parasAttributesDto.getData().getResults().get(0).getMetadata().getAttributes() != null,
                "相应参数为空" + parasAttributesDto);
        List<ParasAttributesDto.DataDTO.ResultsDTO.MetadataDTO.AttributesDTO> attributes = parasAttributesDto.getData().getResults().get(0).getMetadata().getAttributes();
        return attributes;
    }

    // 获取nft属性稀有度
    public ParasRarityRequestDto getNFTAttributesRarity(List<ParasAttributesDto.DataDTO.ResultsDTO.MetadataDTO.AttributesDTO> attributes) {
        AssertUtil.shouldBeTrue(CollectionUtil.isNotEmpty(attributes), "nft属性不能为空");
        // 构建请求对象
        ParasRarityRequestDto parasRarityRequestDto = new ParasRarityRequestDto();
        parasRarityRequestDto.setCollectionId("asac.near");
        parasRarityRequestDto.setTtl(120);
        LinkedList<ParasRarityRequestDto.AttributesDTO> attributesDTOS = new LinkedList<>();
        parasRarityRequestDto.setAttributes(attributesDTOS);
        attributes.forEach(attribute -> {
            ParasRarityRequestDto.AttributesDTO attributesDto = new ParasRarityRequestDto.AttributesDTO();
            attributesDto.setTraitType(attribute.getTraitType());
            attributesDto.setValue(attribute.getValue());
            attributesDTOS.add(attributesDto);
        });

        // 对象转换成post请求的form格式
        // Map map = JsonUtil.beanToMap(parasRarityRequestDto);
        ResponseEntity<byte[]> nftAttributesRarity = parasClient.getNFTAttributesRarity(JsonUtil.objectToString(parasRarityRequestDto));
        final String result = CompressAndDecompressUtil.brDecompress(nftAttributesRarity.getBody());
        return JsonUtil.jsonStringToObject(result, ParasRarityRequestDto.class);
    }

}
