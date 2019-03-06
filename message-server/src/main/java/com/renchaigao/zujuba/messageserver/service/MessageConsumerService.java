package com.renchaigao.zujuba.messageserver.service;

import com.renchaigao.zujuba.mongoDB.info.message.MessageContent;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;

public interface MessageConsumerService {

    void CreateNewTeam(TeamInfo teamInfo);
    void SystemSendMessage(MessageContent messageContent);
    void FriendSendMessage(MessageContent messageContent);
    void UserSendMessage(MessageContent messageContent);

}
