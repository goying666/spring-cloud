package com.renchaigao.zujuba.clubserver.service;

import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.club.ClubInfo;

public interface ClubService {
    //    用户创建一个club
    ResponseEntity CreateNewClub(String userId, String placeId, ClubInfo clubInfo);
    ResponseEntity GetUserAllClub(String userId, String tocken);
}
