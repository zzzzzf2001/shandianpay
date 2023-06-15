package com.shanjupay.transaction.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shanjupay.common.cache.Cache;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.RedisUtil;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@org.apache.dubbo.config.annotation.Service
@Slf4j
public class PayChannelServiceImpl implements PayChannelService {
    @Autowired
    private PlatformChannelMapper platformChannelMapper;

    @Autowired
    private AppPlatformChannelMapper appPlatformChannelMapper;

    @Autowired
    PayChannelParamMapper payChannelParamMapper;

    @Autowired
    private Cache cache;

    @Override
    public List<PlatformChannelDTO> queryPlatformChannel() {
        List<PlatformChannel> platformChannels = platformChannelMapper.selectList(null);
        List<PlatformChannelDTO> platformChannelDTOS = PlatformChannelConvert.INSTANCE.listentity2listdto(platformChannels);
        return platformChannelDTOS;
    }

    @Override
    public void bindPlatFormChannelForApp(String appId, String platfromChannelCodes) throws BusinessException {
        AppPlatformChannel appPlatformChannel = appPlatformChannelMapper.selectOne(new LambdaQueryWrapper<AppPlatformChannel>().eq(AppPlatformChannel::getAppId, appId).eq(AppPlatformChannel::getPlatformChannel, platfromChannelCodes));
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
        AppPlatformChannel appPlatformChannel = appPlatformChannelMapper.selectOne(new LambdaQueryWrapper<AppPlatformChannel>().eq(AppPlatformChannel::getAppId, appId).eq(AppPlatformChannel::getPlatformChannel, platfromChannelCodes));
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
        //先删除缓存
        String appId = payChannelParamDTO.getAppId();
        String platformChannelCode = payChannelParamDTO.getPlatformChannelCode();
        String redisKey = RedisUtil.keyBuilder(appId, platformChannelCode);
        updateCache(appId, platformChannelCode);

        //根据应用、服务类型、支付渠道查询一条记录
        //首先根据应用和支付服务类型查询应用与服务类型的绑定id
        Long AppPlatformChannelId = selectIdByAppPlatformChannel(payChannelParamDTO.getAppId(), payChannelParamDTO.getPlatformChannelCode());

        //根据应用与服务类型绑定的id和支付渠道查询PayChannelParam的一条记录
        PayChannelParam payChannelParam = payChannelParamMapper.selectOne(new LambdaQueryWrapper<PayChannelParam>().eq(PayChannelParam::getPayChannel, payChannelParamDTO.getPayChannel()).eq(PayChannelParam::getAppPlatformChannelId, AppPlatformChannelId));

        PayChannelParam payChannelParam1 = PayChannelParamConvert.INSTANCE.dto2entity(payChannelParamDTO);

        //如果不存在则添加配置
        if (payChannelParam == null) {
            payChannelParam1.setId(null);
            payChannelParam1.setAppPlatformChannelId(AppPlatformChannelId);
            payChannelParamMapper.insert(payChannelParam1);
            updateCache(appId, platformChannelCode);
            cache.set(redisKey, JSON.toJSON(payChannelParam1).toString());
        }
        //如果存在配置则更新
        else {
            payChannelParam.setParam(payChannelParam1.getParam());
            payChannelParam.setChannelName(payChannelParam1.getChannelName());
            payChannelParamMapper.updateById(payChannelParam);
            updateCache(appId, platformChannelCode);
            cache.set(redisKey, JSON.toJSON(payChannelParam).toString());
        }

        log.info("更新缓存");
    }

    private void updateCache(String appId, String platformChannel) {

        String redisKey = RedisUtil.keyBuilder(appId, platformChannel);
        //2.查询redis,检查key是否存在
        Boolean exists = cache.exists(redisKey);
        if (exists) {//存在，则清除
            log.info("命中缓存");
            //删除原有缓存
            cache.del(redisKey);
        }
    }

    /**
     * 查询支付渠道参数
     *
     * @param appId           应用id
     * @param platformChannel 服务类型代码
     * @return
     */
    @Override
    public List<PayChannelParamDTO> queryPayChannelParamByAppAndPlatform(String appId, String platformChannel) {
        String redisKey = RedisUtil.keyBuilder(appId, platformChannel);
//是否有缓存
        Boolean exists = cache.exists(redisKey);
        if (exists) {
            log.info("命中缓存");

//从redis获取key对应的value
            String value = cache.get(redisKey);
//将value转成对象
            List<PayChannelParamDTO> paramDTOS = JSONObject.parseArray(value, PayChannelParamDTO.class);
            return paramDTOS;
        }


//查出应用id和服务类型代码在app_platform_channel主键
        Long appPlatformChannelId = selectIdByAppPlatformChannel(appId, platformChannel);
//根据appPlatformChannelId从pay_channel_param查询所有支付参数
        List<PayChannelParam> payChannelParams = payChannelParamMapper.selectList(new LambdaQueryWrapper<PayChannelParam>().eq(PayChannelParam::getAppPlatformChannelId, appPlatformChannelId));
        List<PayChannelParamDTO> paramDTOS = PayChannelParamConvert.INSTANCE.listentity2listdto(payChannelParams);
        updateCache(appId, platformChannel);

        if (paramDTOS != null) {
//存入缓存
            cache.set(redisKey, JSON.toJSON(paramDTOS).toString());
        }

        return paramDTOS;
    }

    private Long selectIdByAppPlatformChannel(String appId, String platformChannel) {
        AppPlatformChannel appPlatformChannel = appPlatformChannelMapper.selectOne(new LambdaQueryWrapper<AppPlatformChannel>().eq(AppPlatformChannel::getAppId, appId).eq(AppPlatformChannel::getPlatformChannel, platformChannel));
        return appPlatformChannel.getId();
    }

    @Override
    public PayChannelParamDTO queryParamByAppPlatformAndPayChannel(String appId, String platformChannel, String payChannel) throws BusinessException {
        List<PayChannelParamDTO> payChannelParamDTOS = queryPayChannelParamByAppAndPlatform(appId, platformChannel);
        for (PayChannelParamDTO payChannelParam : payChannelParamDTOS) {
            if (payChannelParam.getPayChannel().equals(payChannel)) {
                return payChannelParam;
            }
        }
        return null;
    }


}
