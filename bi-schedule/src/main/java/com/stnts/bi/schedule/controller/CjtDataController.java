package com.stnts.bi.schedule.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.schedule.deduct.changjingtong.CjtDDPidDayTask;
import com.stnts.bi.schedule.deduct.changjingtong.CjtDeductTask;
import com.stnts.bi.schedule.deduct.changjingtong.CjtSlyTask;
import com.stnts.bi.schedule.deduct.changjingtong.CjtSszmTask;
import com.stnts.bi.schedule.deduct.vo.CjtDeductAgainVO;
import com.stnts.bi.schedule.deduct.vo.CjtSszmVO;
import com.stnts.bi.schedule.service.CjtDataService;
import com.stnts.bi.schedule.util.SignUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/sign/cjtDataController")
public class CjtDataController {
    private static final Logger log = LoggerFactory.getLogger(CjtDataController.class);

    @Autowired
    private SignUtil signUtil;
    @Autowired
    private CjtSszmTask cjtSszmTask;
    @Autowired
    private CjtDeductTask cjtDeductTask;
    @Autowired
    private CjtDataService cjtDataService;
    @Autowired
    private CjtDDPidDayTask cjtDDPidDayTask;
    @Autowired
    private CjtSlyTask cjtSlyTask;

    private ExecutorService pool = Executors.newFixedThreadPool(15);

    /**
     * 重跑SSZM和XKSD 日数据
     */
    @PostMapping("/accumulative")
    public ResultEntity accumulative(@RequestBody CjtSszmVO cjtSszmVO) {
        try {
            if (ObjectUtil.isEmpty(cjtSszmVO.getPartitionDateStart()) || ObjectUtil.isEmpty(cjtSszmVO.getPartitionDateEnd())) {
                return ResultEntity.success("开始日期和结束日期不可为空（一天则填相同日期）");
            }
            if (CollectionUtil.isNotEmpty(cjtSszmVO.getChannelIdList()) && CollectionUtil.isNotEmpty(cjtSszmVO.getSubChannelIdList())) {
                return ResultEntity.success("渠道ID和子渠道ID不可同时有值");
            }
            if (StringUtils.isBlank(cjtSszmVO.getModel())) {
                return ResultEntity.success("模块必填：SSZM / XKSD");
            }
            Long startTime = System.currentTimeMillis();
            log.info("\n\n=============开始执行任务[" + cjtSszmVO.getModel() + "日数据重跑]" + JSON.toJSONString(cjtSszmVO));
            cjtSszmTask.startAgain(cjtSszmVO);
            Long endTime = System.currentTimeMillis();
            log.info("=============执行任务[" + cjtSszmVO.getModel() + "日数据重跑]结束,耗时[" + (endTime - startTime) + "]ms===========\n\n");
        } catch (Exception e) {
            log.info("=============执行任务[" + cjtSszmVO.getModel() + "日数据重跑]异常" + e.getMessage(), e);
            return ResultEntity.exception(e.getMessage());
        }

        return ResultEntity.success(null);
    }

    /**
     * 重跑DD 日数据
     */
    @PostMapping("/cjtDDPidDayTask/startAgain")
    public ResultEntity cjtDDPidDayTaskStartAgain(@RequestBody CjtSszmVO cjtSszmVO) {
        try {
            if (ObjectUtil.isEmpty(cjtSszmVO.getPartitionDateStart()) || ObjectUtil.isEmpty(cjtSszmVO.getPartitionDateEnd())) {
                throw new Exception("开始日期和结束日期不可为空（一天则填相同日期）");
            }
            if (CollectionUtil.isNotEmpty(cjtSszmVO.getChannelIdList()) && CollectionUtil.isNotEmpty(cjtSszmVO.getSubChannelIdList())) {
                throw new Exception("渠道ID和子渠道ID不可同时有值");
            }

            Long startTime = System.currentTimeMillis();
            log.info("\n\n=============开始执行任务[DD日数据重跑]" + JSON.toJSONString(cjtSszmVO));
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    cjtDDPidDayTask.startAgain(cjtSszmVO);
                }
            });
            Long endTime = System.currentTimeMillis();
            log.info("=============执行任务[DD日数据重跑]结束,耗时[" + (endTime - startTime) + "]ms===========\n\n");
        } catch (Exception e) {
            log.info("=============执行任务[DD日数据重跑]异常：" + e.getMessage(), e);
            return ResultEntity.exception(e.getMessage());
        }

        return ResultEntity.success("涉及hive查询，用异步执行");
    }

    /**
     * 重跑随乐游 日数据
     */
    @PostMapping("/cjtSlyTask/startAgain")
    public ResultEntity cjtSlyTaskStartAgain(@RequestBody CjtSszmVO cjtSszmVO) {
        try {
            if (ObjectUtil.isEmpty(cjtSszmVO.getPartitionDateStart()) || ObjectUtil.isEmpty(cjtSszmVO.getPartitionDateEnd())) {
                throw new Exception("开始日期和结束日期不可为空（一天则填相同日期）");
            }
            if (CollectionUtil.isNotEmpty(cjtSszmVO.getChannelIdList()) && CollectionUtil.isNotEmpty(cjtSszmVO.getSubChannelIdList())) {
                throw new Exception("渠道ID和子渠道ID不可同时有值");
            }

            Long startTime = System.currentTimeMillis();
            log.info("\n\n=============开始执行任务[随乐游日数据重跑]" + JSON.toJSONString(cjtSszmVO));

            cjtSlyTask.startAgain(cjtSszmVO);

            Long endTime = System.currentTimeMillis();
            log.info("=============执行任务[随乐游日数据重跑]结束,耗时[" + (endTime - startTime) + "]ms===========\n\n");
        } catch (Exception e) {
            log.info("=============执行任务[随乐游日数据重跑]异常：" + e.getMessage(), e);
            return ResultEntity.exception(e.getMessage());
        }

        return ResultEntity.success("涉及hive查询，用异步执行");

    }

    /**
     * 重跑（插入：订单数据，订单扣量，PID扣量，日收益）
     */
    @PostMapping("/cjtDeductTask/startAgain")
    public ResultEntity startAgain(@RequestBody CjtDeductAgainVO cjtDeductAgainVO) {
        try {
            if (ObjectUtil.isEmpty(cjtDeductAgainVO.getPartitionDateStart()) || ObjectUtil.isEmpty(cjtDeductAgainVO.getPartitionDateEnd())) {
                throw new Exception("开始日期和结束日期不可为空（一天则填相同日期）");
            }
            if (CollectionUtil.isNotEmpty(cjtDeductAgainVO.getChannelIdList()) && CollectionUtil.isNotEmpty(cjtDeductAgainVO.getSubChannelIdList())) {
                throw new Exception("渠道ID和子渠道ID不可同时有值");
            }

            Long startTime = System.currentTimeMillis();
            log.info("\n\n=============开始执行任务[ALL扣量重跑]===========");
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        cjtDeductTask.startAgain(cjtDeductAgainVO);
                    } catch (Exception e) {
                        log.info("ALL扣量重跑异常" + e.getMessage(), e);
                    }
                }
            });
            Long endTime = System.currentTimeMillis();
            log.info("=============执行任务[ALL扣量重跑]结束,耗时[" + (endTime - startTime) + "]ms===========\n\n");
        } catch (Exception e) {
            log.info("执行任务[ALL扣量重跑]异常：" + e.getMessage(), e);
            return ResultEntity.exception(e.getMessage());
        }

        return ResultEntity.success("涉及hive查询，用异步执行");
    }

    /**
     * 重跑（更新：PID扣量，日收益）
     */
    @PostMapping("/cjtDeductTask/startAgainDeduct")
    public ResultEntity startAgainDeduct(@RequestBody CjtDeductAgainVO cjtDeductAgainVO) {
        try {
            if (ObjectUtil.isEmpty(cjtDeductAgainVO.getPartitionDateStart()) || ObjectUtil.isEmpty(cjtDeductAgainVO.getPartitionDateEnd())) {
                throw new Exception("开始日期和结束日期不可为空（一天则填相同日期）");
            }
            if (CollectionUtil.isNotEmpty(cjtDeductAgainVO.getChannelIdList()) && CollectionUtil.isNotEmpty(cjtDeductAgainVO.getSubChannelIdList())) {
                throw new Exception("渠道ID和子渠道ID不可同时有值");
            }

            Long startTime = System.currentTimeMillis();
            log.info("\n\n=============开始执行任务[ALL扣量重跑（接口）]===========");
            cjtDeductTask.startAgainDeduct(cjtDeductAgainVO);
            Long endTime = System.currentTimeMillis();
            log.info("=============执行任务[ALL扣量重跑（接口）]结束,耗时[" + (endTime - startTime) + "]ms===========\n\n");
        } catch (Exception e) {
            log.info("=============执行任务[ALL扣量重跑（接口）]异常：" + e.getMessage(), e);
            return ResultEntity.exception(e.getMessage());
        }

        return ResultEntity.success(null);
    }
}
