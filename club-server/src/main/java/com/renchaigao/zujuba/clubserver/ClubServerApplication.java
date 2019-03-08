package com.renchaigao.zujuba.clubserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Configuration;


@SpringBootApplication(exclude = {ClubServerApplication.class})
@EnableAutoConfiguration
@MapperScan("com.renchaigao.zujuba.dao.mapper")
//@EnableScheduling
//@Configuration
@EnableEurekaClient
public class ClubServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClubServerApplication.class, args);
	}

}
