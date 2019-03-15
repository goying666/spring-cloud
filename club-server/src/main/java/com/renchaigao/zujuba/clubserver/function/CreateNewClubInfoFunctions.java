package com.renchaigao.zujuba.clubserver.function;

import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.club.ClubInfo;
import com.renchaigao.zujuba.mongoDB.info.club.ClubUserInfo;
import com.renchaigao.zujuba.mongoDB.info.message.MessageContent;
import com.renchaigao.zujuba.mongoDB.info.store.StoreInfo;
import normal.dateUse;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.core.KafkaTemplate;

import static com.renchaigao.zujuba.PropertiesConfig.ClubConstant.ROLE_CREATER;
import static com.renchaigao.zujuba.PropertiesConfig.ConstantManagement.CLUB_SEND_MESSAGE;
import static com.renchaigao.zujuba.PropertiesConfig.ConstantManagement.CONFIG_RENAME_CLUB_TIMES;
import static com.renchaigao.zujuba.PropertiesConfig.UserConstant.GENDER_BOY;
import static com.renchaigao.zujuba.PropertiesConfig.UserConstant.GENDER_GIRL;

public class CreateNewClubInfoFunctions {

    private UserMapper userMapper;
    private MongoTemplate normalMongoTemplate;
    private MongoTemplate messagelMongoTemplate;

    KafkaTemplate<String, String> kafkaTemplate;

    public CreateNewClubInfoFunctions(UserMapper userMapper, MongoTemplate normalMongoTemplate, MongoTemplate messagelMongoTemplate, KafkaTemplate<String, String> kafkaTemplate) {
        this.userMapper = userMapper;
        this.normalMongoTemplate = normalMongoTemplate;
        this.messagelMongoTemplate = messagelMongoTemplate;
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
        return normalMongoTemplate.findById(clubInfo.getId(), ClubInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_CLUB_INFO) == null;
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
//        4、消息信息部分 和 人员相关信息（场地管理者和创建者的基本信息）
        messagePart(clubInfo);
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
        clubInfo.setRenameTimesLimit(CONFIG_RENAME_CLUB_TIMES);
        String placeAdminId = normalMongoTemplate.findById(clubInfo.getPlaceId(), StoreInfo.class,MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_INFO).getOwnerId();
        clubInfo.getUserIdList().add(clubInfo.getCreaterId());
        clubInfo.getUserIdList().add(placeAdminId);
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
//        ClubTeamInfo clubTeamInfo = new ClubTeamInfo();
//        clubTeamInfo.setId(clubInfo.getId());
//        clubTeamInfo.setClubId(clubInfo.getId());
    }

    private void messagePart(ClubInfo clubInfo) {
//        修改场地相关信息
        normalMongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(clubInfo.getPlaceId())),
                new Update().push("StoreClubArray",clubInfo.getId()),MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_INFO);
//        场地信息人发言
        MessageContent userMessageContent = new MessageContent();
        Long nowTimeLong = dateUse.getNowTimeLong();
        userMessageContent.setIsMe(true);
        userMessageContent.setContent("大家好，我是俱乐部所在场地的负责人，有关场地的任何问题可以咨询我，祝您玩的愉快。");
        String placeAdminId = normalMongoTemplate.findById(clubInfo.getPlaceId(), StoreInfo.class,MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_INFO).getOwnerId();
        userMessageContent.setTitle(clubInfo.getClubName());
        userMessageContent.setSenderId(placeAdminId);
        userMessageContent.setMessageClass(CLUB_SEND_MESSAGE);
        userMessageContent.setSendTime(nowTimeLong);
        userMessageContent.setIdLong(nowTimeLong);
        userMessageContent.setUserId(placeAdminId);
        userMessageContent.setSenderImageUrl(userMapper.selectByPrimaryKey(placeAdminId).getPicPath());
        userMessageContent.setClubId(clubInfo.getId());
        userMessageContent.getReadList().addAll(clubInfo.getUserIdList());//增加消息阅读者——创建地的管理员 和 创建人
        kafkaTemplate.send(CLUB_SEND_MESSAGE, JSONObject.toJSONString(userMessageContent));

//        创建者的发言
        userMessageContent = new MessageContent();
        nowTimeLong = dateUse.getNowTimeLong();
        userMessageContent.setIsMe(true);
        userMessageContent.setContent("Hi~我 创建 了这个组局，欢迎你的参与，祝玩的愉快~");
        userMessageContent.setTitle(clubInfo.getClubName());
        userMessageContent.setSenderId(clubInfo.getCreaterId());
        userMessageContent.setMessageClass(CLUB_SEND_MESSAGE);
        userMessageContent.setSendTime(nowTimeLong);
        userMessageContent.setIdLong(nowTimeLong);
        userMessageContent.setUserId(clubInfo.getCreaterId());
        userMessageContent.setSenderImageUrl(userMapper.selectByPrimaryKey(clubInfo.getCreaterId()).getPicPath());
        userMessageContent.setClubId(clubInfo.getId());
        userMessageContent.getReadList().addAll(clubInfo.getUserIdList());//增加消息阅读者——创建地的管理员 和 创建人
        kafkaTemplate.send(CLUB_SEND_MESSAGE, JSONObject.toJSONString(userMessageContent));

//        修改场地负责人的场地信息
        normalMongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(placeAdminId)),
                new Update().push("clubIdList",clubInfo.getId()),MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_CLUB_INFO);
//        创建人的相关信息更新
        normalMongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(clubInfo.getCreaterId()))
                ,new Update().push("myClubList",clubInfo.getId()).push("clubIdList",clubInfo.getId()),MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_CLUB_INFO);
    }

}