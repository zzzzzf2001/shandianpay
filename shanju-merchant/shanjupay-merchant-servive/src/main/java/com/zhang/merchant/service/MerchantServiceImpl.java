package com.zhang.merchant.service;

import com.zhang.merchant.api.MerchantService;
import com.zhang.merchant.api.dto.MerchantDTO;
import com.zhang.merchant.entity.Merchant;
import com.zhang.merchant.mapper.MerchantMapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author : 15754
 * @version 1.0.0
 * @since : 2023/6/5 16:39
 **/


@org.apache.dubbo.config.annotation.Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
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
}

