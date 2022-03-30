package com.stnts.bi.datamanagement.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import com.stnts.bi.datamanagement.exception.BusinessException;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class ExcelUtils {

    private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

    private static List<List<Object>> lineList = new ArrayList<>();

    /**
     * excel 导出工具类
     *
     * @param response
     * @param fileName    文件名
     * @param projects    对象集合
     * @param columnNames 对应的是对象中的字段名字
     * @param keys        导出的excel中的列名
     */
    public static void export(HttpServletResponse response, String fileName, List<?> projects, String[] columnNames, String[] keys) throws IOException {

        ExcelWriter bigWriter = ExcelUtil.getBigWriter();

        //排除字段操作(如果为true，则不设置alias的字段将不被输出)
        bigWriter.setOnlyAlias(true);

        for (int i = 0; i < columnNames.length; i++) {
            bigWriter.addHeaderAlias(columnNames[i], keys[i]);
            bigWriter.setColumnWidth(i, 20);
        }
        // 一次性写出内容，使用默认样式，强制输出标题
        bigWriter.write(projects, true);
        //response为HttpServletResponse对象
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
        response.setHeader("Content-Disposition", "attachment;filename=" + new String((fileName + ".xlsx").getBytes(), "iso-8859-1"));
        ServletOutputStream out = response.getOutputStream();
        bigWriter.flush(out, true);
        // 关闭writer，释放内存
        bigWriter.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }

    /**
     * 方法说明：hutool实现excel导出工具类
     *
     * @author :leanolee
     * @创建时间：
     * @param response
     * @param clazz    导出数据的类型
     * @param list     数据集合
     * @param fileName 文件名称,自动在后面添加当前时间(yyyyMMDDHHmmss)
     * @param removeField 去除字段(不需要导出到文件中的字段)
     * @return void
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static void exportMothod(HttpServletResponse response, Class clazz, List<?> list, String fileName,String[] removeField)
            throws Exception {
        if (CollUtil.isNotEmpty(list)) {
            if (!list.get(0).getClass().equals(clazz)) {
                logger.error("数据类型与传入的集合数据类型不一致！数据类型：{}；集合数据类型：{}", clazz, list.get(0).getClass());
                throw new Exception("数据类型与传入的集合数据类型不一致！");
            } else {
                try {
                    // 获取输出构造器
                    ExcelWriter writer = ExcelUtil.getWriterWithSheet(fileName);
                    // 获取当前类字段(反射也可以使用hutool工具包里面的相关方法)
                    Field[] fields2 = clazz.getDeclaredFields();
                    // 字段名称集合
                    List<String> fieldNames = new ArrayList<>();
                    // 字段中文名称集合(ApiModelProperty注解的value值)
                    List<String> cnNames = new ArrayList<>();
                    // 时间字段的额列位置
                    for (Field field : fields2) {
                        // 设置属性
                        if (!field.isAccessible()) {
                            field.setAccessible(true);
                        }
                        // 去除序列好和id
                        String fieldName = field.getName();
                        if (!(Arrays.asList(removeField).contains(fieldName))) {
                            fieldNames.add(fieldName);
                            // 判断是否有注解Api

                            /**
                             * 此处判断需要导出的字段方式是使用的swagger的注解作为依据
                             * 如果项目中未使用swgger的注解,可以自定义注解作为依据或者能够作为判断依据的相关表示
                             * 此处除去id字段存在一定的问题,不影响的大局的情况下可以不用考虑
                             * 如果想要除去不必要的字段需要手动编写除去的相关功能
                             **/
                            boolean annotationPresent = field.isAnnotationPresent(ApiModelProperty.class);
                            if (annotationPresent) {
                                ApiModelProperty annotation = field.getAnnotation(ApiModelProperty.class);
                                // 获取注解的值作为导出的表头
                                String name = annotation.value();
                                cnNames.add(name);
                            }
                        } else {
                            //排除字段操作(如果为true，则不设置alias的字段将不被输出)
                            writer.setOnlyAlias(true);
                        }
                    }
                    // 获取表头和对应的字段
                    String[] fs = new String[fieldNames.size()];
                    String[] ns = new String[cnNames.size()];
                    // 集合转换为数组
                    String[] fields = fieldNames.toArray(fs);
                    String[] names = cnNames.toArray(ns);
                    // 设置excel表头及其对应的字段名
                    for (int i = 0; i < names.length; i++) {
                        writer.addHeaderAlias(fields[i], names[i]);
                    }

                    // 设置文本自动换行
                    Workbook workbook = writer.getWorkbook();
                    StyleSet ss = new StyleSet(workbook);
                    ss.setWrapText();
                    writer.setStyleSet(ss);

                    //文本行不自动换行注释掉上面部分的代码

                    //-------------------------------------------------------------------
                    // 一次性写出内容，使用默认样式，强制输出标题
                    writer.write(list, true);
                    // response为HttpServletResponse对象
                    response.setContentType("application/vnd.ms-excel;charset=utf-8");
                    // test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
                    ServletOutputStream out = null;
                    try {
                        /**
                         * 如果字段的值不是太长可以不用设置列宽
                         * 设置列宽
                         * 工具类有自动的列宽,相较于默认列宽较小
                         * 显示日期时间存在显示不全而自动转换隐藏的情况(xxxxxxxxxx)
                         * 显示数据为居中显示
                         **/
                        for (int m = 0; m <= fieldNames.size(); m++) {
                            writer.setColumnWidth(m, 23);
                        }

                        /**
                         * 设置文件名称
                         * 名称加时间的方式显示方便文件在本地磁盘的查看
                         **/
                        fileName = fileName + DateUtil.format(new Date(), "YYYYMMddHHmmss");
                        // 设置请求头属性
                        response.setHeader("Content-Disposition", "attachment;filename=" + new String((fileName + ".xlsx").getBytes(), "iso-8859-1"));
                        out = response.getOutputStream();
                        // 写出到文件
                        writer.flush(out, true);
                        // 关闭writer，释放内存
                        writer.close();
                        // 此处记得关闭输出Servlet流
                        IoUtil.close(out);
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                        e.printStackTrace();
                    }

                } catch (SecurityException e) {
                    logger.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            logger.error("集合数据为空！");
            throw new Exception("集合数据为空！");
        }
    }

    /**
     * excel导入工具类
     * @param file 文件
     * @param columNames 列对应的字段名
     * @param removeRowNum 删除头部前几行，如删除第1行，值为1
     * @return
     */
    public static List<Map<String, Object>> leading(MultipartFile file, String[] columNames, int removeRowNum) throws BusinessException, IOException {
        String fileName = file.getOriginalFilename();
        // 上传文件为空
        if (StringUtils.isEmpty(fileName)) {
            throw new BusinessException("没有导入文件");
        }
        //上传文件大小为1000条数据
        if (file.getSize() > 1024 * 1024 * 10) {
            logger.error("upload | 上传失败: 文件大小超过10M，文件大小为：{}", file.getSize());
            throw new BusinessException("上传失败: 文件大小不能超过10M!");
        }
        // 上传文件名格式不正确
        if (fileName.lastIndexOf(".") != -1 && !".xlsx".equals(fileName.substring(fileName.lastIndexOf(".")))) {
            throw new BusinessException("文件名格式不正确, 请使用后缀名为.XLSX的文件");
        }

        //读取数据
        ExcelUtil.read07BySax(file.getInputStream(), 0, createRowHandler());
        //去除excel中的第若干行数据
        for (int i = 0; i < removeRowNum; i++) {
            if(lineList.size() > 0){
                lineList.remove(0);
            }
        }

        //将数据封装到list<Map>中
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (int i = 0; i < lineList.size(); i++) {
            if (null != lineList.get(i)) {
                Map<String, Object> hashMap = new HashMap<>();
                for (int j = 0; j < columNames.length; j++) {
                    Object property = lineList.get(i).get(j);
                    hashMap.put(columNames[j], property);
                }
                dataList.add(hashMap);
            } else {
                break;
            }
        }
        return dataList;
    }

    /**
     * 通过实现handle方法编写我们要对每行数据的操作方式
     */
    private static RowHandler createRowHandler() {
        //清空一下集合中的数据
        lineList.removeAll(lineList);
        return new RowHandler() {
            @Override
            public void handle(int i, long l, List<Object> list) {
                //将读取到的每一行数据放入到list集合中
                JSONArray jsonObject = new JSONArray(list);
                lineList.add(jsonObject.toList(Object.class));
            }
        };
    }

    public static List<Map<String, Object>> leadingBySheet(MultipartFile file, String[] columNames, int removeRowNum, int sheet) throws BusinessException, IOException {
        String fileName = file.getOriginalFilename();
        // 上传文件为空
        if (StringUtils.isEmpty(fileName)) {
            throw new BusinessException("没有导入文件");
        }
        //上传文件大小为1000条数据
        if (file.getSize() > 1024 * 1024 * 10) {
            logger.error("upload | 上传失败: 文件大小超过10M，文件大小为：{}", file.getSize());
            throw new BusinessException("上传失败: 文件大小不能超过10M!");
        }
        // 上传文件名格式不正确
        if (fileName.lastIndexOf(".") != -1 && !".xlsx".equals(fileName.substring(fileName.lastIndexOf(".")))) {
            throw new BusinessException("文件名格式不正确, 请使用后缀名为.XLSX的文件");
        }

        //读取数据
        ExcelUtil.read07BySax(file.getInputStream(), sheet, createRowHandler());
        //去除excel中的第若干行数据
        for (int i = 0; i < removeRowNum; i++) {
            if (lineList.size() > 0) {
                lineList.remove(0);
            }
        }

        //将数据封装到list<Map>中
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (int i = 0; i < lineList.size(); i++) {
            if (null != lineList.get(i)) {
                Map<String, Object> hashMap = new HashMap<>();
                for (int j = 0; j < columNames.length; j++) {
                    Object property = lineList.get(i).get(j);
                    hashMap.put(columNames[j], property);
                }
                dataList.add(hashMap);
            } else {
                break;
            }
        }
        return dataList;
    }
}
