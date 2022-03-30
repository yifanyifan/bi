package com.stnts.bi.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stnts.bi.entity.sys.UserProductFocusEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/11/30
 */
public interface UserProductFocusMapper extends BaseMapper<UserProductFocusEntity> {

    @Insert("insert into stbi_user_product_focus(user_id, product_id) values (#{bean.userId}, #{bean.productId}) " +
            "on duplicate key update product_id = #{bean.productId} ")
    int insertOrUpdate(@Param("bean") UserProductFocusEntity bean);
}
