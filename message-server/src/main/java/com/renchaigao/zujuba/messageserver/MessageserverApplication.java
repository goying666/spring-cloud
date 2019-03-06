package com.renchaigao.zujuba.messageserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAutoConfiguration
@SpringBootApplication(exclude = MongoAutoConfiguration.class)
@MapperScan("com.renchaigao.zujuba.dao.mapper")
@EnableScheduling
public class MessageserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessageserverApplication.class, args);
	}
}
