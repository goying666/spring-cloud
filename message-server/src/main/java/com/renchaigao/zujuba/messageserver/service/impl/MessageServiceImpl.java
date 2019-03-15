package com.renchaigao.zujuba.messageserver.service.impl;

import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.messageserver.function.AddMessageInfoFunctions;
import com.renchaigao.zujuba.messageserver.function.GetMessageFragmentBeansFunctions;
import com.renchaigao.zujuba.messageserver.function.GetMessageInfoFunctions;
import com.renchaigao.zujuba.messageserver.service.MessageService;
import com.renchaigao.zujuba.mongoDB.info.message.MessageContent;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
    public ResponseEntity AddMessageInfo( MessageContent messageContent) {
        AddMessageInfoFunctions addMessageInfoFunctions = new AddMessageInfoFunctions(userMapper, normalMongoTemplate, messageMongoTemplate, kafkaTemplate);
        switch (messageContent.getMessageClass()) {
            case TEAM_SEND_MESSAGE:
//                用户在组内发送的消息
                return addMessageInfoFunctions.AddTeamMessage(messageContent);
            case FRIEND_SEND_MESSAGE:
                return addMessageInfoFunctions.AddFriendMessage(messageContent);
            case SYSTEM_SEND_MESSAGE:
                return addMessageInfoFunctions.AddSystemMessage(messageContent);
            case CLUB_SEND_MESSAGE:
                return addMessageInfoFunctions.AddClubMessage(messageContent);
        }
        return new ResponseEntity(RespCode.WARN, null);
    }

    @Override
    public ResponseEntity GetMessageInfo(String userid, String ownerId, String messageClass, long lastTime) {
        GetMessageInfoFunctions getMessageInfoFunctions = new GetMessageInfoFunctions(userMapper, normalMongoTemplate, messageMongoTemplate, kafkaTemplate);
        switch (messageClass) {
            case TEAM_SEND_MESSAGE:
                return getMessageInfoFunctions.GetTeamMessageInfo(userid, ownerId);
            case FRIEND_SEND_MESSAGE:
                return getMessageInfoFunctions.GetFriendMessageInfo(userid, ownerId);
            case SYSTEM_SEND_MESSAGE:
                return getMessageInfoFunctions.GetSystemMessageInfo(userid);
            case CLUB_SEND_MESSAGE:
                return getMessageInfoFunctions.GetClubMessageInfo(userid, ownerId);
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
