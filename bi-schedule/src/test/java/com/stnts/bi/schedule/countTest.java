package com.stnts.bi.schedule;

import com.stnts.bi.schedule.util.SqlUtil;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class countTest {

    @Autowired
    private SqlUtil sqlUtil;

    @org.junit.Test
    public void Test() {
        String selectSql = "select count(*) from banyan_bi_sdk.bi_channel_maintain_transfer";
        Integer count = sqlUtil.queryForOne(selectSql);
        System.out.println(count);
    }

}
