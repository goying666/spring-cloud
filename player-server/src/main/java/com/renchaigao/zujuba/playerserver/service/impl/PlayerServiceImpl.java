package com.renchaigao.zujuba.playerserver.service.impl;

import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.playerserver.service.PlayerService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PlayerServiceImpl implements PlayerService {

    private static Logger logger = Logger.getLogger(PlayerServiceImpl.class);


    @Autowired
    UserMapper userMapper;

    @Autowired
    MongoTemplate mongoTemplate;


    @Override
    public ResponseEntity PlayerJoin(String userId, String teamId, String jsonObjectString, MultipartFile[] photos) {
        return null;
    }

    @Override
    public ResponseEntity PlayerUpdate(String userId, String teamId, String jsonObjectString, MultipartFile[] photos) {
        return null;
    }

    @Override
    public ResponseEntity PlayerQuit(String userId, String teamId, String jsonObjectString, MultipartFile[] photos) {
        return null;
    }

    @Override
    public ResponseEntity KickPlayer(String userId, String teamId, String jsonObjectString, MultipartFile[] photos) {
        return null;
    }

    @Override
    public ResponseEntity GetOnePlayerInfo(String userId, String teamId, String jsonObjectString) {
        return null;
    }

    @Override
    public ResponseEntity GetPlayerInfoList(String userId, String teamId, String jsonObjectString) {
        return null;
    }
}
