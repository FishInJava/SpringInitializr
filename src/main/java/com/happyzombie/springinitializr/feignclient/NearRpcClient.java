//package com.happyzombie.springinitializr.feignclient;
//
//import com.happyzombie.springinitializr.service.NearRcpClientConfig;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//
//// todo 这里能全局引入吗
//@FeignClient(name = "NearRpcClient", url = "https://rpc.mainnet.near.org", configuration = NearRcpClientConfig.class)
//// 这里我想增加一个代理对象，或者写一个切面
//public interface NearRpcClient {
//    /**
//     * 获取nft的属性稀有度
//     *
//     * @return
//     */
//    @RequestMapping(method = RequestMethod.POST, value = "")
//    ResponseEntity<byte[]> getNFTAttributesRarity(String jsonBody);
//
//}
