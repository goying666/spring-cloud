package com.renchaigao.zujuba.messageserver.service.impl;

import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.messageserver.function.consumer.CreateNewTeamFunctions;
import com.renchaigao.zujuba.messageserver.service.MessageConsumerService;
import com.renchaigao.zujuba.mongoDB.info.message.MessageContent;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MessageConsumerServiceImpl implements MessageConsumerService {

    private static Logger logger = Logger.getLogger(MessageConsumerServiceImpl.class);

    @Autowired
    UserMapper userMapper;

    @Autowired
    MessageServiceImpl messageServiceImpl;

    @Resource(name = "messageMongoTemplate")
    MongoTemplate messageMongoTemplate;

    @Resource(name = "normalMongoTemplate")
    MongoTemplate normalMongoTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

//    @Override
//    public void CreateNewTeam(TeamInfo teamInfo) {
////        CreateNewTeamFunctions createNewTeamFunctions = new CreateNewTeamFunctions(userMapper, normalMongoTemplate, messageMongoTemplate, kafkaTemplate);
//////        创建team对应的message
////        createNewTeamFunctions.CreateTeamMessageInfo(teamInfo);
//////        系统发送一个通知给创建者 并且 创建者发送一个team的message
////        createNewTeamFunctions.CreaterPart(teamInfo);
//////        系统发送一个通知给管理员 并且 管理员发送一个team的message
////        createNewTeamFunctions.AdminPart(teamInfo);
//    }

    @Override
    public void SystemSendMessage(MessageContent messageContent) {
//        1、保存系统消息到user的消息表里
        messageMongoTemplate.save(messageContent,
                MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE_INFO);
//        2、修改user的消息统计数据userMessageInfo;
        normalMongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(messageContent.getUserId())),
                new Update().inc("nowSystemMessagesNumber", 1)
                        .push("userSystemMessageIdList", messageContent.getId())
                , MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE_INFO);
    }

    @Override
    public void ClubSendMessage(MessageContent messageContent) {
//        保存club消息到club 的消息表里
        messageMongoTemplate.save(messageContent,
                MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_CLUB_MESSAGE_INFO);
//        1、保存系统消息到user的消息表里
        messageMongoTemplate.save(messageContent,
                MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE_INFO);
//        2、修改user的消息统计数据userMessageInfo;
        normalMongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(messageContent.getUserId())),
                new Update().inc("nowClubMessagesNumber", 1)
                        .push("userClubMessageIdList", messageContent.getId())
                , MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE_INFO);

    }

    @Override
    public void FriendSendMessage(MessageContent messageContent) {

    }

    @Override
    public void UserSendMessage(MessageContent messageContent) {

    }

}
