package com.renchaigao.zujuba.teamserver.uti;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.mongoDB.info.AddressInfo;
import com.renchaigao.zujuba.mongoDB.info.store.StoreInfo;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;
import com.renchaigao.zujuba.mongoDB.info.team.TeamPlayerInfo;
import normal.dateUse;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import store.DistanceFunc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.renchaigao.zujuba.PropertiesConfig.ConstantManagement.*;
import static com.renchaigao.zujuba.PropertiesConfig.PhotoConstant.*;

public class GetNearTeamListFunctions {

    private MongoTemplate mongoTemplate;

    public GetNearTeamListFunctions(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /*
     * 说明：获取原始信息 teamInfoList
     */
    public ArrayList<TeamInfo> GetNearTeamInfoListByUserId(String userId) {
        AddressInfo userAddress = mongoTemplate.findById(userId, AddressInfo.class);
        String userCityCode = userAddress != null ? userAddress.getCitycode() : "0755";
        Double userX = userAddress != null ? userAddress.getLatitude() : 0, userY = userAddress != null ? userAddress.getLongitude() : 0;
        List<TeamInfo> teamInfos = mongoTemplate.find(
                Query.query(Criteria.where("addressInfo.citycode").is(userCityCode)), TeamInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_TEAM_INFO);
        for (TeamInfo teamInfo : teamInfos) {
            teamInfo.getAddressInfo().setDistance(DistanceFunc.getDistance(userX, userY,
                    teamInfo.getAddressInfo().getLatitude(),
                    teamInfo.getAddressInfo().getLongitude())
            );
        }
        teamInfos.sort((o1, o2) -> {
//                    从小到大
            return (o1.getAddressInfo().getDistance() - o2.getAddressInfo().getDistance());
//                    从大到小
//                return (int)(o2.getDistance() - o1.getDistance());
        });
        return new ArrayList<>(teamInfos);
    }


    /*
     * 说明：组装参数给前端
     */
    public JSONArray PackageTeamInfoList(ArrayList<TeamInfo> teamInfoArrayList) {
        JSONArray jsonArray = new JSONArray();
        for (TeamInfo o : teamInfoArrayList) {
            JSONObject json = new JSONObject();
            json.put("teamId", o.getId());
            json.put("name", o.getTeamName());
//            图片
            if (o.getTeamGameInfo().isSelect_LRS()) {
                json.put("imageurl", LRS_GAME_IMAGE);
            } else if (o.getTeamGameInfo().isSelect_THQBY()) {
                json.put("imageurl", THQBY_GAME_IMAGE);
            } else if (o.getTeamGameInfo().isSelect_MXTSJ()) {
                json.put("imageurl", HASL_GAME_IMAGE);
            }

            //队伍状态
            switch (o.getState()) {
                case TEAM_STATE_WAITING:
                    json.put("state", "等待中");
                    break;
                case TEAM_STATE_READY:
                    json.put("state", "准备完毕");
                    break;
                case TEAM_STATE_ARRIVALS:
                    json.put("state", "全员到位");
                    break;
                case TEAM_STATE_GAME:
                    json.put("state", "游戏中");
                    break;
                case TEAM_STATE_FINISH:
                    json.put("state", "结束");
                    break;
            }

            switch (o.getAddressInfo().getAddressClass()) {
                case ADDRESS_CLASS_USER:
                    break;
                case ADDRESS_CLASS_STORE:
                    StoreInfo storeInfo = mongoTemplate.findById(
                            o.getAddressInfo().getId(), StoreInfo.class,
                            MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_INFO);
                    json.put("place", storeInfo != null ? storeInfo.getName() : "place");
//                    __后期需要修改成 店铺的特殊标识，现在暂用“5分”替代
                    json.put("placeNote", "5分");
                    json.put("ownerid", storeInfo != null ? storeInfo.getOwnerId() : "ownerid");
                    json.put("placeid", storeInfo != null ? storeInfo.getId() : "placeid");
                    json.put("placeName", storeInfo != null ? storeInfo.getName() : "placeName");
                    break;
                case ADDRESS_CLASS_OPEN:
                    break;
                case ADDRESS_CLASS_HOME:
                    break;
                case ADDRESS_CLASS_SCHOOL:
                    break;
            }
            json.put("rating", 5);//-待完善

            if (o.getAddressInfo().getDistance() > 1000) {
                json.put("distance",
                        Integer.toString(o.getAddressInfo().getDistance() / 1000) + "公里");
            } else {
                json.put("distance", o.getAddressInfo().getDistance().toString() + "米");
            }
            TeamPlayerInfo teamPlayerInfo = mongoTemplate.findById(o.getId(), TeamPlayerInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_TEAM_PLAYER_INFO);
            json.put("boynum", teamPlayerInfo != null ? teamPlayerInfo.getBoySum().toString() : 0);
            json.put("girlnum", teamPlayerInfo != null ? teamPlayerInfo.getGirlSum().toString() : 0);
            int currentPlayerNum = teamPlayerInfo != null ? teamPlayerInfo.getPlayerArrayList().size() : 0,
                    minPlayerNum = o.getPlayerMin(), maxPlayerNum = o.getPlayerMax();
            if (currentPlayerNum < minPlayerNum) {
                json.put("currentPlayer", "(差" + String.valueOf(minPlayerNum - currentPlayerNum) + "人)");
            } else {
                json.put("currentPlayer", currentPlayerNum + "/" + maxPlayerNum + "人)");
            }
            int day = dateUse.CompareTwoStringDate(dateUse.getTodayDate(), o.getStartDate());
            if (day == 0) {
                json.put("date", "今天");
            } else {
                json.put("date", day + "天后");
            }
            String starTime;
            if (o.getStartMinute() < 10)
                starTime = o.getStartHour() + ":0" + o.getStartMinute();
            else
                starTime = o.getStartHour() + ":" + o.getStartMinute();
            json.put("time", starTime);
            JSONObject timeLave = dateUse.CompareStringDateAndTimeToNow(o.getStartDate(), o.getStartHour(), o.getStartMinute());
            if (timeLave.getIntValue("day") == 0) {
                if (timeLave.getIntValue("hour") == 0) {
                    if (timeLave.getIntValue("minute") < 0) {
                        json.put("lave", "已过时");
                    } else {
                        json.put("lave", timeLave.getString("minute").replace("-", "") + "分后");
                    }
                } else
                    json.put("lave", timeLave.getString("hour").replace("-", "") + "小时后");
            } else {
                json.put("lave", ">24小时");
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(formatter.parse(o.getStartDate() + " " + starTime));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            json.put("realDistance", o.getAddressInfo().getDistance());
            json.put("realStartTime", calendar.getTimeInMillis());
            json.put("realPlayerNum", teamPlayerInfo != null ? teamPlayerInfo.getPlayerArrayList().size() : 0);
            jsonArray.add(json);
        }
        return jsonArray;
    }

}
