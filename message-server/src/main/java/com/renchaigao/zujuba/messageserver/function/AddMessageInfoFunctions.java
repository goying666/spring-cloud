package com.renchaigao.zujuba.messageserver.function;

import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.mongoDB.info.PlayerInfo;
import com.renchaigao.zujuba.mongoDB.info.message.MessageContent;
import com.renchaigao.zujuba.mongoDB.info.team.TeamPlayerInfo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.core.KafkaTemplate;

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
    public void AddTeamMessage(MessageContent messageContent) {
//        保存这条消息到teamMessageInfo里面
        messageMongoTemplate.save(messageContent,
                MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_TEAM_MESSAGES );
//        保存这条消息到team内所有player的message里面
        NotifyPlayer(messageContent);
//        自己不保存这条消息到userMessageInfo里面
    }

    private void NotifyPlayer(MessageContent messageContent) {
//        获取玩家信息
        TeamPlayerInfo teamPlayerInfo = normalMongoTemplate.findById(messageContent.getTeamId(),
                TeamPlayerInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_TEAM_PLAYER_INFO);
        for (PlayerInfo playerInfo : teamPlayerInfo.getPlayerArrayList()) {
            if (playerInfo.getId().equals(messageContent.getSenderId()))
                messageContent.setIsMe(true);
            else
                messageContent.setIsMe(false);
            messageContent.setUserId(playerInfo.getId());
            messageContent.setIsReceived(false);
            messageMongoTemplate.save(messageContent,
                    MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE_INFO );
            normalMongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(playerInfo.getId())),
                    new Update().inc("nowTeamMessagesNumber", 1)
                            .push("userTeamMessageIdList", messageContent.getId())
                    , MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE_INFO );
        }
    }


    /*
     * 说明：用户向队伍里发消息；
     */
    public void AddClubMessage(MessageContent messageContent) {
//        保存这条消息到teamMessageInfo里面
        messageMongoTemplate.save(messageContent,
                MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_CLUB_MESSAGE_INFO );
//        保存这条消息到club内所有player的message里面
        NotifyPlayer(messageContent);
//        自己不保存这条消息到userMessageInfo里面
    }

}