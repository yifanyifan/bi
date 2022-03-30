package com.stnts.bi.schedule.util;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * mongodb 连接数据库工具类
 */
@Component
public class MongoDBUtil {
    public MongoDatabase getConnect(String url, String url02, String url03, Integer port, String username, String source, String password, String database) {
        List<ServerAddress> adds = new ArrayList<>();
        //ServerAddress()两个参数分别为 服务器地址 和 端口
        ServerAddress serverAddress = new ServerAddress(url, port);
        adds.add(serverAddress);
        if (StringUtils.isNotBlank(url02)) {
            ServerAddress serverAddress02 = new ServerAddress(url02, port);
            adds.add(serverAddress02);
        }
        if (StringUtils.isNotBlank(url03)) {
            ServerAddress serverAddress03 = new ServerAddress(url03, port);
            adds.add(serverAddress03);
        }

        List<MongoCredential> credentials = new ArrayList<>();
        //MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码
        MongoCredential mongoCredential = MongoCredential.createScramSha1Credential(username, source, password.toCharArray());
        credentials.add(mongoCredential);

        //通过连接认证获取MongoDB连接
        MongoClient mongoClient = new MongoClient(adds, credentials);

        //连接到数据库
        MongoDatabase mongoDatabase = mongoClient.getDatabase(database);

        //返回连接数据库对象
        return mongoDatabase;
    }
}
