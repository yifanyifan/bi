package com.stnts.bi.vo;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/6/2
 */
@Data
public class DmVO {

    private String departmentCode;
    private String departmentName;
    /*
     * 是否内结（1：是，2：否）
     */
    private String settlementType;
    private String ccid;
    private String agentName;
    private String channelName;
    private Integer userid;
    private String username;

    private String ccidKey;
    private String userKey;

    public String getCcidKey(){
//        return StrUtil.join("/", StrUtil.concat(true, "[", ccid, "]"), agentName, channelName);
        return StrUtil.join("/", StrUtil.concat(true, "[", ccid, "]", ("1".equals(settlementType) ? "[内结]" : "")), agentName, channelName);
    }

    public String getUserKey(){
        if(StrUtil.contains(username, "(")){
            return username;
        }
        return StrUtil.concat(true, username, "(", StrUtil.toString(userid), ")");
    }
}
