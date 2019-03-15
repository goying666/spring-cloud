package com.renchaigao.zujuba.clubserver.controller;

import com.renchaigao.zujuba.clubserver.service.impl.ClubServiceImpl;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.club.ClubInfo;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Log4j
@Controller
@RequestMapping()
public class ClubController {
    @Autowired
    ClubServiceImpl clubServiceImpl;

    @Autowired
    UserMapper userMapper;

    @PostMapping(value = "/create", consumes = "application/json")
    @ResponseBody
    public ResponseEntity CreateNewClub(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "placeId") String placeId,
            @RequestBody ClubInfo clubInfo) {
        return clubServiceImpl.CreateNewClub(userId, placeId, clubInfo);
    }

    @GetMapping(value = "/getall")
    @ResponseBody
    public ResponseEntity GetUserAllClub(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "token") String token) {
        return clubServiceImpl.GetUserAllClub(userId, token);
    }

    @GetMapping(value = "/getone")
    @ResponseBody
    public ResponseEntity GetOneClub(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "token") String token,
            @RequestParam(value = "clubId") String clubId) {

        if (userMapper.selectByPrimaryKey(userId).getToken().equals(token)) {
            return clubServiceImpl.GetOneClub(userId, clubId);
        } else return new ResponseEntity(RespCode.TOKENWRONG, null);
    }


}
