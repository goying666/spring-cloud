package com.renchaigao.zujuba.storeserver.function;

import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.mongoDB.info.AddressInfo;
import com.renchaigao.zujuba.mongoDB.info.Photo;
import com.renchaigao.zujuba.mongoDB.info.store.BusinessPart.StoreBusinessInfo;
import com.renchaigao.zujuba.mongoDB.info.store.EquipmentPart.StoreEquipmentInfo;
import com.renchaigao.zujuba.mongoDB.info.store.GoodsPart.StorePackageInfo;
import com.renchaigao.zujuba.mongoDB.info.store.HardwarePart.DeskInfo;
import com.renchaigao.zujuba.mongoDB.info.store.HardwarePart.StoreHardwareInfo;
import com.renchaigao.zujuba.mongoDB.info.store.*;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;
import com.renchaigao.zujuba.mongoDB.info.user.UserPlaces;
import normal.dateUse;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

import static com.renchaigao.zujuba.PropertiesConfig.ConstantManagement.STORE_STATE_CHECK;

public class AddStoreFunctions {

    UserMapper userMapper;
    MongoTemplate mongoTemplate;

    public AddStoreFunctions(UserMapper userMapper, MongoTemplate mongoTemplate) {
        this.userMapper = userMapper;
        this.mongoTemplate = mongoTemplate;
    }

    private String creatPhotoFilePath(String userId, String storeId) {
        String path = "/zjb/download/" + userId + "/" + storeId;
        File file = new File(path);
        if (!file.exists())
            file.mkdirs();
        return path + "/";
    }

    /*
     * 说明：图片信息部分,1、处理图片大小、格式；2、存储图片；3、存储图片数据到数据库；
     */
    public void JoinStoreInfoPhotos(String userId, String placeId, StoreInfo storeInfo, MultipartFile[] photos) throws Exception {
//        处理图片大小（待开发）

//        存储图片
        ArrayList<Photo> photoInfoList = new ArrayList<>();
        for (MultipartFile file : photos) {
            //将不同storeID的文件放入不同的以storeID命名的文件夹下；
            String filePath = creatPhotoFilePath(userId, placeId)
                    + file.getOriginalFilename();
            // 转存文件
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
            out.write(file.getBytes());
            out.flush();
            out.close();
            Photo photoUse = new Photo();
            photoUse.setPhotoPath(filePath);
            photoUse.setPhotoName(file.getOriginalFilename());
            photoUse.setOwnerId(placeId);
            photoUse.setOwnerClass("D");
            photoUse.setReplace(false);
            photoInfoList.add(photoUse);
        }

//        存储图片数据到数据库
        StorePhotoInfo storePhotoInfo = storeInfo.getStorePhotoInfo();
        storePhotoInfo.setStoreAllPhotos(photoInfoList);
        storePhotoInfo.setId(placeId);
        mongoTemplate.save(storePhotoInfo, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_PHOTOINFO);
    }


    /*
     * 说明：检查创建信息正确性、完整性
     */
    public Boolean CheckCreatInfoIsOk(String storeId, String storeInfoStr, MultipartFile[] photos) {
//        其他检查项，待开发：
        StoreInfo storeInfo = mongoTemplate.findById(storeId, StoreInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_INFO);
        return storeInfo == null && storeInfoStr != null && photos.length != 0;
    }


    /*
     * 说明：基础信息,用户创建时填入的部分
     */
    public void JoinStoreInfoBasic(StoreInfo storeInfo) {
        storeInfo.setUpTime(dateUse.DateToString(new Date()));
        storeInfo.setCreaterId(storeInfo.getOwnerId());
        storeInfo.setCreateTime(dateUse.DateToString(new Date()));
        storeInfo.setState(STORE_STATE_CHECK);
        storeInfo.setDeleteStyle(false);
        mongoTemplate.save(storeInfo);
    }

    /*
     * 说明：地址信息信息
     */
    public void JoinStoreInfoAddress(StoreInfo storeInfo) {
        AddressInfo addressInfo = storeInfo.getAddressInfo();
        addressInfo.setId(storeInfo.getId());
        addressInfo.setAddressClass("store");
        addressInfo.setStarValue(1);
        storeInfo.setAddressInfo(addressInfo);
        mongoTemplate.save(addressInfo);
    }

    /*
     * 说明：组局信息
     */
    public void JoinStoreInfoTeam(StoreInfo storeInfo) {
        StoreTeamInfo storeTeamInfo = storeInfo.getStoreTeamInfo();
        storeTeamInfo.setId(storeInfo.getId());
        mongoTemplate.save(storeTeamInfo);
    }

    /*
     * 说明：消费信息
     */
    public void JoinStoreInfoShopping(StoreInfo storeInfo) {
        StoreShoppingInfo storeShoppingInfo = storeInfo.getStoreShoppingInfo();
        storeShoppingInfo.setId(storeInfo.getId());
        mongoTemplate.save(storeShoppingInfo);

    }

    /*
     * 说明：评价信息
     */
    public void JoinStoreEvaluationInfo(StoreInfo storeInfo) {
        StoreEvaluationInfo storeEvaluationInfo = storeInfo.getStoreEvaluationInfo();
        storeEvaluationInfo.setId(storeInfo.getId());
        mongoTemplate.save(storeEvaluationInfo);
    }

    /*
     * 说明：套餐信息
     */
    public void JoinStorePackageInfo(StoreInfo storeInfo) {
        StorePackageInfo storePackageInfo = storeInfo.getStorePackageInfo();
        storePackageInfo.setId(storeInfo.getId());
        mongoTemplate.save(storePackageInfo);
    }

    /*
     * 说明：环境信息
     */
    public void JoinStoreHardwareInfo(StoreInfo storeInfo) {
        StoreHardwareInfo storeHardwareInfo = new StoreHardwareInfo();
        storeHardwareInfo.setId(storeInfo.getId());
        DeskInfo deskInfo = new DeskInfo();
        deskInfo.setMaxUserNum(storeInfo.getMaxPeople());
        deskInfo.setMinUserNum(4);
        ArrayList<Photo> arrayListPhotos = new ArrayList<Photo>();
        Photo photo = new Photo();
        photo.setPhotoName("desk");
        photo.setPhotoPath("null");
        arrayListPhotos.add(photo);
        deskInfo.setDeskAllPhotos(arrayListPhotos);
        storeHardwareInfo.getDeskInfos().add(deskInfo);
        mongoTemplate.save(storeHardwareInfo);
    }

    /*
     * 说明：设备信息
     */
    public void JoinStoreEquipmentInfo(StoreInfo storeInfo) {
        StoreEquipmentInfo storeEquipmentInfo = storeInfo.getStoreEquipmentInfo();
        storeEquipmentInfo.setId(storeInfo.getId());
        mongoTemplate.save(storeEquipmentInfo);
    }

    /*
     * 说明：积分信息
     */
    public void JoinStoreIntegrationInfo(StoreInfo storeInfo) {
        StoreIntegrationInfo storeIntegrationInfo = storeInfo.getStoreIntegrationInfo();
        storeIntegrationInfo.setId(storeInfo.getId());
        mongoTemplate.save(storeIntegrationInfo);
    }

    /*
     * 说明：运营信息
     */
    public void JoinStoreBusinessInfo(StoreInfo storeInfo) {
        StoreBusinessInfo storeBusinessInfo = storeInfo.getStoreBusinessInfo();
        storeBusinessInfo.setId(storeInfo.getId());
        mongoTemplate.save(storeBusinessInfo);
    }

    /*
     * 说明：排名信息
     */
    public void JoinStoreRankInfo(StoreInfo storeInfo) {
        StoreRankInfo storeRankInfo = storeInfo.getStoreRankInfo();
        storeRankInfo.setId(storeInfo.getId());
        mongoTemplate.save(storeRankInfo);
    }

    /*
     * 说明：营业信息
     */
    public void JoinStoreBussinessInfo(StoreInfo storeInfo) {
        StoreBusinessInfo storeBusinessInfo = storeInfo.getStoreBusinessInfo();
        storeBusinessInfo.setId(storeInfo.getId());
        mongoTemplate.save(storeBusinessInfo);
    }

    /*
     * 说明：请求数据库，下载组装所有信息
     */
    public StoreInfo JoinStoreDownloadAllInfo(String storeId) {
        StoreInfo storeInfo = mongoTemplate.findById(storeId, StoreInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_INFO);
        storeInfo.setAddressInfo(mongoTemplate.findById(storeId, AddressInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_ADDRESS_INFO));
        storeInfo.setStoreTeamInfo(mongoTemplate.findById(storeId, StoreTeamInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_TEAM_INFO));
        storeInfo.setStoreShoppingInfo(mongoTemplate.findById(storeId, StoreShoppingInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_SHOPPING_INFO));
        storeInfo.setStoreEvaluationInfo(mongoTemplate.findById(storeId, StoreEvaluationInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_EVALUATION_INFO));
        storeInfo.setStorePackageInfo(mongoTemplate.findById(storeId, StorePackageInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_PACKAGE_INFO));
        storeInfo.setStorePhotoInfo(mongoTemplate.findById(storeId, StorePhotoInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_PHOTOINFO));
        storeInfo.setStoreHardwareInfo(mongoTemplate.findById(storeId, StoreHardwareInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_HARDWARE_INFO));
        storeInfo.setStoreEquipmentInfo(mongoTemplate.findById(storeId, StoreEquipmentInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_EQUIPMENT_INFO));
        storeInfo.setStoreIntegrationInfo(mongoTemplate.findById(storeId, StoreIntegrationInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_INTEGRATION_INFO));
        storeInfo.setStoreBusinessInfo(mongoTemplate.findById(storeId, StoreBusinessInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_BUSINESS_INFO));
        storeInfo.setStoreRankInfo(mongoTemplate.findById(storeId, StoreRankInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_RANK_INFO));
        storeInfo.setStoreBusinessInfo(mongoTemplate.findById(storeId, StoreBusinessInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_BUSINESS_INFO));
        return storeInfo;
    }

    /*
     * 说明：检查所有信息
     */
    public void JoinStoreCheckAllInfo(StoreInfo storeInfo) {
    }

    /*
     * 说明：更新storeInfo信息
     */
    public void JoinStoreUpdateInfo(StoreInfo storeInfo) {
        mongoTemplate.save(storeInfo, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_INFO);
    }

    /*
     * 说明：更新user的place信息
     */
    public void JoinStoreUpdateUserPlaceInfo(String userId, StoreInfo storeInfo) {
        Update update = new Update();
        UserPlaces userPlaces = mongoTemplate.findById(userId, UserPlaces.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_PLACES);
        ArrayList<String> placeList = userPlaces.getAllPlaceId();
        Boolean hasSeam = false;
        if (placeList != null) {
            for (String i : placeList) {
                if (i.equals(storeInfo.getId()))
                    hasSeam = true;
            }
            if (!hasSeam) {
                placeList.add(storeInfo.getId());
                mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(userId)),
                        update.set("allPlaceId", placeList), MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_PLACES);

            }
        } else {
            placeList = new ArrayList<>();
            placeList.add(storeInfo.getId());
            mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(userId)),
                    update.set("allPlaceId", placeList), MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_PLACES);

        }
    }
}
