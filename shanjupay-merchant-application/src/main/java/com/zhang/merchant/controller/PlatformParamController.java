package com.zhang.merchant.controller;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.SecurityUtil;
import com.shanjupay.transaction.api.PayChannelService;
import com.shanjupay.transaction.api.dto.PayChannelParamDTO;
import com.shanjupay.transaction.api.dto.PlatformChannelDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : 15754
 * @version 1.0.0
 * @since : 2023/6/11 18:47
 **/


@RestController
@Api("商户平台-渠道和支付参数相关")
@Slf4j

public class PlatformParamController {
    @Reference
    PayChannelService payChannelService;

    @ApiOperation("获取平台服务类型")
    @GetMapping("/my/platform-channels")
    public List<PlatformChannelDTO> platformChannelDTOList(){
       return payChannelService.queryPlatformChannel();
    }

    @ApiOperation("为应用绑定支付类型")
    @PostMapping("/my/apps/{appId}/platformChannels")
    public void bindPlatformForApp(@PathVariable("appId") String AppId,
                                   @RequestParam("platFormChannelCodes") String platFormChannelCodes){
        payChannelService.bindPlatFormChannelForApp(AppId,platFormChannelCodes);
    }
    @ApiOperation("查询应用绑定支付类型")
    @GetMapping("/my/apps/platformChannels")
    public void queryBindPlatformForApp(@RequestParam("appId") String AppId,
                                   @RequestParam("platFormChannelCodes") String platFormChannelCodes){
        payChannelService.queryAppBindPlatformChannel(AppId,platFormChannelCodes);
    }

    @ApiOperation("商户配置支付渠道参数")
    @PostMapping("/my/pay-channel-params")
    void createPayChannelParam(@RequestBody PayChannelParamDTO payChannelParamDTO){
        if (payChannelParamDTO==null||payChannelParamDTO.getChannelName()==null) {
            throw new BusinessException(CommonErrorCode.E_300009);
        }
        payChannelParamDTO.setMerchantId( SecurityUtil.getMerchantId());
        log.info(payChannelParamDTO.toString());
        payChannelService.savePayChannelParam(payChannelParamDTO);
    }







}
