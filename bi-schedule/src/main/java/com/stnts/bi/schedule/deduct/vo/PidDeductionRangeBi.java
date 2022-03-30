package com.stnts.bi.schedule.deduct.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class PidDeductionRangeBi implements RowMapper {
    @ApiModelProperty("BI渠道媒体商ID")
    private String channelId;

    @ApiModelProperty("产品code")
    private String productCode;

    @ApiModelProperty("PID")
    private String pid;

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

    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        PidDeductionRangeBi pidDeductionRangeBi = new PidDeductionRangeBi();
        pidDeductionRangeBi.setChannelId(resultSet.getString("channel_id"));
        pidDeductionRangeBi.setProductCode(resultSet.getString("product_code"));
        pidDeductionRangeBi.setPid(resultSet.getString("pid"));
        pidDeductionRangeBi.setChannelShareType(resultSet.getString("channel_share_type"));
        pidDeductionRangeBi.setChannelFcType(resultSet.getString("channel_fc_type"));
        pidDeductionRangeBi.setChannelShare(resultSet.getString("channel_share"));
        pidDeductionRangeBi.setChannelShareStep(resultSet.getString("channel_share_step"));
        pidDeductionRangeBi.setBdate(resultSet.getString("bdate"));
        pidDeductionRangeBi.setEdate(resultSet.getString("edate"));
        return pidDeductionRangeBi;
    }
}
