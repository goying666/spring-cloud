package com.renchaigao.zujuba.storeserver.service.impl;

import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;
import com.renchaigao.zujuba.storeserver.service.StoreConsumerService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class StoreConsumerServiceImpl implements StoreConsumerService {

    private static Logger logger = Logger.getLogger(StoreConsumerServiceImpl.class);

    @Autowired
    UserMapper userMapper;

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public void CreateNewTeam(TeamInfo teamInfo) {

    }
}
