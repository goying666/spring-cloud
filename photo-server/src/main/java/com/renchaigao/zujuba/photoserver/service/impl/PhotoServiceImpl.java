package com.renchaigao.zujuba.photoserver.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.photoserver.service.PhotoService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Paths;

@Service
public class PhotoServiceImpl implements PhotoService {

    private static Logger logger = Logger.getLogger(PhotoServiceImpl.class);


    @Autowired
    UserMapper userMapper;

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public void downloadFile(String userid, String placeid, String fileName, HttpServletResponse res) {
        if (fileName != null) {
//             通过文件名查找文件信息
//            FileInfo fileInfo = fileInfoDao.findByFileName(fileName);

            //设置响应头
//            res.setContentType("application/force-download");// 设置强制下载不打开//
            res.setContentType("image/jpeg");
//            res.set
            res.addHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名
//                    new String(fileInfo.getFileOriginName().getBytes("gbk"), "iso8859-1"));// 设置文件名
//            res.setHeader("Context-Type", "application/xmsdownload");

            //判断文件是否存在
            File file = new File(Paths.get("/zjb/download/"
                    + userid + "/" + placeid , fileName).toString());
            if (file.exists()) {
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = res.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                    logger.info("下载成功");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void showFile(String secondStr,  String thirdStr, String filename, HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        response.setHeader("Cache-Control","max-age=7776000");
        response.setHeader("Connection","keep-alive");
//        response.addHeader("Content-Disposition", "attachment;fileName=" + filename);// 设置文件名
        // 获取图片
//        File file = new File("/home/aaa.jpg");
        File file = new File(Paths.get("/zjb/download/"
                + secondStr + "/" + thirdStr , filename).toString());
        // 创建文件输入流
        FileInputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 响应输出流
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 创建缓冲区
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = is.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        is.close();
        out.flush();
        out.close();
    }

    @Override
    public void ShowFileByPath(JSONObject requestBody, HttpServletResponse response)   {

    }


}
