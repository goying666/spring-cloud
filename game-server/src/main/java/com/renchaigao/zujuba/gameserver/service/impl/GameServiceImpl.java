package com.renchaigao.zujuba.gameserver.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.PageBean.CardStoreClubFragmentBean;
import com.renchaigao.zujuba.PageBean.CardStoreTeamFragmentBean;
import com.renchaigao.zujuba.PageBean.StoreManagerBasicFragmentBean;
import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.gameserver.service.GameService;
import com.renchaigao.zujuba.gameserver.socketserver.NettyServer;
import com.renchaigao.zujuba.gameserver.socketserver.ServerHandler;
import com.renchaigao.zujuba.gameserver.socketserver.SocketUsers;
import com.renchaigao.zujuba.mongoDB.info.club.ClubInfo;
import com.renchaigao.zujuba.mongoDB.info.store.StoreInfo;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;
import io.netty.channel.Channel;
import normal.dateUse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import static com.renchaigao.zujuba.PropertiesConfig.ConstantManagement.*;
import static normal.dateUse.CompareTwoStringDate;


@Service
public class GameServiceImpl implements GameService {

    private static Logger logger = Logger.getLogger(GameServiceImpl.class);

    @Autowired
    UserMapper userMapper;

    @Autowired
    MongoTemplate mongoTemplate;


    @Override
    public ResponseEntity GetGameInfo() {

        ConcurrentMap<String, Channel> USERS = SocketUsers.getUSERS();
        if(USERS.containsKey("123")){
            USERS.get("123").writeAndFlush("321");
        }

        return new ResponseEntity(RespCode.SUCCESS,JSONObject.toJSONString(USERS));
    }
}
