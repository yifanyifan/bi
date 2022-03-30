package com.stnts.bi.schedule.deduct.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Data
public class CjtSszmVO implements RowMapper {
    @ApiModelProperty("日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date partitionDate;

    @ApiModelProperty("PID")
    private String pid;

    @ApiModelProperty("CCID")
    private String ccid;

    @ApiModelProperty("产品Code")
    private String productCode;

    @ApiModelProperty("产品名")
    private String productName;

    @ApiModelProperty("主键")
    private Long channelId;

    @ApiModelProperty("渠道名称")
    private String channelName;

    @ApiModelProperty("子渠道id")
    private String subChannelId;

    @ApiModelProperty("子渠道名称")
    private String subChannelName;

    @ApiModelProperty(value = "计费规则")
    private String chargeRule;

    @ApiModelProperty(value = "注册人数")
    private String regCnts;

    @ApiModelProperty(value = "流水")
    private String payMoney;

    @ApiModelProperty(value = "重跑PID集合")
    private List<String> pidList;

    @ApiModelProperty(value = "重跑CCID集合")
    private List<String> ccidList;

    @ApiModelProperty("日期开始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date partitionDateStart;

    @ApiModelProperty("日期结束")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date partitionDateEnd;

    @ApiModelProperty("模块")
    private String model;

    @ApiModelProperty("重跑渠道集合")
    private List<String> channelIdList;

    @ApiModelProperty("重跑子渠道集合")
    private List<String> subChannelIdList;

    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        CjtSszmVO user = new CjtSszmVO();
        user.setPartitionDate(resultSet.getDate("partition_date"));
        user.setPid(resultSet.getString("pid"));
        user.setCcid(resultSet.getString("ccid"));
        user.setProductCode(resultSet.getString("product_code"));
        user.setProductName(resultSet.getString("product_name"));
        user.setChannelId(resultSet.getLong("channel_id"));
        user.setChannelName(resultSet.getString("channel_name"));
        user.setSubChannelId(resultSet.getString("sub_channel_id"));
        user.setSubChannelName(resultSet.getString("sub_channel_name"));
        user.setChargeRule(resultSet.getString("charge_rule"));
        user.setRegCnts(resultSet.getString("reg_cnts"));
        user.setPayMoney(resultSet.getString("pay_money"));
        return user;
    }
}
