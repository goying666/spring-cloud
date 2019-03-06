package com.renchaigao.zujuba.placeserver.function;

import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.mongoDB.info.store.StoreInfo;
import org.springframework.data.mongodb.core.MongoTemplate;

public class GetOneStoreFunctions {

    UserMapper userMapper;
    MongoTemplate mongoTemplate;

    public GetOneStoreFunctions(UserMapper userMapper, MongoTemplate mongoTemplate) {
        this.userMapper = userMapper;
        this.mongoTemplate = mongoTemplate;
    }


    /*
     * 说明：获取以用户(普通用户)查看的店铺信息（全部）
     */
    public void UserGetStoreInfo(String userId, String placeId,JSONObject json){
        StoreInfo storeInfo = mongoTemplate.findById(placeId,StoreInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_INFO);
        JSONObject jsonObject = new JSONObject();

//        1组装店铺名字
        jsonObject.put("name", storeInfo.getName());
//        2组装店铺状态
        switch (storeInfo.getState()) {
            case "C":
                jsonObject.put("state", "新创建");
                break;
            case "S":
                jsonObject.put("state", "审核");
                break;
            case "Y":
                jsonObject.put("state", "营业");
                break;
            case "X":
                jsonObject.put("state", "打样");
                break;
        }
//        3组装店铺评分
        if (storeInfo.getStoreEvaluationInfo().getStoreScore() == 0) {
            jsonObject.put("score", "0.0分");
        } else {
            jsonObject.put("score", storeInfo.getStoreEvaluationInfo().getStoreScore().toString() + "分");
        }
//        4组装店铺多少人玩过
        jsonObject.put("allpeoplenum", storeInfo.getStoreTeamInfo().getAllUsersNum().toString() + "人玩过");
//        5组装店铺人均消费
        jsonObject.put("spend", "人均:¥" + storeInfo.getStoreShoppingInfo().getAverageSpend().toString() + "/时");
//        6组装店铺桌数
        jsonObject.put("desknum", storeInfo.getStoreTeamInfo().getTodayDesk() + "/" + storeInfo.getMaxDeskSum());
//        7组装店铺人数
        jsonObject.put("todaypeople", storeInfo.getStoreTeamInfo().getTodayPeople() + "/" + storeInfo.getMaxPeopleSum());
//        8组装店铺距离
        if (storeInfo.getAddressInfo().getDistance() > 1000) {
            jsonObject.put("distance",
                    Integer.toString(storeInfo.getAddressInfo().getDistance() / 1000) + "k" +
                            Integer.toString(storeInfo.getAddressInfo().getDistance() % 1000) + "m");
        } else {
            jsonObject.put("distance", storeInfo.getAddressInfo().getDistance().toString() + "m");
        }
//        9组装店铺时段
//            DayBusinessInfo dayBusinessInfo =
//                    storeInfo.getStoreBusinessInfo().getDayBusinessInfos().get(storeInfo.getStoreBusinessInfo().getDayBusinessInfos().size() - 1);
//            jsonObject.put("time", dayBusinessInfo.getBusinessTimes().size() + "个时段");
        jsonObject.put("time", storeInfo.getStoreBusinessInfo().getBusinessTimeInfos().size() + "个时段");
//        10组装店铺排名、等荣誉
        switch (storeInfo.getStoreclass()) {
            case "0":
                jsonObject.put("class", "餐馆");
                break;
            case "1":
                jsonObject.put("class", "桌游吧");
                break;
            case "2":
                jsonObject.put("class", "民宿");
                break;
            case "3":
                jsonObject.put("class", "其他");
                break;
        }
//        11组装店铺备注
        jsonObject.put("note", "本店已入驻组局吧，希望通过我们的努力，将我市的桌游文化发展起来。");
//        12组装店铺评论1
        jsonObject.put("evaluate_1", "环境很nice");
//        13组装店铺评论2
        jsonObject.put("evaluate_2", "价格公道");

    }
}
