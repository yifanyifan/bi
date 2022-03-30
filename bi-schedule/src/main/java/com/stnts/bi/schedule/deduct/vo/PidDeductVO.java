package com.stnts.bi.schedule.deduct.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class PidDeductVO implements RowMapper {
    @ApiModelProperty("日期")
    private String date;

    @ApiModelProperty("PID")
    private String pid;

    @ApiModelProperty("ccid")
    private String ccid;

    @ApiModelProperty("产品code")
    private String productCode;

    @ApiModelProperty("产品名称")
    private String productName;

    @ApiModelProperty("渠道ID")
    private String channelId;

    @ApiModelProperty("渠道名称")
    private String channelName;

    @ApiModelProperty("子渠道ID")
    private String subChannelId;

    @ApiModelProperty("子渠道名称")
    private String subChannelName;

    @ApiModelProperty("计费规则")
    private String chargeRule;

    @ApiModelProperty("启动PC")
    private String pc;

    @ApiModelProperty("UV")
    private String uv;

    @ApiModelProperty("原始注册数")
    private String regCount;

    @ApiModelProperty("原始订单量")
    private String payCount;

    @ApiModelProperty("原始订单金额(元)")
    private String payFee;

    @ApiModelProperty("产品扣量后订单量")
    private String orderDeductCount;

    @ApiModelProperty("产品扣量后订单金额(元)")
    private String orderDeductFee;

    @ApiModelProperty("媒体PID扣量后注册数")
    private String pidDeductRegCount;

    @ApiModelProperty("媒体PID扣量后订单金额(元)")
    private String pidDeductFee;

    /*-------------------------------PID扣量规则Start----------------------------------------------*/
    @ApiModelProperty("扣量指标 1.收入2.利润3.注册4.激活")
    private String channelShareType;

    @ApiModelProperty("1固定，2阶梯")
    private String channelFcType;

    @ApiModelProperty("固定扣量比例")
    private String channelShare;

    @ApiModelProperty("阶梯扣量比例  json格式存储")
    private String channelShareStep;

    @ApiModelProperty("设置开始日期")
    private String bdate;

    @ApiModelProperty("设置结束日期")
    private String edate;

    /*---------------------------日扣量Start---------------------------------------------*/
    @ApiModelProperty("原始日收益(元)")
    private String dayEarn;

    @ApiModelProperty("产品扣量后日收益(元)")
    private String dayOrderEarn;

    @ApiModelProperty("媒体PID扣量后日收益(元)")
    private String dayPidEarn;

    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        PidDeductVO pidDeductVO = new PidDeductVO();
        pidDeductVO.setDate(resultSet.getString("dateNow"));
        pidDeductVO.setCcid(resultSet.getString("ccid"));
        pidDeductVO.setPid(resultSet.getString("pid"));
        pidDeductVO.setProductCode(resultSet.getString("product_code"));
        pidDeductVO.setProductName(resultSet.getString("product_name"));
        pidDeductVO.setChannelId(resultSet.getString("channel_id"));
        pidDeductVO.setChannelName(resultSet.getString("channel_name"));
        pidDeductVO.setSubChannelId(resultSet.getString("sub_channel_id"));
        pidDeductVO.setSubChannelName(resultSet.getString("sub_channel_name"));
        pidDeductVO.setChargeRule(resultSet.getString("charge_rule"));
        pidDeductVO.setPc(StringUtils.isNotBlank(resultSet.getString("pc")) ? resultSet.getString("pc") : "0");
        pidDeductVO.setUv(StringUtils.isNotBlank(resultSet.getString("uv")) ? resultSet.getString("uv") : "0");
        pidDeductVO.setRegCount(StringUtils.isNotBlank(resultSet.getString("reg_count")) ? resultSet.getString("reg_count") : "0");
        pidDeductVO.setPayCount(StringUtils.isNotBlank(resultSet.getString("pay_order_count")) ? resultSet.getString("pay_order_count") : "0");
        pidDeductVO.setPayFee(StringUtils.isNotBlank(resultSet.getString("pay_fee")) ? resultSet.getString("pay_fee") : "0");

        pidDeductVO.setChannelShareType(resultSet.getString("channel_share_type"));
        pidDeductVO.setChannelFcType(resultSet.getString("channel_fc_type"));
        pidDeductVO.setChannelShare(resultSet.getString("channel_share"));
        pidDeductVO.setChannelShareStep(resultSet.getString("channel_share_step"));
        pidDeductVO.setBdate(resultSet.getString("bdate"));
        pidDeductVO.setEdate(resultSet.getString("edate"));
        return pidDeductVO;
    }
}
