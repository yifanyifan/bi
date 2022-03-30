package com.stnts.bi.gameop.util;

import cn.hutool.core.io.FileUtil;
import com.stnts.bi.entity.gameop.DimCost;
import org.apache.poi.ss.usermodel.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/8/24
 */
public class ExcelUtil {


    /**
     * 定制解析
     * @param is
     */
    public static List<DimCost> parse(InputStream is){

        List<DimCost> costList = new ArrayList<>();
        Workbook workbook = null;
        try{
            workbook = WorkbookFactory.create(is);
            is.close();
            Sheet sheet = workbook.getSheetAt(0);
            int rowNum = sheet.getLastRowNum();
            for(int i = 1 ; i <= rowNum ; i++){

                Row row = sheet.getRow(i);
                int j = 0;
                Cell cellDate = row.getCell(j++);
                Cell cellPid = row.getCell(j++);
                Cell cellChargeRule = row.getCell(j++);
                Cell cellSource = row.getCell(j++);
                Cell cellBookCost = row.getCell(j++);
                Cell cellRealCost = row.getCell(j);

                DimCost cost = DimCost.builder()
                        .costDate(cellDate.getDateCellValue())
                        .pid(cellPid.getStringCellValue().toUpperCase().trim())
                        .chargeModel(cellChargeRule.getStringCellValue())
                        .source(cellSource.getStringCellValue())
                        .bookCost(cellBookCost.getNumericCellValue())
                        .realCost(cellRealCost.getNumericCellValue())
                        .build();

                costList.add(cost);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return costList;
    }

    public static void main(String[] args) {

        System.out.println(1);
        System.out.println(FileUtil.exist("C:\\Users\\Administrator\\Documents\\游戏发行导入模板.xlsx"));
        parse(FileUtil.getInputStream("C:\\Users\\Administrator\\Documents\\游戏发行导入模板.xlsx")).forEach(System.out::println);
    }
}
