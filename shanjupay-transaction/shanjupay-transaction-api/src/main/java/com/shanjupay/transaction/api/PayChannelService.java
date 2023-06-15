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

    /**
     * 获取指定应用指定服务类型下所包含的原始支付渠道参数列表
     * @param appId 应用id
     * @param platformChannel 服务类型
     * @return
     */
    List<PayChannelParamDTO> queryPayChannelParamByAppAndPlatform(String appId, String
            platformChannel)
            throws BusinessException;
    /**
     * 获取指定应用指定服务类型下所包含的某个原始支付参数
     * @param appId
     * @param platformChannel
     * @param payChannel
     * @return
     * @throws BusinessException
     */
    PayChannelParamDTO queryParamByAppPlatformAndPayChannel(String appId, String platformChannel,
                                                            String payChannel) throws BusinessException;

}
