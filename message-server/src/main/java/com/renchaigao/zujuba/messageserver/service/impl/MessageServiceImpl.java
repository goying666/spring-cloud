package com.renchaigao.zujuba.messageserver.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.PageBean.CardMessageFragmentTipBean;
import com.renchaigao.zujuba.PageBean.MessageFragmentCardBean;
import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.messageserver.function.AddMessageInfoFunctions;
import com.renchaigao.zujuba.messageserver.function.GetMessageFragmentBeansFunctions;
import com.renchaigao.zujuba.messageserver.function.GetMessageInfoFunctions;
import com.renchaigao.zujuba.messageserver.service.MessageService;
import com.renchaigao.zujuba.mongoDB.info.GroupMessages;
import com.renchaigao.zujuba.mongoDB.info.message.MessageContent;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;
import com.renchaigao.zujuba.mongoDB.info.user.UserMessagesInfo;
import com.renchaigao.zujuba.mongoDB.info.user.UserTeams;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.renchaigao.zujuba.PropertiesConfig.ConstantManagement.*;

@Service
public class MessageServiceImpl implements MessageService {

    private static Logger logger = Logger.getLogger(MessageServiceImpl.class);

    @Resource(name = "messageMongoTemplate")
    MongoTemplate messageMongoTemplate;

    @Resource(name = "normalMongoTemplate")
    MongoTemplate normalMongoTemplate;

    @Autowired
    UserMapper userMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private StringRedisTemplate redisClient;

    @Override
    public ResponseEntity GetMessageFragmentBean(String userId) {
        GetMessageFragmentBeansFunctions getMessageFragmentBeansFunctions = new GetMessageFragmentBeansFunctions(normalMongoTemplate, messageMongoTemplate);
        return getMessageFragmentBeansFunctions.GetMessageFragmentBean(userId);
    }

    @Override
    public ResponseEntity AddMessageInfo(String userid, String messageClass, MessageContent messageContent) {
        AddMessageInfoFunctions addMessageInfoFunctions = new AddMessageInfoFunctions(userMapper, normalMongoTemplate, messageMongoTemplate, kafkaTemplate);
        switch (messageClass) {
            case TEAM_SEND_MESSAGE:
//                用户在组内发送的消息
                addMessageInfoFunctions.AddTeamMessage(messageContent);
                return GetMessageInfo(userid, messageContent.getTeamId(), TEAM_SEND_MESSAGE, messageContent.getSendTime());
            case FRIEND_SEND_MESSAGE:
//                用户发送给朋友的消息——待开发
                break;
            case SYSTEM_SEND_MESSAGE:
//                用户发送给系统的消息——待开发
                break;
            case USER_SEND_MESSAGE:
//                用户发送给其他用户的消息——待开发
                break;
            case CLUB_SEND_MESSAGE:

                break;
        }
        return new ResponseEntity(RespCode.WARN, null);
    }

    @Override
    public ResponseEntity GetMessageInfo(String userid, String ownerId, String messageClass, long lastTime) {
        GetMessageInfoFunctions getMessageInfoFunctions = new GetMessageInfoFunctions(userMapper, normalMongoTemplate, messageMongoTemplate, kafkaTemplate);
        switch (messageClass) {
            case TEAM_SEND_MESSAGE:
//                用户获取组局内的消息
                return getMessageInfoFunctions.GetTeamMessageInfo(userid, ownerId, lastTime);
            case FRIEND_SEND_MESSAGE:
//                用户获取好友的消息——待开发
                break;
            case SYSTEM_SEND_MESSAGE:
//                用户获取系统的消息——待开发
                break;
            case USER_SEND_MESSAGE:
//                用户获取其他用户的消息——待开发
                break;
        }
        return null;
    }


    @Override
    public ResponseEntity deleteMessageInfo(String userId, String senderId, String groupId, String messageId) {
        return null;
    }

    @Override
    public ResponseEntity recivedOK(String userId, String senderId, String groupId, String messageId) {
        return null;
    }
}
