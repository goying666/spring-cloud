package com.renchaigao.zujuba.gameserver.controller;

import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.gameserver.service.impl.GameServiceImpl;
import com.renchaigao.zujuba.gameserver.socketserver.NettyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping()
public class GameController {

    @Autowired
    GameServiceImpl gameServiceImpl;

    @Autowired
    UserMapper userMapper;

    @Autowired
    StringRedisTemplate redisTemplate;


    @GetMapping(value = "/get")
    @ResponseBody
    public ResponseEntity GetNearStoreList() {

        return  gameServiceImpl.GetGameInfo();
    }


}

