package com.stnts.bi.gameop.controller;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.gameop.DimPidUser;
import com.stnts.bi.exception.BiException;
import com.stnts.bi.gameop.service.BaseService;
import com.stnts.bi.mapper.gameop.DimPidUserMapper;
import com.stnts.bi.sql.vo.QueryChartResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: liang.zhang
 * @description: 游戏运营V2  明细数据
 * @date: 2021/8/24
 */
@RestController
@RequestMapping("op2/detail")
public class Op2DetailController {

    @Autowired
    private BaseService baseService;

    @PostMapping("chart")
    public ResultEntity<QueryChartResultVO> gameChart(@RequestBody String data) {
        return baseService.getChart(data);
    }

    @PostMapping("export")
    public void gameExport(@RequestParam("data") String data, HttpServletResponse response) throws IOException {
        baseService.export(data, response);
    }

//    @PostMapping("addPidUser")
//    public ResultEntity<String> addPidUser(@Validated @RequestBody DimPidUserOp dimPidUserOp){
//        try{
//            dimPidUserOpMapper.insertOne(dimPidUserOp);
//            return ResultEntity.success(null);
//        }catch(Exception e){
//            throw new BiException(e.getMessage());
//        }
//    }
//
//    @PostMapping("editPidUser")
//    public ResultEntity<String> editPidUser(@Validated @RequestBody DimPidUserOp dimPidUserOp){
//        try{
//            dimPidUserOpMapper.updateOne(dimPidUserOp);
//            return ResultEntity.success(null);
//        }catch(Exception e){
//            throw new BiException(e.getMessage());
//        }
//    }
//
//    @GetMapping("listPidUser")
//    public ResultEntity<List<DimPidUserOp>> listPidUser(String userId, String gameCode, String channelId, String pid){
//        List<DimPidUserOp> dimPidUserOpList = dimPidUserOpMapper.selectList(new QueryWrapper<DimPidUserOp>()
//                .lambda()
//                .eq(StringUtils.isNotBlank(userId), DimPidUserOp::getUserId, userId)
//                .eq(StringUtils.isNotBlank(pid), DimPidUserOp::getPid, pid)
//                .like(StringUtils.isNotBlank(gameCode), DimPidUserOp::getGameCode, gameCode)
//                .like(StringUtils.isNotBlank(channelId), DimPidUserOp::getChannelId, channelId)
//                .orderByAsc(DimPidUserOp::getUserId)
//        );
//        return ResultEntity.success(dimPidUserOpList);
//    }

}
