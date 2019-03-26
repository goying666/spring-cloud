package com.renchaigao.zujuba.userserver.controller;

import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.userserver.service.impl.MineServiceImpl;
import com.renchaigao.zujuba.userserver.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping()
public class MineController {


    @Autowired
    MineServiceImpl mineServiceImpl;
    @Autowired
    UserMapper userMapper;


    /*
     * 获取fragment中 mine的Bean信息
     */
    @GetMapping(value = "/getmine")
    @ResponseBody
    public ResponseEntity GetUserMineInfo(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "token") String token) {
        if (!userMapper.selectByPrimaryKey(userId).getToken().equals(token))
            return new ResponseEntity(RespCode.TOKENWRONG, null);
        else
            return mineServiceImpl.GetUserMineInfo(userId);
    }


    /*
     * 获取用户所有创建的场地信息（mine界面）
     */
    @GetMapping(value = "/placeallcreate")
    @ResponseBody
    public ResponseEntity GetUserPlaceList(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "token") String token) {
        if (!userMapper.selectByPrimaryKey(userId).getToken().equals(token))
            return new ResponseEntity(RespCode.TOKENWRONG, null);
        else
            return mineServiceImpl.GetUserPlaceList(userId);
    }

    /*
     * 获取用户的一个场地信息（mine界面）
     */
    @GetMapping(value = "/oneplace")
    @ResponseBody
    public ResponseEntity GetUserOnePlaceInfo(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "placeId") String placeId,
            @RequestParam(value = "token") String token) {
        if (!userMapper.selectByPrimaryKey(userId).getToken().equals(token))
            return new ResponseEntity(RespCode.TOKENWRONG, null);
        else
            return mineServiceImpl.GetUserOnePlaceInfo(userId, placeId);
    }
    /*
     * 获取用户的统筹信息（main界面）
     */
    @GetMapping(value = "/getminefrag")
    @ResponseBody
    public ResponseEntity GetMineFragmentInfo(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "token") String token) {
        if (!userMapper.selectByPrimaryKey(userId).getToken().equals(token))
            return new ResponseEntity(RespCode.TOKENWRONG, null);
        else
            return mineServiceImpl.GetMineFragmentInfo(userId);
    }
    /*
     * 获取用户的统筹信息（main界面）
     */
    @GetMapping(value = "/getteam")
    @ResponseBody
    public ResponseEntity GetMyTeamInfo(
            @RequestParam(value = "userId") String userId,
            @RequestParam(value = "token") String token) {
        if (!userMapper.selectByPrimaryKey(userId).getToken().equals(token))
            return new ResponseEntity(RespCode.TOKENWRONG, null);
        else
            return mineServiceImpl.GetMyTeamInfo(userId);
    }

}
