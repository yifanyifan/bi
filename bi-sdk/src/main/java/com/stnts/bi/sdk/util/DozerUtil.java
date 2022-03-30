package com.stnts.bi.sdk.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liutianyuan
 * @date 2019-07-25 15:33
 */

public class DozerUtil {

    public static Mapper mapper = DozerBeanMapperBuilder.buildDefault();

    public static <T> T toBean(Object source, Class<T> clazz) {
        if(ObjectUtil.isNull(source)) {
            return ReflectUtil.newInstance(clazz);
        }
        return mapper.map(source, clazz);
    }

    public static <T> List<T> toBeanList(List<?> sourceList, Class<T> clazz) {
        if(CollectionUtil.isEmpty(sourceList)) {
            return Collections.emptyList();
        }
        return sourceList.stream().map(source -> toBean(source, clazz)).collect(Collectors.toList());
    }
}
