package com.zhang.merchant.controller;

import com.zhang.merchant.api.MerchantService;

import com.zhang.merchant.api.dto.MerchantDTO;
import com.zhang.merchant.service.SmsService;
import com.zhang.merchant.vo.MerchantRegisterVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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
    SmsService smsService;

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


    @ApiOperation("商户注册")
    @PostMapping("/merchant/register")
    @ApiImplicitParam(value = "商户注册信息" ,name = "merchantRegisterVO",required = true,paramType = "body")
    public MerchantRegisterVO registerMerchant(@RequestBody MerchantRegisterVO merchantRegisterVO){
        //校验验证码
        smsService.CheckVerify(merchantRegisterVO.getVerifiyCode(),merchantRegisterVO.getVerifiykey());

        MerchantDTO merchantDTO=new MerchantDTO();
        BeanUtils.copyProperties(merchantRegisterVO,merchantDTO);
        //调用dubbo服务接口
        merchantService.createMerchant(merchantDTO);
        return merchantRegisterVO;
    }

}
