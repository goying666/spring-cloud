package com.renchaigao.zujuba.playerserver.service;

import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;

public interface PlayerConsumerService {

    void CreateNewTeam(TeamInfo teamInfo);

}