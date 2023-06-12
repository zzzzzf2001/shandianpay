package com.shanjupay.transaction.api;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.transaction.api.dto.PayChannelParamDTO;
import com.shanjupay.transaction.api.dto.PlatformChannelDTO;

import java.util.List;

/**
 * @author : 15754
 * @version 1.0.0
 * @since : 2023/6/11 21:36
 **/


public interface PayChannelService {

    public List<PlatformChannelDTO> queryPlatformChannel();
    void bindPlatFormChannelForApp(String appId,String platfromChannelCodes) throws BusinessException;

    Integer queryAppBindPlatformChannel(String appId,String platfromChannelCodes);

    void savePayChannelParam(PayChannelParamDTO payChannelParamDTO) throws  BusinessException;

}
