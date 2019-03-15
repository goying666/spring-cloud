package com.renchaigao.zujuba.clubserver.service.impl;

import com.renchaigao.zujuba.PageBean.CardClubFragmentTipBean;
import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.clubserver.function.CreateNewClubInfoFunctions;
import com.renchaigao.zujuba.clubserver.service.ClubService;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.dao.mapper.UserOpenInfoMapper;
import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.club.ClubInfo;
import com.renchaigao.zujuba.mongoDB.info.club.ClubUserInfo;
import com.renchaigao.zujuba.mongoDB.info.user.UserClubInfo;
import com.renchaigao.zujuba.mongoDB.info.user.UserInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;

@Service
public class ClubServiceImpl implements ClubService {

    private static Logger logger = Logger.getLogger(ClubServiceImpl.class);


    @Resource(name = "messageMongoTemplate")
    MongoTemplate messageMongoTemplate;

    @Resource(name = "normalMongoTemplate")
    MongoTemplate normalMongoTemplate;

    @Autowired
    UserOpenInfoMapper userOpenInfoMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public ResponseEntity CreateNewClub(String userId, String placeId, ClubInfo clubInfo) {
//        将信息写入redis
//        TaskWorkFlow taskWorkFlow = new TaskWorkFlow();
//        String taskStr = JSONObject.toJSONString(taskWorkFlow);
//        stringRedisTemplate.opsForValue().set(TASK_CLUB_CREATE_NEW_CLUB + taskWorkFlow.getId(), taskStr);
//        通过kafka启动一个新的Task
//        kafkaTemplate.send(TASK_CLUB_CREATE_NEW_CLUB, taskStr);
//        玩成自身TaskFlow内容
//        修改对应Flow内容；
        CreateNewClubInfoFunctions createNewClubInfoFunctions = new CreateNewClubInfoFunctions(userMapper, normalMongoTemplate,
                messageMongoTemplate, kafkaTemplate);
        try {

            //        检查参数完整性：id重复、名称重复；
            createNewClubInfoFunctions.cheackClubInfo(clubInfo);
            if (!createNewClubInfoFunctions.placeLimitCheck(clubInfo)) {
                return new ResponseEntity(RespCode.CLUB_HAD_BEEN_CREATE, clubInfo);
            }
            createNewClubInfoFunctions.createClub(clubInfo);
//        大本营选择的场所限制————待开发
//        创建club
            normalMongoTemplate.save(clubInfo, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_CLUB_INFO);
            return new ResponseEntity(RespCode.CLUB_CREATE_SUCCESS, clubInfo);

        } catch (Exception e) {
            return new ResponseEntity(RespCode.CLUB_CREATE_FAIL, null);

        }
    }

    @Override
    public ResponseEntity GetUserAllClub(String userId, String tocken) {
//        获取用户的club信息
        UserClubInfo userClubInfo = normalMongoTemplate.findById(userId, UserClubInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_CLUB_INFO);
        ArrayList<CardClubFragmentTipBean> clubFragmentTipBeans = new ArrayList<>();
        if (userClubInfo != null && userClubInfo.getClubIdList() != null) {
            for (String p : userClubInfo.getClubIdList()) {
                ClubInfo clubInfo = normalMongoTemplate.findById(p, ClubInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_CLUB_INFO);
                if (clubInfo != null) {
                    CardClubFragmentTipBean cardClubFragmentTipBean = new CardClubFragmentTipBean();
                    cardClubFragmentTipBean.setClubId(clubInfo.getId());
                    cardClubFragmentTipBean.setClubName(clubInfo.getClubName());
                    cardClubFragmentTipBean.setAllPeopleNum(clubInfo.getAllPeopleNum().toString());
                    if (clubInfo.getCreaterId().equals(userId))
                        cardClubFragmentTipBean.setWhoAmI("管理员");
                    else
                        cardClubFragmentTipBean.setWhoAmI("成员");
                    cardClubFragmentTipBean.setPlaceName(clubInfo.getPlaceName());
                    clubFragmentTipBeans.add(cardClubFragmentTipBean);
                }
            }
        }
        if (clubFragmentTipBeans.size() > 0) {
            return new ResponseEntity(RespCode.CLUB_UPDATE_SUCCESS, clubFragmentTipBeans);
        }

        return new ResponseEntity(RespCode.CLUB_UPDATE_FAIL);
    }

    @Override
    public ResponseEntity GetOneClub(String userId, String clubId) {
//        鉴别用户身份，更具不同身份传递不同的Bean
        UserInfo userInfo = normalMongoTemplate.findById(userId, UserInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_INFO);
        ClubInfo clubInfo = normalMongoTemplate.findById(clubId, ClubInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_CLUB_INFO);
//        ClubUserInfo
        if (clubInfo == null) {
            return new ResponseEntity(RespCode.MESSAGE_USER_GET_CLUB_ZERO, null);
        }
        return new ResponseEntity(RespCode.MESSAGE_USER_GET_CLUB_SUCCESS, clubInfo);
    }


}
