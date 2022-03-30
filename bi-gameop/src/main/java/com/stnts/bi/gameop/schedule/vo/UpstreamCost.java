package com.stnts.bi.gameop.schedule.vo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

/**
 * @author huxinchao
 * @ClassName UpstreamCostRepository.java
 * @Description
 * @createTime 2021年12月23日 09:57:00
 */
@Data
@Document(collection = "game.upstream_cost")
public class UpstreamCost implements Serializable {
    @Id
    private String id;
    @Field(name = "game_code")
    private String gameCode;
    private String pid;
    @Field(name = "share_fee")
    private Integer shareFee;
    @Field(name = "create_time")
    private Date createTime;
    @Field(name = "date_desc")
    private String dateDesc;

    /**
     * @title toArray
     * @description
     * @author huxinchao
     * @updateTime 2021/12/24 9:36
 * @return: java.lang.Object[]
     */
    public Object[] toArray(){
        //(id,game_code,pid,date_desc,share_fee)
        return new Object[]{id,gameCode,pid,dateDesc,shareFee};
    }
}
