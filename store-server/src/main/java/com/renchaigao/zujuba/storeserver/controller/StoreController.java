package com.renchaigao.zujuba.storeserver.controller;

import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.PageBean.StoreManagerBasicFragmentBean;
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

    @GetMapping(value = "/getnear")
    @ResponseBody
    public ResponseEntity GetNearStoreList(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "token") String token) {
        if (!userMapper.selectByPrimaryKey(userId).getToken().equals(token))
            return new ResponseEntity(RespCode.TOKENWRONG, null);
        else
            return storeServiceImpl.GetNearPlace(userId);
    }

    @PostMapping(value = "/add", consumes = "multipart/form-data")
    @ResponseBody
    public ResponseEntity AddStore(
            @RequestParam("userId") String userId,
            @RequestParam("storeId") String storeId,
            @RequestParam(value = "token") String token,
            @RequestParam("json") String jsonObjectString,
            @RequestParam("photo") MultipartFile[] photos) {
        if (!userMapper.selectByPrimaryKey(userId).getToken().equals(token))
            return new ResponseEntity(RespCode.TOKENWRONG, null);
        else
            return storeServiceImpl.AddStore(userId, storeId, jsonObjectString, photos);
    }

    @PostMapping(value = "/manager/update/{firstStr}", consumes = "multipart/form-data")
    @ResponseBody
    public ResponseEntity ManagerUpdateStoreInfo(
            @PathVariable(value = "firstStr") String firstStr,
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "storeId") String storeId,
            @RequestParam(value = "token") String token,
            @RequestParam("json") String jsonObjectString) {
        if (!userMapper.selectByPrimaryKey(userId).getToken().equals(token))
            return new ResponseEntity(RespCode.TOKENWRONG, null);
        else {
            switch (firstStr) {
                case "basic":
                    return storeServiceImpl.ManagerUpdateOneStoreInfo(userId, storeId, JSONObject.parseObject(jsonObjectString, StoreManagerBasicFragmentBean.class));
            }
        }
        return new ResponseEntity(RespCode.FAIL, null);
    }

    @GetMapping(value = "/getone/{where}")
    @ResponseBody
    public ResponseEntity GetOneStoreInfo(
            @PathVariable(value = "where") String whichFunc,
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "storeId") String storeId,
            @RequestParam(value = "token") String token,
            @RequestParam(value = "lastTime") long lastTime) {
        String news = redisTemplate.opsForValue().get("getMessageInfo" + storeId);
        if (!userMapper.selectByPrimaryKey(userId).getToken().equals(token))
            return new ResponseEntity(RespCode.TOKENWRONG, null);
        else if (news != null && news.equals(lastTime))
            return new ResponseEntity(RespCode.WARN, null);
        else {
            switch (whichFunc) {
                case "main":
                    return storeServiceImpl.GetOneStoreInfo(userId, storeId);
                case "team":
                    return storeServiceImpl.GetOneStoreTeamInfo(userId, storeId);
                case "club":
                    return storeServiceImpl.GetOneStoreClubInfo(userId, storeId);
                case "comment":
                    return storeServiceImpl.GetOneStoreCommentInfo(userId, storeId);
                case "game":
                    return storeServiceImpl.GetOneStoreGameInfo(userId, storeId);
            }
        }
        return new ResponseEntity(RespCode.STORE_INFO_GET_FAIL, null);
    }

    @GetMapping(value = "/manager/get/{firstStr}")
    @ResponseBody
    public ResponseEntity ManagerGetOneStoreInfo(
            @PathVariable(value = "firstStr") String firstStr,
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "storeId") String storeId,
            @RequestParam(value = "token") String token) {
        if (!userMapper.selectByPrimaryKey(userId).getToken().equals(token))
            return new ResponseEntity(RespCode.TOKENWRONG, null);
        else {
            switch (firstStr) {
                case "basic":
                    return storeServiceImpl.ManagerGetOneStoreInfo(userId, storeId);
            }
        }
        return new ResponseEntity(RespCode.FAIL, null);
    }

}

