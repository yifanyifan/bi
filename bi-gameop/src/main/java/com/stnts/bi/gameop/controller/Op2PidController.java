package com.stnts.bi.gameop.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.gameop.DimPidUser;
import com.stnts.bi.exception.BiException;
import com.stnts.bi.gameop.service.BaseService;
import com.stnts.bi.mapper.gameop.DimPidUserMapper;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author: liang.zhang
 * @description: 游戏运营V2  PID设置
 * @date: 2021/8/24
 */
@RestController
@RequestMapping("op2/pid")
public class Op2PidController {

    @Autowired
    private BaseService baseService;

    @Autowired
    private DimPidUserMapper dimPidUserMapper;

    @PostMapping("chart")
    public ResultEntity<QueryChartResultVO> gameChart(@RequestBody String data) {
        return baseService.getChart(data);
    }

    @PostMapping("export")
    public void gameExport(@RequestParam("data") String data, HttpServletResponse response) throws IOException {
        baseService.export(data, response);
    }


    @PostMapping("add")
    public ResultEntity<String> addPidUser(@Validated @RequestBody DimPidUser dimPidUser) {
        try {
            dimPidUserMapper.insertNew(dimPidUser);
            return ResultEntity.success(null);
        } catch (Exception e) {
            throw new BiException(e.getMessage());
        }
    }

    @GetMapping("list")
    public ResultEntity<List<DimPidUser>> listPidUser(Integer userId, String gameName, String channelName, String pid) {
        try{
            List<DimPidUser> dimPidUserOpList = dimPidUserMapper.selectList(new QueryWrapper<DimPidUser>()
                    .lambda()
                    .eq(null != userId, DimPidUser::getUserId, userId)
                    .like(StringUtils.isNotBlank(pid), DimPidUser::getPids, pid)
                    .like(StringUtils.isNotBlank(gameName), DimPidUser::getGameNames, gameName)
                    .like(StringUtils.isNotBlank(channelName), DimPidUser::getChannelNames, channelName)
                    .orderByDesc(DimPidUser::getUpdatedTime)
                    .orderByAsc(DimPidUser::getUserId)
            );
            return ResultEntity.success(dimPidUserOpList);
        }catch(Exception e){
            throw new BiException(e.getMessage());
        }
    }

    @GetMapping("detail")
    public ResultEntity<DimPidUser> getOne(@RequestParam(value = "id") Integer id){
        try{
            DimPidUser dimPidUser = dimPidUserMapper.selectById(id);
            return ResultEntity.success(dimPidUser);
        }catch(Exception e){
            throw new BiException(e.getMessage());
        }
    }
}
