package com.stnts.bi.gameop.util;

import com.stnts.bi.entity.gameop.DimCostOp;
import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/8/24
 */
public class OpExcelUtil {


    /**
     * 定制解析
     *
     * @param is
     */
    public static List<DimCostOp> parse(InputStream is) throws Exception {

        List<DimCostOp> costList = new ArrayList<>();
        Workbook workbook = null;
        workbook = WorkbookFactory.create(is);
        is.close();
        Sheet sheet = workbook.getSheetAt(0);
        int rowNum = sheet.getLastRowNum();
        for (int i = 1; i <= rowNum; i++) {

            Row row = sheet.getRow(i);
            int j = 0;
            Cell cellDate = row.getCell(j++);
            Cell cellPid = row.getCell(j++);
            Cell cellRealCost = row.getCell(j);

            DimCostOp cost = DimCostOp.builder().costDate(cellDate.getDateCellValue())
                    .pid(cellPid.getStringCellValue().toUpperCase().trim())
                    .realCost(cellRealCost.getNumericCellValue())
                    .build();

            costList.add(cost);
        }
        return costList;
    }

}
