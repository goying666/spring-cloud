package com.renchaigao.zujuba.messageserver.function;

import com.renchaigao.zujuba.PageBean.CardMessageFragmentTipBean;
import com.renchaigao.zujuba.PageBean.MessageFragmentCardBean;
import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.message.*;
import com.renchaigao.zujuba.mongoDB.info.user.UserMessagesInfo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;

import static com.renchaigao.zujuba.PropertiesConfig.ConstantManagement.*;

public class GetMessageFragmentBeansFunctions {

    private MongoTemplate normalMongoTemplate;
    private MongoTemplate messageMongoTemplate;

    public GetMessageFragmentBeansFunctions(MongoTemplate normalMongoTemplate, MongoTemplate messageMongoTemplate) {
        this.normalMongoTemplate = normalMongoTemplate;
        this.messageMongoTemplate = messageMongoTemplate;

    }

    /*
     *  获取主页message的tips信息，主要功能是获取所有未读消息，将同类型消息的最后一条找出来，组装成tipBean
     * */
    public ResponseEntity GetMessageFragmentBean(String userId) {
        MessageFragmentCardBean messageFragmentCardBean = new MessageFragmentCardBean();
//        获取用户的未读消息列表，
        UserMessagesInfo userMessagesInfo = normalMongoTemplate.findById(userId, UserMessagesInfo.class,
                MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE_INFO);

        if (userMessagesInfo != null) {
            ArrayList<TeamMessages> teamMessagesArrayList = new ArrayList<>();
            ArrayList<SystemMessages> systemMessagesArrayList = new ArrayList<>();
            ArrayList<ClubMessages> clubMessagesArrayList = new ArrayList<>();
            ArrayList<FriendMessages> friendMessagesArrayList = new ArrayList<>();
            ArrayList<CardMessageFragmentTipBean> allBeanList = new ArrayList<>();
            ArrayList<CardMessageFragmentTipBean> teamPartList = new ArrayList<>();
            ArrayList<CardMessageFragmentTipBean> clubPartList = new ArrayList<>();
            ArrayList<CardMessageFragmentTipBean> friendPartList = new ArrayList<>();
            ArrayList<String> teamIdList = new ArrayList<>();
            ArrayList<String> clubIdList = new ArrayList<>();
            ArrayList<String> friendIdList = new ArrayList<>();

            ArrayList<String> teamMessageNoReadList = userMessagesInfo.getUserTeamMessageIdList();
            ArrayList<String> systemMessageNoReadList = userMessagesInfo.getUserSystemMessageIdList();
            ArrayList<String> clubMessageNoReadList = userMessagesInfo.getUserClubMessageIdList();
            ArrayList<String> friendMessageNoReadList = userMessagesInfo.getUserFriendsMessageIdList();

            Integer allNoRead = 0;
            if(teamMessageNoReadList.size()>0){
                for (String teamId : teamMessageNoReadList) {
                    normalMongoTemplate.updateFirst(
                            Query.query(Criteria.where("_id").is(userId))
                            , new Update().pull("userTeamMessageIdList", teamId), UserMessagesInfo.class,
                            MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE_INFO);
                    TeamMessages teamMessages = messageMongoTemplate.findAndModify(
                            Query.query(Criteria.where("_id").is(teamId))
                            , new Update().pull("readList", userId), TeamMessages.class,
                            MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_TEAM_MESSAGES);
                    teamMessagesArrayList.add(teamMessages);
                    MessageTipBeanFunction(teamPartList, teamIdList, teamMessages, TEAM_SEND_MESSAGE);
                    allNoRead++;
                }
            }

            if(systemMessageNoReadList.size()>0){
                for (String systemId : systemMessageNoReadList) {
                    normalMongoTemplate.updateFirst(
                            Query.query(Criteria.where("_id").is(userId))
                            , new Update().pull("userSystemMessageIdList", systemId), UserMessagesInfo.class,
                            MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE_INFO);
                    systemMessagesArrayList.add(messageMongoTemplate.findAndModify(
                            Query.query(Criteria.where("_id").is(systemId))
                            , new Update().pull("readList", userId), SystemMessages.class,
                            MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_SYSTEM_MESSAGES));
                    allNoRead++;
                }
            }

            if(clubMessageNoReadList.size()>0){
                for (String clubId : clubMessageNoReadList) {
                    normalMongoTemplate.updateFirst(
                            Query.query(Criteria.where("_id").is(userId))
                            , new Update().pull("userClubMessageIdList", clubId), UserMessagesInfo.class,
                            MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE_INFO);
                    ClubMessages clubMessages = messageMongoTemplate.findAndModify(
                            Query.query(Criteria.where("_id").is(clubId))
                            , new Update().pull("readList", userId), ClubMessages.class,
                            MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_CLUB_MESSAGES);
                    clubMessagesArrayList.add(clubMessages);
                    MessageTipBeanFunction(clubPartList, clubIdList, clubMessages, CLUB_SEND_MESSAGE);
                    allNoRead++;
                }
            }

            if(friendMessageNoReadList.size()>0){
                for (String friendId : friendMessageNoReadList) {
                    normalMongoTemplate.updateFirst(
                            Query.query(Criteria.where("_id").is(userId))
                            , new Update().pull("userFriendsMessageIdList", friendId), UserMessagesInfo.class,
                            MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE_INFO);
                    FriendMessages friendMessages = messageMongoTemplate.findAndModify(
                            Query.query(Criteria.where("_id").is(friendId))
                            , new Update().pull("readList", userId), FriendMessages.class,
                            MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_FRIEND_MESSAGES);
                    friendMessagesArrayList.add(friendMessages);
                    MessageTipBeanFunction(friendPartList, friendIdList, friendMessages, FRIEND_SEND_MESSAGE);
                    allNoRead++;
                }
            }


            if (allNoRead == 0)
                return new ResponseEntity(RespCode.MESSAGE_USER_GET_ALL_ZERO, null);
            messageFragmentCardBean.setClubMessagesArrayList(clubMessagesArrayList);
            messageFragmentCardBean.setFriendMessagesArrayList(friendMessagesArrayList);
            messageFragmentCardBean.setSystemMessagesArrayList(systemMessagesArrayList);
            messageFragmentCardBean.setTeamMessagesArrayList(teamMessagesArrayList);
            messageFragmentCardBean.setAllNoRead(allNoRead);
            systemMessagesArrayList.sort((o1, o2) -> {
                return (int) (o2.getSendTime() - o1.getSendTime());//从大到小
            });
//        将系统数据归为最后一条未显示Tip；
            allBeanList.add(SystemMessagePartFunction(systemMessagesArrayList.get(0), systemMessagesArrayList.size()));
//        将Team消息数据归为多条以teamId为划分的TipBean；
            allBeanList.addAll(teamPartList);
//        将Club消息数据归为多条以clubId为划分的TipBean；
            allBeanList.addAll(clubPartList);
//        将Friend消息数据归为多条以friendId为划分的TipBean；
            allBeanList.addAll(friendPartList);
//        最后组装
            messageFragmentCardBean.setAllMessageTips(allBeanList);
            return new ResponseEntity(RespCode.MESSAGE_USER_GET_ALL_SUCCESS, messageFragmentCardBean);
        }
        return new ResponseEntity(RespCode.MESSAGE_USER_GET_ALL_FAIL, null);

////        如果没有未读消息直接返回
//
////        如果有：分类查询，将所有内容打包发送给用户，并将 tipBEAN封装好
//
//
////        先获取所有未读的消息；消息从未读变为已读是在聊天室内部请求完成的： GetMessageInfo();
////        List<MessageContent> messageContentNoReadList =
////                messageMongoTemplate.find(Query.query(Criteria.where("isReceived").is(false))
////                        , MessageContent.class,
////                        MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE_INFO);
////        ArrayList<CardMessageFragmentTipBean> teamPartList = new ArrayList<>();
////        ArrayList<MessageContent> systemPartList = new ArrayList<>();
////        ArrayList<String> teamIdList = new ArrayList<>();
////        int systemNoRead = 0;
////        for (MessageContent p : messageContentNoReadList) {
//////            从用户的teamMessage列表里面逐一
////            switch (p.getMessageClass()) {
////                case TEAM_SEND_MESSAGE:
//////                  处理team类
////                    TeamMessagePartFunction(teamPartList, teamIdList, p);
////                    break;
////                case SYSTEM_SEND_MESSAGE:
//////                处理系统类消息
////                    systemNoRead++;
////                    systemPartList.add(p);
////                    break;
////                case USER_SEND_MESSAGE:
//////                  处理游客用户类消息
////                    break;
////                case FRIEND_SEND_MESSAGE:
//////                  处理好友类消息
////                    break;
////            }
////        }
////        Collections.sort(systemPartList, new Comparator<MessageContent>() {
////            @Override
////            public int compare(MessageContent o1, MessageContent o2) {
//////                    从大到小
////                return (int) (o2.getSendTime() - o1.getSendTime());
////            }
////        });
//        allBeanList.add(SystemMessagePartFunction(systemPartList.get(0), systemNoRead));
//        allBeanList.addAll(teamPartList);
//        messageFragmentCardBean.setAllMessageTips(allBeanList)
    }

    /*
     *   将多条未读的系统信息合并成一条未读信息的TipBean（显示在最外围的）
     * */
    private CardMessageFragmentTipBean SystemMessagePartFunction(SystemMessages p, int systemNoRead) {
        CardMessageFragmentTipBean cardMessageFragmentTipBean = new CardMessageFragmentTipBean();
        cardMessageFragmentTipBean.setContent(p.getContent());
        cardMessageFragmentTipBean.setImageUrl(p.getMyImageUrl()); //待修改成功systemUrl
        cardMessageFragmentTipBean.setMClass(SYSTEM_SEND_MESSAGE);
        cardMessageFragmentTipBean.setName(p.getTitle());
        cardMessageFragmentTipBean.setNoRead(systemNoRead);
        cardMessageFragmentTipBean.setTime(p.getSendTime().toString());
        cardMessageFragmentTipBean.setLastTime(p.getSendTime());
        cardMessageFragmentTipBean.setOwnerId(SYSTEM_SEND_MESSAGE);
        return cardMessageFragmentTipBean;
    }

    /*
     *   将team、friend、club 三种类型的 给子多条未读的系统信息合并成同类不同拥有者的TipBean（显示在最外围的）
     * */
    private void MessageTipBeanFunction(ArrayList<CardMessageFragmentTipBean> sameClassTipBeanList,
                                        ArrayList<String> sameClassIdList, MessageContent p, String sameClass) {
        switch (sameClass) {
            case TEAM_SEND_MESSAGE:
                if (sameClassIdList.size() > 0) {
//                    轮训从同类比较是否为同一个team
                    for (String i : sameClassIdList) {
                        if (p.getTeamId().equals(i)) {
//                      teamId相同，说明此team有更早的数据，比较时间，将时间靠后的放入cardBean中
                            sameFuncUse(sameClassTipBeanList, p, sameClass, i);
                        } else {
//                            teamId不相同，说明是新的team，将其保存
                            sameClassIdList.add(p.getTeamId());
                            addNewBeanToMessageFragmentBeans(sameClassTipBeanList, p, 0, sameClass);
                        }
                    }
                } else {
//                    该类型第一条数据，插入此类的IDlist中
                    sameClassIdList.add(p.getTeamId());
                    addNewBeanToMessageFragmentBeans(sameClassTipBeanList, p, 0, sameClass);
                }
                break;
            case CLUB_SEND_MESSAGE:
                if (sameClassIdList.size() > 0) {
                    for (String i : sameClassIdList) {
                        if (p.getClubId().equals(i)) {
                            sameFuncUse(sameClassTipBeanList, p, sameClass, i);
                        } else {
                            sameClassIdList.add(p.getClubId());
                            addNewBeanToMessageFragmentBeans(sameClassTipBeanList, p, 0, sameClass);
                        }
                    }
                } else {
                    sameClassIdList.add(p.getClubId());
                    addNewBeanToMessageFragmentBeans(sameClassTipBeanList, p, 0, sameClass);
                }
                break;
            case FRIEND_SEND_MESSAGE:
                if (sameClassIdList.size() > 0) {
                    for (String i : sameClassIdList) {
                        if (p.getFriendId().equals(i)) {
                            sameFuncUse(sameClassTipBeanList, p, sameClass, i);
                        } else {
                            sameClassIdList.add(p.getFriendId());
                            addNewBeanToMessageFragmentBeans(sameClassTipBeanList, p, 0, sameClass);
                        }
                    }
                } else {
                    sameClassIdList.add(p.getFriendId());
                    addNewBeanToMessageFragmentBeans(sameClassTipBeanList, p, 0, sameClass);
                }
                break;
        }

    }

    private void sameFuncUse(ArrayList<CardMessageFragmentTipBean> sameClassTipBeanList, MessageContent p, String sameClass, String i) {
        for (CardMessageFragmentTipBean m : sameClassTipBeanList) {
            if (m.getOwnerId().equals(i)) {
//                                        找到已有的teamId对应的那条carBean信息
                if (m.getLastTime() < p.getSendTime()) {
                    sameClassTipBeanList.remove(m);
                    addNewBeanToMessageFragmentBeans(sameClassTipBeanList, p, m.getNoRead(), sameClass);
                }
            }
        }
    }

    /*
     * 将新的TipBean加入队列中。
     * */
    private void addNewBeanToMessageFragmentBeans(
            ArrayList<CardMessageFragmentTipBean> cardMessageFragmentTipBeanArrayList,
            MessageContent p, int noReadNum, String sameClass) {
        CardMessageFragmentTipBean cardMessageFragmentTipBean = new CardMessageFragmentTipBean();
        cardMessageFragmentTipBean.setContent(p.getContent());
        cardMessageFragmentTipBean.setImageUrl(p.getMyImageUrl()); //待修改成功teamImageUrl
        cardMessageFragmentTipBean.setMClass(sameClass); //mClass+ownerId 可以定位到teamId
        switch (sameClass) {
            case TEAM_SEND_MESSAGE:
                cardMessageFragmentTipBean.setOwnerId(p.getTeamId());
                break;
            case CLUB_SEND_MESSAGE:
                cardMessageFragmentTipBean.setOwnerId(p.getClubId());
                break;
            case FRIEND_SEND_MESSAGE:
                cardMessageFragmentTipBean.setOwnerId(p.getFriendId());
                break;
        }
        cardMessageFragmentTipBean.setName(p.getTitle());
        cardMessageFragmentTipBean.setNoRead(noReadNum + 1);
        cardMessageFragmentTipBean.setTime(p.getSendTime().toString());
        cardMessageFragmentTipBean.setLastTime(p.getSendTime());
        cardMessageFragmentTipBeanArrayList.add(cardMessageFragmentTipBean);
    }

}
