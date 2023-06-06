package com.zhang.merchant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author : 15754
 * @version 1.0.0
 * @since : 2023/6/3 18:33
 **/

@SpringBootApplication
public class MerchantApplicationBootStrap {


    public static void main(String[] args) {

        SpringApplication.run(MerchantApplicationBootStrap.class);

    }

    @Bean
    RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(new OkHttp3ClientHttpRequestFactory());
       //获取消息转换器
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        //指定字符集为UTF-8
        messageConverters.set(1,new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }


}
