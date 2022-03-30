package com.stnts.bi.gameop.schedule;

import cn.hutool.core.collection.ArrayIter;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.db.Db;
import cn.hutool.db.sql.SqlExecutor;
import com.stnts.bi.gameop.schedule.vo.DownstreamCost;
import com.stnts.bi.gameop.schedule.vo.UpstreamCost;
import com.stnts.bi.sql.util.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;

/**
 * @author huxinchao
 * @Description 同步mongodb的数据到clickhouse
 * 同步的数据： game.downstream_cost -> bi_gameop.dwd_downstream_cost , game.upstream_cost -> bi_gameop.dwd_upstream_cost
 * @createTime 2021年12月24日 09:11:00
 */
@Component
@Slf4j
@ActiveProfiles(profiles = {"dev"})
public class CostSyncScheduleTask {
    @Resource
    private MongoTemplate mongoTemplate;

    private static int SYNC_COUNT = 50000;   //每次同步条数
    //每天 1:20执行
    @Scheduled(cron = "0 20 1 * * ? ")
    public void schedule(){
        String yesterday = DateUtil.formatDate(DateUtil.yesterday());
        log.info("同步downstream_cost，upstream_cost 数据任务开始>>> : 同步日期 = 【{}】",yesterday);
        Db db = DatabaseUtil.getDB("clickhouse", "");
        syncUpstreamCost(yesterday,null,db);   // 同步 game.upstream_cost -> bi_gameop.dwd_upstream_cost
        syncDownstreamCost(yesterday,null,db); // 同步 game.downstream_cost -> bi_gameop.dwd_downstream_cost
        log.info("同步downstream_cost，upstream_cost 数据任务完成<<<  : 同步日期 = 【{}】",yesterday);
    }
    /**
     * @description  game.upstream_cost -> bi_gameop.dwd_upstream_cost
     * @author huxinchao
     * @param: yesterday
     * @param: curMaxId
     * @param: db
     * @updateTime 2021/12/24 15:09
     */
    private void syncUpstreamCost(String yesterday, String curMaxId, Db db){
        Criteria criteria = Criteria.where("date_desc").is(yesterday);
        if(curMaxId != null){
            criteria.and("_id").gt(curMaxId);
        }
        Query query = Query.query(criteria).with(Sort.by(Sort.Order.asc("id"))).limit(SYNC_COUNT);
        List<UpstreamCost> costsList = mongoTemplate.find(query, UpstreamCost.class);
        if(CollectionUtil.isEmpty(costsList)){
           return;
        }
        // 写入到clickhouse的数据
        Object[] costArray = costsList.stream().map(UpstreamCost::toArray).toArray();

        try {
            String sql = "INSERT INTO bi_gameop.dwd_upstream_cost(id,game_code,pid,date_desc,share_fee) Values (?,?,?,?,?)";
            SqlExecutor.executeBatch(db.getConnection(),sql, new ArrayIter(costArray));
            log.info("分批同步game.upstream_cost数据 {} 条",costsList.size());
        } catch (SQLException e) {
            log.error("同步保存bi_gameop.dwd_upstream_cost数据报错",e);
        }
        //分页同步,最后一批
        if(costsList.size() < SYNC_COUNT){
            return;
        }
        curMaxId = costsList.get(costsList.size() -1).getId();
        syncUpstreamCost(yesterday,curMaxId, db);
    }

    /**
     * @description 同步的数据： game.downstream_cost -> bi_gameop.dwd_downstream_cost
     * @author huxinchao
     * @param: yesterday
     * @param: curMaxId
     * @param: db
     * @updateTime 2021/12/24 15:09
     */
    private void syncDownstreamCost(String yesterday, String curMaxId, Db db){
        Criteria criteria = Criteria.where("date_desc").is(yesterday);
        if(curMaxId != null){
            criteria.and("id").gt(curMaxId);
        }
        Query query = Query.query(criteria).limit(SYNC_COUNT);
        List<DownstreamCost> costsList = mongoTemplate.find(query, DownstreamCost.class);
        if(CollectionUtil.isEmpty(costsList)){
            return;
        }
        // 写入到clickhouse的数据
        Object[] costArray = costsList.stream().map(DownstreamCost::toArray).toArray();
        try {
            String sql = "INSERT INTO bi_gameop.dwd_downstream_cost(id,game_code,pid,date_desc,share_fee) Values (?,?,?,?,?)";
            SqlExecutor.executeBatch(db.getConnection(),sql, new ArrayIter(costArray));
            log.info("分批同步game.downstream_cost {} 条",costsList.size());
        } catch (SQLException e) {
            log.error("同步保存bi_gameop.dwd_downstream_cost数据报错",e);
        }
        //分页同步,最后一批
        if(costsList.size() < SYNC_COUNT){
            return;
        }
        curMaxId = costsList.get(costsList.size() -1).getId();
        if(StringUtils.isEmpty(curMaxId)){
            return;
        }
        syncUpstreamCost(yesterday,curMaxId, db);
    }

}
