package com.zhang.merchant.controller;

import com.shanjupay.common.util.SecurityUtil;
import com.zhang.merchant.api.AppService;
import com.zhang.merchant.api.dto.AppDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : 15754
 * @version 1.0.0
 * @since : 2023/6/11 16:43
 **/

 @RestController
 @Api(value = "商户平台-应用管理",tags = "商户平台-应用相关")
public class AppController {
    @Reference
    AppService appService;

     @ApiOperation("商户创建应用")
     @PostMapping("/my/apps")
    public AppDTO createApp(@RequestBody   AppDTO appDTO){
         Long merchantId = SecurityUtil.getMerchantId();
          return appService.createApp(merchantId,appDTO);
     }
    @ApiOperation("根据应用Id查询应用")
    @GetMapping("/my/apps/{appId}")
    public AppDTO getAppsByAppId(@PathVariable("appId") String appId){
         return appService.queryAppByAppId(appId);
    }

    @ApiOperation("根据商户Id查询应用")
    @GetMapping("/my/apps/getMyApps")
    public List<AppDTO> getAppsBymerchantId(){
        return appService.queryAppByMerchantId();
    }



}
