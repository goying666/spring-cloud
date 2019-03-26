package com.renchaigao.zujuba.userserver.service.impl;

import com.renchaigao.zujuba.PageBean.CardUserTeamBean;
import com.renchaigao.zujuba.PageBean.MainMineFragmentBean;
import com.renchaigao.zujuba.PageBean.UserTeamActivityBean;
import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.AddressInfo;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;
import com.renchaigao.zujuba.mongoDB.info.user.UserGames;
import com.renchaigao.zujuba.mongoDB.info.user.UserInfo;
import com.renchaigao.zujuba.mongoDB.info.user.UserTeams;
import com.renchaigao.zujuba.userserver.service.MineService;
import com.renchaigao.zujuba.userserver.uti.mine.MinePlaceFunctions;

import normal.dateUse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import store.DistanceFunc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static normal.dateUse.CompareTwoStringDate;


@Service
public class MineServiceImpl implements MineService {

    private static Logger logger = Logger.getLogger(MineServiceImpl.class);

    @Autowired
    UserMapper userMapper;

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public ResponseEntity GetUserPlaceList(String userId) {
        MinePlaceFunctions minePlaceFunctions = new MinePlaceFunctions(userMapper, mongoTemplate);
        return new ResponseEntity(RespCode.SUCCESS, minePlaceFunctions.GetUserAllCreatePlace(userId));
    }

    @Override
    public ResponseEntity GetUserOnePlaceInfo(String userId, String placeId) {
        MinePlaceFunctions minePlaceFunctions = new MinePlaceFunctions(userMapper, mongoTemplate);
        return new ResponseEntity(RespCode.SUCCESS, minePlaceFunctions.GetUserOnePlaceInfo(userId, placeId));
    }

    @Override
    public ResponseEntity GetMineFragmentInfo(String userId) {
        MainMineFragmentBean mainMineFragmentBean = new MainMineFragmentBean();
        UserInfo userInfo = mongoTemplate.findById(userId, UserInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_INFO);
        if (userInfo != null) {
            mainMineFragmentBean.setMyScore(10);
            mainMineFragmentBean.setNickName(userInfo.getNickName());
            mainMineFragmentBean.setMeili(0);
            mainMineFragmentBean.setGender(userInfo.getGender());
            mainMineFragmentBean.setAgeLevel(userInfo.getAgeLevel());
            mainMineFragmentBean.setSignature(userInfo.getSignature());
            mainMineFragmentBean.setSumOfTeams(userInfo.getUserTeams().getAllTeamsList().size());
            mainMineFragmentBean.setSumOfCreateTeams(userInfo.getUserTeams().getCreateTeamsList().size());
            mainMineFragmentBean.setSumOfJoinTeam(userInfo.getUserTeams().getAllTeamsList().size());
            mainMineFragmentBean.setNickName(userInfo.getNickName());
            return new ResponseEntity(RespCode.SUCCESS, mainMineFragmentBean);
        } else
            return new ResponseEntity(RespCode.FAIL, null);
    }

    @Override
    public ResponseEntity GetMyTeamInfo(String userId) {
        UserTeamActivityBean userTeamActivityBean = new UserTeamActivityBean();
        List<String> allTeamsId = mongoTemplate.findById(userId, UserTeams.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_TEAMS).getAllTeamsList();
        AddressInfo userAddress = mongoTemplate.findById(userId, AddressInfo.class);
        try {
            if (allTeamsId.size() > 0) {
                for (String s : allTeamsId) {
                    CardUserTeamBean cardUserTeamBean = new CardUserTeamBean();
                    TeamInfo teamInfo = mongoTemplate.findById(s, TeamInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_TEAM_INFO);
                    if (teamInfo != null) {
                        if (teamInfo.getCreaterId().equals(userId)) {
                            cardUserTeamBean.setIsMyCreate(true);
                            userTeamActivityBean.setSumOfCreate(userTeamActivityBean.getSumOfCreate() + 1);
                        } else if (teamInfo.getAddressInfo().getOwnerId().equals(userId)) {
                            cardUserTeamBean.setIsMyAdmin(true);
                            userTeamActivityBean.setSumOfAdmin(userTeamActivityBean.getSumOfAdmin() + 1);
                        } else {
                            cardUserTeamBean.setIsMyJoin(true);
                            userTeamActivityBean.setSumOfJoin(userTeamActivityBean.getSumOfJoin() + 1);
                        }
                        cardUserTeamBean.setTeamName(teamInfo.getTeamName());
                        cardUserTeamBean.setTeamState(teamInfo.getState());
                        cardUserTeamBean.setPlaceName(teamInfo.getPlaceName());
                        Double userX = userAddress != null ? userAddress.getLatitude() : 0, userY = userAddress != null ? userAddress.getLongitude() : 0;
                        int distanc = DistanceFunc.getDistance(userX, userY,
                                teamInfo.getAddressInfo().getLatitude(),
                                teamInfo.getAddressInfo().getLongitude());
                        if (distanc > 1000) {
                            cardUserTeamBean.setDistance(Integer.toString(distanc / 1000) + "公里");
                        } else {
                            cardUserTeamBean.setDistance(distanc + "米");
                        }
                        cardUserTeamBean.setMainGame(teamInfo.getMainGame());
                        cardUserTeamBean.setPlayerNumber("人数范围:" + teamInfo.getPlayerNow() + "/" + teamInfo.getPlayerMin() + "~" + teamInfo.getPlayerMax());

//                    1、获取今日日期，比对，比较时间差
                        if (CompareTwoStringDate(dateUse.getTodayDate(), teamInfo.getStartDate()) == 0) {
                            cardUserTeamBean.setIsToday(true);
                            cardUserTeamBean.setStartDay("今日");
                        } else if (CompareTwoStringDate(dateUse.getTodayDate(), teamInfo.getStartDate()) == 1) {
                            cardUserTeamBean.setIsTomorrow(true);
                            cardUserTeamBean.setStartDay("明日");
                        } else if (CompareTwoStringDate(dateUse.getTodayDate(), teamInfo.getStartDate()) < 0) {
                            cardUserTeamBean.setIsFinish(true);
                            cardUserTeamBean.setStartDay("结束");
                        }
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(formatter.parse(teamInfo.getStartDate()));
                        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
                                || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                            cardUserTeamBean.setIsWeekend(true);
                        }
                        cardUserTeamBean.setStartTime(teamInfo.getStartAllTime());
                        userTeamActivityBean.getCardList().add(cardUserTeamBean);
                    }
                }
                return new ResponseEntity(RespCode.SUCCESS, userTeamActivityBean);
            } else
                return new ResponseEntity(RespCode.FAIL, null);
        } catch (Exception e) {
            return new ResponseEntity(RespCode.EXCEPTION, e);
        }
    }

    @Override
    public ResponseEntity GetUserMineInfo(String userId) {
        try {
            UserTeams userTeams = mongoTemplate.findById(userId, UserTeams.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_TEAMS);
            Integer allTeams = 0;
            if (userTeams != null)
                allTeams = userTeams.getAllTeamsList() == null ? 0 : userTeams.getAllTeamsList().size();
            UserGames userGames = mongoTemplate.findById(userId, UserGames.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_GAMES);
            Integer allGames = 0;
            if (userGames != null)
                allGames = userTeams.getAllTeamsList() == null ? 0 : userTeams.getAllTeamsList().size();

            return new ResponseEntity(RespCode.USER_MINE_INFO_SUCCESS, null);
        } catch (Exception e) {
            return new ResponseEntity(RespCode.EXCEPTION, e);

        }
    }
}
