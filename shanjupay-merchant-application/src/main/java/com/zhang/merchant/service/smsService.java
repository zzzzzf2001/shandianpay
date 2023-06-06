package com.zhang.merchant.service;

/**
 * @author : 15754
 * @version 1.0.0
 * @since : 2023/6/6 14:45
 **/


public interface smsService {
    /*
     * @param phone
     * @return  验证码对应的key
     * @Description 发送验证码
     * @author 15754
     * @Date 2023/6/6
     */
    String sendMsg(String phone);
}
