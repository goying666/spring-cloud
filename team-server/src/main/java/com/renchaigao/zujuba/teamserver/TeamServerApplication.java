package com.renchaigao.zujuba.teamserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {MongoDataAutoConfiguration.class, MongoAutoConfiguration.class,TeamServerApplication.class})
@EnableAutoConfiguration
@MapperScan("com.renchaigao.zujuba.dao.mapper")
//@EnableScheduling
@Configuration
@EnableEurekaClient
public class TeamServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeamServerApplication.class, args);
	}
}
