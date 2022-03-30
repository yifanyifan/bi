package com.stnts.bi.datamanagement.util;

import cn.hutool.core.util.StrUtil;
import com.stnts.bi.common.BiSessionUtil;
import com.stnts.bi.entity.sys.UserEntity;
import com.stnts.bi.entity.sys.UserRoleEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/5/5
 */
@Slf4j
public class TmpUtil {

    public static String getDepartmentCode(RedisTemplate redisTemplate, HttpServletRequest request, int roleAdminId){

        //部门临时方案
        try {

            UserEntity user = BiSessionUtil.build(redisTemplate, request).getSessionUser();
            String departmentCode = user.getCode().substring(0, 5);
            List<Integer> roleIdList = user.getRoles().stream().map(UserRoleEntity::getRoleId).collect(Collectors.toList());
            if(!roleIdList.contains(roleAdminId) && StrUtil.isNotEmpty(departmentCode)){
                return departmentCode;
            }
        }catch(Exception e){
            log.warn("获取部门信息出错: {}", e.getMessage());
        }
        return null;
    }
}
