package com.zhang.merchant.api;

import com.shanjupay.common.domain.BusinessException;
import com.zhang.merchant.api.dto.MerchantDTO;
import com.zhang.merchant.api.dto.StaffDTO;
import com.zhang.merchant.api.dto.StoreDTO;

/**
 * @author : 15754
 * @version 1.0.0
 * @since : 2023/6/5 16:32
 **/


public interface MerchantService  {

    //根据 id查询商户
    public MerchantDTO queryMerchantById(Long id);


/*
     * @param merchantDTO
     * @return MerchantDTO
     * @Description  注册服务接口，接收账号、密码、手机号，为了可扩展性，使用merhcantDTO接收数据
     * @author 15754
     * @Date 2023/6/6
     */
     MerchantDTO createMerchant(MerchantDTO merchantDTO) throws BusinessException;

    StoreDTO createStore(StoreDTO storeDTO) throws BusinessException;

    StaffDTO createStaff(StaffDTO staffDTO) throws BusinessException;

    void applyMerchant(Long merchantId, MerchantDTO merchantDTO) throws BusinessException;

    void bindStaffToStore(Long storeId, Long staffId) throws BusinessException;







}

