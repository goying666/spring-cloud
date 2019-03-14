package com.renchaigao.zujuba.teamserver.config.mongoDB;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@ConfigurationProperties(prefix="spring.data.mongodb.normal")
public class NormalMongoConfig extends AbstractMongoConfig{

    @Primary
    @Bean(name = "normalMongoTemplate")
    public MongoTemplate getMongoTemplate(){
        return new MongoTemplate(mongoDbFactory());
    }
}
