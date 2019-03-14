package com.renchaigao.zujuba.messageserver.function;

import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.PlayerInfo;
import com.renchaigao.zujuba.mongoDB.info.message.MessageContent;
import com.renchaigao.zujuba.mongoDB.info.team.TeamPlayerInfo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;
import java.util.List;

import static com.renchaigao.zujuba.PropertiesConfig.ConstantManagement.TEAM_SEND_MESSAGE;

public class GetMessageInfoFunctions {

    UserMapper userMapper;
    MongoTemplate normalMongoTemplate;
    MongoTemplate messageMongoTemplate;
    KafkaTemplate<String, String> kafkaTemplate;

    public GetMessageInfoFunctions(UserMapper userMapper, MongoTemplate normalMongoTemplate, MongoTemplate messageMongoTemplate, KafkaTemplate<String, String> kafkaTemplate) {
        this.userMapper = userMapper;
        this.normalMongoTemplate = normalMongoTemplate;
        this.messageMongoTemplate = messageMongoTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    /*
     * 说明：获取一个队伍里的消息；
     */
    public ResponseEntity GetTeamMessageInfo(String userid, String teamId, long lastTime) {
        ArrayList<MessageContent> messageContentList = new ArrayList<>(
                messageMongoTemplate.find(
                        Query.query(Criteria.where("teamId").is(teamId))
                                .addCriteria(Criteria.where("messageClass").is(TEAM_SEND_MESSAGE))
                                .addCriteria(Criteria.where("isReceived").is(false))
//                                .addCriteria(Criteria.where("sendTime").gt(lastTime))
                        , MessageContent.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE_INFO ));
        messageMongoTemplate.updateMulti(
                Query.query(Criteria.where("teamId").is(teamId))
                        .addCriteria(Criteria.where("messageClass").is(TEAM_SEND_MESSAGE))
                        .addCriteria(Criteria.where("isReceived").is(false))
//                        .addCriteria(Criteria.where("sendTime").gt(lastTime))
                , new Update().set("isReceived", true)
                , MessageContent.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE_INFO );
//
//        ArrayList<MessageContent> messageContentList = new ArrayList<>(
//                messageMongoTemplate.find(
//                        Query.query(Criteria.where("teamId").is(teamId))
//                                .addCriteria(Criteria.where("sendTime").gt(lastTime))
//                        , MessageContent.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE_INFO + userid));

        if (messageContentList.size() > 0) {
            return new ResponseEntity(RespCode.SUCCESS, messageContentList);
        }
        return new ResponseEntity(RespCode.WARN, null);
    }

}