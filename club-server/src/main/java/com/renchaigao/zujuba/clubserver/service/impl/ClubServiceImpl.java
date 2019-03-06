package com.renchaigao.zujuba.clubserver.service.impl;

import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.clubserver.function.CreateNewClubInfoFunctions;
import com.renchaigao.zujuba.clubserver.service.ClubService;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.dao.mapper.UserOpenInfoMapper;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.club.ClubInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ClubServiceImpl implements ClubService {

    private static Logger logger = Logger.getLogger(ClubServiceImpl.class);

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    UserOpenInfoMapper userOpenInfoMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    @Override
    public ResponseEntity CreateNewClub(String userId, String placeId, ClubInfo clubInfo) {
        CreateNewClubInfoFunctions createNewClubInfoFunctions = new CreateNewClubInfoFunctions(userMapper,mongoTemplate,mongoTemplate,kafkaTemplate);
        //        检查参数完整性：id重复、名称重复；
        createNewClubInfoFunctions
//        大本营选择的场所限制————待开发
//        创建club
        mongoTemplate.save(clubInfo, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_CLUB_INFO);
        return null;
    }
}
