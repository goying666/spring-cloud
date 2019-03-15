package com.renchaigao.zujuba.teamserver.uti;

import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.dao.UserOpenInfo;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.dao.mapper.UserOpenInfoMapper;
import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.PlayerInfo;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;
import com.renchaigao.zujuba.mongoDB.info.team.TeamPlayerInfo;
import com.renchaigao.zujuba.mongoDB.info.user.UserTeams;
import normal.dateUse;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.core.KafkaTemplate;

import static com.renchaigao.zujuba.PropertiesConfig.PlayerConstant.PLAYER_COME_FROM_TEAMJOIN;
import static com.renchaigao.zujuba.PropertiesConfig.PlayerConstant.PLAYER_STATE_WAITING;
import static com.renchaigao.zujuba.PropertiesConfig.UserConstant.GENDER_BOY;

public class JoinTeamFunctions {

    UserOpenInfoMapper userOpenInfoMapper;
    UserMapper userMapper;
    MongoTemplate normalMongoTemplate;
    KafkaTemplate<String, String> kafkaTemplate;

    public JoinTeamFunctions(UserMapper userMapper,
                             MongoTemplate mongoTemplate,
                             UserOpenInfoMapper userOpenInfoMapper,
                             KafkaTemplate<String, String> kafkaTemplate) {
        this.userMapper = userMapper;
        this.normalMongoTemplate = mongoTemplate;
        this.userOpenInfoMapper = userOpenInfoMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    /*
     * 说明：获取team的筛选信息，判断加入的用户是否满足，若不满足则返回相应的失败提示。
     */
    public Boolean FilterPart(String userId, String teamId) {
//        获取筛选内容
        TeamInfo teamInfo = normalMongoTemplate.findById(teamId, TeamInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_TEAMINFO);
//        获取用户公开数据
//        比对：按照结果返回；
        return true;
    }

    /*
     * 说明：更新team的信息，1、新增通过筛选的玩家信息；2、更新基础信息；
     */
    public void TeamPart(String userId, String teamId, TeamPlayerInfo teamPlayerInfo) {
//        增加用户信息
        UserOpenInfo userOpenInfo = userOpenInfoMapper.selectByPrimaryKey(userId);
//        获取用户是否已存在，
        PlayerInfo playerInfo = new PlayerInfo();
//        将user的信息填入playerInfo中
        playerInfo.setId(userId);
        playerInfo.setHomeOwner("0");
        playerInfo.setUserOpenInfo(userOpenInfo);
        playerInfo.setState(PLAYER_STATE_WAITING);
        playerInfo.setComeFrom(PLAYER_COME_FROM_TEAMJOIN);
        playerInfo.setJoinTime(dateUse.GetStringDateNow());
        playerInfo.setPlayerNumber(teamPlayerInfo.getPlayerArrayList().size() + 1);
//        用户距离————待开发；
//        修改teamPlayerInfo内的内容；1、修改男女数量,并添加用户player信息；
        Update update = new Update();
//        如果有性别限制，则进行男女人数统计
        if (userOpenInfo.getGender().equals(GENDER_BOY)) {
            update.inc("boySum", 1);
        } else {
            update.inc("girlSum", 1);
        }
        update.inc("watingSum", 1).push("playerArrayList", playerInfo);
        normalMongoTemplate.updateFirst(Query.query(Criteria.where("teamId").is(teamId)), update,
                TeamPlayerInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_TEAM_PLAYER_INFO);

    }

    /*
     * 说明：更新成功加入组局的用户的个人信息：1、userTeams；2、系统生成用户的加入message 到team的聊天室内；
     */
    public void UserPart(String userId, String teamId) {
        //        更新用户方的team信息
        normalMongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(userId)),
                new Update().push("doingTeamsList", teamId).push("allTeamsList", teamId),
                UserTeams.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_TEAMS);
    }


}
