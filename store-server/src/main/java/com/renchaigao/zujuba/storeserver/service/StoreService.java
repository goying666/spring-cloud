package com.renchaigao.zujuba.storeserver.service;

import com.renchaigao.zujuba.PageBean.StoreManagerBasicFragmentBean;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface StoreService {

    ResponseEntity AddStore(String userId, String placeId, String jsonObjectString, MultipartFile[] photos);

//    ResponseEntity UpdateStore(String parameter, String userId, String placeId, String jsonObjectString);
//
//    ResponseEntity DeleteStore(String parameter, String userId, String placeId, String jsonObjectString);
//
    ResponseEntity GetOneStoreInfo(String userId, String storeId);
    ResponseEntity GetOneStoreTeamInfo(String userId, String storeId);
    ResponseEntity GetOneStoreClubInfo(String userId, String storeId);
    ResponseEntity GetOneStoreCommentInfo(String userId, String storeId);
    ResponseEntity GetOneStoreGameInfo(String userId, String storeId);

    ResponseEntity ManagerGetOneStoreInfo(String userId, String storeId);
    ResponseEntity ManagerUpdateOneStoreInfo(String userId, String storeId,StoreManagerBasicFragmentBean o);
//
//    ResponseEntity GetMyStore(String parameter, String userId, String placeId, String jsonObjectString);

    ResponseEntity GetNearPlace(String userId);



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
