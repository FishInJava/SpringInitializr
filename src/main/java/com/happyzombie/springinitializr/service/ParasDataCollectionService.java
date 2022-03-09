package com.happyzombie.springinitializr.service;

import com.happyzombie.springinitializr.feignclient.ParasClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class ParasDataCollectionService {

    @Autowired
    public ExcelCreate excelCreate;

    @Autowired
    public ParasClient parasClient;

    /**
     * 观察请求，发送请求（确定引入http框架）
     * 观察数据格式
     * 整理重要数据到Excel
     */
    public void getParasData(){
        ResponseEntity<byte[]> nftAttributes = parasClient.getNFTAttributes("asac.near","2694");
        byte[] body = nftAttributes.getBody();
        String s = new String(CompressAndDecompressUtil.gzipDecompress(body), StandardCharsets.UTF_8);
    }

}
