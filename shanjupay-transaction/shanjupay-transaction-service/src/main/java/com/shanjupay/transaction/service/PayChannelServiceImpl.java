package com.shanjupay.transaction.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.SecurityUtil;
import com.shanjupay.transaction.api.PayChannelService;
import com.shanjupay.transaction.api.dto.PayChannelParamDTO;
import com.shanjupay.transaction.api.dto.PlatformChannelDTO;
import com.shanjupay.transaction.convert.PayChannelParamConvert;
import com.shanjupay.transaction.convert.PlatformChannelConvert;
import com.shanjupay.transaction.entity.AppPlatformChannel;
import com.shanjupay.transaction.entity.PayChannelParam;
import com.shanjupay.transaction.entity.PlatformChannel;
import com.shanjupay.transaction.mapper.AppPlatformChannelMapper;
import com.shanjupay.transaction.mapper.PayChannelParamMapper;
import com.shanjupay.transaction.mapper.PlatformChannelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;

@org.apache.dubbo.config.annotation.Service
public class PayChannelServiceImpl implements PayChannelService {
    @Autowired
    private PlatformChannelMapper platformChannelMapper;

    @Autowired
    private AppPlatformChannelMapper appPlatformChannelMapper;

    @Autowired
    PayChannelParamMapper payChannelParamMapper;

    @Override
    public List<PlatformChannelDTO> queryPlatformChannel() {
        List<PlatformChannel> platformChannels = platformChannelMapper.selectList(null);
        List<PlatformChannelDTO> platformChannelDTOS =
                PlatformChannelConvert.INSTANCE.listentity2listdto(platformChannels);
        return platformChannelDTOS;
    }

    @Override
    public void bindPlatFormChannelForApp(String appId, String platfromChannelCodes) throws BusinessException {
        AppPlatformChannel appPlatformChannel = appPlatformChannelMapper.selectOne(new LambdaQueryWrapper<AppPlatformChannel>()
                .eq(AppPlatformChannel::getAppId, appId)
                .eq(AppPlatformChannel::getPlatformChannel, platfromChannelCodes)
        );
        if (!Objects.isNull(appPlatformChannel)) {
            throw new BusinessException(CommonErrorCode.E_200238);
        }
        AppPlatformChannel entity = new AppPlatformChannel();

        entity.setAppId(appId);
        entity.setPlatformChannel(platfromChannelCodes);

        appPlatformChannelMapper.insert(entity);

    }

    @Override
    public Integer queryAppBindPlatformChannel(String appId, String platfromChannelCodes) {
        AppPlatformChannel appPlatformChannel = appPlatformChannelMapper.selectOne(new LambdaQueryWrapper<AppPlatformChannel>()
                .eq(AppPlatformChannel::getAppId, appId)
                .eq(AppPlatformChannel::getPlatformChannel, platfromChannelCodes)
        );
        if (appPlatformChannel != null) {
            return 1;
        }
        return 0;
    }

    /**
     * @param payChannelParamDTO 包括：商户id、应用id、服务类型code、配置名称、配置参数
     * @return void
     * @Description 支付渠道参数配置
     * @author 15754
     * @Date 2023/6/12
     **/

    @Override
    public void savePayChannelParam(PayChannelParamDTO payChannelParamDTO) throws BusinessException {
        //根据应用、服务类型、支付渠道查询一条记录
        //首先根据应用和支付服务类型查询应用与服务类型的绑定id
        AppPlatformChannel appPlatformChannel = appPlatformChannelMapper.selectOne(new LambdaQueryWrapper<AppPlatformChannel>()
                .eq(AppPlatformChannel::getAppId, payChannelParamDTO.getAppId())
                .eq(AppPlatformChannel::getPlatformChannel, payChannelParamDTO.getPlatformChannelCode())
        );

        //根据应用与服务类型绑定的id和支付渠道查询PayChannelParam的一条记录
        PayChannelParam payChannelParam = payChannelParamMapper.selectOne(new LambdaQueryWrapper<PayChannelParam>()
                .eq(PayChannelParam::getPayChannel, payChannelParamDTO.getPayChannel())
                .eq(PayChannelParam::getAppPlatformChannelId, appPlatformChannel.getId())
        );

        PayChannelParam payChannelParam1 = PayChannelParamConvert.INSTANCE.dto2entity(payChannelParamDTO);

        //如果不存在则添加配置
        if (payChannelParam == null) {
            payChannelParam1.setId(null);
            payChannelParam1.setAppPlatformChannelId(appPlatformChannel.getId());
            payChannelParamMapper.insert(payChannelParam1);
            return;
        }
        //如果存在配置则更新
        else {
            payChannelParam.setParam(payChannelParam1.getParam());
            payChannelParam.setChannelName(payChannelParam1.getChannelName());
            payChannelParamMapper.updateById(payChannelParam);
        }
    }


}
