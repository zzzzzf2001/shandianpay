package com.zhang.merchant.api;

import com.shanjupay.common.domain.BusinessException;
import com.zhang.merchant.api.dto.AppDTO;

import java.util.List;

/**
 * @author : 15754
 * @version 1.0.0
 * @Description  商户应用接口
 * @since : 2023/6/11 14:29
 **/




public interface AppService  {

    AppDTO createApp(Long MerchantId,AppDTO appDTO) throws BusinessException;
    List<AppDTO> queryAppByMerchantId( )throws BusinessException;

    AppDTO queryAppByAppId(String appId) throws BusinessException;


}
