package com.renchaigao.zujuba.playerserver.controller;

import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.playerserver.service.impl.PlayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping()
public class PlayerController {

    @Autowired
    PlayerServiceImpl playerServiceImpl;


    @PostMapping(value = "/{firstStr}/{secondStr}/{thirdStr}/{fourthStr}", consumes = "multipart/form-data")
    @ResponseBody
    public ResponseEntity PlaceControllerFuns(@PathVariable("firstStr") String fistStr,
                                              @PathVariable("secondStr") String secondStr,
                                              @PathVariable("thirdStr") String thirdStr,
                                              @PathVariable("fourthStr") String fourthStr,
                                              @RequestParam("json") String jsonObjectString,
                                              @RequestParam("photo") MultipartFile[] photos) {
        switch (fistStr) {
            case "":
                switch (secondStr) {
                    case "join":
                        return playerServiceImpl.PlayerJoin(thirdStr, fourthStr, jsonObjectString, photos);                case "update":
               }
                break;
        }
        return new ResponseEntity(RespCode.WRONGIP, null);
    }
}

