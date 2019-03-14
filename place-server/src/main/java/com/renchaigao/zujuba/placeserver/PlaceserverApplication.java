package com.renchaigao.zujuba.placeserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {PlaceserverApplication.class})
@EnableAutoConfiguration
@MapperScan("com.renchaigao.zujuba.dao.mapper")
//@EnableScheduling
@Configuration
@EnableEurekaClient
public class PlaceserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlaceserverApplication.class, args);
	}

}

