package com.renchaigao.zujuba.placeserver.service.impl;

import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.placeserver.service.PlaceService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PlaceServiceImpl implements PlaceService {

    private static Logger logger = Logger.getLogger(PlaceServiceImpl.class);

    @Autowired
    UserMapper userMapper;

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public ResponseEntity JoinPlace(String userId, String placeId, String jsonObjectString, MultipartFile[] photos) {
//        StoreInfo storeInfo = JSONObject.parseObject(jsonObjectString, StoreInfo.class);
//        JoinStoreFunctions joinStoreFuntions = new JoinStoreFunctions(userMapper, mongoTemplate);//
////        检查创建信息的正确性、完整性；
//        if (!joinStoreFuntions.CheckCreatInfoIsOk(placeId, jsonObjectString, photos)) {
//            return new ResponseEntity(RespCode.WRONGPARAMETER, null);
//        }
//        //        图片信息
//        try {
//            joinStoreFuntions.JoinStoreInfoPhotos(userId, placeId, storeInfo, photos);
//        } catch (Exception e) {
//            logger.error(e);
//            return new ResponseEntity(RespCode.WARN, e);
//        }
//        //基础信息,用户创建时填入的部分
//        joinStoreFuntions.JoinStoreInfoBasic(storeInfo);
//        //地址信息信息
//        joinStoreFuntions.JoinStoreInfoAddress(storeInfo);
//        //组局信息
//        joinStoreFuntions.JoinStoreInfoTeam(storeInfo);
//        //消费信息
//        joinStoreFuntions.JoinStoreInfoShopping(storeInfo);
//        //评价信息
//        joinStoreFuntions.JoinStoreEvaluationInfo(storeInfo);
//        //套餐信息
//        joinStoreFuntions.JoinStorePackageInfo(storeInfo);
//        //环境信息
//        joinStoreFuntions.JoinStoreHardwareInfo(storeInfo);
//        //设备信息
//        joinStoreFuntions.JoinStoreEquipmentInfo(storeInfo);
//        //积分信息
//        joinStoreFuntions.JoinStoreIntegrationInfo(storeInfo);
//        //运营信息
//        joinStoreFuntions.JoinStoreBusinessInfo(storeInfo);
//        //排名信息
//        joinStoreFuntions.JoinStoreRankInfo(storeInfo);
//        //营业信息
//        joinStoreFuntions.JoinStoreBussinessInfo(storeInfo);
//
////        请求数据库，查询所有信息
//        StoreInfo storeInfoNew = joinStoreFuntions.JoinStoreDownloadAllInfo(placeId);
////        检查信息
//        joinStoreFuntions.JoinStoreCheckAllInfo(storeInfo);
////        更新storeInfo信息
//        joinStoreFuntions.JoinStoreUpdateInfo(storeInfoNew);
////        更新User的信息
//        joinStoreFuntions.JoinStoreUpdateUserPlaceInfo(userId,storeInfoNew);
//
//
//        return new ResponseEntity(RespCode.SUCCESS, storeInfoNew);
        return null;
    }

    @Override
    public ResponseEntity GetOnePlace(String userId, String placeId, String jsonObjectString) {
//        JSONObject jsonObject = JSONObject.parseObject(jsonObjectString);
//        GetOneStoreFunctions getOneStoreFunctions = new GetOneStoreFunctions(userMapper, mongoTemplate);
////        通过json信息判断获取信息的用户的身份,并分派不同的处理结果;
//        switch (jsonObject.getString("identity")){
//            case "OWNER":
//                break;
//            case "USER":
//                getOneStoreFunctions.UserGetStoreInfo(userId,placeId,jsonObject);
//                break;
//            case "ADMIN":
//                break;
//        }
//
//        return new ResponseEntity(RespCode.SUCCESS,
//                mongoTemplate.findById(placeId,StoreInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_INFO));
        return null;
    }


    @Override
    public ResponseEntity GetNearPlace(String userId, String placeId, String jsonObjectString) {
//        GetStoreListFunctions getStoreListFunctions = new GetStoreListFunctions(userMapper, mongoTemplate);
////      获取以用户定位经纬度附近的店铺信息（全部）
//        List<StoreInfo> storeInfosList = getStoreListFunctions.getStoreInfos(userId);
////      返回编排好的数据给用户前端；
//        return new ResponseEntity(RespCode.SUCCESS, getStoreListFunctions.AssembleStoreList(storeInfosList));
        return null;
    }



}
