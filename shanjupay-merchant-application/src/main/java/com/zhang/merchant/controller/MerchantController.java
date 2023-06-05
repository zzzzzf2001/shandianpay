package com.zhang.merchant.controller;

import com.zhang.merchant.api.MerchantService;
import com.zhang.merchant.api.dto.MerchantDTO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : 15754
 * @version 1.0.0
 * @since : 2023/6/5 16:42
 **/


@RestController
@Api("商户平台应用的接口")
public class MerchantController {

    @Reference
    MerchantService merchantService;

    @GetMapping("/merchant/{id}")
    @ApiOperation("根据id查询商家")
    public MerchantDTO getMerchantById(@PathVariable("id") Long id){
      return   merchantService.queryMerchantById(id);
    }


}
