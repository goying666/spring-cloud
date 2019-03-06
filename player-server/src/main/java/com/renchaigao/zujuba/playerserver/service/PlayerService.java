package com.renchaigao.zujuba.playerserver.service;


import com.renchaigao.zujuba.domain.response.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface PlayerService {

    ResponseEntity PlayerJoin(String userId, String teamId, String jsonObjectString, MultipartFile[] photos);

    ResponseEntity PlayerUpdate(String userId, String teamId, String jsonObjectString, MultipartFile[] photos);

    ResponseEntity PlayerQuit(String userId, String teamId, String jsonObjectString, MultipartFile[] photos);

    ResponseEntity KickPlayer(String userId, String teamId, String jsonObjectString, MultipartFile[] photos);

    ResponseEntity GetOnePlayerInfo(String userId, String teamId, String jsonObjectString);

    ResponseEntity GetPlayerInfoList(String userId, String teamId, String jsonObjectString);

}
