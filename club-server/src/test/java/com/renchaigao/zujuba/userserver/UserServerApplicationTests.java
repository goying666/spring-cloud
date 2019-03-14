package com.renchaigao.zujuba.userserver;

import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.clubserver.service.impl.ClubServiceImpl;
import com.renchaigao.zujuba.mongoDB.info.club.ClubMessageInfo;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServerApplicationTests {

//	private static Logger logger = Logger.getLogger(UserServerApplicationTests.class);
//
//	@Resource(name = "messageMongoTemplate")
//	MongoTemplate messageMongoTemplate;

	@Test
	public void contextLoads() {
//		int i = 50000;
//		while (i>0){
//			ClubMessageInfo clubMessageInfo = new ClubMessageInfo();
//			messageMongoTemplate.save(clubMessageInfo, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_CLUB_MESSAGE_INFO);
//			logger.info(i);
//			i--;
//		}
	}

}
