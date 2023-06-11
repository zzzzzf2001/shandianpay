package com.zhang.merchant.common.intercept;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.domain.ErrorCode;
import com.shanjupay.common.domain.RestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : 15754
 * @version 1.0.0
 * @since : 2023/6/7 18:25
 * 全局异常处理器
 **/


@ControllerAdvice
public class GlobalExceptionHandler {
    //捕获异常处理
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestResponse processException(HttpServletRequest request, HttpServletResponse response, Exception e) {

        //解析异常信息
        //如果事系统自定义异常，直接取出errorCode和errorMessage
        if (e instanceof BusinessException) {
            //解析异常信息
            BusinessException exception = (BusinessException) e;
            ErrorCode errorCode = exception.getErrorCode();
            //错误代码
            int code = errorCode.getCode();
            //错误信息
            String desc = errorCode.getDesc();
            //返回错误信息
            return new RestResponse(code,desc);

        }
            //不是系统自定义的异常，直接返回未知错误
        return new RestResponse(CommonErrorCode.UNKOWN.getCode(),CommonErrorCode.UNKOWN.getDesc());
    }


}
