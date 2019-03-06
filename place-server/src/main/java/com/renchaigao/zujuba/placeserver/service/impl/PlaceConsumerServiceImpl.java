package com.renchaigao.zujuba.placeserver.service.impl;

import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;
import com.renchaigao.zujuba.placeserver.service.PlaceConsumerService;
import javafx.scene.shape.Circle;
import normal.dateUse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import static com.renchaigao.zujuba.PropertiesConfig.ConstantManagement.*;

@Service
public class PlaceConsumerServiceImpl implements PlaceConsumerService {

    private static Logger logger = Logger.getLogger(PlaceConsumerServiceImpl.class);

    @Autowired
    UserMapper userMapper;

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public void CreateNewTeam(TeamInfo teamInfo) {
//        更新场地管理员的
//        根据组局时间进行分类
        switch (teamInfo.getAddressInfo().getAddressClass()) {
            case ADDRESS_CLASS_USER:
                break;
            case ADDRESS_CLASS_STORE:
                Update update = new Update();
                mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(teamInfo.getAddressInfo().getId())),
                        update.inc("allTeamsTimes", 1)
//                        .inc("allUsersNum", 1)
                                .set("upTime", dateUse.GetStringDateNow())
                                .push("storeAllTeamInfoArrayList", teamInfo)
                        , MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_TEAM_INFO);
                logger.info("CreateNewTeam : " + teamInfo.getId());
                break;
            case ADDRESS_CLASS_OPEN:
                break;
            case ADDRESS_CLASS_HOME:
                break;
            case ADDRESS_CLASS_SCHOOL:
                break;

        }


    }
}
