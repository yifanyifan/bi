package com.stnts.bi.datamanagement.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public static String[] getNullPropertyNamesAddId(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        emptyNames.add("id");
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}
