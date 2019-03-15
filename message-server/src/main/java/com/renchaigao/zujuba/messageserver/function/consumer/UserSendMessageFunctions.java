//package com.renchaigao.zujuba.messageserver.function.consumer;
//
//
//import com.alibaba.fastjson.JSONObject;
//import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
//import com.renchaigao.zujuba.dao.mapper.UserMapper;
//import com.renchaigao.zujuba.mongoDB.info.PlayerInfo;
//import com.renchaigao.zujuba.mongoDB.info.message.MessageContent;
//import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;
//import com.renchaigao.zujuba.mongoDB.info.team.TeamMessageInfo;
//import com.renchaigao.zujuba.mongoDB.info.team.TeamPlayerInfo;
//import com.renchaigao.zujuba.mongoDB.info.user.UserMessagesInfo;
//import normal.dateUse;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.data.mongodb.core.query.Update;
//import org.springframework.kafka.core.KafkaTemplate;
//
//import java.util.ArrayList;
//
//import static com.renchaigao.zujuba.PropertiesConfig.ConstantManagement.MESSAGE_SENDER_SYSTEM;
//import static com.renchaigao.zujuba.PropertiesConfig.ConstantManagement.USER_SEND_MESSAGE;
//
//public class UserSendMessageFunctions {
//
//    UserMapper userMapper;
//    MongoTemplate normalMongoTemplate;
//    MongoTemplate messageMongoTemplate;
//    KafkaTemplate<String, String> kafkaTemplate;
//
//    public UserSendMessageFunctions(UserMapper userMapper, MongoTemplate normalMongoTemplate, MongoTemplate messageMongoTemplate, KafkaTemplate<String, String> kafkaTemplate) {
//        this.userMapper = userMapper;
//        this.normalMongoTemplate = normalMongoTemplate;
//        this.messageMongoTemplate = messageMongoTemplate;
//        this.kafkaTemplate = kafkaTemplate;
//    }
//    //            群消息
//    public void ChangeUserInfo(MessageContent messageContent) {
////        检查发送信息的合法性；_——待开发
//
////        新增信息至teamMessageInfo
//        messageMongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(messageContent.getTeamId())),
//                new Update().push("messageNoteInfoArrayList", messageContent),
//                MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_TEAM_MESSAGE_INFO );
//        if(!messageContent.getIsReceived()){
//            //        用户未阅读该信息，新增信息至userMessages
//            normalMongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(messageContent.getSenderId())),
//                    new Update().push("userTeamMessage", messageContent),
//                    MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE_INFO);
//        }
//    }
//
//
//    //            好友消息
//    public void SendToFriend(MessageContent messageContent) {
//
//    }
//
//}
