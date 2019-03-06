package com.renchaigao.zujuba.clubserver.controller;

import com.renchaigao.zujuba.clubserver.service.impl.ClubServiceImpl;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.club.ClubInfo;
import com.renchaigao.zujuba.teamserver.service.impl.TeamServiceImpl;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Log4j
@Controller
@RequestMapping()
public class ClubController {
    @Autowired
    ClubServiceImpl clubServiceImpl;


//    @GetMapping(value = "/getall")
//    @ResponseBody
//    public ResponseEntity GetMessageFragmentBean(
//            @RequestParam(value = "userId") String userId) {
//        return messageServiceImpl.GetMessageFragmentBean(userId);
//    }

    @PostMapping(value = "/create", consumes = "application/json")
    @ResponseBody
    public ResponseEntity CreateNewClub(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "placeId") String placeId,
            @RequestBody ClubInfo clubInfo) {
        return clubServiceImpl.CreateNewClub(userId, placeId, clubInfo);
    }

}
