package com.renchaigao.zujuba.playerserver.controller;

import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;
import com.renchaigao.zujuba.playerserver.service.impl.PlayerConsumerServiceImpl;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.RestController;

import static com.renchaigao.zujuba.PropertiesConfig.KafkaTopicConstant.CREATE_NEW_TEAM;

@RestController
public class PlayerConsumerController {

    @Autowired
    PlayerConsumerServiceImpl playerConsumerService;

    @KafkaListener(topics = CREATE_NEW_TEAM)
    public void CreateNewTeam(ConsumerRecord<?, String> record) {
        TeamInfo teamInfo = JSONObject.parseObject(record.value(),TeamInfo.class);
        playerConsumerService.CreateNewTeam(teamInfo);
    }
}
