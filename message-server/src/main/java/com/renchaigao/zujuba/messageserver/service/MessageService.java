package com.renchaigao.zujuba.messageserver.service;

import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.message.MessageContent;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;

public interface MessageService {
    //    获取messageFragment界面的所有消息：返回所有新消息
    ResponseEntity GetMessageFragmentBean(String userId);

    //    用户增加一条新消息
    ResponseEntity AddMessageInfo( MessageContent messageContent);

    //    用户获取新消息
    ResponseEntity GetMessageInfo(String userid, String ownerId, String messageClass, long lasttime);

    //    删除消息
    ResponseEntity deleteMessageInfo(String userId, String senderId, String groupId, String messageId);

    //    用户返回接收信息；
    ResponseEntity recivedOK(String userId, String senderId, String groupId, String messageId);

//    用户查询获取个人的消息信息；
//    用户查询获取所有个人的消息信息；
//    用户查询获取群组的消息信息；
//    用户查询获取所有群组的消息信息；
}
