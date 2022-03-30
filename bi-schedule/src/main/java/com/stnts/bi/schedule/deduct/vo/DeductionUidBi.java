package com.stnts.bi.schedule.deduct.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class DeductionUidBi implements RowMapper {

    @ApiModelProperty("产品Code")
    private String productCode;

    @ApiModelProperty("黑名单用户ID集合")
    private String uids;

    @ApiModelProperty("媒体商ID集合")
    private String agentIds;

    @ApiModelProperty("CCID集合")
    private String ccid;

    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        DeductionUidBi deductionUidBi = new DeductionUidBi();
        deductionUidBi.setProductCode(resultSet.getString("product_code"));
        deductionUidBi.setUids(resultSet.getString("uids"));
        deductionUidBi.setAgentIds(resultSet.getString("agent_ids"));
        deductionUidBi.setCcid(resultSet.getString("ccid"));
        return deductionUidBi;
    }
}
