package com.happyzombie.springinitializr.controller;

import com.happyzombie.springinitializr.service.ParasDataCollectionService;
import lombok.extern.slf4j.Slf4j;
import org.hbserver.bean.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@Slf4j
@RequestMapping("/parasController")
public class ParasController {

    @Autowired
    ParasDataCollectionService parasDataCollectionService;

    @CrossOrigin
    @RequestMapping(value = "/announcement", method = RequestMethod.GET)
    public Result<Object> announcement() throws Exception {
        // 请求公告信息
        String parasData = parasDataCollectionService.getParasData();
        log.info("币安/announcement + "+ new Date());
        return Result.successResult(announcement);
    }

}
