package com.renchaigao.zujuba.placeserver.controller;

import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;
import com.renchaigao.zujuba.placeserver.service.impl.PlaceConsumerServiceImpl;
import com.renchaigao.zujuba.placeserver.service.impl.PlaceServiceImpl;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.RestController;

import static com.renchaigao.zujuba.PropertiesConfig.KafkaTopicConstant.*;

@RestController
public class PlaceConsumerController {

    @Autowired
    PlaceConsumerServiceImpl placeConsumerService;

    @KafkaListener(topics = CREATE_NEW_TEAM)
    public void CreateNewTeam(ConsumerRecord<?, String> record) {
        TeamInfo teamInfo = JSONObject.parseObject(record.value(),TeamInfo.class);
        placeConsumerService.CreateNewTeam(teamInfo);
    }
}
