package com.zhang.merchant.controller;

import com.shanjupay.common.domain.RestResponse;
import com.zhang.merchant.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.UUID;

/**
 * @author : 15754
 * @version 1.0.0
 * @since : 2023/6/8 10:20
 **/


@RestController
@Slf4j
public class CommonController {

        @Resource
        private AliOssUtil aliOssUtil;


        @PostMapping("/upload")
        public RestResponse upload(@RequestBody MultipartFile file) throws IOException {

            int extendIndex = file.getOriginalFilename().lastIndexOf(".");
            String extendString = file.getOriginalFilename().substring(extendIndex);
            String ObjectName = UUID.randomUUID().toString() +extendString;
            System.out.println(ObjectName);
            String upload = aliOssUtil.upload(file.getBytes(), ObjectName);
            log.info("生成文件URL为:{}",upload);
            return RestResponse.success(upload);
        }

}
