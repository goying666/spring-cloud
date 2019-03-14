package com.renchaigao.zujuba.teamserver.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.dao.UserOpenInfo;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.dao.mapper.UserOpenInfoMapper;
import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.AddressInfo;
import com.renchaigao.zujuba.mongoDB.info.PlayerInfo;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;
import com.renchaigao.zujuba.mongoDB.info.team.TeamPlayerInfo;
import com.renchaigao.zujuba.mongoDB.info.user.UserTeams;
import com.renchaigao.zujuba.teamserver.service.TeamService;
import com.renchaigao.zujuba.teamserver.uti.CreateNewTeamFunctions;
import com.renchaigao.zujuba.teamserver.uti.GetNearTeamListFunctions;
import com.renchaigao.zujuba.teamserver.uti.GetOneTeamFunctions;
import normal.dateUse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;

@Service
public class TeamServiceImpl implements TeamService {

    private static Logger logger = Logger.getLogger(TeamServiceImpl.class);


    @Resource(name = "messageMongoTemplate")
    private MongoTemplate messageMongoTemplate;

    @Resource(name = "normalMongoTemplate")
    private MongoTemplate normalMongoTemplate;

    @Autowired
    private UserOpenInfoMapper userOpenInfoMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public ResponseEntity CreateNewTeam(String userId, String teamId, String jsonObjectString) {

        CreateNewTeamFunctions createNewTeamFunctions = new CreateNewTeamFunctions(userMapper, normalMongoTemplate,
                messageMongoTemplate, kafkaTemplate);
        TeamInfo teamInfo = JSONObject.parseObject(jsonObjectString, TeamInfo.class);
        teamInfo.setAddressInfo(normalMongoTemplate.findById(teamInfo.getAddressInfo().getId(),
                AddressInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_ADDRESS_INFO));

//        检查创建信息完整性 和 正确性（查重、冲突逻辑等）；
        if (!createNewTeamFunctions.CheckCreateInfo(JSONObject.toJSONString(teamInfo)))
            return null;
//        基础信息
        createNewTeamFunctions.CreateTeamInfoBasic(teamInfo);
//        地址信息
        createNewTeamFunctions.CreateTeamInfoAddress(teamInfo);
//        玩家信息
        createNewTeamFunctions.CreateTeamInfoPlayer(teamInfo);
//        游戏信息
        createNewTeamFunctions.CreateTeamInfoGame(teamInfo);
//        筛选条件信息
        createNewTeamFunctions.CreateTeamInfoFilter(teamInfo);
//        消费信息
        createNewTeamFunctions.CreateTeamInfoSpend(teamInfo);
//        组队消息信息
        createNewTeamFunctions.CreateTeamInfoMessage(teamInfo);
//        请求所有信息
        createNewTeamFunctions.CreateTeamInfoAll(teamInfo);
//        检查组局信息是否完整
        createNewTeamFunctions.CheckAllInfoIsRight(teamInfo);
//        更新teamInfo信息
        createNewTeamFunctions.UpdateTeamInfoAll(teamInfo);
//        修改个人team信息，myTeamsInfo
        createNewTeamFunctions.UpdateMyTeamsInfo(teamInfo);
//        场地信息部分
        createNewTeamFunctions.UpdateTeamPlaceInfo(teamInfo);
//        聊天信息部分
        createNewTeamFunctions.CreateTeamMessageInfo(teamInfo);
//
        normalMongoTemplate.save(teamInfo);

        return new ResponseEntity(RespCode.SUCCESS, JSONObject.toJSONString(teamInfo));

    }


    @Override
    public ResponseEntity GetNearTeams(String userId, String parameter, String jsonObjectString) {
        GetNearTeamListFunctions getNearTeamListFunctions = new GetNearTeamListFunctions(normalMongoTemplate);
        //        获取原始信息
        ArrayList<TeamInfo> teamInfoArrayList = getNearTeamListFunctions.GetNearTeamInfoListByUserId(userId);
        //        组装参数给前端
        JSONArray jsonArray = getNearTeamListFunctions.PackageTeamInfoList(teamInfoArrayList);
        return new ResponseEntity(RespCode.SUCCESS, jsonArray);
    }

    @Override
    public ResponseEntity FindOneTeam(String userId, String teamId, String jsonObjectString) {
        GetOneTeamFunctions getOneTeamFunctions = new GetOneTeamFunctions(userMapper, normalMongoTemplate, userOpenInfoMapper, kafkaTemplate);
        TeamInfo teamInfo = getOneTeamFunctions.GetDatabaseInfo(teamId);
        return new ResponseEntity(RespCode.SUCCESS, getOneTeamFunctions.AssembleOtherInfo(userId, teamInfo));
    }

    @Override
    public ResponseEntity JoinTeam(String userId, String teamId, String jsonObjectString) {

        Long funBegin = Calendar.getInstance().getTimeInMillis();
//        获取team信息，
        TeamInfo teamInfo = normalMongoTemplate.findById(teamId, TeamInfo.class, "teamInfo");
//        获取team的筛选信息，判断加入的用户是否满足，若不满足则返回相应的提示。

//        增加用户信息
        UserOpenInfo userOpenInfo = normalMongoTemplate.findById(userId, UserOpenInfo.class, "userOpenInfo");
//        获取用户是否已存在，
        TeamPlayerInfo teamPlayerInfo = normalMongoTemplate.findOne(Query.query(Criteria.where("teamId").is(teamId)),
                TeamPlayerInfo.class, "teamPlayerInfo");
        if (teamPlayerInfo != null) {
            for (PlayerInfo s : teamPlayerInfo.getPlayerArrayList()) {
                if (s.getId().equals(userId))
                    return new ResponseEntity(RespCode.TEAM_HAD_BEEN_JOIN, teamInfo);
            }
        }
        PlayerInfo playerInfo = new PlayerInfo();
//        将user的信息填入playerInfo中
        playerInfo.setId(userId);
        playerInfo.setHomeOwner("0");
        playerInfo.setUserOpenInfo(userOpenInfo);
        playerInfo.setState("JOIN");
        playerInfo.setComeFrom("T");
        playerInfo.setJoinTime(dateUse.GetStringDateNow());
        normalMongoTemplate.save(playerInfo, "playerInfo");
//        用户距离————待开发；
//        修改teamPlayerInfo内的内容；
//        修改男女数量,并添加用户player信息；
        Update update = new Update();
//        如果有性别限制，则进行男女人数统计
//        if (userOpenInfo.getGender().equals("B")) {
//            update.inc("boySum", 1);
//        } else {
//            update.inc("girlSum", 1);
//        }
        update.inc("watingSum", 1).push("playerArrayList", playerInfo);
        normalMongoTemplate.updateFirst(Query.query(Criteria.where("teamId").is(teamId)), update,
                TeamPlayerInfo.class, "teamPlayerInfo");

//        更新用户的team信息
        normalMongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(userId)),
                new Update().push("doingTeamsList", teamId).push("allTeamsList", teamId),
                UserTeams.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_TEAMS);

        if (teamInfo != null) {
            teamInfo.setTeamPlayerInfo(normalMongoTemplate.findOne(Query.query(Criteria.where("teamId").is(teamId)),
                    TeamPlayerInfo.class, "teamPlayerInfo"));
        }

        teamInfo = normalMongoTemplate.findById(teamId, TeamInfo.class, "teamInfo");
        Long funEnd = Calendar.getInstance().getTimeInMillis();
        logger.info("This function spend times : " + (funEnd - funBegin) / 60000 + "m"
                + (funEnd - funBegin) / 1000 + "s" + (funEnd - funBegin) % 1000 + "ms");
        return new ResponseEntity(RespCode.SUCCESS, teamInfo);

    }


//
//    @Override
//    public ResponseEntity FindOneTeam(String userId, String parameter, String teamId, String jsonObjectString) {
//        return null;
//    }
//
//    @Override
//    public ResponseEntity DeleteMyTeams(String userId, String parameter, String teamId, String jsonObjectString) {
//        return null;
//    }

    @Override
    public ResponseEntity UpdateTeam(String userId, String teamId, String parameter, String jsonObjectString) {
        //        通过teamID获取team的info信息；
        TeamInfo teamInfo = normalMongoTemplate.findById(teamId, TeamInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_TEAMINFO);
//        判断获取team信息的user是否已经加入该team；
        TeamPlayerInfo playerInfo = normalMongoTemplate.findById(teamId, TeamPlayerInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_PLAYERINFO);
        if (playerInfo != null) {
            for (PlayerInfo p : playerInfo.getPlayerArrayList()) {
                if (p.getId().equals(userId)) {
//                说明用户已存在于该team中；
                    return new ResponseEntity(RespCode.UPDATE_TEAM_SUCCESS_JOIN, teamInfo);
                }
            }
        }
        return new ResponseEntity(RespCode.UPDATE_TEAM_SUCCESS, teamInfo);
    }
//
//
//    @Override
//    public ResponseEntity QuitTeam(String userId, String parameter, String teamId, String jsonObjectString) {
//        return null;
//    }
//
//    @Override
//    public ResponseEntity DeleteTeam(String userId, String parameter, String teamId, String jsonObjectString) {
//        return null;
//    }
//
//    @Override
//    public ResponseEntity ReportTeam(String userId, String parameter, String teamId, String jsonObjectString) {
//        return null;
//    }
//
//    @Override
//    public ResponseEntity FindMyTeams(String userId, String parameter, String teamId, String jsonObjectString) {
//        return null;
//    }
//
}
