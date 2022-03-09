package com.happyzombie.springinitializr.controller;

import com.happyzombie.springinitializr.common.bean.Result;
import com.happyzombie.springinitializr.service.ParasDataCollectionService;
import lombok.extern.slf4j.Slf4j;
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
    @RequestMapping(value = "/getParasData", method = RequestMethod.GET)
    public Result<Object> getParasData() throws Exception {
        String parasData = parasDataCollectionService.getParasData();
        log.info("getParasData + "+ new Date());
        return Result.successResult(parasData);
    }

}
