package com.renchaigao.zujuba.playerserver.service.impl;

import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.dao.mapper.UserOpenInfoMapper;
import com.renchaigao.zujuba.mongoDB.info.PlayerInfo;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;
import com.renchaigao.zujuba.mongoDB.info.team.TeamPlayerInfo;
import com.renchaigao.zujuba.playerserver.function.consumer.CreateNewTeamFunctions;
import com.renchaigao.zujuba.playerserver.service.PlayerConsumerService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class PlayerConsumerServiceImpl implements PlayerConsumerService {

    private static Logger logger = Logger.getLogger(PlayerConsumerServiceImpl.class);


    @Autowired
    UserMapper userMapper;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    UserOpenInfoMapper userOpenInfoMapper;

    @Override
    public void CreateNewTeam(TeamInfo teamInfo) {
        CreateNewTeamFunctions createNewTeamFunctions = new CreateNewTeamFunctions(userMapper,mongoTemplate,userOpenInfoMapper);
//        创建房主的player信息
        PlayerInfo createrPlayerInfo = createNewTeamFunctions.CreateOwnerPlayerInfo(teamInfo);
        logger.info(createrPlayerInfo);
//        创建team对应的teamPlayerInfo
        TeamPlayerInfo teamPlayerInfo = createNewTeamFunctions.CreateTeamPlayerInfo(createrPlayerInfo,teamInfo);
        logger.info(teamPlayerInfo);
    }
}
