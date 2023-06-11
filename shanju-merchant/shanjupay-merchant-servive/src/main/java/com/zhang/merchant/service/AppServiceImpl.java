package com.zhang.merchant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.SecurityUtil;
import com.zhang.merchant.api.AppService;
import com.zhang.merchant.api.dto.AppDTO;
import com.zhang.merchant.convert.AppCovert;
import com.zhang.merchant.entity.App;
import com.zhang.merchant.entity.Merchant;
import com.zhang.merchant.mapper.AppMapper;
import com.zhang.merchant.mapper.MerchantMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

/**
 * @author : 15754
 * @version 1.0.0
 * @since : 2023/6/11 14:32
 **/


@Service
public class AppServiceImpl implements AppService {

    @Resource
    MerchantMapper merchantMapper;

    @Resource
    AppMapper appMapper;

    /**
     * @param 商户id merchantId,
     * @param 应用信息 appDTO
     * @return 创建成功的应用信息
     * @Description 创建应用
     * @author 15754
     * @Date 2023/6/11
     **/


    @Override
    public AppDTO createApp(Long MerchantId, AppDTO appDTO) throws BusinessException {
        //参数合法校验
        if (MerchantId == null || appDTO == null || StringUtils.isBlank(appDTO.getAppName())) {
            throw new BusinessException(CommonErrorCode.E_300009);
        }
//        查询商户信息状态
        Merchant merchantInDB = merchantMapper.selectById(MerchantId);
        if (merchantInDB == null) {
            throw new BusinessException(CommonErrorCode.E_200227);
        }
        //商户是否通过资质审核
        String auditStatus = merchantInDB.getAuditStatus();
        if (Integer.parseInt(auditStatus) != 2) {
            throw new BusinessException(CommonErrorCode.E_200236);
        }
        //校验应用名称是否唯一
        String appName = appDTO.getAppName();
        if (hasAppName(appName)) {
            throw new BusinessException(CommonErrorCode.E_200237);
        }


        //生成应用ID
        String appId = UUID.randomUUID().toString();

        App app = AppCovert.Instance.dto2entity(appDTO);

        app.setAppId(appId);
        app.setMerchantId(MerchantId);
        appMapper.insert(app);

        return AppCovert.Instance.entity2dto(app);
    }

    /**
     * @return AppDto
     * @Description 根据商户Id查询应用
     * @author 15754
     * @Date 2023/6/11
     **/
    @Override
    public List<AppDTO> queryAppByMerchantId() throws BusinessException {
        Long merchantId = SecurityUtil.getMerchantId();
        List<App> apps = appMapper.selectList(new LambdaQueryWrapper<App>().eq(App::getMerchantId,merchantId));

        return AppCovert.Instance.listEntity2DTO(apps);
    }

    /**
     * @param appId
     * @return list<AppDTO>
     * @Description 根据AppId查询应用信息
     * @author 15754
     * @Date 2023/6/11
     **/
    @Override
    public AppDTO queryAppByAppId(String appId) throws BusinessException {
        App apps = appMapper.selectOne(new LambdaQueryWrapper<App>()
                                            .eq(App::getAppId, appId));
      AppDTO appDTO = AppCovert.Instance.entity2dto(apps);
        return appDTO;
    }

    private boolean hasAppName(String AppName) {
        App appInDb = appMapper.selectOne(new LambdaQueryWrapper<App>()
                .eq(App::getAppName, AppName));
        return !(appInDb == null);
    }
}
