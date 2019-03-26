package com.renchaigao.zujuba.storeserver.function;

import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.PageBean.StoreActivityBean;
import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.AddressInfo;
import com.renchaigao.zujuba.mongoDB.info.store.StoreInfo;
import org.springframework.data.mongodb.core.MongoTemplate;
import store.DistanceFunc;

import java.util.ArrayList;

import static com.renchaigao.zujuba.PropertiesConfig.ConstantManagement.STORE_STATE_CHECK;
import static com.renchaigao.zujuba.PropertiesConfig.ConstantManagement.STORE_STATE_DAYOFF;
import static com.renchaigao.zujuba.PropertiesConfig.ConstantManagement.STORE_STATE_DO_BUSINESS;

public class GetOneStoreFunctions {

    private MongoTemplate mongoTemplate;

    public GetOneStoreFunctions(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    /*
     * 说明：获取以用户(普通用户)查看的店铺信息（全部）
     */
    public ResponseEntity UserGetStoreInfo(String userId, String placeId, Boolean isOwner) {
        StoreActivityBean storeActivityBean = new StoreActivityBean();
        try {
            if (isOwner) {
                storeActivityBean.setIsCreater(true);
            } else {
                storeActivityBean.setIsCreater(false);
            }
//        基本信息
            StoreInfo storeInfo = mongoTemplate.findById(placeId, StoreInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_INFO);
            JSONObject jsonObject = new JSONObject();
//        1组装店铺名字
//        jsonObject.put("name", storeInfo.getName());
            storeActivityBean.setStoreName(storeInfo.getName());
//        2组装店铺状态
            switch (storeInfo.getState()) {
//                case "C":
//                    storeActivityBean.setState("新创建");
//                    break;
                case STORE_STATE_CHECK:
                    storeActivityBean.setState("审核");
                    break;
                case STORE_STATE_DO_BUSINESS:
                    storeActivityBean.setState("营业");
                    break;
                case STORE_STATE_DAYOFF:
                    storeActivityBean.setState("打样");
                    break;
            }
//        3组装店铺评分
            if (storeInfo.getStoreEvaluationInfo().getStoreScore() == 0) {
                storeActivityBean.setCommentScore("0.0");

            } else {
                storeActivityBean.setCommentScore(storeInfo.getStoreEvaluationInfo().getStoreScore().toString() + "");
            }
//        4组装店铺多少人玩过
//        jsonObject.put("allpeoplenum", storeInfo.getStoreTeamInfo().getAllUsersNum().toString() + "人玩过");
            storeActivityBean.setSumOfPlayer(storeInfo.getStoreTeamInfo().getAllUsersNum().toString() + "人玩过");
//        5组装店铺人均消费
//        jsonObject.put("spend", "人均:¥" + storeInfo.getStoreShoppingInfo().getAverageSpend().toString() + "/时");
            storeActivityBean.setSpendMoney(storeInfo.getStoreShoppingInfo().getAverageSpend().toString());
//        6组装店铺桌数
//        jsonObject.put("desknum", storeInfo.getStoreTeamInfo().getTodayDesk() + "/" + storeInfo.getMaxDeskSum());
//        7组装店铺人数
//        jsonObject.put("todaypeople", storeInfo.getStoreTeamInfo().getTodayPeople() + "/" + storeInfo.getMaxPeopleSum());
//        8组装店铺距离
            AddressInfo userAddress = mongoTemplate.findById(userId, AddressInfo.class);
            Double userX = userAddress != null ? userAddress.getLatitude() : 0, userY = userAddress != null ? userAddress.getLongitude() : 0;
            Integer distance = DistanceFunc.getDistance(userX
                    , userY
                    , storeInfo.getAddressInfo().getLatitude()
                    , storeInfo.getAddressInfo().getLongitude());
            if (distance > 1000) {
                storeActivityBean.setDistance(Integer.toString(distance / 1000) + "k" +
                        Integer.toString(distance % 1000) + "m");
            } else {
                storeActivityBean.setDistance(distance.toString() + "m");
            }
//        9组装店铺时段
//            DayBusinessInfo dayBusinessInfo =
//                    storeInfo.getStoreBusinessInfo().getDayBusinessInfos().get(storeInfo.getStoreBusinessInfo().getDayBusinessInfos().size() - 1);
//            jsonObject.put("time", dayBusinessInfo.getBusinessTimes().size() + "个时段");
            jsonObject.put("time", storeInfo.getStoreBusinessInfo().getBusinessTimeInfos().size() + "个时段");
//        10组装店铺排名、等荣誉
            switch (storeInfo.getStoreclass()) {
                case "0":
//                jsonObject.put("class", "餐馆");
                    storeActivityBean.setStoreclass("餐馆");
                    break;
                case "1":
//                jsonObject.put("class", "");
                    storeActivityBean.setStoreclass("桌游吧");
                    break;
                case "2":
//                jsonObject.put("class", "");
                    storeActivityBean.setStoreclass("民宿");
                    break;
                case "3":
//                jsonObject.put("class", "");
                    storeActivityBean.setStoreclass("其他");
                    break;
            }
//        11组装店铺备注
            storeActivityBean.setPlaceinfo(storeInfo.getPlaceinfo());
//        jsonObject.put("note", "本店已入驻组局吧，希望通过我们的努力，将我市的桌游文化发展起来。");
//        12组装店铺评论1
//        jsonObject.put("evaluate_1", "环境很nice");
//        13组装店铺评论2
//        jsonObject.put("evaluate_2", "价格公道");
//        星级信息
            storeActivityBean.setStar(1);
//        俱乐部数量
            storeActivityBean.setClubNum(String.valueOf(storeInfo.getStoreClubIdArrayList().size()));
//        组局次数
            storeActivityBean.setSumOfTeams(String.valueOf(storeInfo.getStoreTeamIdArrayList().size()));
//        评论人数       ——待开发
            storeActivityBean.setTelephoneNumber("");
//        地址信息
            storeActivityBean.setAddressAbstract(storeInfo.getAddressInfo().getFormatAddress());
//        地址信息备注
            storeActivityBean.setAddressNotes(storeInfo.getAddressNote());
//        图片信息
            ArrayList<String> pathList = new ArrayList<>();
            for (int i = 1; i < 5; i++) {
                pathList.add(storeInfo.getOwnerId() + "/" + storeInfo.getId() + "/photo" + i + ".jpg");
            }
            storeActivityBean.setImagePaths(pathList);
//        评论信息
            storeActivityBean.setCommentTimes("0");
//        组局信息
//        套餐信息
            return new ResponseEntity(RespCode.STORE_INFO_GET_SUCCESS, storeActivityBean);
        } catch (Exception e) {
            return new ResponseEntity(RespCode.STORE_INFO_GET_FAIL, e);
        }
    }
}
