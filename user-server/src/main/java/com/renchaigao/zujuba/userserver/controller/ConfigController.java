package com.renchaigao.zujuba.userserver.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope // 使用该注解的类，会在接到SpringCloud配置中心配置刷新的时候，自动将新的配置更新到该类对应的字段中。
@RestController
@EnableAutoConfiguration
@Configuration
public class ConfigController {

//    @Value("${from}")
//    String from;
//
//    @RequestMapping(value = "/from")
//    public String writer(){
//        return this.from;
//    }

}
