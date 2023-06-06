package com.zhang.merchant.service.impl;

import com.alibaba.fastjson.JSON;
import com.zhang.merchant.service.smsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class smsServiceImpl implements smsService {

    @Value("${sms.url}")
    String url;
    @Value("${sms.effectiveTime}")
    String effectiveTime;

    @Resource
    RestTemplate restTemplate;

    @Override
    public String sendMsg(String phone) {
        String sms_url=url+"/generate?name=sms&effectiveTime="+effectiveTime;
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

        String key =(String) result.get("key");
        log.info("得到验证码对应的key:{}",key);


        return key;
    }
}
