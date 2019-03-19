package com.renchaigao.zujuba.storeserver.controller;

import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.storeserver.service.impl.StoreServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping()
public class StoreController {

    @Autowired
    StoreServiceImpl storeServiceImpl;

    @Autowired
    UserMapper userMapper;

    @Autowired
    StringRedisTemplate redisTemplate;

    @GetMapping(value = "/getone")
    @ResponseBody
    public ResponseEntity GetOneStoreInfo(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "storeId") String storeId,
            @RequestParam(value = "token") String token,
            @RequestParam(value = "lastTime") long lastTime) {
        String news = redisTemplate.opsForValue().get("getMessageInfo" + storeId);
        if (!userMapper.selectByPrimaryKey(userId).getToken().equals(token))
            return new ResponseEntity(RespCode.TOKENWRONG, null);
        else if (news != null && news.equals(lastTime))
            return new ResponseEntity(RespCode.WARN, null);
        else
            return storeServiceImpl.GetOneStoreInfo(userId, storeId);
    }

    @GetMapping(value = "/{firstStr}/{secondStr}/{thirdStr}/{fourthStr}")
    @ResponseBody
    public ResponseEntity PlaceAboutUserControllerFuns(@PathVariable("firstStr") String fistStr,
                                                       @PathVariable("secondStr") String secondStr,
                                                       @PathVariable("thirdStr") String thirdStr,
                                                       @PathVariable("fourthStr") String fourthStr) {
        switch (fistStr) {
            case "user":
                switch (secondStr) {
                    case "allcreate":
                        return storeServiceImpl.GetUserPlaceList(thirdStr, fourthStr);
                    case "one":
                        return storeServiceImpl.GetUserOnePlaceInfo(thirdStr, fourthStr);
                }
                break;
        }
        return new ResponseEntity(RespCode.WRONGIP, null);
    }


    @PostMapping(value = "/{firstStr}/{secondStr}/{thirdStr}/{fourthStr}", consumes = "multipart/form-data")
    @ResponseBody
    public ResponseEntity PlaceControllerFuns(@PathVariable("firstStr") String fistStr,
                                              @PathVariable("secondStr") String secondStr,
                                              @PathVariable("thirdStr") String thirdStr,
                                              @PathVariable("fourthStr") String fourthStr,
                                              @RequestParam("json") String jsonObjectString,
                                              @RequestParam("photo") MultipartFile[] photos) {
        switch (fistStr) {
            case "store":
                switch (secondStr) {
                    case "join":
                        return storeServiceImpl.JoinPlace(thirdStr, fourthStr, jsonObjectString, photos);
                    case "getnear":
                        return storeServiceImpl.GetNearPlace(thirdStr, fourthStr, jsonObjectString);
                }
                break;
            case "user":
                switch (secondStr) {
                    case "allcreate":
                        return storeServiceImpl.GetUserPlaceList(thirdStr, fourthStr);
                    case "one":
                        return storeServiceImpl.GetUserOnePlaceInfo(thirdStr, fourthStr);
                }
                break;
        }
        return new ResponseEntity(RespCode.WRONGIP, null);
    }
}

