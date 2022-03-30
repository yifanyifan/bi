package com.stnts.bi.datamanagement.init;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stnts.bi.datamanagement.module.cooperation.entity.Cooperation;
import com.stnts.bi.datamanagement.module.cooperation.mapper.CooperationMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/4/16
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CcidTest {

    @Autowired
    private CooperationMapper cooperationMapper;

    @Test
    public void initCcid() {

        /** 解析出excel数据 */
        List<CsvVO> vos = parseCsv();
        vos.stream().forEach(System.out::println);

        Set<String> companyNames = vos.stream().map(CsvVO::getCompanyName).collect(Collectors.toSet());
        Map<String, Long> companyMap = companyMap(companyNames);
        System.out.println(companyMap);

    }

    /**
     * 解析csv
     * @return
     */
    private List<CsvVO> parseCsv() {
        return FileUtil.readLines(new File("D:\\chromeDown\\副本云飞扬确认表.csv"), Charset.forName("GBK")).stream().skip(1).map(line -> {
            String[] tokens = StringUtils.splitPreserveAllTokens(line, ",");
            int i = 0;
            return new CsvVO(tokens[i++], tokens[i++], tokens[i++], tokens[i++], tokens[i++], tokens[i++], tokens[i++], tokens[i++], tokens[i++], tokens[i++], tokens[i++], tokens[i++], tokens[i++], tokens[i++], tokens[i++], tokens[i++], tokens[i++]);
        }).collect(Collectors.toList());
    }

    private Map<String, Long> companyMap(Set<String> companyNames){
        return cooperationMapper.selectList(new QueryWrapper<Cooperation>().select("id", "company_name")
                .lambda().in(Cooperation::getCompanyName, companyNames)).stream().collect(Collectors.toMap(Cooperation::getCompanyName, Cooperation::getId));
    }
}
