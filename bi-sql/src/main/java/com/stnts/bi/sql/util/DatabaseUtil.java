package com.stnts.bi.sql.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import com.stnts.bi.sql.constant.DataSourceConstant;
import com.stnts.bi.sql.constant.NameSpaceConstant;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatabaseUtil {

    public static Db getDB(String namespace, String dataSource) {
        Db db = null;
        if(StrUtil.isEmpty(dataSource)) {
            if (NameSpaceConstant.CLICKHOUSE.equals(namespace)) {
                db = Db.use(DataSourceConstant.CLICKHOUSE);
                log.info("使用数据源{}", DataSourceConstant.CLICKHOUSE);
            }
        } else {
            db = Db.use(dataSource);
            log.info("使用自定义数据源{}", dataSource);
        }
        return db;
    }

}
