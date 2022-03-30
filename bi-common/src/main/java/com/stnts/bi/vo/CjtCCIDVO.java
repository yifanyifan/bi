package com.stnts.bi.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/6/2
 */
@Data
public class CjtCCIDVO implements RowMapper {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("CCID")
    private String pid;

    @ApiModelProperty("CCID")
    private String ccid;

    @ApiModelProperty("渠道ID")
    private String channelId;

    @ApiModelProperty("渠道名称")
    private String channelName;

    @ApiModelProperty("计费方式")
    private String chargeRule;

    @ApiModelProperty("渠道费率")
    private String channelRate;

    @ApiModelProperty("渠道分成")
    private String channelShare;

    @ApiModelProperty("渠道阶梯分成")
    private String channelShareStep;

    @ApiModelProperty("单价")
    private String price;

    @ApiModelProperty("结算指标【1.收入2.利润3.注册4.激活】")
    private String channelShareType;

    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        CjtCCIDVO cjtCCIDVO = new CjtCCIDVO();
        cjtCCIDVO.setPid(resultSet.getString("pid"));
        cjtCCIDVO.setCcid(resultSet.getString("ccid"));
        cjtCCIDVO.setChannelId(resultSet.getString("channel_id"));
        cjtCCIDVO.setChannelName(resultSet.getString("channel_name"));
        cjtCCIDVO.setChargeRule(resultSet.getString("charge_rule"));
        cjtCCIDVO.setChannelRate(resultSet.getString("channel_rate"));
        cjtCCIDVO.setChannelShare(resultSet.getString("channel_share"));
        cjtCCIDVO.setChannelShareStep(resultSet.getString("channel_share_step"));
        cjtCCIDVO.setPrice(resultSet.getString("price"));
        cjtCCIDVO.setChannelShareType(resultSet.getString("channel_share_type"));
        return cjtCCIDVO;
    }
}
