package com.happyzombie.springinitializr.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

// todo 这里能全局引入吗
@FeignClient(name = "ParasClient", url = "https://api-v2-mainnet.paras.id",configuration = ParasClientConfig.class)
public interface ParasClient {
    /**
     * 先请求到attributes信息，在通过属性信息获取稀有度信息
     * {"collection_id":"asac.near","attributes":[{"trait_type":"Antisocial Ape Club","value":"Genesis Apes"},{"trait_type":"Skin","value":"Blue"},{"trait_type":"Mouth","value":"Blunt"},{"trait_type":"Eyes","value":"Eyepatch"},{"trait_type":"Head","value":"Sombrero"},{"trait_type":"Neck","value":"None"},{"trait_type":"Earrings","value":"Silver Stud"}],"ttl":120}
     */

    /**
     * "/history/list?chain={chain}&page_count=10&start_time=0&token_id=&user_addr={userAddr}")
     * token_id=asac.near
     */
    @RequestMapping(method = RequestMethod.GET, value = "/token-series?contract_id={contractId}&token_series_id={tokenSeriesId}")
    ResponseEntity<byte[]> getNFTAttributes(@PathVariable(value="contractId") String contractId,@PathVariable(value="tokenSeriesId") String tokenSeriesId);

    // todo 这里要走代理
    @RequestMapping(method = RequestMethod.POST, value = "/rarity")
    ResponseEntity<byte[]> getNFTAttributesRarity();

}
