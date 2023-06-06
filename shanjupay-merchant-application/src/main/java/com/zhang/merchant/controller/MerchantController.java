package com.zhang.merchant.controller;

import com.alibaba.fastjson.JSON;
import com.zhang.merchant.api.MerchantService;
import com.zhang.merchant.api.dto.MerchantDTO;

import com.zhang.merchant.service.smsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : 15754
 * @version 1.0.0
 * @since : 2023/6/5 16:42
 **/


@RestController
@Api("商户平台应用的接口")
@Slf4j
public class MerchantController {

    @Reference
    MerchantService merchantService;
    @Resource
    smsService smsService;

    @GetMapping("/merchant/{id}")
    @ApiOperation("根据id查询商家")
    public MerchantDTO getMerchantById(@PathVariable("id") Long id) {
        return merchantService.queryMerchantById(id);
    }


    @GetMapping("/sms")
    @ApiOperation("获取手机验证码")
    @ApiImplicitParam(value = "手机号" ,name = "phone",required = true,dataType = "string",paramType = "query")
    public void getSMSCode(@RequestParam("mobile") String phone) {
        smsService.sendMsg(phone);
    }
}
