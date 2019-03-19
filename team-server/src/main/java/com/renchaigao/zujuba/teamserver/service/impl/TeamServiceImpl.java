package com.renchaigao.zujuba.teamserver.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.PageBean.TeamActivityBean;
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
import com.renchaigao.zujuba.mongoDB.info.user.UserInfo;
import com.renchaigao.zujuba.mongoDB.info.user.UserTeams;
import com.renchaigao.zujuba.teamserver.service.TeamService;
import com.renchaigao.zujuba.teamserver.uti.CreateNewTeamFunctions;
import com.renchaigao.zujuba.teamserver.uti.GetNearTeamListFunctions;
import com.renchaigao.zujuba.teamserver.uti.GetOneTeamFunctions;
import com.renchaigao.zujuba.teamserver.uti.JoinTeamFunctions;
import normal.dateUse;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
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

import static com.renchaigao.zujuba.PropertiesConfig.UserConstant.GENDER_BOY;

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
    public ResponseEntity JoinTeam(String userId, String teamId) {
        TeamPlayerInfo teamPlayerInfo = normalMongoTemplate.findOne(Query.query(Criteria.where("teamId").is(teamId)),
                TeamPlayerInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_TEAM_PLAYER_INFO);
//        检查是否已经存在在队伍中了
        if (teamPlayerInfo != null) {
            for (PlayerInfo s : teamPlayerInfo.getPlayerArrayList()) {
                if (s.getId().equals(userId))
                    return new ResponseEntity(RespCode.TEAM_HAD_BEEN_JOIN, null);
            }
        }
        JoinTeamFunctions joinTeamFunctions = new JoinTeamFunctions(userMapper, normalMongoTemplate, userOpenInfoMapper, kafkaTemplate);
        try {
            joinTeamFunctions.FilterPart(userId, teamId);
            joinTeamFunctions.TeamPart(userId, teamId, teamPlayerInfo);
            joinTeamFunctions.UserPart(userId, teamId);
            return new ResponseEntity(RespCode.TEAM_JOIN_SUCCESS, null);
        } catch (Exception e) {
            return new ResponseEntity(RespCode.TEAM_JOIN_FAIL, e);
        }
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
