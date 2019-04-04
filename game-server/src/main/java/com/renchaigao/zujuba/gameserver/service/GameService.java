package com.renchaigao.zujuba.gameserver.service;

import com.renchaigao.zujuba.domain.response.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface GameService {

//    ResponseEntity AddGame(String userId, String placeId, String jsonObjectString, MultipartFile[] photos);
    ResponseEntity GetGameInfo();

}
