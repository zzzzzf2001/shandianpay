package com.zhang.merchant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.PhoneUtil;
import com.zhang.merchant.api.MerchantService;
import com.zhang.merchant.api.dto.MerchantDTO;
import com.zhang.merchant.convert.MerchantCovert;
import com.zhang.merchant.entity.Merchant;
import com.zhang.merchant.mapper.MerchantMapper;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author : 15754
 * @version 1.0.0
 * @since : 2023/6/5 16:39
 **/


@org.apache.dubbo.config.annotation.Service
public class MerchantServiceImpl implements MerchantService {

    @Resource
    MerchantMapper merchantMapper;

    @Override
    public MerchantDTO queryMerchantById(Long id) {
        Merchant merchant = merchantMapper.selectById(id);
        MerchantDTO merchantDTO = new MerchantDTO();
        merchantDTO.setId(merchant.getId());
        merchantDTO.setMerchantName(merchant.getMerchantName());
        //....
        return merchantDTO;
    }

    @Override
    public MerchantDTO createMerchant(MerchantDTO merchantDTO) throws BusinessException {
        //校验

        if (merchantDTO == null) {
            throw new BusinessException(CommonErrorCode.E_100103);
        }
        String mobile = merchantDTO.getMobile();
        if (StringUtils.isBlank(mobile)) {
            throw new BusinessException(CommonErrorCode.E_200230);
        }
        if (!PhoneUtil.isMatches(mobile)) {
            throw new BusinessException(CommonErrorCode.E_200224);
        }

        LambdaQueryWrapper<Merchant> queryWrapper = new LambdaQueryWrapper<>();

        if (merchantMapper.selectCount(queryWrapper.eq(Merchant::getMobile, mobile)) > 0) {
            throw new BusinessException(CommonErrorCode.E_200203);
        }


        //利用MapStrut 进行拷贝·

        Merchant merchant = MerchantCovert.INSTANCE.dto2entity(merchantDTO);

        merchant.setAuditStatus("0");

        merchantMapper.insert(merchant);

        merchantDTO.setId(merchant.getId());


        return merchantDTO;
    }

    /**
     * @param merchantId,merchantDTO
     * @return
     * @Description 注册商户服务的接口，接收账号密码手机号，为了延展性，使用MerchantDTO做接收参数
     * @author 15754
     * @Date 2023/6/8
     **/
    @Override
    public void applyMerchant(Long merchantId, MerchantDTO merchantDTO) throws BusinessException{
        if (merchantId == null || merchantDTO == null) {
            throw new BusinessException(CommonErrorCode.E_300009);
        }

        Merchant merchantInDB = merchantMapper.selectById(merchantId);
        if (Objects.isNull(merchantInDB)) {
            throw new BusinessException(CommonErrorCode.E_200227);
        }

        merchantInDB.setBusinessLicensesImg(merchantDTO.getBusinessLicensesImg());
        merchantInDB.setIdCardAfterImg(merchantDTO.getIdCardAfterImg());
        merchantInDB.setIdCardFrontImg(merchantDTO.getIdCardFrontImg());
        merchantInDB.setAuditStatus("1");

        //进行 修改
        merchantMapper.updateById(merchantInDB);

    }


}

