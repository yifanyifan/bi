package com.stnts.bi.schedule.deduct.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

@Data
public class OrderDeductVO implements RowMapper {
    //select a.id as order_id, a.uid, a.pay_fee, a.pay_type, a.ctime, a.channel_id as pay_pid, b.channel_id as reg_pid, '带带' as product_name, c.channel_name, c.sub_channel_name, c.ccid as reg_ccid

    //产品订单号、用户UID、充值金额(金额)、支付方式、	创建订单时间、支付PID、注册PID、产品名称、注册媒体商、注册子渠道、注册CCID、扣量状态

    @ApiModelProperty("产品订单号")
    private String orderId;

    @ApiModelProperty("用户UID")
    private String uId;

    @ApiModelProperty("充值金额")
    private String payFee;

    @ApiModelProperty("支付方式")
    private String payType;

    @ApiModelProperty("创建订单时间")
    private String cTime;

    @ApiModelProperty("支付PID")
    private String payPid;

    @ApiModelProperty("注册PID")
    private String regPid;

    @ApiModelProperty("产品Code")
    private String productCode;

    @ApiModelProperty("产品名称")
    private String productName;

    @ApiModelProperty("注册媒体商ID")
    private String channelId;

    @ApiModelProperty("注册媒体商")
    private String channelName;

    @ApiModelProperty("注册子渠道Id")
    private String subChannelId;

    @ApiModelProperty("注册子渠道")
    private String subChannelName;

    @ApiModelProperty("注册CCID")
    private String regCCID;

    @ApiModelProperty("扣量状态 1：扣量，2：未扣量")
    private String deductStatus;

    @ApiModelProperty("计费规则")
    private String chargeRule;

    @ApiModelProperty("创建时间")
    private String createTime;

    /**
     * --------------------------请求参数-------------------------------
     **/
    @ApiModelProperty("创建订单时间开始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTimeStart;

    @ApiModelProperty("创建订单时间结束")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTimeEnd;

    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        OrderDeductVO orderDeductVO = new OrderDeductVO();
        orderDeductVO.setOrderId(resultSet.getString("order_id"));
        orderDeductVO.setUId(resultSet.getString("uid"));
        orderDeductVO.setPayFee(resultSet.getString("pay_fee"));
        orderDeductVO.setPayType(resultSet.getString("pay_type"));
        orderDeductVO.setCTime(resultSet.getString("ctime"));
        orderDeductVO.setPayPid(resultSet.getString("pay_pid"));
        orderDeductVO.setRegPid(resultSet.getString("reg_pid"));
        orderDeductVO.setProductCode(resultSet.getString("product_code"));
        orderDeductVO.setProductName(resultSet.getString("product_name"));
        orderDeductVO.setChannelId(resultSet.getString("channel_id"));
        orderDeductVO.setChannelName(resultSet.getString("channel_name"));
        orderDeductVO.setSubChannelId(resultSet.getString("sub_channel_id"));
        orderDeductVO.setSubChannelName(resultSet.getString("sub_channel_name"));
        orderDeductVO.setRegCCID(resultSet.getString("reg_ccid"));
        orderDeductVO.setDeductStatus(resultSet.getString("deductStatus"));
        orderDeductVO.setChargeRule(resultSet.getString("charge_rule"));
        return orderDeductVO;
    }
}
