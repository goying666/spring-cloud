package com.renchaigao.zujuba.messageserver.controller;

import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.messageserver.service.impl.MessageServiceImpl;
import com.renchaigao.zujuba.mongoDB.info.message.MessageContent;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping
public class MessageController {
    @Autowired
    MessageServiceImpl messageServiceImpl;

    @GetMapping(value = "/getteam")
    @ResponseBody
    public ResponseEntity GetMessageInfo(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "ownerId") String ownerId,
            @RequestParam(value = "messageClass") String messageClass,
            @RequestParam(value = "lastTime") long lastTime) {
        return messageServiceImpl.GetMessageInfo(userId, ownerId, messageClass, lastTime);
    }

    @GetMapping(value = "/getall")
    @ResponseBody
    public ResponseEntity GetMessageFragmentBean(
            @RequestParam(value = "userId") String userId) {
        return messageServiceImpl.GetMessageFragmentBean(userId);
    }

    @PostMapping(value = "/add", consumes = "application/json")
    @ResponseBody
    public ResponseEntity AddMessageInfo(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "messageClass") String messageClass,
            @RequestBody MessageContent messageContent) {
        return messageServiceImpl.AddMessageInfo(userId, messageClass, messageContent);
    }


//    @PostMapping(value = "/{firstStr}/{secondStr}/{thirdStr}/{fourthStr}", consumes = "application/json")
//    @ResponseBody
//    public ResponseEntity MessageControllerPostFuns(@PathVariable("firstStr") String fistStr,
//                                                    @PathVariable("secondStr") String secondStr,
//                                                    @PathVariable("thirdStr") String thirdStr,
//                                                    @PathVariable("fourthStr") String fourthStr,
//                                                    @RequestBody String jsonObjectString) {
//        switch (fistStr) {
//            case "add":
//                return messageServiceImpl.AddMessageInfo(secondStr, thirdStr, JSONObject.parseObject(jsonObjectString, MessageContent.class));
//            case "getall":
//                return messageServiceImpl.GetMessageFragmentBean(secondStr, jsonObjectString);
//        }
//        return new ResponseEntity(RespCode.WRONGIP, null);
//    }


//    @PostMapping(value = "/group", consumes = "application/json")
//    @ResponseBody
//    public ResponseEntity creatNewGroupMessageInfo(@RequestBody TeamInfo teamInfo) {
//        return messageServiceImpl.createNewGropuMessageTable(teamInfo);
//    }

//    @PostMapping(value = "/add/{userid}/{groupid}/{lasttime}", consumes = "application/json")
//    @ResponseBody
//    public ResponseEntity addNewMessageInfo(@PathVariable("userid") String userid,
//                                            @PathVariable("groupid") String groupid,
//                                            @PathVariable("lasttime") String lasttime,
//                                            @RequestBody MessageContent messageContent) {
//        return messageServiceImpl.AddMessageInfo(userid, groupid, lasttime, messageContent);
//    }

//    @GetMapping(value = "/get/{userid}/{groupid}/{lasttime}", consumes = "application/json")
//    @ResponseBody
//    public ResponseEntity getMessages(@PathVariable("userid") String userid,
//                                      @PathVariable("groupid") String groupid,
//                                      @PathVariable("lasttime") String lasttime) {
//        return messageServiceImpl.UserGetTeamMessageInfo(userid, groupid, lasttime);
//    }


//    @GetMapping(value = "/{firstStr}/{secondStr}/{thirdStr}/{fourthStr}")
//    @ResponseBody
//    public ResponseEntity MessageControllerGetFuns(@PathVariable("firstStr") String fistStr,
//                                                   @PathVariable("secondStr") String secondStr,
//                                                   @PathVariable("thirdStr") String thirdStr,
//                                                   @PathVariable("fourthStr") String fourthStr,
//                                                   @RequestParam(value = "lastTime") long lastTime) {
//        switch (fistStr) {
//            case "getteam":
//                return messageServiceImpl.GetMessageInfo(secondStr, thirdStr, fourthStr, lastTime);
//        }
//        return new ResponseEntity(RespCode.WRONGIP, null);
//    }

//    @PostMapping(value = "/{firstStr}/{secondStr}/{thirdStr}/{fourthStr}", consumes = "application/json")
//    @ResponseBody
//    public ResponseEntity MessageControllerPostFuns(@PathVariable("firstStr") String fistStr,
//                                                    @PathVariable("secondStr") String secondStr,
//                                                    @PathVariable("thirdStr") String thirdStr,
//                                                    @PathVariable("fourthStr") String fourthStr,
//                                                    @RequestBody String jsonObjectString) {
//        switch (fistStr) {
//            case "add":
//                return messageServiceImpl.AddMessageInfo(secondStr, thirdStr, JSONObject.parseObject(jsonObjectString, MessageContent.class));
//            case "getall":
//                return messageServiceImpl.GetMessageFragmentBean(secondStr, jsonObjectString);
//        }
//        return new ResponseEntity(RespCode.WRONGIP, null);
//    }

}
