package com.renchaigao.zujuba.placeserver.service;

import com.renchaigao.zujuba.domain.response.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface PlaceService {

    ResponseEntity JoinPlace(String userId, String placeId, String jsonObjectString, MultipartFile[] photos);

//    ResponseEntity UpdateStore(String parameter, String userId, String placeId, String jsonObjectString);
//
//    ResponseEntity DeleteStore(String parameter, String userId, String placeId, String jsonObjectString);
//
    ResponseEntity GetOnePlace(String userId, String placeId, String jsonObjectString);
//
//    ResponseEntity GetMyStore(String parameter, String userId, String placeId, String jsonObjectString);

    ResponseEntity GetNearPlace(String parameter, String userId, String jsonObjectString);


//    ResponseEntity AddOpen(String parameter, String userId, String placeId, String jsonObjectString, MultipartFile[] photos);
//
//    ResponseEntity DeleteOpen(String parameter, String userId, String placeId, String jsonObjectString);
//
//    ResponseEntity UpdateOpen(String parameter, String userId, String placeId, String jsonObjectString);
//
//    ResponseEntity GetNearOpen(String parameter, String userId, String placeId, String jsonObjectString);
//
//    ResponseEntity GetOneOpen(String parameter, String userId, String placeId, String jsonObjectString);
//
//    ResponseEntity GetMyOpen(String parameter, String userId, String placeId, String jsonObjectString);
//
//    ResponseEntity GetAllMyPlace(String parameter, String userId, String placeId, String jsonObjectString);

}
