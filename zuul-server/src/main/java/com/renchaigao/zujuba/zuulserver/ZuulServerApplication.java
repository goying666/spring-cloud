package com.renchaigao.zujuba.zuulserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.web.bind.annotation.RestController;

@EnableZuulProxy
@SpringBootApplication
@EnableEurekaClient
public class ZuulServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZuulServerApplication.class, args);
	}

}
