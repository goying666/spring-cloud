package com.renchaigao.zujuba.messageserver.function;

import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.PlayerInfo;
import com.renchaigao.zujuba.mongoDB.info.club.ClubInfo;
import com.renchaigao.zujuba.mongoDB.info.message.MessageContent;
import com.renchaigao.zujuba.mongoDB.info.team.TeamPlayerInfo;
import com.renchaigao.zujuba.mongoDB.info.user.UserMessagesInfo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;

public class AddMessageInfoFunctions {

    UserMapper userMapper;
    MongoTemplate normalMongoTemplate;
    MongoTemplate messageMongoTemplate;
    KafkaTemplate<String, String> kafkaTemplate;

    public AddMessageInfoFunctions(UserMapper userMapper, MongoTemplate normalMongoTemplate, MongoTemplate messageMongoTemplate, KafkaTemplate<String, String> kafkaTemplate) {
        this.userMapper = userMapper;
        this.normalMongoTemplate = normalMongoTemplate;
        this.messageMongoTemplate = messageMongoTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    /*
     * 说明：用户向队伍里发消息；
     */
    public ResponseEntity AddTeamMessage(MessageContent messageContent) {
        try {
//        获取玩家信息
            TeamPlayerInfo teamPlayerInfo = normalMongoTemplate.findById(messageContent.getTeamId(),
                    TeamPlayerInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_TEAM_PLAYER_INFO);
            if (teamPlayerInfo != null) {
                for (PlayerInfo playerInfo : teamPlayerInfo.getPlayerArrayList()) {
//                修改个人消息表
                    normalMongoTemplate.updateFirst(
                            Query.query(Criteria.where("_id").is(playerInfo.getId()))
                            , new Update()
                                    .inc("nowTeamMessagesNumber", 1)
                                    .push("userTeamMessageIdList", messageContent.getId())
                            , UserMessagesInfo.class
                            , MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE_INFO);
//                修改消息表内玩家的读取id
                    messageContent.getReadList().add(playerInfo.getId());
                }
            }
            messageMongoTemplate.save(messageContent,
                    MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_TEAM_MESSAGES);
            return new ResponseEntity(RespCode.MESSAGE_ADD_TEAM_SUCCESS, null);
        } catch (Exception e) {
            return new ResponseEntity(RespCode.MESSAGE_ADD_TEAM_FAIL, null);
        }

    }

    /*
     * 说明：系统向用户发消息；
     */
    public ResponseEntity AddSystemMessage(MessageContent messageContent) {
        try {
//        修改个人信息
            normalMongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(messageContent.getUserId()))
                    , new Update().push("userSystemMessageIdList", messageContent.getId())
                            .inc("nowSystemMessagesNumber", 1)
                    , UserMessagesInfo.class
                    , MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE_INFO);
//        增加系统信息
            messageMongoTemplate.save(messageContent, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_SYSTEM_MESSAGES);
            return new ResponseEntity(RespCode.MESSAGE_ADD_SYSTEM_SUCCESS, null);
        } catch (Exception e) {
            return new ResponseEntity(RespCode.MESSAGE_ADD_SYSTEM_FAIL, null);
        }
    }


    /*
     * 说明：用户向俱乐部里发消息；
     */
    public ResponseEntity AddClubMessage(MessageContent messageContent) {
        try {
//        获取玩家信息
            ClubInfo clubInfo = normalMongoTemplate.findOne(Query.query(Criteria.where("_id").is(messageContent.getClubId())),
                    ClubInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_CLUB_INFO);
            ArrayList<String> userIdList = clubInfo.getUserIdList();
            for (String userId : userIdList) {
                normalMongoTemplate.updateFirst(
                        Query.query(Criteria.where("_id").is(userId))
                        , new Update()
                                .inc("nowClubMessagesNumber", 1)
                                .push("userClubMessageIdList", messageContent.getId())
                        , UserMessagesInfo.class
                        , MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE_INFO);
            }
            messageContent.setReadList(userIdList);
            messageMongoTemplate.save(messageContent,
                    MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_CLUB_MESSAGES);
            return new ResponseEntity(RespCode.MESSAGE_ADD_CLUB_SUCCESS, null);
        } catch (Exception e) {
            return new ResponseEntity(RespCode.MESSAGE_ADD_CLUB_FAIL, null);
        }
    }

    /*
     * 说明：用户其他用户发消息；
     */
    public ResponseEntity AddFriendMessage(MessageContent messageContent) {
        try {
//        获取玩家信息
            normalMongoTemplate.updateFirst(
                    Query.query(Criteria.where("_id").is(messageContent.getSenderId()))
                    , new Update()
                            .inc("nowFriendMessagesNumber", 1)
                            .push("userFriendsMessageIdList", messageContent.getId())
                    , UserMessagesInfo.class
                    , MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE_INFO);
            normalMongoTemplate.updateFirst(
                    Query.query(Criteria.where("_id").is(messageContent.getFriendId()))
                    , new Update()
                            .inc("nowFriendMessagesNumber", 1)
                            .push("userFriendsMessageIdList", messageContent.getId())
                    , UserMessagesInfo.class
                    , MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE_INFO);
            messageContent.getReadList().add(messageContent.getSenderId());
            messageContent.getReadList().add(messageContent.getFriendId());
            messageMongoTemplate.save(messageContent,
                    MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_FRIEND_MESSAGES);
            return new ResponseEntity(RespCode.MESSAGE_ADD_FRIEND_SUCCESS, null);
        } catch (Exception e) {
            return new ResponseEntity(RespCode.MESSAGE_ADD_FRIEND_FAIL, null);
        }
    }
}