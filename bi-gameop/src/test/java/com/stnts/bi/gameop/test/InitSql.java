package com.stnts.bi.gameop.test;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URLDecoder;
import java.util.*;
import java.util.stream.IntStream;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/12/7
 */
public class InitSql {

    public static void main(String[] args) throws Exception{

        //insert into dim_seq(seq_id) values (0), (1), (2), (3), (4), (5), (6), (7), (8), (9), (10), (11), (12);

//        String pre = "insert into dim_seq(seq_id) values ";
//        StringJoiner sj = new StringJoiner(",");
//        IntStream.range(0, 1000).forEach(i -> sj.add(String.format("(%s)", i)));
//        System.out.println(pre + sj.toString());
//        String line = "%5B%7B%22country%22%3A%22%E4%B8%AD%E5%9B%BD%22%2C%22matrix_id%22%3A%22%22%2C%22email_bind%22%3A%22%22%2C%22password%22%3A%22R3o3n40V%24Q3WYs4cUavD31Iew9rIhJ.%22%2C%22qq_unionid%22%3A%22UID_7709BCE9AA917CA80B99DEC740FB1B35%22%2C%22passport%22%3A%22eZB9LUGR7pIq%2FRCAeglltjYSTUqsYMfRICXiVJeFJVE%3D%22%2C%22constellation%22%3A%22%22%2C%22province%22%3A%22%22%2C%22weixin_unionid%22%3A%22%22%2C%22register_ip%22%3A%220%22%2C%22qq%22%3A%22%22%2C%22area%22%3A%22%22%2C%22zip%22%3A%22%22%2C%22line_nickname%22%3A%22%22%2C%22is_invited%22%3A%220%22%2C%22degree%22%3A%220%22%2C%22qq_openid%22%3A%22D63E0389F1AD0EB05B251B4F64E5235C%22%2C%22baquan_openid%22%3A%22%22%2C%22phone_bind%22%3A%22aSEKwuELVgL8Yzq9XqtUTA%3D%3D%22%2C%22facebook_id%22%3A%22%22%2C%22steam_uid%22%3A%22%22%2C%22facebook_nickname%22%3A%22%22%2C%22phone%22%3A%22aSEKwuELVgL8Yzq9XqtUTA%3D%3D%22%2C%22pwd_flag%22%3A%220%22%2C%22nick_name%22%3A%22%22%2C%22integration%22%3A%220%22%2C%22logo_status%22%3A%221%22%2C%22status%22%3A%220%22%2C%22birthday%22%3A%221970-01-01%2008%3A00%3A00%22%2C%22experience_value%22%3A%220%22%2C%22weibo_nickname%22%3A%22%22%2C%22gender%22%3A%22M%22%2C%22logo_path%22%3A%22%22%2C%22city%22%3A%22%22%2C%22modify_time%22%3A%222021-12-09%2014%3A56%3A24%22%2C%22flags%22%3A%223%22%2C%22real_name%22%3A%22%22%2C%22line_id%22%3A%22%22%2C%22uid%22%3A%22123992111%22%2C%22weixin_nickname%22%3A%22%22%2C%22qq_nickname%22%3A%2244CA44CA44CA%22%2C%22third_uname%22%3A%22%22%2C%22pay_password%22%3A%22%22%2C%22matrix_key%22%3A%22%22%2C%22email_enable%22%3A%222%22%2C%22third_uid%22%3A%22%22%2C%22identification_card%22%3A%22%22%2C%22weixin_openid%22%3A%22%22%2C%22email%22%3A%22%22%2C%22game_id%22%3A%22%22%2C%22address%22%3A%22%22%2C%22weibo_openid%22%3A%22%22%2C%22register_date%22%3A%222021-12-09%2014%3A56%3A24%22%2C%22integral_clean_year%22%3A%220%22%7D%5D";
//        String decode = URLDecoder.decode(line, "UTF-8");
//        System.out.println(decode);
//        ObjectMapper om = new ObjectMapper();
//        JavaType type = om.getTypeFactory().constructParametricType(ArrayList.class, HashMap.class);
//        List<Map<String, String>> lists = om.readValue(decode, type);
//
//        System.out.println(JSON.toJSONString(lists.get(0)));

        Date d1 = new Date();
        Date d2 = new Date();

        System.out.println(!d1.before(d2));
    }
}
