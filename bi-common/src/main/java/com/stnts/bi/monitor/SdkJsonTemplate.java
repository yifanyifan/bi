package com.stnts.bi.monitor;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/1/25
 */
@Data
public class SdkJsonTemplate {

    private String type = "object";
    private List<SdkPutTemplateSub> para = new ArrayList<>();

    public SdkJsonTemplate (LogBO logBO) {
        SdkPutTemplateSub sdkPutTemplateSub = new SdkPutTemplateSub();
        sdkPutTemplateSub.setData(Arrays.asList(logBO));
        sdkPutTemplateSub.setMq("dt_monitor_api_log");
        para.add(sdkPutTemplateSub);
    }

    @Data
    private class SdkPutTemplateSub{
        private String mq;
        private List<LogBO> data;
    }


}
