package com.renchaigao.zujuba.teamserver.controller;

import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.message.MessageContent;
import com.renchaigao.zujuba.teamserver.service.impl.TeamServiceImpl;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Log4j
@Controller
@RequestMapping()
@RestController
public class TeamController {

    @Autowired
    TeamServiceImpl teamServiceImpl;

    @Autowired
    UserMapper userMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping(value = "/{firstStr}/{secondStr}/{thirdStr}/{fourthStr}", consumes = "multipart/form-data")
    @ResponseBody
    public ResponseEntity TeamControllerFuns(@PathVariable("firstStr") String fistStr,
                                             @PathVariable("secondStr") String secondStr,
                                             @PathVariable("thirdStr") String thirdStr,
                                             @PathVariable("fourthStr") String fourthStr,
                                             @RequestParam("json") String jsonObjectString) {
        switch (fistStr) {
            case "create":
                return teamServiceImpl.CreateNewTeam( secondStr, thirdStr, jsonObjectString);
            case "getnear":
                return teamServiceImpl.GetNearTeams(secondStr, thirdStr, jsonObjectString);
            case "getone":
                return teamServiceImpl.FindOneTeam( secondStr, thirdStr, jsonObjectString);
//            case "update":
//                return teamServiceImpl.UpdateTeam(userId, secondStr, teamId, jsonObjectString);
//            case "quit":
//                return teamServiceImpl.QuitTeam(userId, secondStr, teamId, jsonObjectString);
//            case "delete":
//                return teamServiceImpl.DeleteTeam(userId, secondStr, teamId, jsonObjectString);
//            case "report":
//                return teamServiceImpl.ReportTeam(userId, secondStr, teamId, jsonObjectString);
//            case "getcon":
//                return teamServiceImpl.GetNearTeams(userId, secondStr, teamId, jsonObjectString);
//            case "deletemine":
//                return teamServiceImpl.DeleteMyTeams(userId, secondStr, teamId, jsonObjectString);
        }
        return new ResponseEntity(RespCode.WRONGIP,null);
    }


    @PostMapping(value = "/join")
    @ResponseBody
    public ResponseEntity AddMessageInfo(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "token") String token,
            @RequestParam(value = "teamId") String teamId) {
        if(userMapper.selectByPrimaryKey(userId).getToken().equals(token)){
            return teamServiceImpl.JoinTeam( userId,teamId);
        }else return new ResponseEntity(RespCode.TOKENWRONG,null);
    }

//    private Boolean UserAuthenticationFun(String userId,String userToken){
//        User user = userMapper.selectByPrimaryKey(userId);
//        if(user.getToken().equals(userToken))
//            return true;
//        else return false;
//    }

}
