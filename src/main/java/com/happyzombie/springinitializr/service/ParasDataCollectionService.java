package com.happyzombie.springinitializr.service;

import com.happyzombie.springinitializr.common.util.CompressAndDecompressUtil;
import com.happyzombie.springinitializr.feignclient.ParasClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
    public String getParasData(){
        ResponseEntity<byte[]> nftAttributes = parasClient.getNFTAttributes("asac.near","2694");
        byte[] body = nftAttributes.getBody();
        // todo 这种不灵活可以考虑取相应头的 Content-Encoding: br 然后根据结果解码
        return CompressAndDecompressUtil.brDecompress(body);
    }

}
