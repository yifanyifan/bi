package com.stnts.bi.schedule;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.stnts.bi.schedule.sdk.SdkAccRegPayTask;
import com.stnts.bi.schedule.util.SqlUtil;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestSdkAccRegPayTask {

    @Autowired
    private SdkAccRegPayTask sdkAccRegPayTask;

    @Autowired
    private SqlUtil sqlUtil;

    @org.junit.Test
    public void Test() {
        String sql = "truncate table banyan_bi_sdk.acc_reg_pay";
        sqlUtil.executeSql(sql);

        DateTime date = DateUtil.offsetDay(new Date(), -30);
        //DateTime date = DateUtil.parseDate("2020-01-01");
        while (true) {
            sdkAccRegPayTask.insert(date);
            if(DateUtil.isSameDay(date, DateUtil.yesterday())) {
                break;
            }
            date = DateUtil.offsetDay(date, 1);
        }


    }

}
