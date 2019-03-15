//package com.renchaigao.zujuba.messageserver.function.consumer;
//
//
//import com.alibaba.fastjson.JSONObject;
//import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
//import com.renchaigao.zujuba.dao.mapper.UserMapper;
//import com.renchaigao.zujuba.mongoDB.info.message.MessageContent;
//import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;
//import com.renchaigao.zujuba.mongoDB.info.team.TeamMessageInfo;
//import com.renchaigao.zujuba.mongoDB.info.user.UserMessagesInfo;
//import normal.dateUse;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.data.mongodb.core.query.Update;
//import org.springframework.kafka.core.KafkaTemplate;
//
//import static com.renchaigao.zujuba.PropertiesConfig.ConstantManagement.*;
//import static com.renchaigao.zujuba.PropertiesConfig.PhotoConstant.ZJB_LOGO_IMAGE;
//
//public class CreateNewTeamFunctions {
//
//    UserMapper userMapper;
//    MongoTemplate normalMongoTemplate;
//    MongoTemplate messageMongoTemplate;
//    KafkaTemplate<String, String> kafkaTemplate;
//
//    public CreateNewTeamFunctions(UserMapper userMapper, MongoTemplate normalMongoTemplate, MongoTemplate messageMongoTemplate, KafkaTemplate<String, String> kafkaTemplate) {
//        this.userMapper = userMapper;
//        this.normalMongoTemplate = normalMongoTemplate;
//        this.messageMongoTemplate = messageMongoTemplate;
//        this.kafkaTemplate = kafkaTemplate;
//    }
//
//    //        创建team对应的message
//    public void CreateTeamMessageInfo(TeamInfo teamInfo) {
//        TeamMessageInfo teamMessageInfo = new TeamMessageInfo();
//        teamMessageInfo.setId(teamInfo.getId());
//        teamMessageInfo.setUpTime(dateUse.GetStringDateNow());
//        teamMessageInfo.setCreateId(teamInfo.getCreaterId());
//        teamMessageInfo.setPlaceAdminId(teamInfo.getAddressInfo().getOwnerId());
//        messageMongoTemplate.save(teamMessageInfo,
//                MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_TEAM_MESSAGE_INFO);
//    }
//
//    public void CreaterPart(TeamInfo teamInfo) {
////        1、系统通知创建者 并 给创建者创建一个teamMessageInfo
//        MessageContent sendSystemMessageContent = new MessageContent();//创建系统发送给创建者的消息content
//        sendSystemMessageContent.setUserId(teamInfo.getCreaterId());
//        sendSystemMessageContent.setContent("您的组局创建成功~");
//        sendSystemMessageContent.setTitle("新组局消息");
//        sendSystemMessageContent.setSenderId(MESSAGE_SENDER_SYSTEM);
//        sendSystemMessageContent.setMessageClass(SYSTEM_SEND_MESSAGE);
////        设置任务链接id ————————待开发//        sendSystemMessageContent.setGotoId();
//        Long nowTimeLong = dateUse.getNowTimeLong();
//        sendSystemMessageContent.setSendTime(nowTimeLong);
//        sendSystemMessageContent.setIdLong(nowTimeLong);
//        sendSystemMessageContent.setSenderImageUrl(ZJB_LOGO_IMAGE);
//        sendSystemMessageContent.setTeamId(teamInfo.getId());
//        kafkaTemplate.send(SYSTEM_SEND_MESSAGE, JSONObject.toJSONString(sendSystemMessageContent));
//
////        2、创建者在team群里发送一个消息content
//        MessageContent userMessageContent = new MessageContent();
//        userMessageContent.setIsMe(true);
//        userMessageContent.setContent("Hi~我 创建 了这个组局，欢迎你的参与，祝玩的愉快~");
//        userMessageContent.setTitle(teamInfo.getTeamName());
//        userMessageContent.setSenderId(teamInfo.getCreaterId());
//        userMessageContent.setMessageClass(TEAM_SEND_MESSAGE);
//        userMessageContent.setSendTime(nowTimeLong);
//        userMessageContent.setIdLong(nowTimeLong);
//        userMessageContent.setSenderImageUrl(userMapper.selectByPrimaryKey(teamInfo.getCreaterId()).getPicPath());
//        userMessageContent.setTeamId(teamInfo.getId());
//
//        kafkaTemplate.send(TEAM_SEND_MESSAGE, JSONObject.toJSONString(userMessageContent));
//    }
//
//    //        系统发送一个通知给管理员 并 给管理员创建一个message
//    public void AdminPart(TeamInfo teamInfo) {
////        1、系统通知创建者 并 给创建者创建一个teamMessageInfo
//        MessageContent sendSystemMessageContent = new MessageContent();//创建系统发送给创建者的消息content
//        sendSystemMessageContent.setUserId(teamInfo.getAddressInfo().getOwnerId());
//        sendSystemMessageContent.setContent("您的场地有新组局信息啦~");
//        sendSystemMessageContent.setTitle("新组局消息");
//        sendSystemMessageContent.setSenderId(MESSAGE_SENDER_SYSTEM);
//        sendSystemMessageContent.setMessageClass(SYSTEM_SEND_MESSAGE);
////        设置任务链接id ————————待开发//        sendSystemMessageContent.setGotoId();
//        Long nowTimeLong = dateUse.getNowTimeLong();
//        sendSystemMessageContent.setSendTime(nowTimeLong);
//        sendSystemMessageContent.setIdLong(nowTimeLong);
//        sendSystemMessageContent.setSenderImageUrl(ZJB_LOGO_IMAGE);
//        sendSystemMessageContent.setTeamId(teamInfo.getId());
//        kafkaTemplate.send(SYSTEM_SEND_MESSAGE, JSONObject.toJSONString(sendSystemMessageContent));
////        2、创建者在team群里发送一个消息content
//        MessageContent userMessageContent = new MessageContent();
//        userMessageContent.setIsMe(true);
//        userMessageContent.setContent("Hi~我是此次组局的场地负责人，您有任何与场地相关的问题都可以咨询我，祝玩的愉快~");
//        userMessageContent.setTitle(teamInfo.getTeamName());
//        userMessageContent.setSenderId(teamInfo.getAddressInfo().getOwnerId());
//        userMessageContent.setMessageClass(TEAM_SEND_MESSAGE);
//        userMessageContent.setSendTime(nowTimeLong);
//        userMessageContent.setIdLong(nowTimeLong);
//        userMessageContent.setSenderImageUrl(userMapper.selectByPrimaryKey(teamInfo.getAddressInfo().getOwnerId()).getPicPath());
//        userMessageContent.setTeamId(teamInfo.getId());
//        kafkaTemplate.send(TEAM_SEND_MESSAGE, JSONObject.toJSONString(userMessageContent));
//    }
//
//}
