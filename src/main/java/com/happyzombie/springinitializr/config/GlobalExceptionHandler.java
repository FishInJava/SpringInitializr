package com.happyzombie.springinitializr.config;

import com.happyzombie.springinitializr.common.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * @author hbz
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler
    public Object handlerAllException(HttpServletResponse httpServletResponse, Exception exception){
        log.error("全局异常处理",exception);
        return Result.errorResult(exception);
    }

}
