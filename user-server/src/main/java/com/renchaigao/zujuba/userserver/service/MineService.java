package com.renchaigao.zujuba.userserver.service;

import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.domain.response.ResponseEntity;

public interface MineService {

    ResponseEntity GetUserMineInfo(String userId);

    ResponseEntity GetUserPlaceList(String userId);

    ResponseEntity GetUserOnePlaceInfo(String userId, String placeId);
    ResponseEntity GetMineFragmentInfo(String userId);
    ResponseEntity GetMyTeamInfo(String userId);
}
