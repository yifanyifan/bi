/*
 * Copyright 2019-2029
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stnts.generator;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.stnts.generator.config.GeneratorStrategy;
import com.stnts.generator.constant.GeneratorConstant;
import com.stnts.generator.properties.GeneratorProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * spring-boot-stnts代码生成器入口类
 *
 * @author stnts
 * @date 2019-10-22
 **/
@Component
public class SpringBootPlusGenerator {

    /**
     * 生成代码
     * @param args
     */
    public static void main(String[] args) {
        GeneratorProperties generatorProperties = new GeneratorProperties();

        // 设置基本信息
        generatorProperties
                .setMavenModuleName("bi-data-management")
                .setParentPackage("com.stnts.bi.datamanagement.module")
                .setModuleName("channel")
                .setAuthor("易樊")
                .setFileOverride(true);

        // 设置表信息
        generatorProperties.addTable("dm_channel_application","id");
        // 设置表前缀
        generatorProperties.setTablePrefix(Arrays.asList("dm_"));

        // 数据源配置
        generatorProperties.getDataSourceConfig()
                .setUsername("bi_dev2")
                .setPassword("bBnmU0BDLCOUM9Dl")
                .setDriverName("com.mysql.cj.jdbc.Driver")
                .setUrl("jdbc:mysql://10.0.44.120:33067/bi_data_management_dev2?useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8");

        // 生成配置
        generatorProperties.getGeneratorConfig()
                .setGeneratorStrategy(GeneratorStrategy.SINGLE)
                .setGeneratorEntity(true)
                .setGeneratorController(true)
                .setGeneratorService(true)
                .setGeneratorServiceImpl(true)
                .setGeneratorMapper(true)
                .setGeneratorMapperXml(true)
                .setGeneratorPageParam(true)
                .setGeneratorQueryVo(true)
                .setRequiresPermissions(false)
                .setPageListOrder(false)
                .setParamValidation(true)
                .setSwaggerTags(true)
                .setOperationLog(false);

        // 全局配置
        generatorProperties.getMybatisPlusGeneratorConfig().getGlobalConfig()
                .setOpen(true)
                .setSwagger2(true)
                .setIdType(IdType.AUTO)
                .setDateType(DateType.ONLY_DATE);

        // 策略配置
        generatorProperties.getMybatisPlusGeneratorConfig().getStrategyConfig()
                .setNaming(NamingStrategy.underline_to_camel)
                .setColumnNaming(NamingStrategy.underline_to_camel)
                .setEntityLombokModel(true)
                .setRestControllerStyle(true)
                .setControllerMappingHyphenStyle(true)
                .setVersionFieldName(GeneratorConstant.VERSION)
                .setLogicDeleteFieldName(GeneratorConstant.DELETED);

        // 生成代码
        CodeGenerator codeGenerator = new CodeGenerator();
        codeGenerator.generator(generatorProperties);

    }


}
