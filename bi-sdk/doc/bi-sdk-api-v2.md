- [通用接口参数文档](#通用接口参数文档)
- [数据概况](#数据概况)
  - [全局筛选](#全局筛选)
  - [运营指标](#运营指标)
    - [所有卡片指标](#所有卡片指标)
    - [访问构成-左图](#访问构成-左图)
    - [访问构成-右图](#访问构成-右图)
    - [主指标趋势](#主指标趋势)
    - [主指标趋势-分布-左图](#主指标趋势-分布-左图)
    - [主指标趋势-分布-右图](#主指标趋势-分布-右图)
  - [付费指标](#付费指标)
    - [所有卡片指标](#所有卡片指标-1)
    - [新用户转化-左图](#新用户转化-左图)
    - [新用户转化-右图](#新用户转化-右图)
    - [主指标趋势](#主指标趋势-1)
    - [主指标趋势-分布-左图](#主指标趋势-分布-左图-1)
    - [主指标趋势-分布-右图](#主指标趋势-分布-右图-1)
- [趋势分析](#趋势分析)
  - [终端类型下拉](#终端类型下拉)
  - [版本下拉](#版本下拉)
  - [渠道下拉](#渠道下拉)
  - [子渠道下拉](#子渠道下拉)
  - [计费别名下拉](#计费别名下拉)
  - [指标趋势](#指标趋势)
  - [指标明细](#指标明细)
- [渠道分析](#渠道分析)
  - [全局筛选](#全局筛选-1)
  - [渠道分布](#渠道分布)
    - [数据趋势-左图](#数据趋势-左图)
    - [数据趋势-右图](#数据趋势-右图)
    - [数据明细-指标去重](#数据明细-指标去重)
    - [数据明细-指标均值](#数据明细-指标均值)
  - [渠道趋势](#渠道趋势)
    - [数据趋势](#数据趋势)
    - [数据明细](#数据明细)
    - [新设备分布](#新设备分布)
    - [用户地域分布](#用户地域分布)
    - [活跃用户活跃终端分布-下拉](#活跃用户活跃终端分布-下拉)
    - [活跃用户活跃终端分布-左图](#活跃用户活跃终端分布-左图)
    - [活跃用户活跃终端分布-右图](#活跃用户活跃终端分布-右图)
  - [渠道效果](#渠道效果)
    - [渠道分组](#渠道分组)
    - [留存数据](#留存数据)
    - [留存数据-展开渠道](#留存数据-展开渠道)
    - [LTV](#ltv)
    - [LTV-展开渠道](#ltv-展开渠道)
    - [渠道分析矩阵](#渠道分析矩阵)
- [终端分析](#终端分析)
- [用户分析](#用户分析)
  - [用户留存](#用户留存)
  - [用户LTV](#用户ltv)
# 通用接口参数文档
- 查询参数
```json
{
    "chartName": "PV",  //图表名称
    "chartType": "line",	//图表类型.可传值table(表格),table(普通表格),retain(留存图),text(文本提),line(折线图),histogram(柱形图),two-axis(双轴图),line-area(面积图),radar(雷达图)，pie(饼图)
    "databaseName": "bi",	//数据库名
    "tableName": "view_sdk_app_sdk_web",	//数据表名
    "limit": "10",  //返回数量
    "rowFormat": 0, //返回行格式数据。0：否；1：是。
    "rollup":0, //通过去掉所有维度实现上卷，用于实现表格中的总计。0：否；1：是。
    "withRollup":0, //通过sql中得roolup实现上卷，用于实现柱状图中的总计。0：否；1：是。
    "retainTimeNum": "1,2,3",   //留存图距起始时间间隔，多个用逗号分隔。
    "dimension": [
        {
            "name": "date",	//维度名称
            "group": "day",	//聚合粒度。仅时间类型维度可以使用。可传值year、quarter、month、week、day、hour、minute
     		"aliasName": "年份", //维度别名
            "order": 0, //排序，默认升序。0：升序；-1：降序；-2：不参与排序。
            "orderContentList":["当日","1天","2~3天"], //自定义排序。
            "groupDataOrderContentList":["新用户","老用户"], //图例自定义排序。
     		"is_column": false, //是否行专列
            "format":"", //维度格式化。"Y-w"：第几周，"Y-w-d"：周区间。
            "havingLogic":"", //sql中的having功能。可传值 eq: 等于; neq: 不等于。
            "havingValue":"" //值
        }
    ],
    "measure": [
        {
            "name": "uv",	//度量名称
            "func": "uniqArray",	//聚合函数。可传count、count_distinct、sum、avg、max、min、uniqArray等
			"aliasName": "网吧", //度量别名
            "order": 0, //排序，默认升序。0：升序；-1：降序；-2：不参与排序。
            "orderContentList":["当日","1天","2~3天"], //自定义排序。
			"minvalue": null, //结果筛选最小值(null为自动)
            "maxvalue": null, //结果筛选最大值(null为自动)
            "digitDisplay": "", //数值显示。percent:百分数显示，保留2位小数。
            "decimal": 3,   //保留小数位数
            "proportion" : 0,   //计算总计占比(percent of total)。0：否；1：是。
            "percentOfMax" : 0,   //计算最大值占比(percent of max)。0：否；1：是。
			"contrast": "mom", //高级计算
			/*
			('d_yoy', '昨日同比'), ('w_yoy', '上周同比'), ('m_yoy', '上月同比'), ('y_yoy', '上年同比'),
			('d_yoy_rate', '昨日同比增长率'), ('w_yoy_rate', '上周同比增长率'), ('m_yoy_rate', '上月同比增长率'), ('y_yoy_rate', '上年同比增长率'),
			('mom', '环比'), ('mom_rate', '环比增长率'),
			('d_yoy_value', '昨日同期'), ('w_yoy_value', '上周同期'), ('m_yoy_value', '上月同期'), ('y_yoy_value', '上年同期')
            ('yoy', '同比'),('yoy_rate', '同比增长率')
			*/
        }
    ],
    "dashboard": [{	//全局筛选，针对全部图表。
        "name" : "date",	//全局筛选字段名称
        "logic": "between",	//筛选条件。in: 包含; notin: 不包含; eq: 等于; neq: 不等于;like: 包含; notlike: 不包含; startswith: 开头包含; endswith: 结尾包含;gt: 大于; gte: 大于等于; lt: 小于; lte: 小于等于; between: 区间；isnull: name is null; isblank: name = ''; isempty: name is null or name = ''; isnotnull:name is not null; isnotblank: name != '';isnotempty:name is not null and name != '';
        "value": "lastThirtyDays",	//值。logic是in、between时值为数组,logic是eq时值为普通字符串和数字
        "func": "day"	//函数(仅日期类型字段用)。可传值year(年),month(月),day(日),hour(时),minute(分)
		/*
		 日期类型字段动态条件如下
		今天：logic传eq，value传today
		昨天：logic传eq，value传yesterday
		本周:logic传between，value传thisWeek
		上周:logic传between，value传lastWeek
		本月:logic传between，value传thisMonth
		上月:logic传between，value传lastMonth
		近7天:logic传between，value传inSevenDays
		过去7天：logic传between，value传lastSevenDays
		近30天:logic传between，value传inThirtyDays
		过去30天:logic传between，value传lastThirtyDays
		近90天:logic传between，value传inNinetyDays
		过去90天:logic传between，value传lastNinetyDays
		近15天:logic传between，value传inFifteenDays
		过去15天:logic传between，value传lastFifteenDays
		*/
    }],
    "screen": [{	//图内筛选，针对当前图表。传值逻辑同全局筛选。
        "name" : "channel",
        "logic": "eq",
        "value": "aa",
        "func": ""
    }],
    "filter": [{
        "relation": "and", //条件关系。传值and或者or
        "member": [ //条件。传值逻辑同全局筛选。
            {
                "name" : "channel",
                "logic": "eq",
                "value": "aa",
                "func": ""
            }
        ]
    }],
    "compare": [{	//对比条件。传值逻辑同全局筛选。
        "name" : "date",
        "logic": "between",
        "value": "lastThirtyDays",
        "func": "day"
    }]
}
```

# 数据概况
## 全局筛选
接口请求地址 /bi-bak/bi-sdk/sdk/data/overview/selector/get  
参数同趋势分析中下拉接口
## 运营指标
接口请求地址 /bi-bak/bi-sdk/sdk/data/overview/operation/chart/get  
### 所有卡片指标
- 说明
卡片中的值从cardData对象获取。  
textValue：文本值。
yoyRateValue：同比值。
momRateValue：环比值。
yoyDate：同比时间，悬浮显示。
momDate：环比时间，悬浮显示。
其他卡片图也是这个规律。
- 查询参数
```json
{
    "id": "data-overview-operation-text-0",
    "chartType": "table",    
    "tableName": "view_sdk_app_web_register_payment_agg",
    "dimension": [
        {
            "name": "date",
            "group": "day",
            "aliasName": "时间"
        }
    ],
    "measure": [
        {
            "name": "pv",
            "func": "sum",
            "contrast": "",
            "aliasName": "浏览量(PV)"
        },
        {
            "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid)))))) + uniqExactArray(uid)",
            "func": "",
            "contrast": "",
            "aliasName": "访客数"
        },
        {
            "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid))))))",
            "func": "",
            "contrast": "",
            "aliasName": "游客数"
        },
        {
            "name": "register_user",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "新增用户"
        },
        {
            "name": "uniqExactArray(register_user) / (uniqExactArray(register_user) + length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid)))))))",
            "func": "",
            "contrast": "",
            "aliasName": "新用户转化率"
        },
        {
            "name": "uid",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "活跃用户数"
        },
        {
            "name": "sum(online_duration)/uniqExactArray(session)/60000",
            "func": "",
            "contrast": "",
            "aliasName": "平均访问时长(m)"
        },
        {
            "name": "(uniqExactArray(session) - uniqExactArray(session_again)) / uniqExactArray(session)",
            "func": "",
            "contrast": "",
            "aliasName": "跳出率"
        }
    ],
    "dashboard": [
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-06-20",
                "2021-06-20"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "app_version",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，版本。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen": []
}
```
### 访问构成-左图
- 查询参数
```json
{
    "id": "data-overview-operation-chart-10",
    "chartType": "pie",
    "tableName": "view_sdk_app_web_register_payment_agg",
    "dimension": [
    ],
    "measure": [
        {
            "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_first))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid))))))",
            "func": "",
            "contrast": "",
            "aliasName": "新游客"
        },
        {
            "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), arrayConcat(uv_with_uid, uv_first)))))))",
            "func": "",
            "contrast": "",
            "aliasName": "老游客"
        },
        {
            "name": "length(bitmapToArray(bitmapAnd(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), register_user))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uid))))))",
            "func": "",
            "contrast": "",
            "aliasName": "新增用户"
        },
        {
            "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uid))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), register_user))))))",
            "func": "",
            "contrast": "",
            "aliasName": "老用户"
        }
    ],
    "dashboard": [
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-05-01",
                "2021-06-01"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "app_version",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，版本。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen": []
}
```
### 访问构成-右图
- 查询参数
```json
{
    "id": "data-overview-operation-chart-20",
    "chartType": "histogram",
    "tableName": "view_sdk_app_web_register_payment_agg",
    "dimension": [
        {
            "name": "date",
            "group": "day",
            "aliasName": "时间"
        }
    ],
    "measure": [
        {
            "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_first))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid))))))",
            "func": "",
            "contrast": "",
            "aliasName": "新游客",
            "注释": "左图点击新游客时，仅传新游客度量，其余三个度量不用传。"
        },
        {
            "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), arrayConcat(uv_with_uid, uv_first)))))))",
            "func": "",
            "contrast": "",
            "aliasName": "老游客",
            "注释": "左图点击老游客时，仅传老游客度量，其余三个度量不用传。"
        },
        {
            "name": "length(bitmapToArray(bitmapAnd(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), register_user))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uid))))))",
            "func": "",
            "contrast": "",
            "aliasName": "新增用户",
            "注释": "左图点击新增用户时，仅传新增用户度量，其余三个度量不用传。"
        },
        {
            "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uid))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), register_user))))))",
            "func": "",
            "contrast": "",
            "aliasName": "老用户",
            "注释": "左图点击老用户时，仅传老用户度量，其余三个度量不用传。"
        }
    ],
    "dashboard": [
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-05-01",
                "2021-06-01"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "app_version",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，版本。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen": []
}
```
### 主指标趋势
- 说明  
  该接口新增momCompare参数表示环比对比，传值0或1；yoyCompare参数表示同比对比，传值0或1。  
  对比数据分别取返回数据中的momCompareData、yoyCompareData。  
  对比时间分别取返回数据中的momCompareStartDateStr、momCompareEndDateStr、yoyCompareStartDateStr、yoyCompareEndDateStr。  
  左侧平均值、最大值、最小值分别取返回数据中的measureAvg、measureMax、measureMin。  

指标选中浏览量(PV)时，度量为
```json
{
    "name": "pv",
    "func": "sum",
    "contrast": "",
    "aliasName": "浏览量(PV)"
}
```  
指标选中访客数时，度量为
```json
{
    "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid)))))) + uniqExactArray(uid)",
    "func": "",
    "contrast": "",
    "aliasName": "访客数"
}
```  
指标选中游客数时，度量为
```json
{
    "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid))))))",
    "func": "",
    "contrast": "",
    "aliasName": "游客数"
}
```  
指标选中新访客时，度量为
```json
{
    "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_first))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid)))))) + length(bitmapToArray(bitmapAnd(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), register_user))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uid))))))",
    "func": "",
    "contrast": "",
    "aliasName": "新访客"
}
```  
指标选中新增用户时，度量为
```json
{
    "name": "register_user",
    "func": "uniqArray",
    "contrast": "",
    "aliasName": "新增用户"
}
```
指标选中新用户转化率时，度量为
```json
{
    "name": "uniqExactArray(register_user) / (uniqExactArray(register_user) + length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid)))))))",
    "func": "",
    "contrast": "",
    "aliasName": "新用户转化率"
}
```
指标选中活跃用户数时，度量为
```json
{
    "name": "uid",
    "func": "uniqArray",
    "contrast": "",
    "aliasName": "活跃用户数"
}
```
指标选中次日留存率时，度量为
```json
{
    "name": "length(arrayIntersect(groupArrayArray(uid), groupArrayArray(yesterday_register_user))) / uniqArray(yesterday_register_user)",
    "func": "",
    "contrast": "",
    "aliasName": "次日留存率"
}
```
指标选中平均访问时长(m)时，度量为
```json
{
    "name": "sum(online_duration)/uniqExactArray(session)/60000",
    "func": "",
    "contrast": "",
    "aliasName": "平均访问时长(m)"
}
```
指标选中跳出率时，度量为
```json
{
    "name": "(uniqExactArray(session) - uniqExactArray(session_again)) / uniqExactArray(session)",
    "func": "",
    "contrast": "",
    "aliasName": "跳出率"
}
```
- 查询参数
```json
{
    "id": "data-overview-operation-chart-30",
    "chartType": "line",
    "tableName": "view_sdk_app_web_register_payment_agg",
    "momCompare": 0,
    "yoyCompare": 0,
    "dimension": [
        {
            "name": "date",
            "group": "day",
            "aliasName": "时间"
        }
    ],
    "measure": [
        {
            "name": "pv",
            "func": "sum",
            "contrast": "",
            "aliasName": "浏览量(PV)"
        }
    ],
    "dashboard": [
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-05-15",
                "2021-06-01"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "app_version",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，版本。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen": []
}
```
### 主指标趋势-分布-左图
- 查询参数
```json
{
    "id": "data-overview-operation-chart-40",
    "chartType": "pie",
    "tableName": "view_sdk_app_web_register_payment_agg",
    "dimension": [
        {
            "name": "os_name",
            "group": "",
            "aliasName": "终端",
            "注释": "
            选中终端分布，name为os_name，aliasName为终端；
            选中渠道分布，name为channel_name，aliasName为渠道；
            选中子渠道分布，name为sub_channel_name，aliasName为子渠道；
            选中计费名分布，name为billing_name，aliasName为计费名；
            选中版本分布，name为app_version，aliasName为版本；
            "
        }
    ],
    "measure": [
        {
            "name": "pv",
            "func": "sum",
            "contrast": "",
            "aliasName": "浏览量(PV)",
            "order": "-1",
            "注释": "度量变化规则同data-overview-operation-chart-30"
        }
    ],
    "dashboard": [
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-05-15",
                "2021-06-01"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "app_version",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，版本。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen": []
}
```
### 主指标趋势-分布-右图
- 查询参数
```json
{
    "id": "data-overview-operation-chart-50",
    "chartType": "histogram",
    "tableName": "view_sdk_app_web_register_payment_agg",
    "dimension": [
        {
            "name": "date",
            "group": "day",
            "aliasName": "时间"
        },
        {
            "name": "os_name",
            "group": "",
            "aliasName": "终端",
            "注释": "
            选中终端分布，name为os_name，aliasName为终端；
            选中渠道分布，name为channel_name，aliasName为渠道；
            选中子渠道分布，name为sub_channel_name，aliasName为子渠道；
            选中计费名分布，name为billing_name，aliasName为计费名；
            选中版本分布，name为app_version，aliasName为版本；
            "
        }
    ],
    "measure": [
        {
            "name": "pv",
            "func": "sum",
            "contrast": "",
            "aliasName": "浏览量(PV)",
            "注释": "度量变化规则同data-overview-operation-chart-30"
        }
    ],
    "dashboard": [
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-05-15",
                "2021-06-01"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "app_version",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，版本。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen": [
        {
            "name": "os_name",
            "logic": "eq",
            "value": "PC",
            "func": "",
            "注释": "图内筛选，实现左图点击联动效果，value为点击的值。
            选中终端分布，name为os_name，aliasName为终端；
            选中渠道分布，name为channel_name，aliasName为渠道；
            选中子渠道分布，name为sub_channel_name，aliasName为子渠道；
            选中计费名分布，name为billing_name，aliasName为计费名；
            选中版本分布，name为app_version，aliasName为版本；"
        }
    ]
}
```

## 付费指标
接口请求地址 /bi-bak/bi-sdk/sdk/data/overview/payment/chart/get  
### 所有卡片指标
- 查询参数
```json
{
    "id": "data-overview-payment-text-0",
    "chartType": "table",    
    "tableName": "view_sdk_app_web_register_payment_agg",
    "dimension": [
        {
            "name": "date",
            "group": "day",
            "aliasName": "时间"
        }
    ],
    "measure": [
        {
            "name": "pay_user_register_today",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "新付费用户数"
        },
        {
            "name": "pay_fee_register_today",
            "func": "sum",
            "contrast": "",
            "aliasName": "新付费金额"
        },
        {
            "name": "uniqExactArray(pay_user_register_today) / uniqExactArray(register_user)",
            "func": "",
            "contrast": "",
            "aliasName": "新付费转化率"
        },
        {
            "name": "pay_user",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "付费用户数"
        },
        {
            "name": "pay_fee",
            "func": "sum",
            "contrast": "",
            "aliasName": "付费金额"
        },
        {
            "name": "uniqExactArray(pay_user) / uniqExactArray(uid)",
            "func": "",
            "contrast": "",
            "aliasName": "活跃付费率"
        },
        {
            "name": "pay_user_again",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "复购用户数"
        },
        {
            "name": "sum(pay_fee) / uniqExactArray(uid)",
            "func": "",
            "contrast": "",
            "aliasName": "活跃ARPU"
        },
        {
            "name": "sum(pay_fee) / uniqExactArray(pay_user)",
            "func": "",
            "contrast": "",
            "aliasName": "付费ARPPU"
        }
    ],
    "dashboard": [
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-06-20",
                "2021-06-20"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "app_version",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，版本。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen": []
}
```
### 新用户转化-左图
- 查询参数
```json
{
    "id": "data-overview-payment-chart-10",
    "chartType": "histogram",    
    "tableName": "view_sdk_app_web_register_payment_agg",
    "dimension": [
    ],
    "measure": [
        {
            "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid))))))",
            "func": "",
            "contrast": "",
            "aliasName": "访客数"
        },
        {
            "name": "register_user",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "新增用户"
        },
        {
            "name": "pay_user_register_today",
            "func": "uniqArray",
            "contrast": "",
            "aliasName":"新付费数"
        }
    ],
    "dashboard": [
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-05-01",
                "2021-06-01"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "app_version",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，版本。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen": []
}
```
### 新用户转化-右图
- 说明  

点击访客数，度量为
```json
{
    "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid))))))",
    "func": "",
    "contrast": "",
    "aliasName": "访客数"
}
``` 
点击注册数，度量为
```json
{
    "name": "register_user",
    "func": "uniqArray",
    "contrast": "",
    "aliasName":"新增用户"
},
{
    "name": "uniqExactArray(register_user) / (uniqExactArray(register_user) + length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid)))))))",
    "func": "",
    "contrast": "",
    "aliasName": "新增用户转化率​"
}
```
点击新付费数，度量为
```json
{
    "name": "pay_user_register_today",
    "func": "uniqArray",
    "contrast": "",
    "aliasName": "新付费用户数"
},
{
    "name": "uniqExactArray(pay_user_register_today) / uniqExactArray(register_user)",
    "func": "",
    "contrast": "",
    "aliasName": "新付费转化率"
}
```  
- 查询参数
```json
{
    "id": "data-overview-payment-chart-20",
    "chartType": "histogram",    
    "tableName": "view_sdk_app_web_register_payment_agg",
    "dimension": [
        {
            "name": "date",
            "group": "day",
            "aliasName": "日期"
        }
    ],
    "measure": [
        {
            "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid))))))",
            "func": "",
            "contrast": "",
            "aliasName": "访客数"
        }
    ],
    "dashboard": [
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-05-01",
                "2021-06-01"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "app_version",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，版本。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen": []
}
```
### 主指标趋势
- 说明  
同 data-overview-operation-chart-30

指标选中新付费用户数时，度量为
```json
{
    "name": "pay_user_register_today",
    "func": "uniqArray",
    "contrast": "",
    "aliasName": "新付费用户数"
}
```  
指标选中新付费金额时，度量为
```json
 {
    "name": "pay_fee_register_today",
    "func": "sum",
    "contrast": "",
    "aliasName": "新付费金额"
}
```  
指标选中新付费转化率时，度量为
```json
{
    "name": "uniqExactArray(pay_user_register_today) / uniqExactArray(register_user)",
    "func": "",
    "contrast": "",
    "aliasName": "新付费转化率"
}
```  
指标选中付费用户数时，度量为
```json
{
    "name": "pay_user",
    "func": "uniqArray",
    "contrast": "",
    "aliasName": "付费用户数"
}
```  
指标选中付费金额时，度量为
```json
{
    "name": "pay_fee",
    "func": "sum",
    "contrast": "",
    "aliasName": "付费金额"
}
```
指标选中活跃付费率时，度量为
```json
{
    "name": "uniqExactArray(pay_user) / uniqExactArray(uid)",
    "func": "",
    "contrast": "",
    "aliasName": "活跃付费率"
}
```
指标选中复购用户数时，度量为
```json
{
    "name": "pay_user_again",
    "func": "uniqArray",
    "contrast": "",
    "aliasName": "复购用户数"
}
```
指标选中复购占比时，度量为
```json
{
    "name": "uniqExactArray(pay_user_again) / uniqExactArray(pay_user)",
    "func": "",
    "contrast": "",
    "aliasName": "复购占比"
}
```
指标选中活跃ARPU时，度量为
```json
{
    "name": "sum(pay_fee) / uniqExactArray(uid)",
    "func": "",
    "contrast": "",
    "aliasName": "活跃ARPU"
}
```
指标选中付费ARPPU时，度量为
```json
{
    "name": "sum(pay_fee) / uniqExactArray(pay_user)",
    "func": "",
    "contrast": "",
    "aliasName": "付费ARPPU"
}
```
指标选中退款用户数时，度量为
```json
{
    "name": "",
    "func": "",
    "contrast": "",
    "aliasName": "退款用户数"
}
```
指标选中退款金额时，度量为
```json
{
    "name": "",
    "func": "",
    "contrast": "",
    "aliasName": "退款金额"
}
```
- 查询参数
```json
{
    "id": "data-overview-payment-chart-30",
    "chartType": "line",
    "tableName": "view_sdk_app_web_register_payment_agg",
    "dimension": [
        {
            "name": "date",
            "group": "day",
            "aliasName": "时间"
        }
    ],
    "measure": [
        {
            "name": "pay_user_register_today",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "新付费用户数"
        }
    ],
    "dashboard": [
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-05-15",
                "2021-06-01"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "app_version",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，版本。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen": []
}
```
### 主指标趋势-分布-左图
- 查询参数
```json
{
    "id": "data-overview-payment-chart-40",
    "chartType": "pie",
    "tableName": "view_sdk_app_web_register_payment_agg",
    "dimension": [
        {
            "name": "os_name",
            "group": "",
            "aliasName": "终端",
            "注释": "
            选中终端分布，name为os_name，aliasName为终端；
            选中渠道分布，name为channel_name，aliasName为渠道；
            选中子渠道分布，name为sub_channel_name，aliasName为子渠道；
            选中计费名分布，name为billing_name，aliasName为计费名；
            "
        }
    ],
    "measure": [
        {
            "name": "pay_user_register_today",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "新付费用户数",
            "注释": "度量变化规则同data-overview-payment-chart-30"
        }
    ],
    "dashboard": [
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-05-15",
                "2021-06-01"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "app_version",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，版本。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen": []
}
```
### 主指标趋势-分布-右图
- 查询参数
```json
{
    "id": "data-overview-payment-chart-50",
    "chartType": "histogram",
    "tableName": "view_sdk_app_web_register_payment_agg",
    "dimension": [
        {
            "name": "date",
            "group": "day",
            "aliasName": "时间"
        },
        {
            "name": "os_name",
            "group": "",
            "aliasName": "终端",
            "注释": "
            选中终端分布，name为os_name，aliasName为终端；
            选中渠道分布，name为channel_name，aliasName为渠道；
            选中子渠道分布，name为sub_channel_name，aliasName为子渠道；
            选中计费名分布，name为billing_name，aliasName为计费名；
            "
        }
    ],
    "measure": [
        {
            "name": "pay_user_register_today",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "新付费用户数",
            "注释": "度量变化规则同data-overview-payment-chart-30"
        }
    ],
    "dashboard": [
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-05-15",
                "2021-06-01"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "app_version",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，版本。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen": [
        {
            "name": "os_name",
            "logic": "eq",
            "value": "PC",
            "func": "",
            "注释": "图内筛选，实现左图点击联动效果，value为点击的值。
            选中终端分布，name为os_name，aliasName为终端；
            选中渠道分布，name为channel_name，aliasName为渠道；
            选中子渠道分布，name为sub_channel_name，aliasName为子渠道；
            选中计费名分布，name为billing_name，aliasName为计费名；"
        }
    ]
}
```

# 趋势分析
测试环境域名 http://bi-test.stnts.com:8089/  
接口请求地址 /bi-bak/bi-sdk/sdk/analyse/tendency/chart/get  
## 终端类型下拉
- 查询参数
```json
{
    "id": "tendency-analyse-common-10",
    "chartType": "table",    
    "tableName": "view_sdk_app_web_register_payment_agg",
    "dimension": [
        {
            "name": "os_name",
            "group": "",
            "aliasName": "终端类型"
        }
    ],
    "measure": [
    ],
    "dashboard": [
        {
            "name": "os_name",
            "logic": "isnotempty",
            "value": "",
            "注释": "硬编码"
        },
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-05-01",
                "2021-06-01"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "app_version",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，版本。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen": []
}
```
## 版本下拉
- 查询参数
```json
{
    "id": "tendency-analyse-common-20",
    "chartType": "table",    
    "tableName": "view_sdk_app_web_register_payment_agg",
    "dimension": [
        {
            "name": "app_version",
            "group": "",
            "aliasName": "版本"
        }
    ],
    "measure": [
    ],
    "dashboard": [
        {
            "name": "app_version",
            "logic": "isnotempty",
            "value": "",
            "注释": "硬编码"
        },
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-05-01",
                "2021-06-01"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen": []
}
```
## 渠道下拉
- 查询参数
```json
{
    "id": "tendency-analyse-common-30",
    "chartType": "table",
    "tableName": "view_sdk_app_web_register_payment_agg",
    "dimension": [
        {
            "name": "channel_name",
            "group": "",
            "aliasName": "渠道"
        },
        {
            "name": "agent_name",
            "group": "",
            "aliasName": "供应商",
            "注释": "供应商显示在渠道后面，用括号括起来。"
        }
    ],
    "measure": [
    ],
    "dashboard": [
        {
            "name": "channel_name",
            "logic": "isnotempty",
            "value": "",
            "注释": "硬编码"
        },
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-05-01",
                "2021-06-01"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "app_version",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，版本。"
        }
    ],
    "screen": []
}
```
## 子渠道下拉
- 查询参数
```json
{
    "id": "tendency-analyse-common-40",
    "chartType": "table",    
    "tableName": "view_sdk_app_web_register_payment_agg",
    "dimension": [
        {
            "name": "sub_channel_name",
            "group": "",
            "aliasName": "子渠道"
        }
    ],
    "measure": [],
    "dashboard": [
        {
            "name": "sub_channel_name",
            "logic": "isnotempty",
            "value": "",
            "注释": "硬编码"
        },
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-05-01",
                "2021-06-01"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "app_version",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，版本。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
    ],
    "screen": []
}
```
## 计费别名下拉
- 查询参数
```json
{
    "id": "tendency-analyse-common-50",
    "chartType": "table",    
    "tableName": "view_sdk_app_web_register_payment_agg",
    "dimension": [
        {
            "name": "billing_name",
            "group": "",
            "aliasName": "计费别名"
        }
    ],
    "measure": [],
    "dashboard": [
        {
            "name": "billing_name",
            "logic": "isnotempty",
            "value": "",
            "注释": "硬编码"
        },
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-05-01",
                "2021-06-01"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "app_version",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，版本。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        }
    ],
    "screen": []
}
```
## 指标趋势
- 查询参数
```json
{
    "id": "tendency-analyse-10",
    "chartType": "line",
    "tableName": "view_sdk_app_web_register_payment_agg",
    "dimension": [
        {
            "name": "date",
            "group": "day",
            "aliasName": "时间"
        },
        {
            "name": "os_name",
            "group": "",
            "aliasName": "终端",
            "注释":"按终端时传递"
        },
        {
            "name": "channel_name",
            "group": "",
            "aliasName": "渠道",
            "注释":"按渠道时传递。
            选中按渠道，name为channel_name，aliasName为渠道；
            选中按子渠道，name为sub_channel_name，aliasName为子渠道,并带上渠道维度；
            选中按计费名，name为billing_name，aliasName为计费名,并带上渠道、子渠道维度；"
        }
    ],
    "measure": [
        {
            "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid)))))) + uniqExactArray(uid)",
            "func": "",
            "contrast": "",
            "aliasName": "访客数(UV)"
        },
        {
            "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid))))))",
            "func": "",
            "contrast": "",
            "aliasName": "游客数"
        },
        {
            "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_first))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid)))))) + length(bitmapToArray(bitmapAnd(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), register_user))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uid))))))",
            "func": "",
            "contrast": "",
            "aliasName": "新访客"
        },
        {
            "name": "register_user",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "新增用户"
        },
        {
            "name": "uniqExactArray(register_user) / (uniqExactArray(register_user) + length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid)))))))",
            "func": "",
            "contrast": "",
            "aliasName": "新用户转化率"
        },
        {
            "name": "uid",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "活跃用户数"
        },
        {
            "name": "length(arrayIntersect(groupArrayArray(uid), groupArrayArray(yesterday_register_user))) / uniqArray(yesterday_register_user)",
            "func": "",
            "contrast": "",
            "aliasName": "次日留存率"
        },
        {
            "name": "length(arrayIntersect(groupArrayArray(uid), groupArrayArray(register_user))) / uniqArray(uid)",
            "func": "",
            "contrast": "",
            "aliasName": "活跃构成（新用户占比）"
        },
        {
            "name": "uniqExactArray(yesterday_uid) / uniqExactArray(last_thirty_days_uid)",
            "func": "",
            "contrast": "",
            "aliasName": "活跃粘度（昨日活跃数/30日活跃数）"
        },
        {
            "name": "pay_user_register_today",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "新付费用户数"
        },
        {
            "name": "uniqExactArray(pay_user_register_today) / uniqExactArray(register_user)",
            "func": "",
            "contrast": "",
            "aliasName": "新付费转化率"
        },
        {
            "name": "pay_fee_register_today",
            "func": "sum",
            "contrast": "",
            "aliasName": "新付费金额"
        },
        {
            "name": "sum(pay_fee_register_today) / uniqExactArray(pay_user_register_today)",
            "func": "",
            "contrast": "",
            "aliasName": "新用户ARPPU"
        },
        {
            "name": "pay_user",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "付费用户数"
        },
        {
            "name": "pay_fee",
            "func": "sum",
            "contrast": "",
            "aliasName": "付费金额"
        },
        {
            "name": "sum(pay_fee) / uniqExactArray(uid)",
            "func": "",
            "contrast": "",
            "aliasName": "活跃ARPU"
        },
        {
            "name": "sum(pay_fee) / uniqExactArray(pay_user)",
            "func": "",
            "contrast": "",
            "aliasName": "付费ARPPU"
        },
        {
            "name": "uniqExactArray(pay_user_register_today) / uniqExactArray(pay_user)",
            "func": "",
            "contrast": "",
            "aliasName": "新付费用户占比"
        },
        {
            "name": "sum(pay_fee_register_today) / sum(pay_fee)",
            "func": "",
            "contrast": "",
            "aliasName": "新付费金额占比"
        },
        {
            "name": "pay_user_again",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "复购用户数"
        },
        {
            "name": "uniqExactArray(pay_user_again) / uniqExactArray(pay_user)",
            "func": "",
            "contrast": "",
            "aliasName": "复购占比"
        },
        {
            "name": "pay_user_first",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "首付用户数"
        },
        {
            "name": "pay_fee_first_day",
            "func": "sum",
            "contrast": "",
            "aliasName": "首次付费金额"
        },
        {
            "name": "(sum(yesterday_pay_fee_register_today) + sum(pay_fee_register_yesterday)) / uniqArray(yesterday_register_user)",
            "func": "",
            "contrast": "",
            "aliasName": "LTV1"
        },
        {
            "name": "sum(pay_fee_register_today) / uniqExactArray(register_user)",
            "func": "",
            "contrast": "",
            "aliasName": "LTV0",
            "decimal": 2
        },
        {
            "name": "pay_count",
            "func": "sum",
            "contrast": "",
            "aliasName": "付费次数"
        },
        {
            "name": "sum(pay_count) / sum(order_count)",
            "func": "",
            "contrast": "",
            "aliasName": "付费成功率"
        },
        {
            "name": "pv",
            "func": "sum",
            "contrast": "",
            "aliasName": "浏览量(PV)"
        },
        {
            "name": "session",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "访问(启动)次数"
        },
        {
            "name": "sum(online_duration)/uniqExactArray(session)/60000",
            "func": "",
            "contrast": "",
            "aliasName": "平均访问时长(m)"
        },
        {
            "name": "sum(online_duration)/sum(pv)/60000",
            "func": "",
            "contrast": "",
            "aliasName": "平均页面停留时长(m)"
        },
        {
            "name": "sum(pv) / uniqExactArray(session)",
            "func": "",
            "contrast": "",
            "aliasName": "平均访问深度"            
        },
        {
            "name": "(uniqExactArray(session) - uniqExactArray(session_again)) / uniqExactArray(session)",
            "func": "",
            "contrast": "",
            "aliasName": "跳出率"
        },
        {
            "name": "",
            "func": "",
            "contrast": "",
            "aliasName": "退款用户数"
        },
        {
            "name": "",
            "func": "",
            "contrast": "",
            "aliasName": "退款订单数"
        },
        {
            "name": "",
            "func": "",
            "contrast": "",
            "aliasName": "退款金额"
        }
    ],
    "dashboard": [
        {
            "name": "product_id",
            "logic": "eq",
            "value": "320000",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-06-20",
                "2021-06-20"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "app_version",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，版本。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen": []
}
```
## 指标明细
- 说明  
维度和指标与上一个图表“指标趋势”一样。  
总计用"rollup":1参数实现,数据在totalData中。  
同环比用"contrast": "yoy,mom"参数实现。yoy表示同比，mom表示环比。  
- 查询参数
```json
{
    "id": "tendency-analyse-20",
    "chartType": "table",    
    "tableName": "view_sdk_app_web_register_payment_agg",
    "rollup":1,
    "dimension": [
        {
            "name": "date",
            "group": "day",
            "aliasName": "时间"            
        },
        {
            "name": "os_name",
            "group": "",
            "aliasName": "终端",
            "注释":"按终端时传递"
        },
        {
            "name": "channel_name",
            "group": "",
            "aliasName": "渠道",
            "注释":"按渠道时传递。
            选中按渠道，name为channel_name，aliasName为渠道；
            选中按子渠道，name为sub_channel_name，aliasName为子渠道,并带上渠道维度；
            选中按计费名，name为billing_name，aliasName为计费名,并带上渠道、子渠道维度；"
        }
    ],
    "measure": [
        {
            "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid)))))) + uniqExactArray(uid)",
            "func": "",
            "contrast": "yoy_rate,mom_rate",
            "aliasName": "访客数(UV)"
        },
        {
            "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid))))))",
            "func": "",
            "contrast": "",
            "aliasName": "游客数"
        },
        {
            "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_first))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid)))))) + length(bitmapToArray(bitmapAnd(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), register_user))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uid))))))",
            "func": "",
            "contrast": "",
            "aliasName": "新访客"
        },
        {
            "name": "register_user",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "新增用户"
        },
        {
            "name": "uniqExactArray(register_user) / (uniqExactArray(register_user) + length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid)))))))",
            "func": "",
            "contrast": "",
            "aliasName": "新用户转化率"
        },
        {
            "name": "uid",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "活跃用户数"
        },
        {
            "name": "length(arrayIntersect(groupArrayArray(uid), groupArrayArray(yesterday_register_user))) / uniqArray(yesterday_register_user)",
            "func": "",
            "contrast": "",
            "aliasName": "次日留存率"
        },
        {
            "name": "length(arrayIntersect(groupArrayArray(uid), groupArrayArray(register_user))) / uniqArray(uid)",
            "func": "",
            "contrast": "",
            "aliasName": "活跃构成（新用户占比）"
        },
        {
            "name": "uniqExactArray(yesterday_uid) / uniqExactArray(last_thirty_days_uid)",
            "func": "",
            "contrast": "",
            "aliasName": "活跃粘度（昨日活跃数/30日活跃数）"
        },
        {
            "name": "pay_user_register_today",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "新付费用户数"
        },
        {
            "name": "uniqExactArray(pay_user_register_today) / uniqExactArray(register_user)",
            "func": "",
            "contrast": "",
            "aliasName": "新付费转化率"
        },
        {
            "name": "pay_fee_register_today",
            "func": "sum",
            "contrast": "",
            "aliasName": "新付费金额"
        },
        {
            "name": "sum(pay_fee_register_today) / uniqExactArray(pay_user_register_today)",
            "func": "",
            "contrast": "",
            "aliasName": "新用户ARPPU"
        },
        {
            "name": "pay_user",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "付费用户数"
        },
        {
            "name": "pay_fee",
            "func": "sum",
            "contrast": "",
            "aliasName": "付费金额"
        },
        {
            "name": "sum(pay_fee) / uniqExactArray(uid)",
            "func": "",
            "contrast": "",
            "aliasName": "活跃ARPU"
        },
        {
            "name": "sum(pay_fee) / uniqExactArray(pay_user)",
            "func": "",
            "contrast": "",
            "aliasName": "付费ARPPU"
        },
        {
            "name": "uniqExactArray(pay_user_register_today) / uniqExactArray(pay_user)",
            "func": "",
            "contrast": "",
            "aliasName": "新付费用户占比"
        },
        {
            "name": "sum(pay_fee_register_today) / sum(pay_fee)",
            "func": "",
            "contrast": "",
            "aliasName": "新付费金额占比"
        },
        {
            "name": "pay_user_again",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "复购用户数"
        },
        {
            "name": "uniqExactArray(pay_user_again) / uniqExactArray(pay_user)",
            "func": "",
            "contrast": "",
            "aliasName": "复购占比"
        },
        {
            "name": "pv",
            "func": "sum",
            "contrast": "",
            "aliasName": "浏览量(PV)"
        },
        {
            "name": "session",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "访问(启动)次数"
        },
        {
            "name": "sum(online_duration)/uniqExactArray(session)/60000",
            "func": "",
            "contrast": "",
            "aliasName": "平均访问时长(m)"
        },
        {
            "name": "sum(online_duration)/sum(pv)/60000",
            "func": "",
            "contrast": "",
            "aliasName": "平均页面停留时长(m)"
        },
        {
            "name": "sum(pv) / uniqExactArray(session)",
            "func": "",
            "contrast": "",
            "aliasName": "平均访问深度"            
        },
        {
            "name": "(uniqExactArray(session) - uniqExactArray(session_again)) / uniqExactArray(session)",
            "func": "",
            "contrast": "",
            "aliasName": "跳出率"
        },
        {
            "name": "",
            "func": "",
            "contrast": "",
            "aliasName": "退款用户数"
        },
        {
            "name": "",
            "func": "",
            "contrast": "",
            "aliasName": "退款订单数"
        },
        {
            "name": "",
            "func": "",
            "contrast": "",
            "aliasName": "退款金额"
        }

    ],
    "dashboard": [
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-05-01",
                "2021-06-01"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "app_version",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，版本。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen": []
}
```
# 渠道分析
新增attributionCaliber参数控制归因口径。1：按注册；2：按行为。默认按注册。
## 全局筛选
接口请求地址 /bi-bak/bi-sdk/sdk/analyse/channel/selector/get  
参数同趋势分析中下拉接口
## 渠道分布
接口请求地址 /bi-bak/bi-sdk/sdk/analyse/channel/distribution/chart/get  
### 数据趋势-左图
- 查询参数
```json
{
    "id": "channel-analyse-distribution-10",
    "chartType": "pie",
    "tableName": "view_sdk_app_web_register_payment_agg",
    "dimension": [
        {
            "name": "channel_name",
            "group": "",
            "aliasName": "渠道",
            "注释":"渠道汇总选中按渠道，name为channel_name，aliasName为渠道；
            选中按子渠道，name为sub_channel_name，aliasName为子渠道；
            选中按计费名，name为billing_name，aliasName为计费名；"
        }
    ],
    "measure": [
        {
            "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid)))))) + uniqExactArray(uid)",
            "func": "",
            "contrast": "",
            "aliasName": "访客数(UV)",
            "注释":"度量只传选中的指标"
        },
        {
            "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid))))))",
            "func": "",
            "contrast": "",
            "aliasName": "游客数"
        },
        {
            "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_first))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid)))))) + length(bitmapToArray(bitmapAnd(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), register_user))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uid))))))",
            "func": "",
            "contrast": "",
            "aliasName": "新访客"
        },
        {
            "name": "register_user",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "新增用户"
        },
        {
            "name": "uniqExactArray(register_user) / (uniqExactArray(register_user) + length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid)))))))",
            "func": "",
            "contrast": "",
            "aliasName": "新用户转化率"
        },
        {
            "name": "uid",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "活跃用户数"
        },
        {
            "name": "length(arrayIntersect(groupArrayArray(uid), groupArrayArray(yesterday_register_user))) / uniqArray(yesterday_register_user)",
            "func": "",
            "contrast": "",
            "aliasName": "次日留存率"
        },
        {
            "name": "length(arrayIntersect(groupArrayArray(uid), groupArrayArray(register_user))) / uniqArray(uid)",
            "func": "",
            "contrast": "",
            "aliasName": "活跃构成（新用户占比）"
        },
        {
            "name": "uniqExactArray(yesterday_uid) / uniqExactArray(last_thirty_days_uid)",
            "func": "",
            "contrast": "",
            "aliasName": "活跃粘度（昨日活跃数/30日活跃数）"
        },
        {
            "name": "pay_user_register_today",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "新付费用户数"
        },
        {
            "name": "uniqExactArray(pay_user_register_today) / uniqExactArray(register_user)",
            "func": "",
            "contrast": "",
            "aliasName": "新付费转化率"
        },
        {
            "name": "pay_fee_register_today",
            "func": "sum",
            "contrast": "",
            "aliasName": "新付费金额"
        },
        {
            "name": "sum(pay_fee_register_today) / uniqExactArray(pay_user_register_today)",
            "func": "",
            "contrast": "",
            "aliasName": "新用户ARPPU"
        },
        {
            "name": "pay_user",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "付费用户数"
        },
        {
            "name": "pay_fee",
            "func": "sum",
            "contrast": "",
            "aliasName": "付费金额"
        },
        {
            "name": "sum(pay_fee) / uniqExactArray(uid)",
            "func": "",
            "contrast": "",
            "aliasName": "活跃ARPU"
        },
        {
            "name": "sum(pay_fee) / uniqExactArray(pay_user)",
            "func": "",
            "contrast": "",
            "aliasName": "付费ARPPU"
        },
        {
            "name": "uniqExactArray(pay_user_register_today) / uniqExactArray(pay_user)",
            "func": "",
            "contrast": "",
            "aliasName": "新付费用户占比"
        },
        {
            "name": "sum(pay_fee_register_today) / sum(pay_fee)",
            "func": "",
            "contrast": "",
            "aliasName": "新付费金额占比"
        },
        {
            "name": "pay_user_again",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "复购用户数"
        },
        {
            "name": "uniqExactArray(pay_user_again) / uniqExactArray(pay_user)",
            "func": "",
            "contrast": "",
            "aliasName": "复购占比"
        },
        {
            "name": "pv",
            "func": "sum",
            "contrast": "",
            "aliasName": "浏览量(PV)"
        },
        {
            "name": "session",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "访问(启动)次数"
        },
        {
            "name": "sum(online_duration)/uniqExactArray(session)/60000",
            "func": "",
            "contrast": "",
            "aliasName": "平均访问时长(m)"
        },
        {
            "name": "sum(online_duration)/sum(pv)/60000",
            "func": "",
            "contrast": "",
            "aliasName": "平均页面停留时长(m)"
        },
        {
            "name": "sum(pv) / uniqExactArray(session)",
            "func": "",
            "contrast": "",
            "aliasName": "平均访问深度"            
        },
        {
            "name": "(uniqExactArray(session) - uniqExactArray(session_again)) / uniqExactArray(session)",
            "func": "",
            "contrast": "",
            "aliasName": "跳出率"
        }

    ],
    "dashboard": [
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-05-01",
                "2021-06-01"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "app_version",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，版本。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen": []
}
```
### 数据趋势-右图
- 查询参数
```json
{
    "id": "channel-analyse-distribution-20",
    "chartType": "histogram",
    "tableName": "view_sdk_app_web_register_payment_agg",
    "dimension": [
        {
            "name": "date",
            "group": "day",
            "aliasName": "时间"
        },
        {
            "name": "channel_name",
            "group": "",
            "aliasName": "渠道",
            "注释":"渠道汇总选中按渠道，name为channel_name，aliasName为渠道；
            选中按子渠道，name为sub_channel_name，aliasName为子渠道；
            选中按计费名，name为billing_name，aliasName为计费名；"
        }
    ],
    "measure": [
        {
            "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid)))))) + uniqExactArray(uid)",
            "func": "",
            "contrast": "",
            "aliasName": "访客数(UV)",
            "注释":"度量只传选中的指标"
        },
        {
            "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid))))))",
            "func": "",
            "contrast": "",
            "aliasName": "游客数"
        },
        {
            "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_first))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid)))))) + length(bitmapToArray(bitmapAnd(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), register_user))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uid))))))",
            "func": "",
            "contrast": "",
            "aliasName": "新访客"
        },
        {
            "name": "register_user",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "新增用户"
        },
        {
            "name": "uniqExactArray(register_user) / (uniqExactArray(register_user) + length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid)))))))",
            "func": "",
            "contrast": "",
            "aliasName": "新用户转化率"
        },
        {
            "name": "uid",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "活跃用户数"
        },
        {
            "name": "length(arrayIntersect(groupArrayArray(uid), groupArrayArray(yesterday_register_user))) / uniqArray(yesterday_register_user)",
            "func": "",
            "contrast": "",
            "aliasName": "次日留存率"
        },
        {
            "name": "length(arrayIntersect(groupArrayArray(uid), groupArrayArray(register_user))) / uniqArray(uid)",
            "func": "",
            "contrast": "",
            "aliasName": "活跃构成（新用户占比）"
        },
        {
            "name": "uniqExactArray(yesterday_uid) / uniqExactArray(last_thirty_days_uid)",
            "func": "",
            "contrast": "",
            "aliasName": "活跃粘度（昨日活跃数/30日活跃数）"
        },
        {
            "name": "pay_user_register_today",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "新付费用户数"
        },
        {
            "name": "uniqExactArray(pay_user_register_today) / uniqExactArray(register_user)",
            "func": "",
            "contrast": "",
            "aliasName": "新付费转化率"
        },
        {
            "name": "pay_fee_register_today",
            "func": "sum",
            "contrast": "",
            "aliasName": "新付费金额"
        },
        {
            "name": "sum(pay_fee_register_today) / uniqExactArray(pay_user_register_today)",
            "func": "",
            "contrast": "",
            "aliasName": "新用户ARPPU"
        },
        {
            "name": "pay_user",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "付费用户数"
        },
        {
            "name": "pay_fee",
            "func": "sum",
            "contrast": "",
            "aliasName": "付费金额"
        },
        {
            "name": "sum(pay_fee) / uniqExactArray(uid)",
            "func": "",
            "contrast": "",
            "aliasName": "活跃ARPU"
        },
        {
            "name": "sum(pay_fee) / uniqExactArray(pay_user)",
            "func": "",
            "contrast": "",
            "aliasName": "付费ARPPU"
        },
        {
            "name": "uniqExactArray(pay_user_register_today) / uniqExactArray(pay_user)",
            "func": "",
            "contrast": "",
            "aliasName": "新付费用户占比"
        },
        {
            "name": "sum(pay_fee_register_today) / sum(pay_fee)",
            "func": "",
            "contrast": "",
            "aliasName": "新付费金额占比"
        },
        {
            "name": "pay_user_again",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "复购用户数"
        },
        {
            "name": "uniqExactArray(pay_user_again) / uniqExactArray(pay_user)",
            "func": "",
            "contrast": "",
            "aliasName": "复购占比"
        },
        {
            "name": "pv",
            "func": "sum",
            "contrast": "",
            "aliasName": "浏览量(PV)"
        },
        {
            "name": "session",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "访问(启动)次数"
        },
        {
            "name": "sum(online_duration)/uniqExactArray(session)/60000",
            "func": "",
            "contrast": "",
            "aliasName": "平均访问时长(m)"
        },
        {
            "name": "sum(online_duration)/sum(pv)/60000",
            "func": "",
            "contrast": "",
            "aliasName": "平均页面停留时长(m)"
        },
        {
            "name": "sum(pv) / uniqExactArray(session)",
            "func": "",
            "contrast": "",
            "aliasName": "平均访问深度"            
        },
        {
            "name": "(uniqExactArray(session) - uniqExactArray(session_again)) / uniqExactArray(session)",
            "func": "",
            "contrast": "",
            "aliasName": "跳出率"
        }

    ],
    "dashboard": [
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-05-01",
                "2021-06-01"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "app_version",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，版本。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen": [
        {
            "name": "channel_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "图内筛选，实现左图点击联动效果，value为点击的值。
            渠道汇总选中按渠道，name为channel_name，aliasName为渠道；
            选中按子渠道，name为sub_channel_name，aliasName为子渠道；
            选中按计费名，name为billing_name，aliasName为计费名；"
        }
    ]
}
```
### 数据明细-指标去重
- 说明  
度量根据选中的指标传值
- 增长指标如下
```json
    {
        "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid)))))) + uniqExactArray(uid)",
        "func": "",
        "contrast": "",
        "aliasName": "访客数(UV)",
        "注释":"度量只传选中的指标"
    },
    {
        "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid))))))",
        "func": "",
        "contrast": "",
        "aliasName": "游客数"
    },
    {
        "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_first))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid)))))) + length(bitmapToArray(bitmapAnd(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), register_user))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uid))))))",
        "func": "",
        "contrast": "",
        "aliasName": "新访客"
    },
    {
        "name": "register_user",
        "func": "uniqArray",
        "contrast": "",
        "aliasName": "新增用户"
    },
    {
        "name": "uniqExactArray(register_user) / (uniqExactArray(register_user) + length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid)))))))",
        "func": "",
        "contrast": "",
        "aliasName": "新用户转化率"
    },
    {
        "name": "uid",
        "func": "uniqArray",
        "contrast": "",
        "aliasName": "活跃用户数"
    },
    {
        "name": "length(arrayIntersect(groupArrayArray(uid), groupArrayArray(yesterday_register_user))) / uniqArray(yesterday_register_user)",
        "func": "",
        "contrast": "",
        "aliasName": "次日留存率"
    },
    {
        "name": "length(arrayIntersect(groupArrayArray(uid), groupArrayArray(register_user))) / uniqArray(uid)",
        "func": "",
        "contrast": "",
        "aliasName": "活跃构成（新用户占比）"
    },
    {
        "name": "uniqExactArray(yesterday_uid) / uniqExactArray(last_thirty_days_uid)",
        "func": "",
        "contrast": "",
        "aliasName": "活跃粘度（昨日活跃数/30日活跃数）"
    },
    {
        "name": "uniqExactArray(register_user) / (uniqExactArray(register_user) + length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid))))))) * uniqExactArray(pay_user_register_today) / uniqExactArray(register_user)",
        "func": "",
        "contrast": "",
        "aliasName": "总体转化率"
    }
```
- 付费指标如下
```json
    {
        "name": "pay_user_register_today",
        "func": "uniqArray",
        "contrast": "",
        "aliasName": "新付费用户数"
    },
    {
        "name": "uniqExactArray(pay_user_register_today) / uniqExactArray(register_user)",
        "func": "",
        "contrast": "",
        "aliasName": "新付费转化率"
    },
    {
        "name": "pay_fee_register_today",
        "func": "sum",
        "contrast": "",
        "aliasName": "新付费金额"
    },
    {
        "name": "sum(pay_fee_register_today) / uniqExactArray(pay_user_register_today)",
        "func": "",
        "contrast": "",
        "aliasName": "新用户ARPPU"
    },
    {
        "name": "pay_user",
        "func": "uniqArray",
        "contrast": "",
        "aliasName": "付费用户数"
    },
    {
        "name": "pay_fee",
        "func": "sum",
        "contrast": "",
        "aliasName": "付费金额"
    },
    {
        "name": "uniqExactArray(pay_user) / uniqExactArray(uid)",
        "func": "",
        "contrast": "",
        "aliasName": "活跃付费率"
    },
    {
        "name": "sum(pay_fee) / uniqExactArray(uid)",
        "func": "",
        "contrast": "",
        "aliasName": "活跃ARPU"
    },
    {
        "name": "sum(pay_fee) / uniqExactArray(pay_user)",
        "func": "",
        "contrast": "",
        "aliasName": "付费ARPPU"
    },
    {
        "name": "uniqExactArray(pay_user_register_today) / uniqExactArray(pay_user)",
        "func": "",
        "contrast": "",
        "aliasName": "新付费用户占比"
    },
    {
        "name": "sum(pay_fee_register_today) / sum(pay_fee)",
        "func": "",
        "contrast": "",
        "aliasName": "新付费金额占比"
    },
    {
        "name": "pay_user_again",
        "func": "uniqArray",
        "contrast": "",
        "aliasName": "复购用户数"
    },
    {
        "name": "uniqExactArray(pay_user_again) / uniqExactArray(pay_user)",
        "func": "",
        "contrast": "",
        "aliasName": "复购占比"
    },
    {
        "name": "pay_user_first",
        "func": "uniqArray",
        "contrast": "",
        "aliasName": "首付用户数"
    },
    {
        "name": "pay_fee_first_day",
        "func": "sum",
        "contrast": "",
        "aliasName": "首次付费金额"
    },
    {
        "name": "(sum(yesterday_pay_fee_register_today) + sum(pay_fee_register_yesterday)) / uniqArray(yesterday_register_user)",
        "func": "",
        "contrast": "",
        "aliasName": "LTV1"
    },
    {
        "name": "sum(pay_fee_register_today) / uniqExactArray(register_user)",
        "func": "",
        "contrast": "",
        "aliasName": "LTV0",
        "decimal": 2
    },
    {
        "name": "pay_count",
        "func": "sum",
        "contrast": "",
        "aliasName": "付费次数"
    },
    {
        "name": "sum(pay_count) / sum(order_count)",
        "func": "",
        "contrast": "",
        "aliasName": "付费成功率"
    }
```
- 访问指标如下
```json
    {
        "name": "pv",
        "func": "sum",
        "contrast": "",
        "aliasName": "浏览量(PV)"
    },
    {
        "name": "session",
        "func": "uniqArray",
        "contrast": "",
        "aliasName": "访问(启动)次数"
    },
    {
        "name": "sum(online_duration)/uniqExactArray(session)/60000",
        "func": "",
        "contrast": "",
        "aliasName": "平均访问时长(m)"
    },
    {
        "name": "sum(online_duration)/sum(pv)/60000",
        "func": "",
        "contrast": "",
        "aliasName": "平均页面停留时长(m)"
    },
    {
        "name": "sum(pv) / uniqExactArray(session)",
        "func": "",
        "contrast": "",
        "aliasName": "平均访问深度"            
    },
    {
        "name": "(uniqExactArray(session) - uniqExactArray(session_again)) / uniqExactArray(session)",
        "func": "",
        "contrast": "",
        "aliasName": "跳出率"
    }
```
- 查询参数
```json
{
    "id": "channel-analyse-distribution-30",
    "chartType": "table",
    "tableName": "view_sdk_app_web_register_payment_agg",
    "dimension": [
        {
            "name": "date",
            "group": "day",
            "aliasName": "时间"
        },
        {
            "name": "channel_name",
            "group": "",
            "aliasName": "渠道",
            "注释":"渠道汇总选中按渠道，name为channel_name，aliasName为渠道；
            选中按子渠道，name为sub_channel_name，aliasName为子渠道；
            选中按计费名，name为billing_name，aliasName为计费名；"
        }
    ],
    "measure": [
    ],
    "dashboard": [
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-05-01",
                "2021-06-01"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "app_version",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，版本。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen": []
}
```
### 数据明细-指标均值
"id": "channel-analyse-distribution-40"
只修改id参数，其他参数逻辑和“数据明细-指标去重”相同。
## 渠道趋势
接口请求地址 /bi-bak/bi-sdk/sdk/analyse/channel/tendency/chart/get  
### 数据趋势
- 查询参数
```json
{
    "id": "channel-analyse-tendency-10",
    "chartType": "histogram",
    "tableName": "view_sdk_app_web_register_payment_agg",
    "dimension": [
        {
            "name": "date",
            "group": "day",
            "aliasName": "时间"
        }
    ],
    "measure": [
        {
            "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid)))))) + uniqExactArray(uid)",
            "func": "",
            "contrast": "",
            "aliasName": "访客数(UV)",
            "注释":"度量只传选中的指标"
        },
        {
            "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid))))))",
            "func": "",
            "contrast": "",
            "aliasName": "游客数"
        },
        {
            "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_first))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid)))))) + length(bitmapToArray(bitmapAnd(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), register_user))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uid))))))",
            "func": "",
            "contrast": "",
            "aliasName": "新访客"
        },
        {
            "name": "register_user",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "新增用户"
        },
        {
            "name": "uniqExactArray(register_user) / (uniqExactArray(register_user) + length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid)))))))",
            "func": "",
            "contrast": "",
            "aliasName": "新用户转化率"
        },
        {
            "name": "uid",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "活跃用户数"
        },
        {
            "name": "length(arrayIntersect(groupArrayArray(uid), groupArrayArray(yesterday_register_user))) / uniqArray(yesterday_register_user)",
            "func": "",
            "contrast": "",
            "aliasName": "次日留存率"
        },
        {
            "name": "length(arrayIntersect(groupArrayArray(uid), groupArrayArray(register_user))) / uniqArray(uid)",
            "func": "",
            "contrast": "",
            "aliasName": "活跃构成（新用户占比）"
        },
        {
            "name": "uniqExactArray(yesterday_uid) / uniqExactArray(last_thirty_days_uid)",
            "func": "",
            "contrast": "",
            "aliasName": "活跃粘度（昨日活跃数/30日活跃数）"
        },
        {
            "name": "pay_user_register_today",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "新付费用户数"
        },
        {
            "name": "uniqExactArray(pay_user_register_today) / uniqExactArray(register_user)",
            "func": "",
            "contrast": "",
            "aliasName": "新付费转化率"
        },
        {
            "name": "pay_fee_register_today",
            "func": "sum",
            "contrast": "",
            "aliasName": "新付费金额"
        },
        {
            "name": "sum(pay_fee_register_today) / uniqExactArray(pay_user_register_today)",
            "func": "",
            "contrast": "",
            "aliasName": "新用户ARPPU"
        },
        {
            "name": "pay_user",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "付费用户数"
        },
        {
            "name": "pay_fee",
            "func": "sum",
            "contrast": "",
            "aliasName": "付费金额"
        },
        {
            "name": "sum(pay_fee) / uniqExactArray(uid)",
            "func": "",
            "contrast": "",
            "aliasName": "活跃ARPU"
        },
        {
            "name": "sum(pay_fee) / uniqExactArray(pay_user)",
            "func": "",
            "contrast": "",
            "aliasName": "付费ARPPU"
        },
        {
            "name": "uniqExactArray(pay_user_register_today) / uniqExactArray(pay_user)",
            "func": "",
            "contrast": "",
            "aliasName": "新付费用户占比"
        },
        {
            "name": "sum(pay_fee_register_today) / sum(pay_fee)",
            "func": "",
            "contrast": "",
            "aliasName": "新付费金额占比"
        },
        {
            "name": "pay_user_again",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "复购用户数"
        },
        {
            "name": "uniqExactArray(pay_user_again) / uniqExactArray(pay_user)",
            "func": "",
            "contrast": "",
            "aliasName": "复购占比"
        },
        {
            "name": "pv",
            "func": "sum",
            "contrast": "",
            "aliasName": "浏览量(PV)"
        },
        {
            "name": "session",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "访问(启动)次数"
        },
        {
            "name": "sum(online_duration)/uniqExactArray(session)/60000",
            "func": "",
            "contrast": "",
            "aliasName": "平均访问时长(m)"
        },
        {
            "name": "sum(online_duration)/sum(pv)/60000",
            "func": "",
            "contrast": "",
            "aliasName": "平均页面停留时长(m)"
        },
        {
            "name": "sum(pv) / uniqExactArray(session)",
            "func": "",
            "contrast": "",
            "aliasName": "平均访问深度"            
        },
        {
            "name": "(uniqExactArray(session) - uniqExactArray(session_again)) / uniqExactArray(session)",
            "func": "",
            "contrast": "",
            "aliasName": "跳出率"
        }

    ],
    "dashboard": [
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-05-01",
                "2021-06-01"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "app_version",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，版本。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen": [],
    "compare": [
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-05-01",
                "2021-06-01"
            ],
            "func": "day",
            "注释": "对比时间。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "对比渠道。
            渠道汇总选中按渠道，name为channel_name；
            选中按子渠道，name为sub_channel_name；
            选中按计费名，name为billing_name；"
        },
    ]
}
```
### 数据明细
- 说明  
度量根据选中的指标传值
- 增长指标如下
```json
    {
        "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid)))))) + uniqExactArray(uid)",
        "func": "",
        "contrast": "",
        "aliasName": "访客数(UV)",
        "注释":"度量只传选中的指标"
    },
    {
        "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid))))))",
        "func": "",
        "contrast": "",
        "aliasName": "游客数"
    },
    {
        "name": "length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_first))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid)))))) + length(bitmapToArray(bitmapAnd(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), register_user))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uid))))))",
        "func": "",
        "contrast": "",
        "aliasName": "新访客"
    },
    {
        "name": "register_user",
        "func": "uniqArray",
        "contrast": "",
        "aliasName": "新增用户"
    },
    {
        "name": "uniqExactArray(register_user) / (uniqExactArray(register_user) + length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid)))))))",
        "func": "",
        "contrast": "",
        "aliasName": "新用户转化率"
    },
    {
        "name": "uid",
        "func": "uniqArray",
        "contrast": "",
        "aliasName": "活跃用户数"
    },
    {
        "name": "length(arrayIntersect(groupArrayArray(uid), groupArrayArray(yesterday_register_user))) / uniqArray(yesterday_register_user)",
        "func": "",
        "contrast": "",
        "aliasName": "次日留存率"
    },
    {
        "name": "length(arrayIntersect(groupArrayArray(uid), groupArrayArray(register_user))) / uniqArray(uid)",
        "func": "",
        "contrast": "",
        "aliasName": "活跃构成（新用户占比）"
    },
    {
        "name": "uniqExactArray(yesterday_uid) / uniqExactArray(last_thirty_days_uid)",
        "func": "",
        "contrast": "",
        "aliasName": "活跃粘度（昨日活跃数/30日活跃数）"
    },
    {
        "name": "uniqExactArray(register_user) / (uniqExactArray(register_user) + length(bitmapToArray(bitmapAndnot(bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv))),bitmapBuild(groupArrayArray(arrayMap(x->xxHash32(x), uv_with_uid))))))) * uniqExactArray(pay_user_register_today) / uniqExactArray(register_user)",
        "func": "",
        "contrast": "",
        "aliasName": "总体转化率"
    }
```
- 付费指标如下
```json
    {
        "name": "pay_user_register_today",
        "func": "uniqArray",
        "contrast": "",
        "aliasName": "新付费用户数"
    },
    {
        "name": "uniqExactArray(pay_user_register_today) / uniqExactArray(register_user)",
        "func": "",
        "contrast": "",
        "aliasName": "新付费转化率"
    },
    {
        "name": "pay_fee_register_today",
        "func": "sum",
        "contrast": "",
        "aliasName": "新付费金额"
    },
    {
        "name": "sum(pay_fee_register_today) / uniqExactArray(pay_user_register_today)",
        "func": "",
        "contrast": "",
        "aliasName": "新用户ARPPU"
    },
    {
        "name": "pay_user",
        "func": "uniqArray",
        "contrast": "",
        "aliasName": "付费用户数"
    },
    {
        "name": "pay_fee",
        "func": "sum",
        "contrast": "",
        "aliasName": "付费金额"
    },
    {
        "name": "uniqExactArray(pay_user) / uniqExactArray(uid)",
        "func": "",
        "contrast": "",
        "aliasName": "活跃付费率"
    },
    {
        "name": "sum(pay_fee) / uniqExactArray(uid)",
        "func": "",
        "contrast": "",
        "aliasName": "活跃ARPU"
    },
    {
        "name": "sum(pay_fee) / uniqExactArray(pay_user)",
        "func": "",
        "contrast": "",
        "aliasName": "付费ARPPU"
    },
    {
        "name": "uniqExactArray(pay_user_register_today) / uniqExactArray(pay_user)",
        "func": "",
        "contrast": "",
        "aliasName": "新付费用户占比"
    },
    {
        "name": "sum(pay_fee_register_today) / sum(pay_fee)",
        "func": "",
        "contrast": "",
        "aliasName": "新付费金额占比"
    },
    {
        "name": "pay_user_again",
        "func": "uniqArray",
        "contrast": "",
        "aliasName": "复购用户数"
    },
    {
        "name": "uniqExactArray(pay_user_again) / uniqExactArray(pay_user)",
        "func": "",
        "contrast": "",
        "aliasName": "复购占比"
    }
```
- 访问指标如下
```json
    {
        "name": "pv",
        "func": "sum",
        "contrast": "",
        "aliasName": "浏览量(PV)"
    },
    {
        "name": "session",
        "func": "uniqArray",
        "contrast": "",
        "aliasName": "访问(启动)次数"
    },
    {
        "name": "sum(online_duration)/uniqExactArray(session)/60000",
        "func": "",
        "contrast": "",
        "aliasName": "平均访问时长(m)"
    },
    {
        "name": "sum(online_duration)/sum(pv)/60000",
        "func": "",
        "contrast": "",
        "aliasName": "平均页面停留时长(m)"
    },
    {
        "name": "sum(pv) / uniqExactArray(session)",
        "func": "",
        "contrast": "",
        "aliasName": "平均访问深度"            
    },
```
- 查询参数
```json
{
    "id": "channel-analyse-tendency-20",
    "chartType": "table",
    "tableName": "view_sdk_app_web_register_payment_agg",
    "dimension": [
        {
            "name": "date",
            "group": "day",
            "aliasName": "时间"
        },
        {
            "name": "channel_name",
            "group": "",
            "aliasName": "渠道",
            "注释":"渠道汇总选中按渠道，name为channel_name，aliasName为渠道；
            选中按子渠道，name为sub_channel_name，aliasName为子渠道；
            选中按计费名，name为billing_name，aliasName为计费名；"
        }
    ],
    "measure": [
    ],
    "dashboard": [
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-05-01",
                "2021-06-01"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "app_version",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，版本。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen": []
}
```
### 新设备分布
- 查询参数
```json
{
    "id": "channel-analyse-tendency-30",
    "chartType": "table",
    "tableName": "view_uv_first",
    "dimension": [
        {
            "name": "brand",
            "group": "",
            "aliasName": "设备",
            "注释": "
            按设备，name为brand，aliasName为设备；
            按浏览器，name为browser，aliasName为浏览器；"
        }
    ],
    "measure": [
        {
            "name": "uv",
            "func": "count_distinct",
            "contrast": "",
            "aliasName": "新访客",
            "order": -1,
            "proportion" : 1,
            "注释": "图表类型为柱形图时，proportion参数传0，不返回占比；图表类型切换为表格时，proportion参数传1，返回占比。"
        }
    ],
    "dashboard": [
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-06-01",
                "2021-06-09"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "app_version",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，版本。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen": []
}
```
### 用户地域分布
- 查询参数
```json
{
    "id": "channel-analyse-tendency-40",
    "chartType": "table",
    "tableName": "view_register_detail",
    "tableName注释" : "选中新增用户,tableName为view_register_detail。选中付费用户,tableName为view_payment_detail。",
    "dimension": [
        {
            "name": "province",
            "group": "",
            "aliasName": "省"
        }
    ],
    "measure": [
        {
            "name": "uid",
            "func": "count_distinct",
            "contrast": "",
            "aliasName": "用户数"            
        }
    ],
    "dashboard": [
        {
            "name": "country",
            "logic": "eq",
            "value": "中国",
            "func": "",
            "注释": "硬编码"
        },
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-06-20",
                "2021-06-20"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "app_version",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，版本。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen": []
}
```
### 活跃用户活跃终端分布-下拉
- 查询参数
```json
{
    "id": "channel-analyse-tendency-45",
    "chartType": "table",
    "tableName": "view_sdk_app_web_register_payment_agg",
    "dimension": [        
    ],
    "measure": [
        {
            "name": "os_name_array",
            "func": "distinct",
            "contrast": "",
            "aliasName": "终端"
        }
    ],
    "dashboard": [
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-05-01",
                "2021-06-01"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "app_version",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，版本。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen": []
}
```
### 活跃用户活跃终端分布-左图
- 查询参数
```json
{
    "id": "channel-analyse-tendency-50",
    "chartType": "pie",
    "tableName": "view_sdk_app_web_register_payment_agg",
    "dimension": [
        {
            "name": "os_name_array",
            "group": "",
            "aliasName": "终端"
        }
    ],
    "measure": [
        {
            "name": "uid",
            "func": "count_distinct",
            "contrast": "",
            "order": -1,
            "aliasName": "活跃用户数"
        }
    ],
    "dashboard": [
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-06-20",
                "2021-06-20"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "app_version",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，版本。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen": [
        {
            "name": "os_name_array",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "左图联动，传递选中终端类型。"
        }
    ]
}
```
### 活跃用户活跃终端分布-右图
- 查询参数
```json
{
    "id": "channel-analyse-tendency-60",
    "chartType": "histogram",
    "tableName": "view_sdk_app_web_register_payment_agg",
    "dimension": [
        {
            "name": "date",
            "group": "day",
            "aliasName": "时间"
        },
        {
            "name": "os_name_array",
            "group": "",
            "aliasName": "终端"
        }
    ],
    "measure": [
        {
            "name": "uid",
            "func": "count_distinct",
            "contrast": "",
            "aliasName": "活跃用户数"
        }
    ],
    "dashboard": [
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-06-19",
                "2021-06-21"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "app_version",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，版本。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen": [
        {
            "name": "os_name_array",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "左图联动，传递选中终端类型。"
        }
    ]
}
```
## 渠道效果
接口请求地址 /bi-bak/bi-sdk/sdk/analyse/channel/effect/chart/get
### 渠道分组
- 查询参数
```json
{
    "id": "channel-analyse-effect-05",
    "chartName": "留存数据",
    "chartType": "table",
    "tableName": "view_user_retain",
    "tableName注释": "留存数据-新增用户：view_user_retain；留存数据-付费用户：view_payment_retain；留存数据-活跃用户：view_active_retain；LTV-新增用户：view_user_retain；LTV-首付用户：view_payment_first_ltv；LTV-新增且后续有付费的用户：view_accumulate_ltv；",
    "dimension": [
        {
            "name": "channel_name",
            "group": "",
            "aliasName": "渠道",
            "注释": "渠道汇总
            选中渠道，aliasName为渠道，name为channel_name；
            选中子渠道，aliasName为子渠道，name为sub_channel_name；
            选中计费名，aliasName为计费名，name为billing_name。"
        }
    ],
    "measure": [
        {
            "name": "initial",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "初始值",
            "order":-1
        }
    ],
    "dashboard": [
        {
            "name": "interval",
            "logic": "eq",
            "value": 0,
            "注释": "硬编码"
        },
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-06-20",
                "2021-06-21"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ]

}
```
### 留存数据
- 查询参数
```json
{
    "id": "channel-analyse-effect-10",
    "chartName": "留存数据",
    "chartType": "retain-table",
    "chartType注释": "数据表：retain-table；留存趋势图：retain-line-total；留存变化图：retain-line-change",
    "tableName": "view_user_retain",
    "tableName注释": "新增用户：view_user_retain；付费用户：view_payment_retain；活跃用户：view_active_retain；",
    "retainTimeNum": "1,2,3",
    "retainTimeNum注释": "留存图距起始时间间隔，多个用逗号分隔。",
    "dimension": [
        {
            "name": "date",
            "group": "day",
            "aliasName": "起始时间"
        },
        {
            "name": "interval",
            "group": "",
            "aliasName": "距起始时间间隔"
        },
        {
            "name": "channel_name",
            "group": "",
            "aliasName": "渠道",
            "注释": "渠道汇总
            选中渠道，aliasName为渠道，name为channel_name；
            选中子渠道，aliasName为子渠道，name为sub_channel_name；
            选中计费名，aliasName为计费名，name为billing_name。"
        }
    ],
    "measure": [
        {
            "name": "initial",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "初始值"
        },
        {
            "name": "retain",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "留存值"
        }
    ],
    "dashboard": [
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-06-20",
                "2021-06-21"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen" : [
        {
            "name": "interval",
            "logic": "lte",
            "value": 7,
            "注释": "图内筛选，间隔天数。"
        }
    ]
}
```
### 留存数据-展开渠道
- 查询参数
```json
{
    "id": "channel-analyse-effect-20",
    "chartName": "留存数据",
    "chartType": "retain-table",
    "tableName": "view_user_retain",
    "tableName注释": "新增用户：view_user_retain；付费用户：view_payment_retain；活跃用户：view_active_retain；",
    "retainTimeNum": "1,2,3",
    "retainTimeNum注释": "留存图距起始时间间隔，多个用逗号分隔。",
    "dimension": [
        {
            "name": "date",
            "group": "day",
            "aliasName": "起始时间"
        },
        {
            "name": "interval",
            "group": "",
            "aliasName": "距起始时间间隔"
        }
    ],
    "measure": [
        {
            "name": "initial",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "初始值"
        },
        {
            "name": "retain",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "留存值"
        }
    ],
    "dashboard": [
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-06-01",
                "2021-07-01"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen" : [
        {
            "name": "interval",
            "logic": "lte",
            "value": 7,
            "注释": "图内筛选，间隔天数。"
        },
        {
            "name": "channel_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "图内筛选，渠道。
            选中渠道，name为channel_name；
            选中子渠道，name为sub_channel_name；
            选中计费名，name为billing_name。
            value为点击的渠道名，点全部传空。
            "
        }
    ]
}
```
### LTV
- 查询参数
```json
{
    "id": "channel-analyse-effect-30",
    "chartName": "留存数据",
    "chartType": "retain-table",
    "chartType注释": "数据表：retain-table；留存趋势图：retain-line-total；留存变化图：retain-line-change",
    "tableName": "view_user_retain",
    "tableName注释": "新增用户：view_user_retain；首付用户：view_payment_first_ltv；新增且后续有付费的用户：view_accumulate_ltv；",
    "retainTimeNum": "1,2,3",
    "retainTimeNum注释": "留存图距起始时间间隔，多个用逗号分隔。",
    "dimension": [
        {
            "name": "date",
            "group": "day",
            "aliasName": "起始时间"
        },
        {
            "name": "interval",
            "group": "",
            "aliasName": "距起始时间间隔"
        },
        {
            "name": "channel_name",
            "group": "",
            "aliasName": "渠道",
            "注释": "渠道汇总
            选中渠道，aliasName为渠道，name为channel_name；
            选中子渠道，aliasName为子渠道，name为sub_channel_name；
            选中计费名，aliasName为计费名，name为billing_name。"
        }
    ],
    "measure": [
        {
            "name": "initial",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "初始值"
        },
        {
            "name": "pay_fee",
            "func": "sum",
            "contrast": "",
            "aliasName": "留存值"
        }
    ],
    "dashboard": [
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-06-01",
                "2021-07-01"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen" : [
        {
            "name": "interval",
            "logic": "lte",
            "value": 7,
            "注释": "图内筛选，间隔天数。"
        }
    ]
}
```
### LTV-展开渠道
- 查询参数
```json
{
    "id": "channel-analyse-effect-40",
    "chartName": "留存数据",
    "chartType": "retain-table",
    "tableName": "view_user_retain",
    "tableName注释": "新增用户：view_user_retain；首付用户：view_payment_first_ltv；新增且后续有付费的用户：view_accumulate_ltv",
    "retainTimeNum": "1,2,3",
    "retainTimeNum注释": "留存图距起始时间间隔，多个用逗号分隔。",
    "dimension": [
        {
            "name": "date",
            "group": "day",
            "aliasName": "起始时间"
        },
        {
            "name": "interval",
            "group": "",
            "aliasName": "距起始时间间隔"
        }
    ],
    "measure": [
        {
            "name": "initial",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "初始值"
        },
        {
            "name": "pay_fee",
            "func": "sum",
            "contrast": "",
            "aliasName": "留存值"
        }
    ],
    "dashboard": [
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-06-01",
                "2021-07-01"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen" : [
        {
            "name": "interval",
            "logic": "lte",
            "value": 7,
            "注释": "图内筛选，间隔天数。"
        },
        {
            "name": "channel_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "图内筛选，渠道。
            选中渠道，name为channel_name；
            选中子渠道，name为sub_channel_name；
            选中计费名，name为billing_name。
            value为点击的渠道名，点全部传空。
            "
        }
    ]
}
```
### 渠道分析矩阵
- 查询参数
```json
{
    "id": "channel-analyse-effect-50",
    "chartName": "渠道分析矩阵",
    "chartType": "table",
    "tableName": "view_user_retain",
    "dimension": [
        {
            "name": "channel_name",
            "group": "",
            "aliasName": "渠道",
            "注释": "渠道汇总
            选中渠道，aliasName为渠道，name为channel_name；
            选中子渠道，aliasName为子渠道，name为sub_channel_name；
            选中计费名，aliasName为计费名，name为billing_name。"
        }
    ],
    "measure": [
        {
            "name": "sum(pay_fee_today)",
            "func": "",
            "contrast": "",
            "aliasName": "新付费金额"
        },
        {
            "name": "avg(LTV1)",
            "func": "",
            "contrast": "",
            "aliasName": "LTV1"
        },
        {
            "name": "avg(LTV0)",
            "func": "",
            "contrast": "",
            "aliasName": "LTV0"
        },        
        {
            "name": "uniqExactArray(`register_user`)",
            "func": "",
            "contrast": "",
            "aliasName": "新增用户"
        },
        {
            "name": "uniqExactArray(retain)",
            "func": "",
            "contrast": "",
            "aliasName": "次日留存数"
        },
        {
            "name": "uniqExactArray(retain) / uniqExactArray(`register_user`)",
            "func": "",
            "contrast": "",
            "aliasName": "次日留存率"
        },
        {
            "name": "uniqExactArray(`pay_user_today`)",
            "func": "",
            "contrast": "",
            "aliasName": "新付费用户数"
        }
    ],
    "dashboard": [
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-06-20",
                "2021-06-20"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen" : []
}
```
# 终端分析
接口同渠道分析，唯一的区别是将渠道维度替换为终端维度。
{
    "name": "os_name",
    "group": "",
    "aliasName": "终端类型"
}
# 用户分析
## 用户留存
接口请求地址 /bi-bak/bi-sdk/sdk/analyse/user/retain/chart/get  
和channel-analyse-effect-10相比，去掉渠道维度。
- 查询参数
```json
{
    "id": "user-analyse-retain-10",
    "chartName": "留存数据",
    "chartType": "retain-table",
    "chartType注释": "数据表：retain-table；留存趋势图：retain-line-total；留存变化图：retain-line-change",
    "tableName": "view_user_retain",
    "tableName注释": "新增用户：view_user_retain；付费用户：view_payment_retain；活跃用户：view_active_retain；",
    "retainTimeNum": "1,2,3",
    "retainTimeNum注释": "留存图距起始时间间隔，多个用逗号分隔。",
    "dimension": [
        {
            "name": "date",
            "group": "day",
            "aliasName": "起始时间"
        },
        {
            "name": "interval",
            "group": "",
            "aliasName": "距起始时间间隔"
        }        
    ],
    "measure": [
        {
            "name": "initial",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "初始值"
        },
        {
            "name": "retain",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "留存值"
        }
    ],
    "dashboard": [
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-06-20",
                "2021-06-21"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen" : [
        {
            "name": "interval",
            "logic": "lte",
            "value": 7,
            "注释": "图内筛选，间隔天数。"
        }
    ]
}
```
## 用户LTV
接口请求地址 /bi-bak/bi-sdk/sdk/analyse/user/ltv/chart/get  
和channel-analyse-effect-30相比，去掉渠道维度。
- 查询参数
```json
{
    "id": "user-analyse-ltv-10",
    "chartName": "留存数据",
    "chartType": "retain-table",
    "chartType注释": "数据表：retain-table；留存趋势图：retain-line-total；留存变化图：retain-line-change",
    "tableName": "view_user_retain",
    "tableName注释": "新增用户：view_user_retain；首付用户：view_payment_first_ltv；新增且后续有付费的用户：view_accumulate_ltv；",
    "retainTimeNum": "1,2,3",
    "retainTimeNum注释": "留存图距起始时间间隔，多个用逗号分隔。",
    "dimension": [
        {
            "name": "date",
            "group": "day",
            "aliasName": "起始时间"
        },
        {
            "name": "interval",
            "group": "",
            "aliasName": "距起始时间间隔"
        }
    ],
    "measure": [
        {
            "name": "initial",
            "func": "uniqArray",
            "contrast": "",
            "aliasName": "初始值"
        },
        {
            "name": "pay_fee",
            "func": "sum",
            "contrast": "",
            "aliasName": "留存值"
        }
    ],
    "dashboard": [
        {
            "name": "product_id",
            "logic": "eq",
            "value": "",
            "注释": "全局筛选，产品线。"
        },
        {
            "name": "date",
            "logic": "between",
            "value": [
                "2021-06-01",
                "2021-07-01"
            ],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "os_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，终端类型。"
        },
        {
            "name": "channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "sub_channel_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，子渠道。"
        },
        {
            "name": "billing_name",
            "logic": "in",
            "value": [],
            "func": "",
            "注释": "全局筛选，计费别名。"
        }
    ],
    "screen" : [
        {
            "name": "interval",
            "logic": "lte",
            "value": 7,
            "注释": "图内筛选，间隔天数。"
        }
    ]
}
```