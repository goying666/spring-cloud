package com.renchaigao.zujuba.userserver.uti.user;

import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.dao.User;
import com.renchaigao.zujuba.dao.UserOpenInfo;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.dao.mapper.UserOpenInfoMapper;
import com.renchaigao.zujuba.dao.mapper.UserRankMapper;
import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.AddressInfo;
import com.renchaigao.zujuba.mongoDB.info.user.*;
import normal.dateUse;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import static com.renchaigao.zujuba.PropertiesConfig.UserConstant.GENDER_BOY;
import static com.renchaigao.zujuba.PropertiesConfig.UserConstant.GENDER_GIRL;

public class UpdateUserFunctions {

    UserMapper userMapper;
    UserRankMapper userRankMapper;
    UserOpenInfoMapper userOpenInfoMapper;
    MongoTemplate mongoTemplate;

    public UpdateUserFunctions(UserMapper userMapper, UserRankMapper userRankMapper, MongoTemplate mongoTemplate, UserOpenInfoMapper userOpenInfoMapper) {
        this.userMapper = userMapper;
        this.userRankMapper = userRankMapper;
        this.userOpenInfoMapper = userOpenInfoMapper;
        this.mongoTemplate = mongoTemplate;
    }

    /*
     * 说明：更新用户个人的基础信息
     */
    public ResponseEntity UpdateUserBasicPartFunction(String userId, JSONObject userJson) {
        User userOld = userMapper.selectByPrimaryKey(userId);

        //        更新签名
        if (userJson.getString("signature") != null) {
            return new ResponseEntity(RespCode.USER_UPDATE_INFO_SUCCESS
                    , mongoTemplate.findAndModify(Query.query(Criteria.where("_id").is(userId))
                    , new Update().set("signature", userJson.getString("signature"))
                    , UserInfo.class
                    , MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_INFO));
        }
        //        更新昵称
        if (userJson.getString("nickName") != null) {
            userOld.setNickName(userJson.getString("nickName"));
        }
        //        更新age
        if (userJson.getString("age") != null) {
            userOld.setAge(userJson.getString("age"));
        }
        //        更新ageLevel
        if (userJson.getString("ageLevel") != null) {
            userOld.setAgeLevel(userJson.getString("ageLevel"));
        }
        //        更新realName
        if (userJson.getString("realName") != null) {
            userOld.setRealName(userJson.getString("realName"));
        }
        //        更新idCard
        if (userJson.getString("idCard") != null) {
            userOld.setIdCard(userJson.getString("idCard"));
        }
        //        更新gender
        if (userJson.getString("gender") != null) {
            userOld.setGender(
                    userJson.getString("gender").equals("男") ? GENDER_BOY : GENDER_GIRL);
        }
        //        更新job
        if (userJson.getString("job") != null) {
            userOld.setJob(userJson.getString("job"));
        }
//        //        更新telephone
//        if (userJson.getString("telephone") != null) {
//            userOld.setTelephone(userJson.getString("telephone"));
//        }
        //        更新marriage
        if (userJson.getString("marriage") != null) {
            userOld.setMarriage(userJson.getString("marriage"));
        }
//        //        更新userPWD
//        if (userJson.getString("userPWD") != null) {
//            userOld.setUserPWD(userJson.getString("userPWD"));
//        }


        if (userMapper.updateByPrimaryKeySelective(userOld) == 1) {
            UserOpenInfo userOpenInfo = JSONObject.parseObject(JSONObject.toJSONString(userOld), UserOpenInfo.class);
            userOpenInfo.setUserId(userOld.getId());
            userOpenInfoMapper.updateByPrimaryKeySelective(userOpenInfo);
        } else {
            return new ResponseEntity(RespCode.USER_UPDATE_INFO_FAIL, null);
        }
        UserInfo userInfo = AssembleAllInfo(userId, userOld);
        userInfo.setUpTime(dateUse.GetStringDateNow());
        mongoTemplate.save(userInfo, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_INFO);
        return new ResponseEntity(RespCode.USER_UPDATE_INFO_SUCCESS, userInfo);
    }


    /*
     * 说明：更新用户个人的位置信息
     */
    public ResponseEntity UpdateUserAddressInfoFunction(String userId, JSONObject jsonObject) {
        AddressInfo addressInfo = JSONObject.parseObject(jsonObject.toJSONString(), AddressInfo.class);
        addressInfo.setId(userId);
        addressInfo.setAddressClass("user");
        addressInfo.setUpTime(dateUse.GetStringDateNow());
        mongoTemplate.save(addressInfo, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_ADDRESS_INFO);
//        更新userinfo
        Update update = Update.update("addressInfo", addressInfo);
        mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(userId)),
                Update.update("addressInfo", addressInfo), UserInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_INFO);
        return new ResponseEntity(RespCode.SUCCESS, mongoTemplate.findById(userId, UserInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_INFO));
    }

    /*
     * 说明：组装用户所有信息userInfo
     */
    private UserInfo AssembleAllInfo(String userId, User user) {
        UserInfo userInfo = JSONObject.parseObject(JSONObject.toJSONString(user), UserInfo.class);
        userInfo.setUserRank(userRankMapper.selectByPrimaryKey(userId));
        userInfo.setUserTeams(mongoTemplate.findById(userId, UserTeams.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_TEAMS));
        userInfo.setUserGames(mongoTemplate.findById(userId, UserGames.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_GAMES));
        userInfo.setUserPlaces(mongoTemplate.findById(userId, UserPlaces.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_PLACES));
        userInfo.setUserPhotosInfo(mongoTemplate.findById(userId, UserPhotosInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_PHOTOS));
        userInfo.setAddressInfo(mongoTemplate.findById(userId, AddressInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_ADDRESS_INFO));
        userInfo.setUserSpendInfo(mongoTemplate.findById(userId, UserSpendInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_SPEND));
        userInfo.setUserMessagesInfo(mongoTemplate.findById(userId, UserMessagesInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE_INFO));
        userInfo.setUserFriendInfo(mongoTemplate.findById(userId, UserFriendInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_FRIEND));
        userInfo.setUserIntegrationInfo(mongoTemplate.findById(userId, UserIntegrationInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_INTEGRATION));
        userInfo.setUserPermissionInfo(mongoTemplate.findById(userId, UserPermissionInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_PERMISSION));
        userInfo.setUserOpenInfo(mongoTemplate.findById(userId, UserOpenInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_OPENINFO));
        return userInfo;
    }


}
