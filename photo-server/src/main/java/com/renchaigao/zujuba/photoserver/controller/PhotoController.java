package com.renchaigao.zujuba.photoserver.controller;

import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.photoserver.service.impl.PhotoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

@Controller
@RequestMapping()
public class PhotoController {

    private final ResourceLoader resourceLoader;

    @Autowired
    PhotoServiceImpl photoServiceImpl;

    public PhotoController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
//    @Value("${web.upload-path}")
//    private String path;
//    @GetMapping(value = "/image/{filename}" ,consumes = "image/png" )
//    @ResponseBody
//    public void EasyGetImage(@PathVariable("filename") String filename, HttpServletResponse res){
//        photoServiceImpl.downloadFile(filename, res);
//    }

    @GetMapping(value = "/getimage/{userid}/{placeid}/{filename}")
    @ResponseBody
    public void EasyGetImage(@PathVariable("userid") String userid,
                             @PathVariable("placeid") String placeid,
                             @PathVariable("filename") String filename,
                             HttpServletResponse res) {
        photoServiceImpl.downloadFile(userid, placeid, filename, res);
    }

//    @GetMapping(value = "/showimage/{userid}/{placeid}/{filename}")
//    @ResponseBody
//    public void EasyShowImage(@PathVariable("userid") String userid,
//                              @PathVariable("placeid") String placeid,
//                              @PathVariable("filename") String filename,
//                              HttpServletResponse res) throws IOException {
//        photoServiceImpl.showFile(userid, placeid, filename, res);
//    }

    @GetMapping(value = "/{firstStr}/{secondStr}/{thirdStr}/{fourthStr}")
    @ResponseBody
    public void PhotoServiceController(@PathVariable("firstStr") String fistStr,
                                       @PathVariable("secondStr") String secondStr,
                                       @PathVariable("thirdStr") String thirdStr,
                                       @PathVariable("fourthStr") String fourthStr,
                                       HttpServletResponse res) throws IOException {
        switch (fistStr) {
            case "showimage":
                photoServiceImpl.showFile(secondStr, thirdStr, fourthStr, res);
        }
    }




//    @GetMapping(value = "/show1image/{userid}/{placeid}/{filename}",produces = {MediaType.IMAGE_JPEG_VALUE,MediaType.IMAGE_PNG_VALUE})
//    @ResponseBody
//    public BufferedImage EasyShowImage1(@PathVariable("userid") String userid,
//                                        @PathVariable("placeid") String placeid,
//                                        @PathVariable("filename") String filename,
//                                        HttpServletResponse res) throws IOException {
//        return ImageIO.read(new FileInputStream(new File(Paths.get("/zjb/download/"
//                + userid + "/" + placeid , filename).toString())));
//    }


//    @GetMapping(value = "/static/getimage/{userid}/{placeid}/{filename}")
//    @ResponseBody
//    public org.springframework.http.ResponseEntity showPhoto(
//            @PathVariable("userid") String userid,
//            @PathVariable("placeid") String placeid,
//            @PathVariable("filename") String filename) {
//        try {
//            // 由于是读取本机的文件，file是一定要加上的， path是在application配置文件中的路径
//            return org.springframework.http.ResponseEntity.ok(resourceLoader.getResource(
//                    "file:" + "/zjb/download/" +
//                    userid + "/" + placeid + "/" + filename));
//        } catch (Exception e) {
//            return org.springframework.http.ResponseEntity.notFound().build();
//        }
//    }

//
//    @GetMapping(value = "/{firstStr}/{secondStr}/{thirdStr}/{fourthStr}")
//    @ResponseBody
//    public byte[] PhotoControllerFuns(@PathVariable("firstStr") String fistStr,
//                                              @PathVariable("secondStr") String secondStr,
//                                              @PathVariable("thirdStr") String thirdStr,
//                                              @PathVariable("fourthStr") String fourthStr) {
//        switch (fistStr) {
//            case "user":
//                switch (secondStr){
//                    case "join":
//                        return placeServiceImpl.JoinStore(thirdStr,fourthStr,jsonObjectString,photos);
//            }
//                break;
//        }
//        return new ResponseEntity(RespCode.WRONGIP,null);
//    }


}
