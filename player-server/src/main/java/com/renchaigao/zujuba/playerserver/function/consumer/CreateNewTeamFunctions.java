package com.renchaigao.zujuba.playerserver.function.consumer;


import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.dao.mapper.UserOpenInfoMapper;
import com.renchaigao.zujuba.mongoDB.info.PlayerInfo;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;
import com.renchaigao.zujuba.mongoDB.info.team.TeamPlayerInfo;
import com.renchaigao.zujuba.mongoDB.info.user.UserInfo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import store.DistanceFunc;

import java.util.ArrayList;

import static com.renchaigao.zujuba.PropertiesConfig.PlayerConstant.*;
import static com.renchaigao.zujuba.PropertiesConfig.UserConstant.USER_GENDER;

public class CreateNewTeamFunctions {

    private UserOpenInfoMapper userOpenInfoMapper;
    private MongoTemplate mongoTemplate;


    public CreateNewTeamFunctions(MongoTemplate mongoTemplate, UserOpenInfoMapper userOpenInfoMapper) {
        this.mongoTemplate = mongoTemplate;
        this.userOpenInfoMapper = userOpenInfoMapper;
    }

    public PlayerInfo CreateOwnerPlayerInfo(TeamInfo teamInfo) {
        String createrId = teamInfo.getCreaterId();
        UserInfo userInfo = mongoTemplate.findById(createrId, UserInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_INFO);
//        创建playerInfo:房主的player信息
        PlayerInfo playerInfo = new PlayerInfo();//
        playerInfo.setId(createrId);
        playerInfo.setHomeOwner(PLAYER_ROLE[0]);//creater
        playerInfo.setDistance(DistanceFunc.getDistance(teamInfo.getAddressInfo().getLatitude(),
                teamInfo.getAddressInfo().getLongitude(),
                userInfo != null ? userInfo.getAddressInfo().getLatitude() : 0,
                userInfo != null ? userInfo.getAddressInfo().getLongitude() : 0));
        playerInfo.setComeFrom(PLAYER_COME_FROM_TEAMJOIN);
        playerInfo.setState(PLAYER_STATE[0]);
        playerInfo.setPlayerNumber(0); //玩家编号
//        玩家开放信息设置
        playerInfo.setUserOpenInfo(userOpenInfoMapper.selectByPrimaryKey(createrId));
        return playerInfo;
    }

    public TeamPlayerInfo CreateTeamPlayerInfo(PlayerInfo createrPlayerInfo, TeamInfo teamInfo) {
        String createrId = teamInfo.getCreaterId();
        UserInfo userInfo = mongoTemplate.findById(createrId, UserInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_INFO);
        TeamPlayerInfo teamPlayerInfo = new TeamPlayerInfo();
        teamPlayerInfo.setId(teamInfo.getId());
        teamPlayerInfo.setTeamId(teamInfo.getId());
        teamPlayerInfo.setPlayerArrayList(new ArrayList<>());
        teamPlayerInfo.getPlayerArrayList().add(createrPlayerInfo);
        if (userInfo == null || userInfo.getGender().equals(USER_GENDER[0])) {
//            性别未定
            teamPlayerInfo.setBoySum(0);
            teamPlayerInfo.setGirlSum(0);
        } else if (userInfo.getGender().equals(USER_GENDER[1])) {
//            性别为男
            teamPlayerInfo.setBoySum(1);
            teamPlayerInfo.setGirlSum(0);
        } else if (userInfo.getGender().equals(USER_GENDER[2])) {
//            性别为女
            teamPlayerInfo.setBoySum(0);
            teamPlayerInfo.setGirlSum(1);
        }
        teamPlayerInfo.setWatingSum(1);
        teamPlayerInfo.setReadySum(0);
        teamPlayerInfo.setGameSum(0);
        teamPlayerInfo.setMissSum(teamInfo.getPlayerMin() - 1);
        mongoTemplate.save(teamPlayerInfo, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_TEAM_PLAYER_INFO);

        mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(teamPlayerInfo.getTeamId()))
                , new Update().set("teamPlayerInfo", teamPlayerInfo)
                , MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_TEAM_INFO);
        return teamPlayerInfo;
    }

}
