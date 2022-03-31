package com.stnts.bi.sys.schedule;

import com.stnts.bi.sys.service.LoginService;
import com.stnts.bi.sys.service.OlapMenuService;
import com.stnts.bi.sys.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2020/10/9
 *
 *   配置管理相关的一些定时任务放在这里
 */
@Component
@Slf4j
public class SysJob {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ProductService productService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private OlapMenuService olapMenuService;

    //    @Scheduled(cron = "0 0 0/1 * * ?")
//    @Scheduled(cron = "0 0 0/1 * * ?")
    public void syncProduct() {
        productService.syncProduct();
        log.info("[定时调度]产品线同步");
    }

//    @Scheduled(cron = "0 0 0/1 * * ?")
    public void syncUser() {
        loginService.syncUser();
        log.info("[定时调度]用户同步");
    }

//    @Scheduled(cron = "0 0 0/1 * * ?")
    public void syncDepartment() {
        loginService.syncDepartment();
        log.info("[定时调度]部门同步");
    }

    //@Scheduled(cron = "0 0 0/1 * * ?")
    public void syncOlapMenu() {
        olapMenuService.initMenu();
        log.info("[定时调度]olap一级菜单同步");
    }

    /**
     * 检测任务是否有其它节点正在执行，如果有，则放弃
     * @param id
     * @return
     */
    public boolean onlyOnceCheck(String id) {

//        redisTemplate.opsForValue().set();
        return true;
    }
}
