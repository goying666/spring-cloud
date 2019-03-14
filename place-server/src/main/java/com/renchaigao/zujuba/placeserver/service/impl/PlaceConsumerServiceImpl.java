package com.renchaigao.zujuba.placeserver.service.impl;

import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;
import com.renchaigao.zujuba.placeserver.service.PlaceConsumerService;
import javafx.scene.shape.Circle;
import normal.dateUse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import static com.renchaigao.zujuba.PropertiesConfig.ConstantManagement.*;

@Service
public class PlaceConsumerServiceImpl implements PlaceConsumerService {

    private static Logger logger = Logger.getLogger(PlaceConsumerServiceImpl.class);

    @Autowired
    UserMapper userMapper;

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public void CreateNewTeam(TeamInfo teamInfo) {

    }
}
