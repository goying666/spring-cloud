package com.renchaigao.zujuba.placeserver.controller;

import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.placeserver.service.impl.PlaceServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping()
public class PlaceController {

    @Autowired
    PlaceServiceImpl placeServiceImpl;

//    @PutMapping(value = "/{firstStr}/{secondStr}/{thirdStr}/{fourthStr}")
//    @ResponseBody
//    public ResponseEntity PlaceControllerPutFunctions(){
//        return null;
//    }
//
//    @GetMapping(value = "/{firstStr}/{secondStr}/{thirdStr}/{fourthStr}")
//    @ResponseBody
//    public ResponseEntity PlaceControllerGetFunctions(){
//        return null;
//    }

//
//
//
//    @PostMapping(value = "/{firstStr}/{secondStr}/{thirdStr}/{fourthStr}", consumes = "multipart/form-data")
//    @ResponseBody
//    public ResponseEntity PlaceControllerFuns(@PathVariable("firstStr") String fistStr,
//                                              @PathVariable("secondStr") String secondStr,
//                                              @PathVariable("thirdStr") String thirdStr,
//                                              @PathVariable("fourthStr") String fourthStr,
////                                             @ResponseBody("json") ResponseBody responseBody,
//                                              @RequestParam("json") String jsonObjectString,
//                                              @RequestParam("photo") MultipartFile[] photos) {
////        if (!userMapper.selectByPrimaryKey(userId).getToken().equals(userToken))
////            return new ResponseEntity(RespCode.AUTHENTICATIONFAIL, null);
//        switch (fistStr) {
//            case "store":
//                switch (secondStr) {
//                    case "join":
//                        return placeServiceImpl.JoinPlace(thirdStr, fourthStr, jsonObjectString, photos);
//                    case "getnear":
//                        return placeServiceImpl.GetNearPlace(thirdStr, fourthStr, jsonObjectString);
//                    case "getone":
//                        return placeServiceImpl.GetOnePlace(thirdStr, fourthStr, jsonObjectString);//                    case "update":
////                        return placeServiceImpl.UpdateStore(parameter,userId,placeId,jsonObjectString);
////                    case "delete":
////                        return placeServiceImpl.DeleteStore(parameter,userId,placeId,jsonObjectString);
////                    case "getmine":
////                        return placeServiceImpl.GetMyStore(parameter,userId,placeId,jsonObjectString);
//                }
//                break;
//        }
//        return new ResponseEntity(RespCode.WRONGIP, null);
//    }

}

//
//    @PostMapping(value = "/{firstStr}/{secondStr}/{parameter}/{userId}/{userToken}/{placeId}",consumes = "multipart/form-data")
//    @ResponseBody
//    public ResponseEntity PlaceControllerFuns(@PathVariable("firstStr") String fistStr,
//                                              @PathVariable("secondStr") String secondStr,
//                                              @PathVariable("parameter") String parameter,
//                                              @PathVariable("userId") String userId,
//                                              @PathVariable("userToken") String userToken,
//                                              @PathVariable("placeId") String placeId,
//                                              @RequestParam("json") String jsonObjectString,
//                                              @RequestParam("photo") MultipartFile[] photos) {
//        if (!userMapper.selectByPrimaryKey(userId).getToken().equals(userToken))
//            return new ResponseEntity(RespCode.AUTHENTICATIONFAIL, null);
//        switch (fistStr) {
//            case "store":
//                switch (secondStr){
//                    case "join":
//                        return placeServiceImpl.JoinPlace(parameter,userId,placeId,jsonObjectString,photos);
//                    case "update":
//                        return placeServiceImpl.UpdateStore(parameter,userId,placeId,jsonObjectString);
//                    case "delete":
//                        return placeServiceImpl.DeleteStore(parameter,userId,placeId,jsonObjectString);
//                    case "getone":
//                        return placeServiceImpl.GetOnePlace(parameter,userId,placeId,jsonObjectString);
//                    case "getmine":
//                        return placeServiceImpl.GetMyStore(parameter,userId,placeId,jsonObjectString);
//                    case "getnear":
//                        return placeServiceImpl.GetNearPlace(parameter,userId,placeId,jsonObjectString);
//                }
//                break;
//            case "open":
//                switch (secondStr){
//                    case "add":
//                        return placeServiceImpl.AddOpen(parameter,userId,placeId,jsonObjectString,photos);
//                    case "update":
//                        return placeServiceImpl.UpdateOpen(parameter,userId,placeId,jsonObjectString);
//                    case "delete":
//                        return placeServiceImpl.DeleteOpen(parameter,userId,placeId,jsonObjectString);
//                    case "getone":
//                        return placeServiceImpl.GetOneOpen(parameter,userId,placeId,jsonObjectString);
//                    case "getmine":
//                        return placeServiceImpl.GetMyOpen(parameter,userId,placeId,jsonObjectString);
//                    case "getnear":
//                        return placeServiceImpl.GetNearOpen(parameter,userId,placeId,jsonObjectString);
//                }
//                break;
//        }
//        return new ResponseEntity(RespCode.WRONGIP,null);
//    }
//
