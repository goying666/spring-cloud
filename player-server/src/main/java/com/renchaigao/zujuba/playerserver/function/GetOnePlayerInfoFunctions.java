package com.renchaigao.zujuba.playerserver.function;

import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.mongoDB.info.store.StoreInfo;
import org.springframework.data.mongodb.core.MongoTemplate;

public class GetOnePlayerInfoFunctions {

    UserMapper userMapper;
    MongoTemplate mongoTemplate;

    public GetOnePlayerInfoFunctions(UserMapper userMapper, MongoTemplate mongoTemplate) {
        this.userMapper = userMapper;
        this.mongoTemplate = mongoTemplate;
    }

}
