package com.renchaigao.zujuba.messageserver.controller;

import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.messageserver.service.impl.MessageConsumerServiceImpl;
import com.renchaigao.zujuba.messageserver.service.impl.MessageServiceImpl;
import com.renchaigao.zujuba.mongoDB.info.message.MessageContent;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.RestController;

import static com.renchaigao.zujuba.PropertiesConfig.ConstantManagement.*;
import static com.renchaigao.zujuba.PropertiesConfig.KafkaTopicConstant.CREATE_NEW_TEAM;

@RestController
public class MessageConsumerController {

    @Autowired
    MessageConsumerServiceImpl messageConsumerServiceImpl;

    @Autowired
    MessageServiceImpl messageServiceImpl;

//    @KafkaListener(topics = CREATE_NEW_TEAM)
//    public void CreateNewTeam(ConsumerRecord<?, String> record) {
//        TeamInfo teamInfo = JSONObject.parseObject(record.value(),TeamInfo.class);
//        messageConsumerServiceImpl.CreateNewTeam(teamInfo);
//    }

    @KafkaListener(topics = SYSTEM_SEND_MESSAGE)
    public void SystemSendMessage(ConsumerRecord<?, String> record) {
        MessageContent messageContent = JSONObject.parseObject(record.value(),MessageContent.class);
        messageConsumerServiceImpl.SystemSendMessage(messageContent);
    }
    @KafkaListener(topics = TEAM_SEND_MESSAGE)
    public void TEAM_SEND_MESSAGE(ConsumerRecord<?, String> record) {
        MessageContent messageContent = JSONObject.parseObject(record.value(),MessageContent.class);
        messageServiceImpl.AddMessageInfo(messageContent.getUserId(), messageContent.getMessageClass(), messageContent);
    }
    @KafkaListener(topics = USER_SEND_MESSAGE)
    public void UserSendMessage(ConsumerRecord<?, String> record) {
        MessageContent messageContent = JSONObject.parseObject(record.value(),MessageContent.class);
        messageConsumerServiceImpl.UserSendMessage(messageContent);
    }
    @KafkaListener(topics = FRIEND_SEND_MESSAGE)
    public void FRIEND_SEND_MESSAGE(ConsumerRecord<?, String> record) {
        MessageContent messageContent = JSONObject.parseObject(record.value(),MessageContent.class);
        messageConsumerServiceImpl.FriendSendMessage(messageContent);
    }
    @KafkaListener(topics = CLUB_SEND_MESSAGE)
    public void CLUB_SEND_MESSAGE(ConsumerRecord<?, String> record) {
        MessageContent messageContent = JSONObject.parseObject(record.value(),MessageContent.class);
        messageConsumerServiceImpl.ClubSendMessage(messageContent);
    }

}
