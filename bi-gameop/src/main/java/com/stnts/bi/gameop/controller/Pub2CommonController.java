package com.stnts.bi.gameop.controller;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.gameop.DimCost;
import com.stnts.bi.exception.BiException;
import com.stnts.bi.gameop.util.ExcelUtil;
import com.stnts.bi.mapper.gameop.DimCostMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author: liang.zhang
 * @description: 游戏发行2.0 公共部分
 * @date: 2021/8/24
 */
@RestController
@RequestMapping("pub2/common")
@Slf4j
public class Pub2CommonController {

    @Autowired
    private DimCostMapper dimCostMapper;

    public static final String EXCEL_SUFFIX = ".xlsx";

    /**
     * 上传PID消耗数据
     * @param file
     * @return
     */
    @RequestMapping("uploadCost")
    public ResultEntity<String> uploadCost(@RequestParam("file") MultipartFile file){

        if(null == file || file.isEmpty()){
            throw new BiException("请上传文件");
        }
        String filename = file.getOriginalFilename();
        log.info("上传文件 {}", filename);
        if(!StringUtils.endsWith(filename, EXCEL_SUFFIX)){
            return ResultEntity.failure("只支持.xlsx类型文件");
        }
        try {
            List<DimCost> dimCosts = ExcelUtil.parse(file.getInputStream());
            dimCostMapper.insertBatch(dimCosts);
        } catch (IOException e) {

        }
        return ResultEntity.success(null);
    }

    @PostMapping("addCost")
    public ResultEntity<String> addCost(@Validated @RequestBody DimCost dimCost){
        try{
            dimCostMapper.insertOne(dimCost);
            return ResultEntity.success(null);
        }catch(Exception e){
            throw new BiException(e.getMessage());
        }
    }

    @GetMapping("listCost")
    public ResultEntity<List<DimCost>> listDimCost(){
        List<DimCost> dimCosts = dimCostMapper.selectList(null);
        return ResultEntity.success(dimCosts);
    }

    @Resource
    private ResourceLoader resourceLoader;

    @GetMapping("download")
    public void download(HttpServletRequest request, HttpServletResponse response){

        InputStream inputStream = null;
        ServletOutputStream servletOutputStream = null;
        try {
            String filename = "template.xlsx";
            String path = "template/import.xlsx";
            org.springframework.core.io.Resource resource = resourceLoader.getResource("classpath:"+path);

            response.setContentType("application/vnd.ms-excel");
            response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.addHeader("charset", "utf-8");
            response.addHeader("Pragma", "no-cache");
            String encodeName = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + encodeName + "\"; filename*=utf-8''" + encodeName);

            inputStream = resource.getInputStream();
            servletOutputStream = response.getOutputStream();
            IOUtils.copy(inputStream, servletOutputStream);
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (servletOutputStream != null) {
                    servletOutputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
