package com.stnts.bi.schedule.deduct.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class CjtDeductAgainVO implements Serializable {

    @ApiModelProperty("日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date partitionDate;

    @ApiModelProperty("扣量后，未扣量订单统计（订单扣量后订单量、订单扣量后订单金额）")
    private Map<String, Map<String, String>> orderDeductMap;


    /**
     * ========================== 重跑 ===============================
     */
    @ApiModelProperty("重跑CCID集合")
    private List<String> ccidList;

    @ApiModelProperty("重跑PID集合")
    private List<String> pidList;

    @ApiModelProperty("重跑ProductCode集合")
    private List<String> productCodeList;

    @ApiModelProperty("重跑渠道集合")
    private List<String> channelIdList;

    @ApiModelProperty("重跑子渠道集合")
    private List<String> subChannelIdList;

    @ApiModelProperty("日期开始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date partitionDateStart;

    @ApiModelProperty("日期结束")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date partitionDateEnd;
}
