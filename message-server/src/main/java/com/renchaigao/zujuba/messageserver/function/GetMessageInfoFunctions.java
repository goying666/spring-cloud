package com.renchaigao.zujuba.messageserver.function;

import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.message.ClubMessages;
import com.renchaigao.zujuba.mongoDB.info.message.FriendMessages;
import com.renchaigao.zujuba.mongoDB.info.message.SystemMessages;
import com.renchaigao.zujuba.mongoDB.info.message.TeamMessages;
import com.renchaigao.zujuba.mongoDB.info.user.UserMessagesInfo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;

public class GetMessageInfoFunctions {

    UserMapper userMapper;
    MongoTemplate normalMongoTemplate;
    MongoTemplate messageMongoTemplate;
    KafkaTemplate<String, String> kafkaTemplate;

    public GetMessageInfoFunctions(UserMapper userMapper, MongoTemplate normalMongoTemplate, MongoTemplate messageMongoTemplate, KafkaTemplate<String, String> kafkaTemplate) {
        this.userMapper = userMapper;
        this.normalMongoTemplate = normalMongoTemplate;
        this.messageMongoTemplate = messageMongoTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    /*
     * 说明：获取一个队伍里的消息；
     */
    public ResponseEntity GetTeamMessageInfo(String userid, String teamId) {
//        获取用户的team未读信息id
        UserMessagesInfo userMessageInfo = normalMongoTemplate.findById(userid, UserMessagesInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE_INFO);
        if (userMessageInfo != null) {
            ArrayList<String> teamMessageNoReadIdList = userMessageInfo.getUserTeamMessageIdList();
            ArrayList<TeamMessages> teamMessagesArrayList = new ArrayList<>();
            for (String teamMessageId : teamMessageNoReadIdList) {
//                取未读消息，并将消息中的未读信息删除用户
                TeamMessages teamMessages = messageMongoTemplate.findAndModify(
                        Query.query(Criteria.where("_id").is(teamMessageId)
                                .andOperator(Criteria.where("teamId").is(teamId)))
                        , new Update().pull("readList", userid)
                        , TeamMessages.class
                        , MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_TEAM_MESSAGES);
                if (teamMessages != null) {
                    teamMessagesArrayList.add(teamMessages);
                    normalMongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(userid))
                            , new Update().pull("userTeamMessageIdList", teamMessageId)
                                    .inc("nowTeamMessagesNumber",-1)
                            , UserMessagesInfo.class
                            , MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE_INFO);
                }
            }
            return new ResponseEntity(RespCode.MESSAGE_USER_GET_TEAM_SUCCESS, teamMessagesArrayList);
        } else {
            return new ResponseEntity(RespCode.MESSAGE_USER_GET_TEAM_ZERO, null);
        }
    }

    /*
     * 说明：获取一个俱乐部里的消息；
     */
    public ResponseEntity GetClubMessageInfo(String userid, String clubId) {
//        获取用户的team未读信息id
        UserMessagesInfo userMessageInfo = normalMongoTemplate.findById(userid, UserMessagesInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE_INFO);
        if (userMessageInfo != null) {
            ArrayList<String> userClubMessageIdList = userMessageInfo.getUserClubMessageIdList();
            ArrayList<ClubMessages> clubMessagesArrayList = new ArrayList<>();
            for (String clubMessageId : userClubMessageIdList) {
//                取未读消息，并将消息中的未读信息删除用户
                ClubMessages clubMessages = messageMongoTemplate.findAndModify(
                        Query.query(Criteria.where("_id").is(clubMessageId)
                                .andOperator(Criteria.where("clubId").is(clubId)))
                        , new Update().pull("readList", userid)
                        , ClubMessages.class
                        , MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_CLUB_MESSAGES);
//                取出所有未读的clubList后，不一定全都是同一个club的，所以需要选择性的剔除clubId
                if (clubMessages != null) {
                    clubMessagesArrayList.add(clubMessages);
                    normalMongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(userid))
                            , new Update().pull("userClubMessageIdList", clubMessageId)
                                    .inc("nowClubMessagesNumber",-1)
                            , UserMessagesInfo.class
                            , MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE_INFO);
                }
            }
            return new ResponseEntity(RespCode.MESSAGE_USER_GET_CLUB_SUCCESS, clubMessagesArrayList);
        } else {
            return new ResponseEntity(RespCode.MESSAGE_USER_GET_CLUB_ZERO, null);
        }
    }

    /*
     * 说明：获取和一个好友的消息；
     */
    public ResponseEntity GetFriendMessageInfo(String userid, String friendId) {
//        获取用户的team未读信息id
        UserMessagesInfo userMessageInfo = normalMongoTemplate.findById(userid, UserMessagesInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE_INFO);
        if (userMessageInfo != null) {
            ArrayList<String> friendMessageNoReadIdList = userMessageInfo.getUserFriendsMessageIdList();
            ArrayList<FriendMessages> friendMessagesArrayList = new ArrayList<>();
            for (String friendMessageId : friendMessageNoReadIdList) {
//                取未读消息，并将消息中的未读信息删除用户
                FriendMessages friendMessages = messageMongoTemplate.findAndModify(
                        Query.query(Criteria.where("_id").is(friendMessageId)
                                .andOperator(Criteria.where("clubId").is(friendId)))
                        , new Update().pull("readList", userid)
                        , FriendMessages.class
                        , MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_FRIEND_MESSAGES);
//                取出所有未读的clubList后，不一定全都是同一个club的，所以需要选择性的剔除clubId
                if (friendMessages != null) {
                    friendMessagesArrayList.add(friendMessages);
                    normalMongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(userid))
                            , new Update().pull("userFriendsMessageIdList", friendMessageId)
                                    .inc("nowFriendMessagesNumber",-1)
                            , UserMessagesInfo.class
                            , MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE_INFO);
                }
            }
            return new ResponseEntity(RespCode.MESSAGE_USER_GET_FRIEND_SUCCESS, friendMessagesArrayList);
        } else {
            return new ResponseEntity(RespCode.MESSAGE_USER_GET_FRIEND_ZERO, null);
        }
    }

    /*
     * 说明：获取个人的系统消息；
     */
    public ResponseEntity GetSystemMessageInfo(String userid) {
        UserMessagesInfo userMessageInfo = normalMongoTemplate.findById(userid, UserMessagesInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE_INFO);
        if (userMessageInfo != null) {
            ArrayList<String> userSystemMessageIdList = userMessageInfo.getUserSystemMessageIdList();
            ArrayList<SystemMessages> systemMessagesArrayList = new ArrayList<>();
            for (String systemMessageId : userSystemMessageIdList) {
                SystemMessages systemMessages = messageMongoTemplate.findAndModify(
                        Query.query(Criteria.where("_id").is(systemMessageId))
                        , new Update().pull("readList", userid)
                        , SystemMessages.class
                        , MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_SYSTEM_MESSAGES);
                if (systemMessages != null) {
                    systemMessagesArrayList.add(systemMessages);
                    normalMongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(userid))
                            , new Update().pull("userSystemMessageIdList", systemMessageId)
                            .inc("nowSystemMessagesNumber",-1)
                            , UserMessagesInfo.class
                            , MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE_INFO);
                }
            }
            return new ResponseEntity(RespCode.MESSAGE_USER_GET_TEAM_SUCCESS, systemMessagesArrayList);
        } else {
            return new ResponseEntity(RespCode.MESSAGE_USER_GET_TEAM_ZERO, null);
        }
    }

}