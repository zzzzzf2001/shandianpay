package com.zhang.merchant.api;

import com.zhang.merchant.api.dto.MerchantDTO;

/**
 * @author : 15754
 * @version 1.0.0
 * @since : 2023/6/5 16:32
 **/


public interface MerchantService {

    //根据 id查询商户
    public MerchantDTO queryMerchantById(Long id);
}