package com.renchaigao.zujuba.messageserver.function;

import com.renchaigao.zujuba.PageBean.CardMessageFragmentTipBean;
import com.renchaigao.zujuba.PageBean.MessageFragmentCardBean;
import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.mongoDB.info.message.MessageContent;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.*;

import static com.renchaigao.zujuba.PropertiesConfig.ConstantManagement.*;

public class GetMessageFragmentBeansFunctions {

    UserMapper userMapper;
    MongoTemplate normalMongoTemplate;
    MongoTemplate messageMongoTemplate;
    KafkaTemplate<String, String> kafkaTemplate;

    public GetMessageFragmentBeansFunctions(UserMapper userMapper, MongoTemplate normalMongoTemplate, MongoTemplate messageMongoTemplate, KafkaTemplate<String, String> kafkaTemplate) {
        this.userMapper = userMapper;
        this.normalMongoTemplate = normalMongoTemplate;
        this.messageMongoTemplate = messageMongoTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    /*
     *  获取主页message的tips信息，主要功能是获取所有未读消息，将同类型消息的最后一条找出来，组装成tipBean
     * */
    public MessageFragmentCardBean GetMessageFragmentBean(String userId) {
//        先获取所有未读的消息；消息从未读变为已读是在聊天室内部请求完成的： GetMessageInfo();
        List<MessageContent> messageContentNoReadList =
                messageMongoTemplate.find(Query.query(Criteria.where("isReceived").is(false))
                        , MessageContent.class,
                        MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_MESSAGE + userId);
        MessageFragmentCardBean messageFragmentCardBean = new MessageFragmentCardBean();
        ArrayList<CardMessageFragmentTipBean> allBeanList = new ArrayList<>();
        ArrayList<CardMessageFragmentTipBean> teamPartList = new ArrayList<>();
        ArrayList<MessageContent> systemPartList = new ArrayList<>();
        ArrayList<String> teamIdList = new ArrayList<>();
        int systemNoRead = 0;
        for (MessageContent p : messageContentNoReadList) {
//            从用户的teamMessage列表里面逐一
            switch (p.getMessageClass()) {
                case TEAM_SEND_MESSAGE:
//                  处理team类
                    TeamMessagePartFunction(teamPartList, teamIdList, p);
                    break;
                case SYSTEM_SEND_MESSAGE:
//                处理系统类消息
                    systemNoRead++;
                    systemPartList.add(p);
                    break;
                case USER_SEND_MESSAGE:
//                  处理游客用户类消息
                    break;
                case FRIEND_SEND_MESSAGE:
//                  处理好友类消息
                    break;
            }
        }
        Collections.sort(systemPartList, new Comparator<MessageContent>() {
            @Override
            public int compare(MessageContent o1, MessageContent o2) {
//                    从大到小
                return (int) (o2.getSendTime() - o1.getSendTime());
            }
        });
        allBeanList.add(SystemMessagePartFunction(systemPartList.get(0), systemNoRead));
        allBeanList.addAll(teamPartList);
        messageFragmentCardBean.setAllMessageTips(allBeanList);
        return messageFragmentCardBean;
    }

    private CardMessageFragmentTipBean SystemMessagePartFunction(MessageContent p, int systemNoRead) {
        CardMessageFragmentTipBean cardMessageFragmentTipBean = new CardMessageFragmentTipBean();
        cardMessageFragmentTipBean.setContent(p.getContent());
        cardMessageFragmentTipBean.setImageUrl(p.getMyImageUrl()); //待修改成功systemUrl
        cardMessageFragmentTipBean.setMClass(SYSTEM_SEND_MESSAGE);
        cardMessageFragmentTipBean.setName(p.getTitle());
        cardMessageFragmentTipBean.setNoRead(systemNoRead);
        cardMessageFragmentTipBean.setTime(p.getSendTime().toString());
        cardMessageFragmentTipBean.setLastTime(p.getSendTime());
        cardMessageFragmentTipBean.setOwnerId(p.getSenderId());
        return cardMessageFragmentTipBean;
    }

    private void TeamMessagePartFunction(
            ArrayList<CardMessageFragmentTipBean> teamPartList,
            ArrayList<String> teamIdList, MessageContent p) {
        if (teamIdList.size() > 0) {
            for (String i : teamIdList) {
                if (p.getTeamId().equals(i)) {
//                    teamId相同，比较时间，将时间靠后的放入cardBean中
                    for (CardMessageFragmentTipBean m : teamPartList) {
                        if (m.getOwnerId().equals(i)) {
//                                        找到已有的teamId对应的那条carBean信息
                            if (m.getLastTime() < p.getSendTime()) {
                                teamPartList.remove(m);
                                addNewBeanToMessageFragmentBeans(teamPartList, p, m.getNoRead());
                            }
                        }
                    }
                } else {
                    teamIdList.add(p.getTeamId());
                    addNewBeanToMessageFragmentBeans(teamPartList, p, 0);
                }
            }
        } else {
            teamIdList.add(p.getTeamId());
            addNewBeanToMessageFragmentBeans(teamPartList, p, 0);
        }
    }

    private void addNewBeanToMessageFragmentBeans(
            ArrayList<CardMessageFragmentTipBean> cardMessageFragmentTipBeanArrayList,
            MessageContent p, int noReadNum) {
        CardMessageFragmentTipBean cardMessageFragmentTipBean = new CardMessageFragmentTipBean();
        cardMessageFragmentTipBean.setContent(p.getContent());
        cardMessageFragmentTipBean.setImageUrl(p.getMyImageUrl()); //待修改成功teamImageUrl
        cardMessageFragmentTipBean.setMClass(TEAM_SEND_MESSAGE); //mClass+ownerId 可以定位到teamId
        cardMessageFragmentTipBean.setOwnerId(p.getTeamId());
        cardMessageFragmentTipBean.setName(p.getTitle());
        cardMessageFragmentTipBean.setNoRead(noReadNum + 1);
        cardMessageFragmentTipBean.setTime(p.getSendTime().toString());
        cardMessageFragmentTipBean.setLastTime(p.getSendTime());
        cardMessageFragmentTipBeanArrayList.add(cardMessageFragmentTipBean);
    }

}
