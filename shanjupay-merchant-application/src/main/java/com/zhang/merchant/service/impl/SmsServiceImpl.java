package com.zhang.merchant.service.impl;

import com.alibaba.fastjson.JSON;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.zhang.merchant.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : 15754
 * @version 1.0.0
 * @since : 2023/6/6 14:47
 **/


@Service
@Slf4j
public class SmsServiceImpl implements SmsService {

    @Value("${sms.url}")
    String prefix_url;
    @Value("${sms.effectiveTime}")
    String effectiveTime;

    @Resource
    RestTemplate restTemplate;

    @Override
    public String sendMsg(String phone) {
        String sms_url=prefix_url+"/generate?name=sms&effectiveTime="+effectiveTime;
        //请求体定义
        Map<String,Object> body=new HashMap<>();
        body.put("mobile",phone);
        //请求头
        HttpHeaders httpHeaders = new HttpHeaders();
        //指定ContentType：application/json
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        //接收信息传入 header和body
        HttpEntity entity = new HttpEntity(body,httpHeaders);

        Map bodyMap=null;
        try {
            //发起请求
            ResponseEntity<Map> exchange = restTemplate.exchange(sms_url, HttpMethod.POST, entity, Map.class);
            log.info("请求验证码服务，得到相应:{}"+ JSON.toJSONString(exchange));

             bodyMap = exchange.getBody();
        }
        catch (RestClientException e){
            e.printStackTrace();
            throw new RuntimeException("验证码发送失败");
        }

        if (bodyMap==null||bodyMap.get("result")==null){
            throw new RuntimeException("发送验证码失败");
        }


        Map result = (Map)bodyMap.get("result");

        String code =(String) result.get("key");
        log.info("得到验证码对应的key:{}",code);
        return null;
    }

    @Override
    public void CheckVerify(String code, String key)  throws BusinessException{
        //获取验证码的URL
        String URL=prefix_url+"/verify?name=sms&verificationCode="+code+"&verificationKey="+key;

        ResponseEntity<Map>     exchange =null;
        Map<String,Object> body=null;

        try{

            exchange = restTemplate.exchange(URL, HttpMethod.POST, HttpEntity.EMPTY, Map.class);
                 body = exchange.getBody();
        }
        catch(Exception e){
                e.printStackTrace();
            throw new BusinessException(CommonErrorCode.E_100102);
        }
        log.info("请求验证码服务，得到相应:{}"+ JSON.toJSONString(exchange));

        if(body==null||body.get("result")==null||!(Boolean)body.get("result")){
            log.info("报错了");
            throw new BusinessException(CommonErrorCode.E_100102);
        }
    }
}
