package com.stnts.bi.schedule.deduct.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class CjtOrderDeductAgainVO implements RowMapper {
    @ApiModelProperty("pid")
    private String pid;

    @ApiModelProperty("countSum")
    private String countSum;

    @ApiModelProperty("feeSum")
    private String feeSum;

    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        CjtOrderDeductAgainVO cjtOrderDeductAgainVO = new CjtOrderDeductAgainVO();
        cjtOrderDeductAgainVO.setPid(resultSet.getString("pid"));
        cjtOrderDeductAgainVO.setCountSum(resultSet.getString("countSum"));
        cjtOrderDeductAgainVO.setFeeSum(resultSet.getString("feeSum"));

        return cjtOrderDeductAgainVO;
    }
}
