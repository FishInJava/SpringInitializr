package com.happyzombie.springinitializr.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ParasDataCollection {

    @Autowired
    public ExcelCreate excelCreate;

    /**
     * 观察请求，发送请求（确定引入http框架）
     * 观察数据格式
     * 整理重要数据到Excel
     */
    public void getParasData(){

    }

}
