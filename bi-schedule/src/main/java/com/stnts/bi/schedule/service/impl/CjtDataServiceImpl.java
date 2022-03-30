package com.stnts.bi.schedule.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.stnts.bi.schedule.deduct.vo.OrderDeductVO;
import com.stnts.bi.schedule.deduct.vo.PidDeductVO;
import com.stnts.bi.schedule.service.CjtDataService;
import com.stnts.bi.schedule.util.MongoDBUtil;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.*;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/6/2
 */
@Service
public class CjtDataServiceImpl implements CjtDataService {
    @Value("${schedule.jdbc.mongo.url}")
    private String mongoUrl;
    @Value("${schedule.jdbc.mongo.url02}")
    private String mongoUrl02;
    @Value("${schedule.jdbc.mongo.url03}")
    private String mongoUrl03;
    @Value("${schedule.jdbc.mongo.port}")
    private Integer mongoPort;
    @Value("${schedule.jdbc.mongo.username}")
    private String mongoUserName;
    @Value("${schedule.jdbc.mongo.authsource}")
    private String mongoSourceName;
    @Value("${schedule.jdbc.mongo.password}")
    private String mongoPassWord;
    @Value("${schedule.jdbc.mongo.dbname}")
    private String mongoDbName;


    @Autowired
    private MongoDBUtil mongoDBUtil;

    @Override
    public List<OrderDeductVO> orderDeductList(OrderDeductVO orderDeductVO) {
        MongoDatabase mongoDatabase = mongoDBUtil.getConnect(mongoUrl, mongoUrl02, mongoUrl03, mongoPort, mongoUserName, mongoSourceName, mongoPassWord, mongoDbName);
        MongoCollection mongoCollection = mongoDatabase.getCollection("deduct.order");

        FindIterable<Document> findIterable = null;
        List<Bson> bsonList = getQuery(orderDeductVO);
        if (CollectionUtil.isNotEmpty(bsonList)) {
            findIterable = mongoCollection.find(and(bsonList));
        } else {
            findIterable = mongoCollection.find();
        }

        List<OrderDeductVO> orderDeductVOList = new ArrayList<OrderDeductVO>();
        if (ObjectUtil.isNotEmpty(findIterable)) {
            for (Document doc : findIterable) {
                OrderDeductVO db = JSONUtil.toBean(doc.toJson(), OrderDeductVO.class);
                orderDeductVOList.add(db);
            }
        }

        return orderDeductVOList;
    }

    @Override
    public List<PidDeductVO> pidDeductList(PidDeductVO pidDeductVO) {
        MongoDatabase mongoDatabase = mongoDBUtil.getConnect(mongoUrl, mongoUrl02, mongoUrl03, mongoPort, mongoUserName, mongoSourceName, mongoPassWord, mongoDbName);
        MongoCollection mongoCollection = mongoDatabase.getCollection("deduct.pid");

        FindIterable<Document> findIterable = null;
        List<Bson> bsonList = getQueryPID(pidDeductVO);
        if (CollectionUtil.isNotEmpty(bsonList)) {
            findIterable = mongoCollection.find(and(bsonList));
        } else {
            findIterable = mongoCollection.find();
        }

        List<PidDeductVO> pidDeductVOList = new ArrayList<PidDeductVO>();
        if (ObjectUtil.isNotEmpty(findIterable)) {
            for (Document doc : findIterable) {
                PidDeductVO db = JSONUtil.toBean(doc.toJson(), PidDeductVO.class);
                pidDeductVOList.add(db);
            }
        }

        return pidDeductVOList;
    }

    private List<Bson> getQueryPID(PidDeductVO pidDeductVO) {
        List<Bson> bsonList = new ArrayList<Bson>();
        if (StringUtils.isNotBlank(pidDeductVO.getDate())) {
            bsonList.add(eq("date", pidDeductVO.getDate()));
        }
        if (StringUtils.isNotBlank(pidDeductVO.getPid())) {
            bsonList.add(eq("pid", pidDeductVO.getPid()));
        }
        if (StringUtils.isNotBlank(pidDeductVO.getProductCode())) {
            bsonList.add(eq("productCode", pidDeductVO.getProductCode()));
        }
        if (StringUtils.isNotBlank(pidDeductVO.getChannelId())) {
            bsonList.add(eq("channelId", pidDeductVO.getChannelId()));
        }
        if (StringUtils.isNotBlank(pidDeductVO.getSubChannelId())) {
            bsonList.add(eq("subChannelId", pidDeductVO.getSubChannelId()));
        }
        if (StringUtils.isNotBlank(pidDeductVO.getPc())) {
            bsonList.add(eq("pc", pidDeductVO.getPc()));
        }
        if (StringUtils.isNotBlank(pidDeductVO.getUv())) {
            bsonList.add(eq("uv", pidDeductVO.getUv()));
        }
        if (StringUtils.isNotBlank(pidDeductVO.getRegCount())) {
            bsonList.add(eq("regCount", pidDeductVO.getRegCount()));
        }
        if (StringUtils.isNotBlank(pidDeductVO.getPayCount())) {
            bsonList.add(eq("payCount", pidDeductVO.getPayCount()));
        }
        if (StringUtils.isNotBlank(pidDeductVO.getPayFee())) {
            bsonList.add(eq("payFee", pidDeductVO.getPayFee()));
        }
        if (StringUtils.isNotBlank(pidDeductVO.getOrderDeductCount())) {
            bsonList.add(eq("orderDeductCount", pidDeductVO.getOrderDeductCount()));
        }
        if (StringUtils.isNotBlank(pidDeductVO.getOrderDeductFee())) {
            bsonList.add(eq("orderDeductFee", pidDeductVO.getOrderDeductFee()));
        }
        if (StringUtils.isNotBlank(pidDeductVO.getPidDeductRegCount())) {
            bsonList.add(eq("pidDeductCount", pidDeductVO.getPidDeductRegCount()));
        }
        if (StringUtils.isNotBlank(pidDeductVO.getPidDeductFee())) {
            bsonList.add(eq("pidDeductFee", pidDeductVO.getPidDeductFee()));
        }
        return bsonList;
    }

    public List<Bson> getQuery(OrderDeductVO orderDeductVO) {
        List<Bson> bsonList = new ArrayList<Bson>();
        if (ObjectUtil.isNotEmpty(orderDeductVO.getCreateTimeStart())) {
            bsonList.add(gt("createTime", DateUtil.format(orderDeductVO.getCreateTimeStart(), "yyyy-MM-dd HH:mm:ss")));
        }
        if (ObjectUtil.isNotEmpty(orderDeductVO.getCreateTimeEnd())) {
            bsonList.add(lt("createTime", DateUtil.format(orderDeductVO.getCreateTimeEnd(), "yyyy-MM-dd HH:mm:ss")));
        }
        if (StringUtils.isNotBlank(orderDeductVO.getOrderId())) {
            bsonList.add(eq("orderId", orderDeductVO.getOrderId()));
        }
        if (StringUtils.isNotBlank(orderDeductVO.getUId())) {
            bsonList.add(eq("uId", orderDeductVO.getUId()));
        }
        if (StringUtils.isNotBlank(orderDeductVO.getPayFee())) {
            bsonList.add(eq("payFee", orderDeductVO.getPayFee()));
        }
        if (StringUtils.isNotBlank(orderDeductVO.getPayType())) {
            bsonList.add(eq("payType", orderDeductVO.getPayType()));
        }
        if (ObjectUtil.isNotEmpty(orderDeductVO.getCTime())) {
            bsonList.add(eq("cTime", orderDeductVO.getCTime()));
        }
        if (StringUtils.isNotBlank(orderDeductVO.getPayPid())) {
            bsonList.add(eq("payPid", orderDeductVO.getPayPid()));
        }
        if (StringUtils.isNotBlank(orderDeductVO.getRegPid())) {
            bsonList.add(eq("regPid", orderDeductVO.getRegPid()));
        }
        if (StringUtils.isNotBlank(orderDeductVO.getProductCode())) {
            bsonList.add(eq("productCode", orderDeductVO.getProductCode()));
        }
        if (StringUtils.isNotBlank(orderDeductVO.getProductName())) {
            bsonList.add(eq("productName", orderDeductVO.getProductName()));
        }
        if (StringUtils.isNotBlank(orderDeductVO.getChannelId())) {
            bsonList.add(eq("channelId", orderDeductVO.getChannelId()));
        }
        if (StringUtils.isNotBlank(orderDeductVO.getChannelName())) {
            bsonList.add(eq("channelName", orderDeductVO.getChannelName()));
        }
        if (StringUtils.isNotBlank(orderDeductVO.getSubChannelId())) {
            bsonList.add(eq("subChannelId", orderDeductVO.getSubChannelId()));
        }
        if (StringUtils.isNotBlank(orderDeductVO.getSubChannelName())) {
            bsonList.add(eq("subChannelName", orderDeductVO.getSubChannelName()));
        }
        if (StringUtils.isNotBlank(orderDeductVO.getRegCCID())) {
            bsonList.add(eq("regCCID", orderDeductVO.getRegCCID()));
        }
        if (StringUtils.isNotBlank(orderDeductVO.getDeductStatus())) {
            bsonList.add(eq("deductStatus", orderDeductVO.getDeductStatus()));
        }
        return bsonList;
    }

}