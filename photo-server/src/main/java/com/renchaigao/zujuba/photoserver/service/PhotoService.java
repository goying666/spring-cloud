package com.renchaigao.zujuba.photoserver.service;

import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.domain.response.ResponseEntity;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface PhotoService {
//
//    ResponseEntity GetUserPhoto(String userId);
//    ResponseEntity GetPlaceMainPhoto(String placeId);
//    ResponseEntity GetPlaceAllPhoto(String placeId);
    void downloadFile(String userid,String placeid,String filename,HttpServletResponse res);

    void showFile(String userid,String placeid,String filename,HttpServletResponse res) throws IOException;
    void ShowFileByPath(JSONObject requestBody, HttpServletResponse response);
}
