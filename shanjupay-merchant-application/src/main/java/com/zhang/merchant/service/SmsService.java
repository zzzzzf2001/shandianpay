package com.zhang.merchant.service;

import com.shanjupay.common.domain.BusinessException;

/**
 * @author : 15754
 * @version 1.0.0
 * @since : 2023/6/6 14:45
 **/


public interface SmsService {
    /*
     * @param phone
     * @return  验证码对应的key
     * @Description 发送验证码
     * @author 15754
     * @Date 2023/6/6
     */
    String sendMsg(String phone) throws BusinessException;
    void  CheckVerify(String code,String key)  throws BusinessException;
}
