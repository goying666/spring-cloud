package com.renchaigao.zujuba.clubserver.config.mongoDB;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@ConfigurationProperties(prefix="spring.data.mongodb.message")
public class MessageMongoConfig extends AbstractMongoConfig{
    @Bean(name = "messageMongoTemplate")
    public MongoTemplate getMongoTemplate(){
        return new MongoTemplate(mongoDbFactory());
    }
}
