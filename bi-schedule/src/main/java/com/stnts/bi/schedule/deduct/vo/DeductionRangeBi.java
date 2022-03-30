package com.stnts.bi.schedule.deduct.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class DeductionRangeBi implements RowMapper {
    @ApiModelProperty("产品Code")
    private String productCode;

    /*@ApiModelProperty("黑名单用户ID集合")
    private String agentIds;

    @ApiModelProperty("黑名单用户ID集合")
    private String ccid;*/

    @ApiModelProperty("黑名单用户ID集合")
    private String bdate;

    @ApiModelProperty("黑名单用户ID集合")
    private String edate;

    @ApiModelProperty("黑名单用户ID集合")
    private String detail;

    @ApiModelProperty("黑名单用户ID集合")
    private String updatedAt;

    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        DeductionRangeBi deductionUidBi = new DeductionRangeBi();
        deductionUidBi.setProductCode(resultSet.getString("product_code"));
        /*deductionUidBi.setAgentIds(resultSet.getString("agent_ids"));
        deductionUidBi.setCcid(resultSet.getString("ccid"));*/
        deductionUidBi.setBdate(resultSet.getString("bdate"));
        deductionUidBi.setEdate(resultSet.getString("edate"));
        deductionUidBi.setDetail(resultSet.getString("detail"));
        deductionUidBi.setUpdatedAt(resultSet.getString("updated_at"));
        return deductionUidBi;
    }
}
