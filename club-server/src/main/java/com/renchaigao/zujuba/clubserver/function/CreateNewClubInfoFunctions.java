package com.renchaigao.zujuba.clubserver.function;

import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.PropertiesConfig.UserConstant;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.PlayerInfo;
import com.renchaigao.zujuba.mongoDB.info.club.ClubInfo;
import com.renchaigao.zujuba.mongoDB.info.club.ClubTeamInfo;
import com.renchaigao.zujuba.mongoDB.info.club.ClubUserInfo;
import com.renchaigao.zujuba.mongoDB.info.message.MessageContent;
import com.renchaigao.zujuba.mongoDB.info.team.TeamPlayerInfo;
import com.renchaigao.zujuba.mongoDB.info.user.UserClubInfo;
import normal.dateUse;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.core.KafkaTemplate;

import static com.renchaigao.zujuba.PropertiesConfig.ClubConstant.ROLE_CREATER;
import static com.renchaigao.zujuba.PropertiesConfig.KafkaTopicConstant.CREATE_NEW_CLUB;
import static com.renchaigao.zujuba.PropertiesConfig.UserConstant.GENDER_BOY;
import static com.renchaigao.zujuba.PropertiesConfig.UserConstant.GENDER_GIRL;
import static com.renchaigao.zujuba.PropertiesConfig.UserConstant.GENDER_NULL;

public class CreateNewClubInfoFunctions {

    UserMapper userMapper;
    MongoTemplate normalMongoTemplate;
    KafkaTemplate<String, String> kafkaTemplate;

    public CreateNewClubInfoFunctions(UserMapper userMapper, MongoTemplate normalMongoTemplate, KafkaTemplate<String, String> kafkaTemplate) {
        this.userMapper = userMapper;
        this.normalMongoTemplate = normalMongoTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    /*
     * 说明：检查参数完整性：id重复、名称重复；
     */
    public Boolean cheackClubInfo(ClubInfo clubInfo) {
        return true;
    }

    /*
     * 说明：大本营选择的场所限制————待开发
     */
    public Boolean placeLimitCheck(ClubInfo clubInfo) {
        return true;
    }

    /*
     * 说明：创建club
     */
    public ResponseEntity createClub(ClubInfo clubInfo) {
//        1、基本信息部分
        basicPart(clubInfo);
//        2、成员信息部分
        userPart(clubInfo);
//        3、组局信息部分
        teamPart(clubInfo);
//        4、消息信息部分
        messagePart(clubInfo);
//        5、创建者信息部分
        createrPart(clubInfo);
        return new ResponseEntity(RespCode.SUCCESS, clubInfo);
    }

    private void basicPart(ClubInfo clubInfo) {
        clubInfo.setUpTime(dateUse.GetStringDateNow());
//        添加创建者的男女性别
        String gender = userMapper.selectByPrimaryKey(clubInfo.getCreaterId()).getGender();
        switch (gender) {
            case GENDER_BOY:
                clubInfo.setBoyNum(clubInfo.getBoyNum() + 1);
                break;
            case GENDER_GIRL:
                clubInfo.setGirlNum(clubInfo.getGirlNum() + 1);
                break;
        }
        clubInfo.setAllPeopleNum(clubInfo.getAllPeopleNum() + 1);
        clubInfo.setRenameTimesLimit(3);
        clubInfo.getUserIdList().add(clubInfo.getCreaterId());
    }

    private void userPart(ClubInfo clubInfo) {
        ClubUserInfo clubUserInfo = new ClubUserInfo();
        clubUserInfo.setUserId(clubInfo.getCreaterId());
        clubUserInfo.setClubId(clubInfo.getId());
        clubUserInfo.setRole(ROLE_CREATER);
        clubUserInfo.setJoinTime(dateUse.GetStringDateNow());
        normalMongoTemplate.save(clubUserInfo, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_CLUB_USER_INFO);
    }

    private void teamPart(ClubInfo clubInfo) {

    }

    private void messagePart(ClubInfo clubInfo) {
//        通过kafka发送一个createNewClub的消息给message-server；
        kafkaTemplate.send(CREATE_NEW_CLUB, JSONObject.toJSONString(clubInfo));
//        验证kafka的消息成功完成任务；——待开发
    }

    private void createrPart(ClubInfo clubInfo) {
//        通过kafka发送一个createNewClub的消息给message-server；
        kafkaTemplate.send(CREATE_NEW_CLUB, JSONObject.toJSONString(clubInfo));
        //        验证kafka的消息成功完成任务；——待开发
    }

}