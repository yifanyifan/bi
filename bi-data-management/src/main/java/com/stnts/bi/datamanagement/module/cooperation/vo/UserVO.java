package com.stnts.bi.datamanagement.module.cooperation.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @author 刘天元
 */
@Data
public class UserVO {
    private Integer id;
    private String cnname;
    private String cardNumber;
    @JsonIgnore
    private Integer status;
}
