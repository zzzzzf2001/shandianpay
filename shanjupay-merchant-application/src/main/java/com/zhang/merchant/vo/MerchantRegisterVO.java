package com.zhang.merchant.vo;

/**
 * @author : 15754
 * @version 1.0.0
 * @since : 2023/6/6 16:26
 **/


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;


/*
 * @Description  用于接收前端请求的数据
 * @author 15754
 * @Date 2023/6/6
 */
@ApiModel(value = "MerchantRegisterVO", description = "商户注册信息")
@Data
public class MerchantRegisterVO implements Serializable{
    @ApiModelProperty("商户手机号")
    private String mobile;
    @ApiModelProperty("商户用户名")
    private String username;
    @ApiModelProperty("商户密码")
    private String password;
    @ApiModelProperty("验证码的key")
    private String verifiykey;
    @ApiModelProperty("验证码")
    private String verifiyCode;
}
