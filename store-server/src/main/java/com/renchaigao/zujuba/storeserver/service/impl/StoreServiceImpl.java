package com.renchaigao.zujuba.storeserver.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.PageBean.CardStoreClubFragmentBean;
import com.renchaigao.zujuba.PageBean.CardStoreTeamFragmentBean;
import com.renchaigao.zujuba.PageBean.StoreManagerBasicFragmentBean;
import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.club.ClubInfo;
import com.renchaigao.zujuba.mongoDB.info.store.StoreInfo;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;
import com.renchaigao.zujuba.storeserver.function.GetOneStoreFunctions;
import com.renchaigao.zujuba.storeserver.function.GetStoreListFunctions;
import com.renchaigao.zujuba.storeserver.function.AddStoreFunctions;
import com.renchaigao.zujuba.storeserver.service.StoreService;
import normal.dateUse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.renchaigao.zujuba.PropertiesConfig.ConstantManagement.STORE_STATE_CHECK;
import static com.renchaigao.zujuba.PropertiesConfig.ConstantManagement.STORE_STATE_DAYOFF;
import static com.renchaigao.zujuba.PropertiesConfig.ConstantManagement.STORE_STATE_DO_BUSINESS;
import static normal.dateUse.CompareTwoStringDate;


@Service
public class StoreServiceImpl implements StoreService {

    private static Logger logger = Logger.getLogger(StoreServiceImpl.class);

    @Autowired
    UserMapper userMapper;

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public ResponseEntity AddStore(String userId, String placeId, String storeInfoStr, MultipartFile[] photos) {
        AddStoreFunctions joinStoreFuntions = new AddStoreFunctions(userMapper, mongoTemplate);//
        //        检查创建信息的正确性、完整性；
        if (!joinStoreFuntions.CheckCreatInfoIsOk(placeId, storeInfoStr, photos)) {
            return new ResponseEntity(RespCode.WRONGPARAMETER, null);
        }
        StoreInfo storeInfo = JSONObject.parseObject(storeInfoStr, StoreInfo.class);
        //        图片信息
        try {
            joinStoreFuntions.JoinStoreInfoPhotos(userId, placeId, storeInfo, photos);
        } catch (Exception e) {
            logger.error(e);
            return new ResponseEntity(RespCode.WARN, e);
        }
        //基础信息,用户创建时填入的部分
        joinStoreFuntions.JoinStoreInfoBasic(storeInfo);
        //地址信息信息
        joinStoreFuntions.JoinStoreInfoAddress(storeInfo);
        //组局信息
        joinStoreFuntions.JoinStoreInfoTeam(storeInfo);
        //消费信息
        joinStoreFuntions.JoinStoreInfoShopping(storeInfo);
        //评价信息
        joinStoreFuntions.JoinStoreEvaluationInfo(storeInfo);
        //套餐信息
        joinStoreFuntions.JoinStorePackageInfo(storeInfo);
        //环境信息
        joinStoreFuntions.JoinStoreHardwareInfo(storeInfo);
        //设备信息
        joinStoreFuntions.JoinStoreEquipmentInfo(storeInfo);
        //积分信息
        joinStoreFuntions.JoinStoreIntegrationInfo(storeInfo);
        //运营信息
        joinStoreFuntions.JoinStoreBusinessInfo(storeInfo);
        //排名信息
        joinStoreFuntions.JoinStoreRankInfo(storeInfo);
        //营业信息
        joinStoreFuntions.JoinStoreBussinessInfo(storeInfo);
//        请求数据库，查询所有信息
        StoreInfo storeInfoNew = joinStoreFuntions.JoinStoreDownloadAllInfo(placeId);
//        检查信息
        joinStoreFuntions.JoinStoreCheckAllInfo(storeInfo);
//        更新storeInfo信息
        joinStoreFuntions.JoinStoreUpdateInfo(storeInfoNew);
//        更新User的信息
        joinStoreFuntions.JoinStoreUpdateUserPlaceInfo(userId, storeInfoNew);
        return new ResponseEntity(RespCode.SUCCESS, storeInfoNew);
    }


    @Override
    public ResponseEntity GetOneStoreInfo(String userId, String placeId) {
        GetOneStoreFunctions getOneStoreFunctions = new GetOneStoreFunctions(mongoTemplate);
        StoreInfo storeInfo = mongoTemplate.findById(placeId, StoreInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_INFO);
        try {
            String ownerId = storeInfo != null ? storeInfo.getOwnerId() : "null";
            if (userId.equals(ownerId)) {
                return getOneStoreFunctions.UserGetStoreInfo(userId, placeId, true);
            } else if (userId.equals("ADMIN999")) {
                return new ResponseEntity(RespCode.STORE_INFO_GET_FAIL, null);
            } else {
                return getOneStoreFunctions.UserGetStoreInfo(userId, placeId, false);
            }
        } catch (Exception e) {
            return new ResponseEntity(RespCode.STORE_NOT_FOUND, null);
        }
    }

    @Override
    public ResponseEntity GetOneStoreTeamInfo(String userId, String storeId) {
        StoreInfo storeInfo = mongoTemplate.findById(storeId
                , StoreInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_INFO);
        try {
            if (storeInfo != null) {
                ArrayList<String> stringArrayList = storeInfo.getStoreTeamIdArrayList();
                ArrayList<CardStoreTeamFragmentBean> cardStoreTeamFragmentBeanArrayList = new ArrayList<>();
                for (String s : stringArrayList) {
                    CardStoreTeamFragmentBean p = new CardStoreTeamFragmentBean();
                    TeamInfo teamInfo = mongoTemplate.findById(s, TeamInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_TEAM_INFO);
                    if (teamInfo != null) {
                        p.setTeamName(teamInfo.getTeamName());
                        p.setState(teamInfo.getState());
                        p.setCreateTime(teamInfo.getCreateTime());
                        p.setStartTimeLong(dateUse.GetStringTimeLong(teamInfo.getStartAllTime()));
                        p.setCreaterNickname(userMapper.selectByPrimaryKey(teamInfo.getCreaterId()).getNickName());
                        p.setPlayerNum(teamInfo.getTeamPlayerInfo().getPlayerArrayList().size());
                        p.setMainGame(teamInfo.getTeamGameInfo().getMainGame() != null ? teamInfo.getTeamGameInfo().getMainGame() : "null");
//                      获取今日日期，比对，比较时间差
                        if (CompareTwoStringDate(dateUse.getTodayDate(), teamInfo.getStartDate()) == 0) {
                            p.setIsToday(true);
                        } else if (CompareTwoStringDate(dateUse.getTodayDate(), teamInfo.getStartDate()) == 1) {
                            p.setIsTomorrow(true);
                        }
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(formatter.parse(teamInfo.getStartDate()));
                        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
                                || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                            p.setIsWeekend(true);
                        }
                        cardStoreTeamFragmentBeanArrayList.add(p);
                    }
                }
                return new ResponseEntity(RespCode.STORE_INFO_GET_SUCCESS, cardStoreTeamFragmentBeanArrayList);
            } else {
                return new ResponseEntity(RespCode.STORE_NOT_FOUND, null);
            }
        } catch (Exception e) {
            return new ResponseEntity(RespCode.EXCEPTION, e);
        }

    }

    @Override
    public ResponseEntity GetOneStoreClubInfo(String userId, String storeId) {
        StoreInfo storeInfo = mongoTemplate.findById(storeId
                , StoreInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_INFO);
        if (storeInfo != null) {
            ArrayList<String> stringArrayList = storeInfo.getStoreClubIdArrayList();
            ArrayList<CardStoreClubFragmentBean> cardStoreClubFragmentBeanArrayList = new ArrayList<>();
            for (String s : stringArrayList) {
                CardStoreClubFragmentBean p = new CardStoreClubFragmentBean();
                ClubInfo clubInfo = mongoTemplate.findById(s, ClubInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_CLUB_INFO);
                if (clubInfo != null) {
                    p.setClubName(clubInfo.getClubName());
                    p.setClubLevel(clubInfo.getClubLevel());
                    p.setCreateTime(clubInfo.getCreateTime());
                    p.setCreaterNickName(clubInfo.getCreateNikeName());
                    p.setSumOfPlayers(clubInfo.getAllPeopleNum());
                    p.setSumOfTeams(clubInfo.getCloseTeamIdList().size());
                    p.setSumOfGames(0);
                    p.setClubId(clubInfo.getId());
                    p.setCreaterId(clubInfo.getCreaterId());
                    cardStoreClubFragmentBeanArrayList.add(p);
                }
            }
            return new ResponseEntity(RespCode.STORE_INFO_GET_SUCCESS, cardStoreClubFragmentBeanArrayList);
        } else {
            return new ResponseEntity(RespCode.STORE_NOT_FOUND, null);
        }
    }

    @Override
    public ResponseEntity GetOneStoreCommentInfo(String userId, String storeId) {
        return null;
    }

    @Override
    public ResponseEntity GetOneStoreGameInfo(String userId, String storeId) {
        return null;
    }

    @Override
    public ResponseEntity ManagerGetOneStoreInfo(String userId, String storeId) {
        StoreInfo storeInfo = mongoTemplate.findById(storeId,StoreInfo.class,MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_INFO);
        StoreManagerBasicFragmentBean storeManagerBasicFragmentBean = new StoreManagerBasicFragmentBean();

        storeManagerBasicFragmentBean.setSumOfHardware("0");
//       根据已有的店铺状态进行设置
        switch (storeInfo.getState()) {
//                case "C":
//                    storeActivityBean.setState("新创建");
//                    break;
            case STORE_STATE_CHECK:
                storeManagerBasicFragmentBean.setStoreState("审核");
                break;
            case STORE_STATE_DO_BUSINESS:
                storeManagerBasicFragmentBean.setStoreState("营业");
                break;
            case STORE_STATE_DAYOFF:
                storeManagerBasicFragmentBean.setStoreState("打样");
                break;
        }

        storeManagerBasicFragmentBean.setStoreName(storeInfo.getName());
        storeManagerBasicFragmentBean.setStoreNotes(storeInfo.getPlaceinfo());
        storeManagerBasicFragmentBean.setAddressInfo(storeInfo.getAddressInfo().getFormatAddress());
        storeManagerBasicFragmentBean.setAddressNotes(storeInfo.getAddressNote());


//        获取已有的时间限制条件，没有就默认给系统附带的默认条件；
//                获取已有的硬件设施库，进行比对，没有的就标记为无；

        return null;
    }


    @Override
    public ResponseEntity GetNearPlace(String userId) {
        GetStoreListFunctions getStoreListFunctions = new GetStoreListFunctions(userMapper, mongoTemplate);
//      获取以用户定位经纬度附近的店铺信息（全部）
        List<StoreInfo> storeInfosList = getStoreListFunctions.getStoreInfos(userId);
//      返回编排好的数据给用户前端；
        return new ResponseEntity(RespCode.SUCCESS, getStoreListFunctions.AssembleStoreList(storeInfosList));
    }


}
