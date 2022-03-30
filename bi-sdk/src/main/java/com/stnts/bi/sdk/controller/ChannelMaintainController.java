package com.stnts.bi.sdk.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sdk.util.SignUtil;
import com.stnts.bi.sql.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;


/**
 * @author liutianyuan
 */
@RestController
@RequestMapping("/channel/maintain")
@Slf4j
@Validated
public class ChannelMaintainController {

    private final SignUtil signUtil;

    public ChannelMaintainController(SignUtil signUtil) {
        this.signUtil = signUtil;
    }

    @PostMapping("/add")
    public ResultEntity addChannelMaintain(String source,
                                           String product_id,
                                           String billing_id,
                                           String billing_name,
                                           String cid,
                                           String cid_name,
                                           String agent_id,
                                           String agent_name,
                                           String channel_id,
                                           String channel_name,
                                           String cooperation_main_id,
                                           String cooperation_main_name,
                                           String remark,
                                           String app_id, Long timestamp, String sign) throws SQLException {
        Dict dict = Dict.create()
                .set("source", source)
                .set("product_id", product_id)
                .set("billing_id", billing_id)
                .set("billing_name", billing_name)
                .set("cid", cid)
                .set("cid_name", cid_name)
                .set("agent_id", agent_id)
                .set("agent_name", agent_name)
                .set("channel_id", channel_id)
                .set("channel_name", channel_name)
                .set("cooperation_main_id", cooperation_main_id)
                .set("cooperation_main_name", cooperation_main_name)
                .set("remark", remark);
        signUtil.checkSign(app_id, timestamp, sign, dict);

        List<Entity> entityList = Db.use("clickhouse").findAll(Entity.create("banyan_bi_sdk.bi_channel_maintain").set("product_id", product_id).set("billing_id", billing_id));
        if(CollectionUtil.isNotEmpty(entityList)) {
            throw new BusinessException(StrUtil.format("product_id:{},billing_id:{}的记录已经存在", product_id, billing_id));
        }

        DbUtil.setReturnGeneratedKeyGlobal(false);

        Db.use("clickhouse").insert(
                Entity.create("banyan_bi_sdk.bi_channel_maintain")
                        .set("source", nullToEmpty(source))
                        .set("product_id", nullToEmpty(product_id))
                        .set("billing_id", nullToEmpty(billing_id))
                        .set("billing_name", nullToEmpty(billing_name))
                        .set("cid", nullToEmpty(cid))
                        .set("cid_name", nullToEmpty(cid_name))
                        .set("agent_id", nullToEmpty(agent_id))
                        .set("agent_name", nullToEmpty(agent_name))
                        .set("channel_id", nullToEmpty(channel_id))
                        .set("channel_name", nullToEmpty(channel_name))
                        .set("remark", nullToEmpty(remark))
        );
        return ResultEntity.success(null);
    }


    private String nullToEmpty(String str) {
        if(str == null) {
            str = "";
        }
        return str;
    }

}
