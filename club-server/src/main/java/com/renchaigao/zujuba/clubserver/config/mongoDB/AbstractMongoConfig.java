package com.renchaigao.zujuba.clubserver.config.mongoDB;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import lombok.Data;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

@Data
public class AbstractMongoConfig {
    protected String host;
    protected int port;
    protected String username;
    protected String password;
    protected String database;
    public MongoDbFactory mongoDbFactory(){
        ServerAddress serverAddress = new ServerAddress(host,port);
        return new SimpleMongoDbFactory(new MongoClient(serverAddress),database);
    }
}
