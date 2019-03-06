package com.renchaigao.zujuba.placeserver.function;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.mongoDB.info.store.BusinessPart.BusinessTimeInfo;
import com.renchaigao.zujuba.mongoDB.info.store.HardwarePart.DeskInfo;
import com.renchaigao.zujuba.mongoDB.info.store.StoreInfo;
import com.renchaigao.zujuba.mongoDB.info.user.UserPlaces;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;

public class UserPlaceFunctions {

    UserMapper userMapper;
    MongoTemplate mongoTemplate;

    public UserPlaceFunctions(UserMapper userMapper, MongoTemplate mongoTemplate) {
        this.userMapper = userMapper;
        this.mongoTemplate = mongoTemplate;
    }

    /*
     * 说明：获取以用户创建的所有场地信息
     */
    public JSONObject GetUserAllCreatePlace(String userId) {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        ArrayList<String> arrayList = mongoTemplate.findById(userId,
                UserPlaces.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_PLACES).getAllPlaceId();
        Integer state_open = 0, state_create = 0, state_wait = 0, state_close = 0;
        for (String i : arrayList) {
            StoreInfo storeInfo = mongoTemplate.findById(i, StoreInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_INFO);
            JSONObject json = new JSONObject();
            json.put("imageurl", "showimage/" + userId + "/" + i + "/photo1.jpg");
            json.put("userid", userId);
            json.put("placeid", storeInfo.getId());
//            jsonObject.pu;
            json.put("name", storeInfo.getName());
            json.put("state", storeInfo.getState());
            switch (storeInfo.getState()) {
                case "C":
                    state_create++;
                    break;
                case "S":
                    state_wait++;
                    break;
                case "Y":
                    state_open++;
                    break;
                case "X":
                    state_close++;
                    break;
//                case "CLOSE":
//                    state_close++;
//                    break;
            }
//            获取系统给于店铺的评价（待开发）
//            jsonObject.put("sysnote",storeInfo.get)
            jsonArray.add(json);
        }
        jsonObject = new JSONObject();
        jsonObject.put("array", jsonArray);
        jsonObject.put("state_open", state_open.toString());
        jsonObject.put("state_create", state_create.toString());
        jsonObject.put("state_wait", state_wait.toString());
        jsonObject.put("state_close", String.valueOf(state_close));
        return jsonObject;
    }

    /*
     * 说明：获取用户创建的一个场地信息——基础部分、组队部分、运营部分
     */
    public JSONObject GetUserOnePlaceInfo(String userId, String placeId) {
        JSONObject retJson = new JSONObject();
        StoreInfo storeInfo = mongoTemplate.findById(placeId, StoreInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_INFO);
        retJson.put("name", storeInfo.getName());
        retJson.put("state", storeInfo.getState());
        ArrayList<BusinessTimeInfo> businessTimeInfos = storeInfo.getStoreBusinessInfo().getBusinessTimeInfos();
        retJson.put("businessTimeInfos",businessTimeInfos);
        retJson.put("timeNum", businessTimeInfos.size());
        Integer i = 1;
        for (BusinessTimeInfo o : businessTimeInfos) {
            retJson.put("time" + i.toString(), o.getStartTime() + "~" + o.getEndTime());
            i++;
        }
        retJson.put("mapDetail", storeInfo.getAddressInfo().getFormatAddress());
        retJson.put("mapNote", storeInfo.getPlaceinfo());

        int num = 0;
        for (DeskInfo o:storeInfo.getStoreHardwareInfo().getDeskInfos()){
            num += o.getMaxUserNum();
        }
        retJson.put("allPeopleNum", num);
        retJson.put("allDeskNum", storeInfo.getStoreHardwareInfo().getDeskInfos().size());

//        桌子详细信息
        JSONArray jsonArray = new JSONArray();
        ArrayList<DeskInfo> deskInfoArrayList = storeInfo.getStoreHardwareInfo().getDeskInfos();
        i = 1;
        for (DeskInfo o : deskInfoArrayList) {
            JSONObject m = new JSONObject();
            m.put("imageUrl", o.getDeskAllPhotos().get(0).getPhotoPath());
            m.put("maxPeopleSum", o.getMaxUserNum().toString());
            m.put("minPeopleNum", o.getMinUserNum().toString());
            m.put("deskNumber", i++);
            m.put("WHICHCARD", "BASIC_DESK");
            jsonArray.add(m);
        }
        retJson.put("array", jsonArray);

        return retJson;
    }


}
