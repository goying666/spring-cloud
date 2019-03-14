package com.renchaigao.zujuba.teamserver.uti;

import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.dao.Address;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.dao.mapper.UserOpenInfoMapper;
import com.renchaigao.zujuba.mongoDB.info.AddressInfo;
import com.renchaigao.zujuba.mongoDB.info.message.MessageContent;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;
import com.renchaigao.zujuba.mongoDB.info.team.TeamMessageInfo;
import normal.dateUse;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.core.KafkaTemplate;

import static com.renchaigao.zujuba.PropertiesConfig.ConstantManagement.*;
import static com.renchaigao.zujuba.PropertiesConfig.GameConstant.*;
import static com.renchaigao.zujuba.PropertiesConfig.KafkaTopicConstant.CREATE_NEW_TEAM;
import static com.renchaigao.zujuba.PropertiesConfig.PhotoConstant.ZJB_LOGO_IMAGE;

public class CreateNewTeamFunctions {

    private UserMapper userMapper;
    private MongoTemplate mongoTemplate;
    private MongoTemplate messageMongoTemplate;
    private KafkaTemplate<String, String> kafkaTemplate;

    public CreateNewTeamFunctions(UserMapper userMapper, MongoTemplate mongoTemplate,
                                  MongoTemplate messageMongoTemplate, KafkaTemplate<String, String> kafkaTemplate) {
        this.userMapper = userMapper;
        this.mongoTemplate = mongoTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.messageMongoTemplate = messageMongoTemplate;
    }

    /*
     * 说明：检查用户创建组局的数据是否无误
     * 检查项：1、缺失类；2、违规类；
     */
    public Boolean CheckCreateInfo(String teamInfoString) {
        return true;
    }

    /*
     * 说明：基础信息,用户创建时填入的部分
     */
    public void CreateTeamInfoBasic(TeamInfo teamInfo) {
        mongoTemplate.save(teamInfo);
//        组队名创建：游戏+人数+“局”
        String teamName = "";
        if (teamInfo.getTeamGameInfo().isSelect_LRS()) {
            teamName = GAME_LRS;
            if (teamInfo.getTeamGameInfo().isSelect_THQBY()) {
                teamName += "," + GAME_THQBY;
            }
            if (teamInfo.getTeamGameInfo().isSelect_MXTSJ()) {
                teamName += "," + GAME_AHSL;
            }
        } else {
            if (teamInfo.getTeamGameInfo().isSelect_THQBY()) {
                teamName = GAME_THQBY;
                if (teamInfo.getTeamGameInfo().isSelect_MXTSJ()) {
                    teamName += "," + GAME_AHSL;
                }
            } else {
                if (teamInfo.getTeamGameInfo().isSelect_MXTSJ()) {
                    teamName = "," + GAME_AHSL;
                }
            }
        }
        teamName += teamInfo.getPlayerMin().toString() + "~" + teamInfo.getPlayerMax().toString() + "人局";
        teamInfo.setTeamName(teamName);

        teamInfo.setStartAllTime(TeamDateFunc.teamTimeFunc(teamInfo.getStartDate(), teamInfo.getStartTime()));

//        设置team编号：按照同城市0~999999来

    }

    /*
     * 说明：地址信息
     */
    public void CreateTeamInfoAddress(TeamInfo teamInfo) {
        Address createAddress = mongoTemplate.findById(teamInfo.getAddressInfo().getId(),
                AddressInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_ADDRESS_INFO);
        switch (createAddress.getAddressClass()) {
            case ADDRESS_CLASS_STORE:
//                通知place-server有新组局创建；
                kafkaTemplate.send(CREATE_NEW_TEAM, JSONObject.toJSONString(teamInfo));
//                更新场地组局信息（增加）

                break;
            case ADDRESS_CLASS_OPEN:
//                自检内容：1、人数超标是否？
//                更新场地组局信息（增加）
                break;
//                未开放功能；
            case ADDRESS_CLASS_USER:
                break;
        }
    }

    /*
     * 说明：玩家信息
     */
    public void CreateTeamInfoPlayer(TeamInfo teamInfo) {
//        player-server 也订阅了 topic为：CREATE_NEW_TEAM 的消息，所以不再进行通知；
//        修改team内有关player的数据；
        teamInfo.setPlayerNow(1);
    }

    /*
     * 说明：游戏信息
     */
    public void CreateTeamInfoGame(TeamInfo teamInfo) {
//        game-server 也订阅了 topic为：CREATE_NEW_TEAM 的消息，所以不再进行通知；
//        待开发
    }

    /*
     * 说明：筛选条件信息
     */
    public void CreateTeamInfoFilter(TeamInfo teamInfo) {
//        待开发

    }

    /*
     * 说明：消费信息
     */
    public void CreateTeamInfoSpend(TeamInfo teamInfo) {
//        待开发

    }

    /*
     * 说明：组队消息信息
     */
    public void CreateTeamInfoMessage(TeamInfo teamInfo) {
//        message-server 也订阅了 topic为：CREATE_NEW_TEAM 的消息，所以不再进行通知；
    }

    /*
     * 说明：请求所有信息
     */
    public void CreateTeamInfoAll(TeamInfo teamInfo) {
//        待开发
    }

    /*
     * 说明：检查组局信息是否完整
     */
    public void CheckAllInfoIsRight(TeamInfo teamInfo) {
    }

    /*
     * 说明：更新teamInfo信息
     */
    public void UpdateTeamInfoAll(TeamInfo teamInfo) {

    }

    /*
     * 说明：修改个人team信息
     */
    public void UpdateMyTeamsInfo(TeamInfo teamInfo) {
//        userTeam
        mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(teamInfo.getCreaterId())),
                new Update().push("userTeams.allTeamsList", teamInfo.getId())
                        .push("userTeams.doingTeamsList", teamInfo.getId()),
                MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_TEAMS);
    }

    /*
     * 说明：场地信息部分
     */
    public void UpdateTeamPlaceInfo(TeamInfo teamInfo) {
//        更新场地管理员的
//        根据组局时间进行分类
        switch (teamInfo.getAddressInfo().getAddressClass()) {
            case ADDRESS_CLASS_USER:
                break;
            case ADDRESS_CLASS_STORE:
                Update update = new Update();
                mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(teamInfo.getAddressInfo().getId())),
                        update.inc("allTeamsTimes", 1)
//                        .inc("allUsersNum", 1)
                                .set("upTime", dateUse.GetStringDateNow())
                                .push("storeAllTeamInfoArrayList", teamInfo)
                        , MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_TEAM_INFO);
                break;
            case ADDRESS_CLASS_OPEN:
                break;
            case ADDRESS_CLASS_HOME:
                break;
            case ADDRESS_CLASS_SCHOOL:
                break;

        }

    }

    /*
     * 说明：聊天信息部分
     */
    public void CreateTeamMessageInfo(TeamInfo teamInfo) {
//        创建team对应的message
        TeamMessageInfo teamMessageInfo = new TeamMessageInfo();
        teamMessageInfo.setId(teamInfo.getId());
        teamMessageInfo.setUpTime(dateUse.GetStringDateNow());
        teamMessageInfo.setCreateId(teamInfo.getCreaterId());
        teamMessageInfo.setPlaceAdminId(teamInfo.getAddressInfo().getOwnerId());
        messageMongoTemplate.save(teamMessageInfo,
                MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_TEAM_MESSAGE_INFO );
//        系统发送一个通知给创建者 并且 创建者发送一个team的message
        //        1、系统通知创建者 并 给创建者创建一个teamMessageInfo
        MessageContent sendSystemMessageContent = new MessageContent();//创建系统发送给创建者的消息content
        sendSystemMessageContent.setUserId(teamInfo.getCreaterId());
        sendSystemMessageContent.setContent("您的组局创建成功~");
        sendSystemMessageContent.setTitle("新组局消息");
        sendSystemMessageContent.setSenderId(MESSAGE_SENDER_SYSTEM);
        sendSystemMessageContent.setMessageClass(SYSTEM_SEND_MESSAGE);
//        设置任务链接id ————————待开发//        sendSystemMessageContent.setGotoId();
        Long nowTimeLong = dateUse.getNowTimeLong();
        sendSystemMessageContent.setSendTime(nowTimeLong);
        sendSystemMessageContent.setIdLong(nowTimeLong);
        sendSystemMessageContent.setSenderImageUrl(ZJB_LOGO_IMAGE);
        sendSystemMessageContent.setTeamId(teamInfo.getId());
        kafkaTemplate.send(SYSTEM_SEND_MESSAGE, JSONObject.toJSONString(sendSystemMessageContent));

//        2、创建者在team群里发送一个消息content
        MessageContent userMessageContent = new MessageContent();
        userMessageContent.setIsMe(true);
        userMessageContent.setContent("Hi~我 创建 了这个组局，欢迎你的参与，祝玩的愉快~");
        userMessageContent.setTitle(teamInfo.getTeamName());
        userMessageContent.setSenderId(teamInfo.getCreaterId());
        userMessageContent.setMessageClass(TEAM_SEND_MESSAGE);
        userMessageContent.setSendTime(nowTimeLong);
        userMessageContent.setIdLong(nowTimeLong);
        userMessageContent.setSenderImageUrl(userMapper.selectByPrimaryKey(teamInfo.getCreaterId()).getPicPath());
        userMessageContent.setTeamId(teamInfo.getId());

        kafkaTemplate.send(TEAM_SEND_MESSAGE, JSONObject.toJSONString(userMessageContent));
//        系统发送一个通知给管理员 并且 管理员发送一个team的message
        //        1、系统通知创建者 并 给创建者创建一个teamMessageInfo
        sendSystemMessageContent = new MessageContent();//创建系统发送给创建者的消息content
        sendSystemMessageContent.setUserId(teamInfo.getAddressInfo().getOwnerId());
        sendSystemMessageContent.setContent("您的场地有新组局信息啦~");
        sendSystemMessageContent.setTitle("新组局消息");
        sendSystemMessageContent.setSenderId(MESSAGE_SENDER_SYSTEM);
        sendSystemMessageContent.setMessageClass(SYSTEM_SEND_MESSAGE);
//        设置任务链接id ————————待开发//        sendSystemMessageContent.setGotoId();
        nowTimeLong = dateUse.getNowTimeLong();
        sendSystemMessageContent.setSendTime(nowTimeLong);
        sendSystemMessageContent.setIdLong(nowTimeLong);
        sendSystemMessageContent.setSenderImageUrl(ZJB_LOGO_IMAGE);
        sendSystemMessageContent.setTeamId(teamInfo.getId());
        kafkaTemplate.send(SYSTEM_SEND_MESSAGE, JSONObject.toJSONString(sendSystemMessageContent));
//        2、创建者在team群里发送一个消息content
        userMessageContent = new MessageContent();
        userMessageContent.setIsMe(true);
        userMessageContent.setContent("Hi~我是此次组局的场地负责人，您有任何与场地相关的问题都可以咨询我，祝玩的愉快~");
        userMessageContent.setTitle(teamInfo.getTeamName());
        userMessageContent.setSenderId(teamInfo.getAddressInfo().getOwnerId());
        userMessageContent.setMessageClass(TEAM_SEND_MESSAGE);
        userMessageContent.setSendTime(nowTimeLong);
        userMessageContent.setIdLong(nowTimeLong);
        userMessageContent.setSenderImageUrl(userMapper.selectByPrimaryKey(teamInfo.getAddressInfo().getOwnerId()).getPicPath());
        userMessageContent.setTeamId(teamInfo.getId());
        kafkaTemplate.send(TEAM_SEND_MESSAGE, JSONObject.toJSONString(userMessageContent));
    }

}
