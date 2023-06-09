package com.zhang.merchant.controller;

import com.shanjupay.common.util.SecurityUtil;
import com.zhang.merchant.api.MerchantService;

import com.zhang.merchant.api.dto.MerchantDTO;
import com.zhang.merchant.convert.MerchantDetailCovert;
import com.zhang.merchant.convert.MerchantRegisterConvert;
import com.zhang.merchant.service.SmsService;
import com.zhang.merchant.vo.MerchantDetailVO;
import com.zhang.merchant.vo.MerchantRegisterVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    SmsService smsService;

    @GetMapping("/merchant/{id}")
    @ApiOperation("根据id查询商家")
    public MerchantDTO getMerchantById(@PathVariable("id") Long id) {
        return merchantService.queryMerchantById(id);
    }


    @GetMapping("/sms")
    @ApiOperation("获取手机验证码")
    public void getSMSCode(@RequestParam("phone") String phone) {
        smsService.sendMsg(phone);
    }


    @ApiOperation("商户注册")
    @PostMapping("/merchants/register")
    @ApiImplicitParam(value = "商户注册信息" ,name = "merchantRegisterVO",required = true,paramType = "body")
    public MerchantRegisterVO registerMerchant(@RequestBody MerchantRegisterVO merchantRegisterVO){
        //校验验证码
        smsService.checkVerifiyCode(merchantRegisterVO.getVerifiyCode(),merchantRegisterVO.getVerifiykey());

        MerchantDTO merchantDTO = MerchantRegisterConvert.INSTANCE.MRVO2DTO(merchantRegisterVO);

        //调用dubbo服务接口
        merchantService.createMerchant(merchantDTO);

        return merchantRegisterVO;
    }



    @ApiOperation("资质申请")
    @PostMapping("/my/merchants/save")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "merchantInfo", value = "商户认证资料", required = true,
                    dataType = "MerchantDetailVO", paramType = "body")
    })

    public void  saveMerchant(@RequestBody MerchantDetailVO merchantDetailVO){

        Long merchantId = SecurityUtil.getMerchantId();

        MerchantDTO merchantDTO = MerchantDetailCovert.INSTANCE.MerchantDVO2DTO(merchantDetailVO);

        merchantService.applyMerchant(merchantId,merchantDTO);



    }



}
