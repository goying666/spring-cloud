package com.renchaigao.zujuba.storeserver.function;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.mongoDB.info.AddressInfo;
import com.renchaigao.zujuba.mongoDB.info.store.StoreInfo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import store.DistanceFunc;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.renchaigao.zujuba.PropertiesConfig.ConstantManagement.STORE_STATE_CHECK;
import static com.renchaigao.zujuba.PropertiesConfig.ConstantManagement.STORE_STATE_DAYOFF;
import static com.renchaigao.zujuba.PropertiesConfig.ConstantManagement.STORE_STATE_DO_BUSINESS;

public class GetStoreListFunctions {

    UserMapper userMapper;
    MongoTemplate mongoTemplate;

    public GetStoreListFunctions(UserMapper userMapper, MongoTemplate mongoTemplate) {
        this.userMapper = userMapper;
        this.mongoTemplate = mongoTemplate;
    }

    /*
     * 说明：获取以用户定位经纬度附近的店铺信息（全部）
     */
    public List<StoreInfo> getStoreInfos(String userId) {
        //        通过用户id查询用户的基本数据，获得用户的城市信息city 和 经纬度
        AddressInfo userAddress = mongoTemplate.findById(userId, AddressInfo.class);
        String userCityCode = userAddress.getSelectCityCode();
        Double userX = userAddress.getLatitude(), userY = userAddress.getLongitude();
//        获取同用户所在城市的所有商铺的信息，并存入redis，保留id、经纬度；
        List<StoreInfo> storeInfosList = mongoTemplate.find(
                Query.query(Criteria.where("addressInfo.citycode").is(userCityCode))
                , StoreInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_INFO);
//        计算在城市中所有商铺距离用户的距离，排序；
        StoreInfo storeInfoUse = new StoreInfo();
        for (int i = 0; i < storeInfosList.size(); i++) {
            storeInfoUse = storeInfosList.get(i);
            storeInfoUse.getAddressInfo().setDistance(DistanceFunc.getDistance
                    (userX, userY, storeInfoUse.getAddressInfo().getLatitude(), storeInfoUse.getAddressInfo().getLongitude()));
        }
        Collections.sort(storeInfosList, new Comparator<StoreInfo>() {
            @Override
            public int compare(StoreInfo o1, StoreInfo o2) {
                return (int) (o1.getAddressInfo().getDistance() - o2.getAddressInfo().getDistance());//从小到大
            }
        });
        return storeInfosList;
    }

    /*
     * 说明：将store的list处理成显示类型（只包含显示内容）
     */
    public JSONArray AssembleStoreList(List<StoreInfo> storeInfoList) {
        JSONArray retJsonArray = new JSONArray();
        for (StoreInfo storeInfo : storeInfoList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ownerid", storeInfo.getOwnerId());
            jsonObject.put("placeid", storeInfo.getId());
////        0组装店铺的代表图
//            jsonObject.put("image",)
//        1组装店铺名字
            jsonObject.put("name", storeInfo.getName());
//        2组装店铺状态
            switch (storeInfo.getState()) {
//                case "C":
//                    jsonObject.put("state", "新创建");
//                    break;
                case STORE_STATE_CHECK:
                    jsonObject.put("state", "审核中");
                    break;
                case STORE_STATE_DO_BUSINESS:
                    jsonObject.put("state", "营业中");
                    break;
                case STORE_STATE_DAYOFF:
                    jsonObject.put("state", "打样中");
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
            jsonObject.put("desknum", storeInfo.getStoreTeamInfo().getTodayDesk() + "/" + storeInfo.getMaxTeams());
//        7组装店铺人数
            jsonObject.put("todaypeople", storeInfo.getStoreTeamInfo().getTodayPeople() + "/" + storeInfo.getMaxPeople());
//        8组装店铺距离
            if (storeInfo.getAddressInfo().getDistance() > 1000) {
                jsonObject.put("distance",
                        Integer.toString(storeInfo.getAddressInfo().getDistance() / 1000) + "公里");
            } else {
                jsonObject.put("distance", storeInfo.getAddressInfo().getDistance().toString() + "米");
            }
//        9组装店铺时段
//            DayBusinessInfo dayBusinessInfo =
//                    storeInfo.getStoreBusinessInfo().getDayBusinessInfos().get(storeInfo.getStoreBusinessInfo().getDayBusinessInfos().size() - 1);
//            jsonObject.put("time", dayBusinessInfo.getBusinessTimes().size() + "个时段");
//            jsonObject.put("time", storeInfo.getStoreBusinessInfo().getBusinessTimeInfos().size() + "个时段");
            jsonObject.put("time",  "1个时段");
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
            retJsonArray.add(jsonObject);
        }

        return retJsonArray;
    }
}
