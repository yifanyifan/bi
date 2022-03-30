- [通用接口参数文档](#通用接口参数文档)
- [收入分析](#收入分析)
  - [收入分析全局筛选-一级分类下拉](#收入分析全局筛选-一级分类下拉)
  - [收入分析全局筛选-二级分类下拉](#收入分析全局筛选-二级分类下拉)
  - [收入分析全局筛选-三级分类下拉](#收入分析全局筛选-三级分类下拉)
  - [收入分析全局筛选-合作CP方](#收入分析全局筛选-合作cp方)
  - [收入分析全局筛选-合作渠道](#收入分析全局筛选-合作渠道)
  - [收入分析全局筛选-游戏类型](#收入分析全局筛选-游戏类型)
  - [收入分析全局筛选-游戏名称(游戏CODE)](#收入分析全局筛选-游戏名称游戏code)
  - [目标实际完成度](#目标实际完成度)
  - [目标实际完成度-ROI、收入、成本](#目标实际完成度-roi收入成本)
  - [KPI指标](#kpi指标)
  - [各业务收入明细](#各业务收入明细)
  - [各业务收入明细-下钻](#各业务收入明细-下钻)
  - [ROI趋势图](#roi趋势图)
  - [ROI趋势图-渠道下拉](#roi趋势图-渠道下拉)
  - [ROI趋势图-游戏分类下拉](#roi趋势图-游戏分类下拉)
  - [ROI趋势图-游戏下拉](#roi趋势图-游戏下拉)
  - [营收明细-各业务收入明细](#营收明细-各业务收入明细)
  - [营收明细-各业务收入明细-下钻](#营收明细-各业务收入明细-下钻)
  - [营收明细-收入构成](#营收明细-收入构成)
  - [营收明细-主要指标趋势](#营收明细-主要指标趋势)
  - [营收明细-成本构成](#营收明细-成本构成)
  - [营收明细-成本收入趋势-对比值](#营收明细-成本收入趋势-对比值)
- [用户分析](#用户分析)
  - [渠道下拉](#渠道下拉)
  - [游戏分类下拉](#游戏分类下拉)
  - [游戏下拉](#游戏下拉)
  - [用户活跃](#用户活跃)
  - [用户注册](#用户注册)
  - [日均活跃分布图](#日均活跃分布图)
  - [日均注册分布图](#日均注册分布图)
  - [热度排行榜](#热度排行榜)
  - [用户数据明细](#用户数据明细)
- [产品分析](#产品分析)
  - [合作方名称下拉](#合作方名称下拉)
  - [游戏分类下拉](#游戏分类下拉-1)
  - [游戏下拉](#游戏下拉-1)
  - [游戏盘面](#游戏盘面)
  - [游戏分布-游戏名称下拉](#游戏分布-游戏名称下拉)
  - [游戏分布](#游戏分布)
  - [ROI排行榜](#roi排行榜)
  - [产品数据明细](#产品数据明细)
  - [产品数据明细-展开游戏类型](#产品数据明细-展开游戏类型)
  - [产品数据明细-点击游戏类型](#产品数据明细-点击游戏类型)
- [渠道分析](#渠道分析)
  - [计费方式下拉](#计费方式下拉)
  - [渠道下拉](#渠道下拉-1)
  - [游戏分类下拉](#游戏分类下拉-2)
  - [游戏下拉](#游戏下拉-2)
  - [推广位下拉](#推广位下拉)
  - [渠道质量指数](#渠道质量指数)
  - [各渠道收入百分比累计图](#各渠道收入百分比累计图)
  - [各渠道贡献度](#各渠道贡献度)
  - [各渠道贡献度-趋势](#各渠道贡献度-趋势)
  - [渠道数据明细](#渠道数据明细)
  - [渠道数据明细-展开推广位](#渠道数据明细-展开推广位)
  - [渠道数据明细-点击推广位](#渠道数据明细-点击推广位)
- [渠道分析V2](#渠道分析v2)
  - [渠道分析全局筛选-一级分类下拉](#渠道分析全局筛选-一级分类下拉)
  - [渠道分析全局筛选-二级分类下拉](#渠道分析全局筛选-二级分类下拉)
  - [渠道分析全局筛选-三级分类下拉](#渠道分析全局筛选-三级分类下拉)
  - [渠道分析全局筛选-合作CP方](#渠道分析全局筛选-合作cp方)
  - [渠道分析全局筛选-合作渠道](#渠道分析全局筛选-合作渠道)
  - [渠道分析全局筛选-游戏类型](#渠道分析全局筛选-游戏类型)
  - [渠道分析全局筛选-游戏名称(游戏CODE)](#渠道分析全局筛选-游戏名称游戏code)
  - [渠道评分-图表](#渠道评分-图表)
  - [渠道评分-雷达图](#渠道评分-雷达图)
  - [各渠道贡献度](#各渠道贡献度-1)
  - [数据明细](#数据明细)

# 通用接口参数文档
参数  

| 字段 | 类型 | 描述     |
| ---- | ---- | -------- |
| data | json | 查询参数 |

查询参数：
```json
{
    "chartName": "PV",  //图表名称
    "chartType": "line",	//图表类型.可传值table(表格),simple-table(普通表格),retain(留存图),text(文本提),line(折线图),histogram(柱形图),two-axis(双轴图),line-area(面积图),radar(雷达图)
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
            "group": "day",	//聚合粒度。仅时间类型维度可以使用。可传值year、quarter、month、week、day、hour
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
返回结果：
```json
{
    "code": 20000,
    "status": "success",
    "data": {
        "limit": null,
        "total": null,
        "sql": "select `category3` as dimension0, sum(`estimate_income`) as measure0 from bi_dashboard.st_game_operate_roi_statistic where (1 = 1 and  toDate(`date_time`) >= ? and  toDate(`date_time`) <= ?) group by `category3` order by dimension0 asc, measure0 asc limit 500 offset 0",
        "bindValues": [
            "2020-08-19",
            "2020-08-19"
        ],
        "cacheHit": false,
        "datasTime": "2020-08-20 10:48:02",
        "datas": [
            {
                "id": 0,
                "category": "dimension",
                "group": "",
                "name": "category3",
                "displayName": "三级分类",
                "data": [
                    "手机游戏",
                    "端游",
                    "页游"
                ],
                "compareData": null,
                "order": null,
                "format": null,
                "distinctData": null,
                "compareDistinctData": null
            },
            {
                "id": 0,
                "data": [
                    100,
                    100,
                    100
                ],
                "compareData": null,
                "compareMap": null,
                "totalData": null,
                "category": "measure",
                "proportion": null,
                "displayName": "预估收入",
                "name": "estimate_income",
                "type": null,
                "order": null,
                "contrast": "",
                "summary": null,
                "groupData": null,
                "compareGroupData": null,
                "dimensionData": null,
                "digitDisplay": null,
                "decimal": null
            }
        ],
        "rowFormatDataList": [
            [
                "手机游戏",
                "100"
            ],
            [
                "端游",
                "100"
            ],
            [
                "页游",
                "100"
            ]
        ]
    }
}
```
# 收入分析
## 收入分析全局筛选-一级分类下拉
查询参数：
```json
{
    "id": "income-analysis-common-0",
    "chartName": "一级分类下拉",
    "chartType": "simple-table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_roi_statistic",
    "dimension": [],
    "measure": [
        {
            "name": "category1",
            "func": "distinct",
            "aliasName": "一级业务类别",
            "contrast": ""
        }
    ],
    "dashboard": [
        {
            "name": "category1",
            "logic": "isnotempty",
            "注释": "硬编码"
        },
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "customer_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作CP方。"
        },
        {
            "name": "agent_id",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作渠道。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏类型。"
        },
        {
            "name": "game_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏名称。"
        },
        {
            "name": "game_code",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏CODE。"
        }
    ]
}
```
## 收入分析全局筛选-二级分类下拉
查询参数：
```json
{
    "id": "income-analysis-common-10",
    "chartName": "二级分类下拉",
    "chartType": "simple-table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_roi_statistic",
    "dimension": [],
    "measure": [
        {
            "name": "category2",
            "func": "distinct",
            "aliasName": "二级业务类别",
            "contrast": ""
        }
    ],
    "dashboard": [
        {
            "name": "category2",
            "logic": "isnotempty",
            "注释": "硬编码"
        },
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "category1",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，一级业务类别。"
        },
        {
            "name": "customer_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作CP方。"
        },
        {
            "name": "agent_id",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作渠道。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏类型。"
        },
        {
            "name": "game_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏名称。"
        },
        {
            "name": "game_code",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏CODE。"
        }
    ]
}
```
## 收入分析全局筛选-三级分类下拉
查询参数：
```json
{
    "id": "income-analysis-common-20",
    "chartName": "三级分类下拉",
    "chartType": "simple-table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_roi_statistic",
    "dimension": [],
    "measure": [
        {
            "name": "category3",
            "func": "distinct",
            "aliasName": "三级业务类别",
            "contrast": ""
        }
    ],
    "dashboard": [
        {
            "name": "category3",
            "logic": "isnotempty",
            "注释": "硬编码"
        },
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "category1",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，一级业务类别。"
        },
        {
            "name": "category2",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，二级业务类别。"
        },
        {
            "name": "customer_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作CP方。"
        },
        {
            "name": "agent_id",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作渠道。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏类型。"
        },
        {
            "name": "game_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏名称。"
        },
        {
            "name": "game_code",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏CODE。"
        }
    ]
}
```
## 收入分析全局筛选-合作CP方
查询参数：
```json
{
    "id": "income-analysis-common-30",
    "chartName": "合作CP方",
    "chartType": "simple-table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_roi_statistic",
    "dimension": [],
    "measure": [
        {
            "name": "customer_name",
            "func": "distinct",
            "aliasName": "合作CP方",
            "contrast": ""
        }
    ],
    "dashboard": [
        {
            "name": "customer_name",
            "logic": "isnotempty",
            "注释": "硬编码"
        },
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "category1",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，一级业务类别。"
        },
        {
            "name": "category2",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，二级业务类别。"
        },
        {
            "name": "category3",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，三级业务类别。"
        },
        {
            "name": "agent_id",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作渠道。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏类型。"
        },
        {
            "name": "game_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏名称。"
        },
        {
            "name": "game_code",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏CODE。"
        }
    ]
}
```
## 收入分析全局筛选-合作渠道
查询参数：
```json
{
    "id": "income-analysis-common-40",
    "chartName": "合作渠道",
    "chartType": "simple-table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_roi_statistic",
    "dimension": [],
    "measure": [
        {
            "name": "agent_id",
            "func": "distinct",
            "aliasName": "合作渠道",
            "contrast": ""
        }
    ],
    "dashboard": [
        {
            "name": "agent_id",
            "logic": "isnotempty",
            "注释": "硬编码"
        },
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "category1",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，一级业务类别。"
        },
        {
            "name": "category2",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，二级业务类别。"
        },
        {
            "name": "category3",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，三级业务类别。"
        },
        {
            "name": "customer_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作CP方。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏类型。"
        },
        {
            "name": "game_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏名称。"
        },
        {
            "name": "game_code",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏CODE。"
        }
    ]
}
```
## 收入分析全局筛选-游戏类型
查询参数：
```json
{
    "id": "income-analysis-common-50",
    "chartName": "游戏类型",
    "chartType": "simple-table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_roi_statistic",
    "dimension": [],
    "measure": [
        {
            "name": "game_category_name",
            "func": "distinct",
            "aliasName": "游戏类型",
            "contrast": ""
        }
    ],
    "dashboard": [
        {
            "name": "game_category_name",
            "logic": "isnotempty",
            "注释": "硬编码"
        },
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "category1",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，一级业务类别。"
        },
        {
            "name": "category2",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，二级业务类别。"
        },
        {
            "name": "category3",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，三级业务类别。"
        },
        {
            "name": "customer_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作CP方。"
        },
        {
            "name": "agent_id",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作渠道。"
        },
        {
            "name": "game_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏名称。"
        },
        {
            "name": "game_code",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏CODE。"
        }
    ]
}
```
## 收入分析全局筛选-游戏名称(游戏CODE)
查询参数：
```json
{
  "id": "income-analysis-common-60",
  "chartName": "游戏名称-游戏code下拉",
  "chartType": "table",
  "databaseName": "bi_dashboard",
  "tableName": "st_game_operate_roi_statistic",
  "dimension": [
    {
      "name": "game_name",
      "aliasName": "游戏名称"
    },
    {
      "name": "game_code",
      "aliasName": "游戏CODE"
    }
  ],
  "measure": [
  ],
  "dashboard": [
    {
      "name": "game_name",
      "logic": "isnotempty"
    },
    {
      "name": "date_time",
      "logic": "between",
      "value": [
        "2020-01-01",
        "2020-12-31"
      ],
      "func": "day",
      "注释": "全局筛选，时间。"
    },
    {
        "name": "category1",
        "logic": "eq",
        "value": "",
        "func": "",
        "注释": "全局筛选，一级业务类别。"
    },
    {
        "name": "category2",
        "logic": "eq",
        "value": "",
        "func": "",
        "注释": "全局筛选，二级业务类别。"
    },
    {
        "name": "category3",
        "logic": "eq",
        "value": "",
        "func": "",
        "注释": "全局筛选，三级业务类别。"
    },
    {
        "name": "customer_name",
        "logic": "eq",
        "value": "",
        "func": "",
        "注释": "全局筛选，合作CP方。"
    },
    {
        "name": "agent_id",
        "logic": "eq",
        "value": "",
        "func": "",
        "注释": "全局筛选，合作渠道。"
    },
    {
        "name": "game_category_name",
        "logic": "eq",
        "value": "",
        "func": "",
        "注释": "全局筛选，游戏类型。"
    }
  ]
}
```
## 目标实际完成度
说明:  
切换为2021年，dashboard中category1的value为["媒体业务","游戏联运","游戏发行"]  
查询参数：
```json
{
    "id": "income-analysis-0",
    "chartName": "目标实际完成度",
    "chartType": "histogram",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_roi_statistic",
    "dimension": [
        {
            "name": "category1",
            "group": "",
            "aliasName": "一级业务类别"
        },{
            "name": "category2",
            "group": "",
            "aliasName": "二级业务类别"
        }
    ],
    "measure": [
        {
            "name": "(sum(real_income) - sum(real_cost)) / 10000",
            "func": "",
            "aliasName": "利润",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "(sum(real_income) - sum(real_cost)) / any(profit_target_b)",
            "func": "",
            "aliasName": "目标实际完成度",
            "contrast": "",
            "digitDisplay": "percent",
            "注释": "默认b档。s档将name中profit_target_b改为profit_target_s，a档将name中profit_target_b改为profit_target_a，c档将name中profit_target_b改为profit_target_c，d档将name中profit_target_b改为profit_target_d"
        }
    ],
    "dashboard": [
        {
            "name": "category1",
            "logic": "in",
            "value": ["游戏运营","网盟业务","游戏发行", "媒体业务"],
            "func": "",
            "注释": "硬编码"
        },{
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        }
    ]
}
```
## 目标实际完成度-ROI、收入、成本
说明：
```
前端画两个图表，后端可以用一个接口查出ROI、收入、成本。前端需要做一下拆分。
```
查询参数：
```json
{
    "id": "income-analysis-40",
    "chartName": "ROI、收入、成本",
    "chartType": "histogram",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_roi_statistic",
    "dimension": [
        {
            "name": "date_time",
            "group": "month",
            "aliasName": "时间"
        }
    ],
    "measure": [
        {
            "name": "sum(estimate_income) / sum(estimate_cost)",
            "func": "",
            "aliasName": "预估ROI",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(real_income) / sum(real_cost)",
            "func": "",
            "aliasName": "实际ROI",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "(sum(estimate_income) - sum(estimate_cost)) / sum(estimate_cost)",
            "func": "",
            "aliasName": "预估利润率",
            "contrast": "",
            "digitDisplay": "percent"
        },
        {
            "name": "(sum(real_income) - sum(real_cost)) / sum(real_cost)",
            "func": "",
            "aliasName": "实际利润率",
            "contrast": "",
            "digitDisplay": "percent"
        },
        {
            "name": "sum(estimate_income) - sum(estimate_cost)",
            "func": "",
            "aliasName": "预估利润",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(real_income) - sum(real_cost)",
            "func": "",
            "aliasName": "实际利润",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(estimate_income)",
            "func": "",
            "aliasName": "预估收入",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(real_income)",
            "func": "",
            "aliasName": "实际收入",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(estimate_cost)",
            "func": "",
            "aliasName": "预估成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(real_cost)",
            "func": "",
            "aliasName": "实际成本",
            "contrast": "",
            "decimal": 2
        }
    ],
    "dashboard": [
        {
            "name": "category1",
            "logic": "in",
            "value": ["游戏运营","网盟业务","游戏发行", "媒体业务"],
            "func": "",
            "注释": "硬编码"
        },
        {
            "name": "category2",
            "logic": "eq",
            "value": "SDK联运",
            "func": "",
            "注释": "如果左侧点击联动，value改为对应的值。"
        },
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        }
    ]
}
```
## KPI指标
说明：
```
下拉选项接口
/bi-bak/bi-dashboard/dashboard/analyse/chart/incomeanalysis45/select
```
查询参数：
```json
{
    "id": "income-analysis-45",
    "chartName": "KPI指标",
    "chartType": "table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_kpi_statistic",
    "dimension": [
        {
            "name": "date_time",
            "group": "month",
            "aliasName": "时间"
        }
    ],
    "measure": [
        {
            "name": "new_user",
            "func": "sum",
            "aliasName": "日均新增用户",
            "contrast": "",
            "decimal": 2,
            "注释": "下拉框为日均新增用户时name为new_user，aliasName为日均新增用户；下拉框为付费用户数时name为pay_user，aliasName为付费用户数；下拉框为KA产品数时name为ka_product，aliasName为KA产品数数；下拉框为CPS的KA渠道数时name为cps_channel，aliasName为CPS的KA渠道数；下拉框为非CPS的KA渠道数时name为no_cps_channel，aliasName为非CPS的KA渠道数；下拉框为总充值超过2w的用户MAU时name为keral_user，aliasName为总充值超过2w的用户MAU；"
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        }
    ]
}
```
## 各业务收入明细
查询参数：
```json
{
    "id": "income-analysis-50",
    "chartName": "各业务收入明细",
    "chartType": "table",
    "rowFormat": 1,
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_roi_statistic",
    "dimension": [
        {
            "name": "category1",
            "group": "",
            "aliasName": "一级分类"
        },
        {
            "name": "category2",
            "group": "",
            "aliasName": "二级分类"
        }
    ],
    "measure": [
        {
            "name": "estimate_income",
            "func": "sum",
            "aliasName": "预估收入",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "estimate_cost",
            "func": "sum",
            "aliasName": "预估成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "real_income",
            "func": "sum",
            "aliasName": "实际收入",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "(sum(real_income)-sum(estimate_income))/sum(estimate_income)",
            "func": "",
            "aliasName": "实际收入(对比)",
            "contrast": "",
            "digitDisplay": "percent",
            "decimal": 1
        },
        {
            "name": "real_cost",
            "func": "sum",
            "aliasName": "实际成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "(sum(real_cost)-sum(estimate_cost))/sum(estimate_cost)",
            "func": "",
            "aliasName": "实际成本(对比)",
            "contrast": "",
            "digitDisplay": "percent",
            "decimal": 1
        },
        {
            "name": "sum(real_income) - sum(real_cost)",
            "func": "",
            "aliasName": "实际利润",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "(sum(real_income) - sum(real_cost)) / sum(real_income)",
            "func": "",
            "aliasName": "实际利润率",
            "contrast": "",
            "digitDisplay": "percent"
        },

        {
            "name": "concat(toString(round(any(profit_target_b)/10000,0)),'w/', if(any(profit_target_b)=0, '--', concat(toString(round((sum(real_income) - sum(real_cost))/any(profit_target_b)*100,2)),'%')))",
            "func": "",
            "aliasName": "考核目标/完成度-利润",
            "contrast": "",
            "注释": "默认b档。s档将name中profit_target_b改为profit_target_s，income_target_b改为income_target_s;a档将name中profit_target_b改为profit_target_a，a档将name中income_target_b改为income_target_a；c档将name中profit_target_b改为profit_target_c，c档将name中income_target_b改为income_target_c;d档将name中profit_target_b改为profit_target_d，d档将name中income_target_b改为income_target_d。"
        },
        {
            "name": "(sum(real_income) - sum(real_cost))/any(profit_target_b)",
            "func": "",
            "aliasName": "考核目标/完成度-利润完成度",
            "contrast": "",
            "注释": "默认b档。s档将name中profit_target_b改为profit_target_s，income_target_b改为income_target_s;a档将name中profit_target_b改为profit_target_a，a档将name中income_target_b改为income_target_a；c档将name中profit_target_b改为profit_target_c，c档将name中income_target_b改为income_target_c;d档将name中profit_target_b改为profit_target_d，d档将name中income_target_b改为income_target_d。"
        },

        {
            "name": "concat(toString(round(any(income_target_b)/10000,0)),'w/', if(any(income_target_b)=0, '--', concat(toString(round(sum(real_income)/any(income_target_b)*100,2)),'%')) )",
            "func": "",
            "aliasName": "考核目标/完成度-收入",
            "contrast": "",
            "注释": "默认b档。s档将name中profit_target_b改为profit_target_s，income_target_b改为income_target_s;a档将name中profit_target_b改为profit_target_a，a档将name中income_target_b改为income_target_a；c档将name中profit_target_b改为profit_target_c，c档将name中income_target_b改为income_target_c;d档将name中profit_target_b改为profit_target_d，d档将name中income_target_b改为income_target_d。"
        },
        {
            "name": "sum(real_income)/any(income_target_b)",
            "func": "",
            "aliasName": "考核目标/完成度-收入完成度",
            "contrast": "",
            "注释": "默认b档。s档将name中profit_target_b改为profit_target_s，income_target_b改为income_target_s;a档将name中profit_target_b改为profit_target_a，a档将name中income_target_b改为income_target_a；c档将name中profit_target_b改为profit_target_c，c档将name中income_target_b改为income_target_c;d档将name中profit_target_b改为profit_target_d，d档将name中income_target_b改为income_target_d。"
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        }
    ]
}
```
## 各业务收入明细-下钻
查询参数：
```json
{
    "id": "income-analysis-55",
    "chartName": "各业务收入明细",
    "chartType": "table",
    "rowFormat": 1,
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_roi_statistic",
    "rollup": 1,
    "dimension": [
        {
            "name": "category1",
            "group": "",
            "aliasName": "一级分类"
        },
        {
            "name": "category2",
            "group": "",
            "aliasName": "二级分类"
        },
        {
            "name": "category3",
            "group": "",
            "aliasName": "三级分类"
        }
    ],
    "measure": [
        {
            "name": "estimate_income",
            "func": "sum",
            "aliasName": "预估收入",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "estimate_cost",
            "func": "sum",
            "aliasName": "预估成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "real_income",
            "func": "sum",
            "aliasName": "实际收入",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "(sum(real_income)-sum(estimate_income))/sum(estimate_income)",
            "func": "",
            "aliasName": "实际收入(对比)",
            "contrast": "",
            "digitDisplay": "percent",
            "decimal": 1
        },
        {
            "name": "real_cost",
            "func": "sum",
            "aliasName": "实际成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "(sum(real_cost)-sum(estimate_cost))/sum(estimate_cost)",
            "func": "",
            "aliasName": "实际成本(对比)",
            "contrast": "",
            "digitDisplay": "percent",
            "decimal": 1
        },
        
        {
            "name": "sum(real_income) - sum(real_cost)",
            "func": "",
            "aliasName": "实际利润",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "(sum(real_income) - sum(real_cost)) / sum(real_income)",
            "func": "",
            "aliasName": "实际利润率",
            "contrast": "",
            "digitDisplay": "percent"
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        }
    ]
}
```
## ROI趋势图
说明：
```
渠道下钻时，date_time条件改为下钻条件，将维度改为
{
    "name": "agent_id",
    "group": "",
    "aliasName": "渠道"
}
游戏下钻时，date_time条件改为下钻条件，将维度改为
{
    "name": "game_name",
    "group": "",
    "aliasName": "游戏"
}
下钻时带上当前筛选条件+柱形对应日期，例如
"dashboard": [
    {
        "name": "date_time",
        "logic": "eq",
        "value": "2020-09",
        "func": "month",
        "注释": "全局筛选，时间。"
    },
    {
        "name": "date_time",
        "logic": "between",
        "value": [
            "2020-09-01",
            "2020-09-13"
        ],
        "func": "day",
        "注释": "全局筛选，时间。"
    }
],
"screen": [
    {
        "name": "agent_id",
        "logic": "in",
        "value": [
            "UC"
        ],
        "func": "",
        "注释": "图内筛选，渠道。"
    },
    {
        "name": "game_category_name",
        "logic": "in",
        "value": [
            "手游"
        ],
        "func": "",
        "注释": "全局筛选，游戏分类。"
    },
    {
        "name": "game_name",
        "logic": "in",
        "value": [
            "梦幻西游"
        ],
        "func": "",
        "注释": "图内筛选，游戏。"
    }
]
```
查询参数：
```json
{
    "id": "income-analysis-60",
    "chartName": "ROI趋势图",
    "chartType": "histogram",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_roi_statistic",
    "dimension": [
        {
            "name": "date_time",
            "group": "month",
            "aliasName": "时间",
            "注释":"按年、按季度、按月、按日切换时，调整对应的group"
        }
    ],
    "measure": [
        {
            "name": "sum(estimate_rmb_income)",
            "func": "",
            "aliasName": "人民币收入",
            "contrast": "",
            "decimal": 2,
            "注释": "预估值，name为：sum(estimate_rmb_income) 。实际值，name为：sum(real_rmb_income)"
        },
        {
            "name": "sum(estimate_coupon_income)",
            "func": "",
            "aliasName": "E币券收入",
            "contrast": "",
            "decimal": 2,
            "注释": "预估值，name为：sum(estimate_coupon_income) 。实际值，name为：sum(real_coupon_income)"
        },
        {
            "name": "sum(estimate_cp_cost)",
            "func": "",
            "aliasName": "CP成本",
            "contrast": "",
            "decimal": 2,
            "注释": "预估值，name为：sum(estimate_cp_cost)。实际值，name为：sum(real_cp_cost)"
        },
        {
            "name": "sum(estimate_channel_cost)",
            "func": "",
            "aliasName": "渠道成本",
            "contrast": "",
            "decimal": 2,
            "注释": "预估值，name为：sum(estimate_channel_cost)。实际值，name为：sum(real_channel_cost)"
        },
        {
            "name": "sum(estimate_coupon_cost)",
            "func": "",
            "aliasName": "E币券成本",
            "contrast": "",
            "decimal": 2,
            "注释": "预估值，name为：sum(estimate_coupon_cost)。实际值，name为：sum(real_coupon_cost)"
        },
        {
            "name": "sum(estimate_income) / sum(estimate_cost)",
            "func": "",
            "aliasName": "ROI",
            "contrast": "",
            "decimal": 2,
            "注释": "预估值，name为：sum(estimate_income) / sum(estimate_cost)。实际值，name为：sum(real_income) / sum(real_cost)"
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        }
    ],
    "screen": [
        {
            "name": "agent_id",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "图内筛选，渠道。"
        },
        {
            "name": "game_category_name",
            "logic": "in",
            "value": ["手机游戏"],
            "func": "",
            "注释": "图内筛选，游戏分类。"
        },
        {
            "name": "game_name",
            "logic": "in",
            "value": ["王者荣耀","qq飞车"],
            "func": "",
            "注释": "图内筛选，游戏。"
        }
    ]
}
```

## ROI趋势图-渠道下拉
查询参数：
```json
{
    "id": "income-analysis-70",
    "chartName": "ROI趋势图-渠道下拉",
    "chartType": "simple-table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_roi_statistic",
    "dimension": [],
    "measure": [
        {
            "name": "agent_id",
            "func": "distinct",
            "aliasName": "渠道",
            "contrast": ""
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        }
    ]
}
```
## ROI趋势图-游戏分类下拉
查询参数：
```json
{
    "id": "income-analysis-75",
    "chartName": "ROI趋势图-游戏分类下拉",
    "chartType": "simple-table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_roi_statistic",
    "dimension": [],
    "measure": [
        {
            "name": "game_category_name",
            "func": "distinct",
            "aliasName": "游戏分类",
            "contrast": ""
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        }
    ],
    "screen": [
        {
            "name": "agent_id",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "图内筛选，渠道。"
        }
    ]
}
```
## ROI趋势图-游戏下拉
查询参数：
```json
{
    "id": "income-analysis-80",
    "chartName": "ROI趋势图-游戏下拉",
    "chartType": "simple-table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_roi_statistic",
    "dimension": [],
    "measure": [
        {
            "name": "game_name",
            "func": "distinct",
            "aliasName": "游戏",
            "contrast": ""
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        }
    ],
    "screen": [
        {
            "name": "agent_id",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "图内筛选，渠道。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "图内筛选，游戏分类。"
        }
    ]
}
```
## 营收明细-各业务收入明细
查询参数：
```json
{
    "id": "income-analysis-90",
    "chartName": "各业务收入明细",
    "chartType": "table",
    "rowFormat": 1,
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_roi_statistic",
    "dimension": [
        {
            "name": "category1",
            "group": "",
            "aliasName": "一级分类"
        },
        {
            "name": "category2",
            "group": "",
            "aliasName": "二级分类"
        }
    ],
    "measure": [
        {
            "name": "estimate_income",
            "func": "sum",
            "aliasName": "预估收入",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "estimate_cost",
            "func": "sum",
            "aliasName": "预估成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "concat(toString(round(sum(real_income),2)),'(',toString(round((sum(real_income)-sum(estimate_income))/sum(estimate_income)*100,1)),'%)')",
            "func": "",
            "aliasName": "实际收入(对比)",
            "contrast": ""
        },
        {
            "name": "concat(toString(round(sum(real_cost),2)),'(',toString(round((sum(real_cost)-sum(estimate_cost))/sum(estimate_cost)*100,1)),'%)')",
            "func": "",
            "aliasName": "实际成本(对比)",
            "contrast": ""
        },
        {
            "name": "sum(real_income) - sum(real_cost)",
            "func": "",
            "aliasName": "实际利润",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "(sum(real_income) - sum(real_cost)) / sum(real_income)",
            "func": "",
            "aliasName": "实际利润率",
            "contrast": "",
            "digitDisplay": "percent"
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "category1",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，一级业务类别。"
        },
        {
            "name": "category2",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，二级业务类别。"
        },
        {
            "name": "category3",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，三级业务类别。"
        },
        {
            "name": "customer_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作CP方。"
        },
        {
            "name": "agent_id",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作渠道。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏类型。"
        },
        {
            "name": "game_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏名称。"
        },
        {
            "name": "game_code",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏CODE。"
        }
    ]
}
```
## 营收明细-各业务收入明细-下钻
同income-analysis-55
## 营收明细-收入构成
查询参数：
```json
{
    "id": "income-analysis-100",
    "chartName": "收入构成",
    "chartType": "table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_roi_statistic",
    "dimension": [
        {
            "name": "category1",
            "group": "",
            "aliasName": "一级分类",
            "注释":"选中二级分类时，name为concat(category1, '/' ,category2)；选中三级分类时，name为concat(category1, '/' ,category2, '/' ,category3)；"
        }
    ],
    "measure": [
        {
            "name": "sum(real_income)",
            "func": "",
            "aliasName": "实际收入",
            "contrast": "",
            "decimal": 2,
            "注释":"
            选中实际收入时，name为sum(real_income),aliasName为实际收入；
            选中实际利润时，name为sum(real_income) - sum(real_cost),aliasName为实际利润；

            选中预估收入时，name为sum(estimate_income),aliasName为预估收入；
            选中预估利润时，name为sum(estimate_income) - sum(estimate_cost),aliasName为预估利润；
            "
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "category1",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，一级业务类别。"
        },
        {
            "name": "category2",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，二级业务类别。"
        },
        {
            "name": "category3",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，三级业务类别。"
        },
        {
            "name": "customer_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作CP方。"
        },
        {
            "name": "agent_id",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作渠道。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏类型。"
        },
        {
            "name": "game_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏名称。"
        },
        {
            "name": "game_code",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏CODE。"
        }
    ]
}
```
## 营收明细-主要指标趋势
查询参数：
```json
{
    "id": "income-analysis-110",
    "chartName": "主要指标趋势",
    "chartType": "table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_roi_statistic",
    "dimension": [
        {
            "name": "date_time",
            "group": "month",
            "aliasName": "时间"
        }
    ],
    "measure": [
        {
            "name": "sum(estimate_income) - sum(estimate_cost)",
            "func": "",
            "aliasName": "预估利润",
            "contrast": "",
            "decimal": 2,
            "注释":"
            默认为利润；
            切换为总收入，name为sum(estimate_income)，aliasName为预估总收入；
            切换为总成本，name为sum(estimate_cost)，aliasName为预估总成本；
            切换为人民币收入，name为sum(estimate_rmb_income)，aliasName为预估人民币收入；
            切换为E币券收入，name为sum(estimate_coupon_income)，aliasName为预估E币券收入；
            切换为CP成本，name为sum(estimate_cp_cost)，aliasName为预估CP成本；
            切换为渠道成本，name为sum(estimate_channel_cost)，aliasName为预估渠道成本；
            切换为E币券成本，name为sum(estimate_coupon_cost)，aliasName为预估E币券成本；
            "
        },
        {
            "name": "sum(real_income) - sum(real_cost)",
            "func": "",
            "aliasName": "实际利润",
            "contrast": "",
            "decimal": 2,
            "注释":"
            默认为利润；
            切换为总收入，name为sum(real_income)，aliasName为实际总收入；
            切换为总成本，name为sum(real_cost)，aliasName为实际总成本；
            切换为人民币收入，name为sum(real_rmb_income)，aliasName为实际人民币收入；

            切换为E币券收入，name为sum(real_coupon_income)，aliasName为实际E币券收入；
            切换为CP成本，name为sum(real_cp_cost)，aliasName为实际CP成本；

            切换为渠道成本，name为sum(real_channel_cost)，aliasName为实际渠道成本；
            切换为E币券成本，name为sum(real_coupon_cost)，aliasName为实际E币券成本；
            "
        },
        {
            "name": "sum(estimate_income) / sum(estimate_cost)",
            "func": "",
            "aliasName": "预估ROI",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(real_income) / sum(real_cost)",
            "func": "",
            "aliasName": "实际ROI",
            "contrast": "",
            "decimal": 2
        },
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "category1",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，一级业务类别。"
        },
        {
            "name": "category2",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，二级业务类别。"
        },
        {
            "name": "category3",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，三级业务类别。"
        },
        {
            "name": "customer_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作CP方。"
        },
        {
            "name": "agent_id",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作渠道。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏类型。"
        },
        {
            "name": "game_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏名称。"
        },
        {
            "name": "game_code",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏CODE。"
        }
    ]
}
```
## 营收明细-成本构成
查询参数：
```json
{
    "id": "income-analysis-120",
    "chartName": "成本构成",
    "chartType": "table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_roi_statistic",
    "dimension": [
        {
            "name": "category1",
            "group": "",
            "aliasName": "一级分类",
            "注释":"选中二级分类时，name为concat(category1, '/' ,category2)；选中三级分类时，name为concat(category1, '/' ,category2, '/' ,category3)；"
        }
    ],
    "measure": [
        {
            "name": "sum(real_cp_cost)",
            "func": "",
            "aliasName": "CP成本",
            "contrast": "",
            "decimal": 2,
            "注释": "切换为预估值时，name改为sum(estimate_cp_cost)"
        },
        {
            "name": "sum(real_channel_cost)",
            "func": "",
            "aliasName": "渠道成本",
            "contrast": "",
            "decimal": 2,
            "注释": "切换为预估值时，name改为sum(estimate_channel_cost)"
        },
        {
            "name": "sum(real_coupon_cost)",
            "func": "",
            "aliasName": "E币券成本",
            "contrast": "",
            "decimal": 2,
            "注释": "切换为预估值时，name改为sum(estimate_coupon_cost)"
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "category1",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，一级业务类别。"
        },
        {
            "name": "category2",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，二级业务类别。"
        },
        {
            "name": "category3",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，三级业务类别。"
        },
        {
            "name": "customer_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作CP方。"
        },
        {
            "name": "agent_id",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作渠道。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏类型。"
        },
        {
            "name": "game_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏名称。"
        },
        {
            "name": "game_code",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏CODE。"
        }
    ]
}
```
## 营收明细-成本收入趋势-对比值
查询参数：
```json
{
    "id": "income-analysis-130",
    "chartName": "成本收入趋势-对比值",
    "chartType": "table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_roi_statistic",
    "dimension": [
        {
            "name": "date_time",
            "group": "month",
            "aliasName": "时间",
            "注释":"按年、按季度、按月、按日切换时，调整对应的group"
        }
    ],
    "measure": [
         {
            "name": "sum(real_rmb_income)",
            "func": "",
            "aliasName": "人民币收入",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "(sum(real_rmb_income)-sum(estimate_rmb_income)) / sum(estimate_rmb_income)",
            "func": "",
            "aliasName": "人民币收入(对比)",
            "contrast": "",
            "digitDisplay": "percent",
            "decimal": 1
        },
        {
            "name": "sum(real_coupon_income)",
            "func": "",
            "aliasName": "E币券收入",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "(sum(real_coupon_income)-sum(estimate_coupon_income)) / sum(estimate_coupon_income)",
            "func": "",
            "aliasName": "E币券收入(对比)",
            "contrast": "",
            "digitDisplay": "percent",
            "decimal": 1
        },
        {
            "name": "sum(real_cp_cost)",
            "func": "",
            "aliasName": "CP成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "(sum(real_cp_cost)-sum(estimate_cp_cost)) / sum(estimate_cp_cost)",
            "func": "",
            "aliasName": "CP成本(对比)",
            "contrast": "",
            "digitDisplay": "percent",
            "decimal": 1
        },
        {
            "name": "sum(real_channel_cost)",
            "func": "",
            "aliasName": "渠道成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "(sum(real_channel_cost)-sum(estimate_channel_cost)) / sum(estimate_channel_cost)",
            "func": "",
            "aliasName": "渠道成本(对比)",
            "contrast": "",
            "digitDisplay": "percent",
            "decimal": 1
        },
        {
            "name": "sum(estimate_coupon_cost)",
            "func": "",
            "aliasName": "E币券成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "(sum(real_coupon_cost)-sum(estimate_coupon_cost)) / sum(estimate_coupon_cost)",
            "func": "",
            "aliasName": "E币券成本(对比)",
            "contrast": "",
            "digitDisplay": "percent",
            "decimal": 1
        },
        {
            "name": "sum(real_income)",
            "func": "",
            "aliasName": "总收入",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "(sum(real_income)-sum(estimate_income)) / sum(estimate_income)",
            "func": "",
            "aliasName": "总收入(对比)",
            "contrast": "",
            "digitDisplay": "percent",
            "decimal": 1
        },
        {
            "name": "sum(estimate_cost)",
            "func": "",
            "aliasName": "总成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "(sum(real_cost)-sum(estimate_cost)) / sum(estimate_cost)",
            "func": "",
            "aliasName": "总成本(对比)",
            "contrast": "",
            "digitDisplay": "percent",
            "decimal": 1
        },
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "category1",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，一级业务类别。"
        },
        {
            "name": "category2",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，二级业务类别。"
        },
        {
            "name": "category3",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，三级业务类别。"
        },
        {
            "name": "customer_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作CP方。"
        },
        {
            "name": "agent_id",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作渠道。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏类型。"
        },
        {
            "name": "game_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏名称。"
        },
        {
            "name": "game_code",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏CODE。"
        }
    ]
}
```
# 用户分析
## 渠道下拉
查询参数：
```json
{
    "id": "user-quality-0",
    "chartName": "渠道下拉",
    "chartType": "simple-table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_channel_statistic",
    "dimension": [],
    "measure": [
        {
            "name": "agent_id",
            "func": "distinct",
            "aliasName": "渠道",
            "contrast": ""
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        }
    ]
}
```
## 游戏分类下拉
查询参数：
```json
{
    "id": "user-quality-10",
    "chartName": "游戏分类下拉",
    "chartType": "simple-table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_channel_statistic",
    "dimension": [],
    "measure": [
        {
            "name": "game_category_name",
            "func": "distinct",
            "aliasName": "游戏分类",
            "contrast": ""
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "agent_id",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，渠道。"
        }
    ]
}
```
## 游戏下拉
查询参数：
```json
{
    "id": "user-quality-20",
    "chartName": "游戏下拉",
    "chartType": "simple-table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_channel_statistic",
    "dimension": [],
    "measure": [
        {
            "name": "game_name",
            "func": "distinct",
            "aliasName": "游戏",
            "contrast": ""
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "agent_id",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏分类。"
        }
    ]
}
```
## 用户活跃
说明：
```
图表上方的日均DAU取返回度量数据中的totalData
```
查询参数：
```json
{
    "id": "user-quality-30",
    "chartName": "用户活跃",
    "chartType": "histogram",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_channel_statistic",
    "rollup": 1,
    "dimension": [
        {
            "name": "date_time",
            "group": "day",
            "aliasName": "时间"
        }
    ],
    "measure": [
        {
            "name": "uniqExactArrayIf(`active_array`, notEmpty(`active_array`)) / count(distinct date_time)",
            "func": "",
            "aliasName": "日均DAU",
            "contrast": "",
            "decimal": 0
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-21"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "agent_id",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏分类。"
        },
        {
            "name": "game_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏。"
        }
    ],
    "screen": [
        {
            "name": "active_cnts",
            "logic": "gt",
            "value": 0,
            "func": "",
            "注释": "硬编码"
        }
    ]
}
```
## 用户注册
说明：
```
图表上方的日均注册取返回度量数据中的totalData
```
查询参数：
```json
{
    "id": "user-quality-40",
    "chartName": "用户注册",
    "chartType": "histogram",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_channel_statistic",
    "rollup": 1,
    "dimension": [
        {
            "name": "date_time",
            "group": "quarter",
            "aliasName": "时间"
        }
    ],
    "measure": [
        {
            "name": "uniqExactArrayIf(`reg_array`, notEmpty(`reg_array`)) / count(distinct date_time)",
            "func": "",
            "aliasName": "日均注册",
            "contrast": "",
            "decimal": 0
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-21"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "agent_id",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏分类。"
        },
        {
            "name": "game_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏。"
        }
    ],
    "screen": [
        {
            "name": "reg_cnts",
            "logic": "gt",
            "value": 0,
            "func": "",
            "注释": "硬编码"
        }
    ]
}
```
## 日均活跃分布图
查询参数：
```json
{
    "id": "user-quality-50",
    "chartName": "日均活跃分布图",
    "chartType": "histogram",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_channel_statistic",
    "dimension": [
        {
            "name": "game_category_name",
            "group": "",
            "aliasName": "游戏类型"
        }
    ],
    "measure": [
        {
            "name": "uniqExactArrayIf(`active_array`, notEmpty(`active_array`)) / count(distinct date_time)",
            "func": "",
            "aliasName": "日均活跃",
            "contrast": "",
            "decimal": 0
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-21"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "agent_id",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏分类。"
        },
        {
            "name": "game_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏。"
        }
    ],
    "screen": [
        {
            "name": "active_cnts",
            "logic": "gt",
            "value": 0,
            "func": "",
            "注释": "硬编码"
        }
    ]
}
```
## 日均注册分布图
查询参数：
```json
{
    "id": "user-quality-60",
    "chartName": "日均注册分布图",
    "chartType": "histogram",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_channel_statistic",
    "dimension": [
        {
            "name": "game_category_name",
            "group": "",
            "aliasName": "游戏类型"
        }
    ],
    "measure": [
        {
            "name": "uniqExactArrayIf(`reg_array`, notEmpty(`reg_array`)) / count(distinct date_time)",
            "func": "",
            "aliasName": "日均注册",
            "contrast": "",
            "decimal": 0
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-21"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "agent_id",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏分类。"
        },
        {
            "name": "game_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏。"
        }
    ],
    "screen": [
        {
            "name": "reg_cnts",
            "logic": "gt",
            "value": 0,
            "func": "",
            "注释": "硬编码"
        }
    ]
}
```
## 热度排行榜
查询参数：
```json
{
    "id": "user-quality-70",
    "chartName": "热度排行榜",
    "chartType": "histogram",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_channel_statistic",
    "dimension": [
        {
            "name": "game_name",
            "group": "",
            "aliasName": "游戏"
        }
    ],
    "measure": [
        {
            "name": "uniqExactArrayIf(`reg_array`, notEmpty(`reg_array`)) / count(distinct date_time)",
            "func": "",
            "aliasName": "日均注册",
            "contrast": "",
            "decimal": 0,
            "percentOfMax": 1,
            "order": -1, 
            "注释": "右上角下拉切换为日均活跃时，name改为uniqExactArrayIf(`active_array`, notEmpty(`active_array`)) / count(distinct date_time)，aliasName改为日均活跃。"
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-21"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "agent_id",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏分类。"
        },
        {
            "name": "game_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏。"
        }
    ],
    "screen": [
        {
            "name": "reg_cnts",
            "logic": "gt",
            "value": 0,
            "func": "",
            "注释": "右上角下拉切换为日均活跃时，name改为active_cnts"
        }
    ]
}
```
## 用户数据明细
说明：
```
左上角切换到活跃，度量改为:
{
    "name": "active_array",
    "func": "uniqArray",
    "aliasName": "活跃",
    "contrast": ""
}
左上角切换到付费，度量改为:
{
    "name": "sum(pay_money) / uniqArrayIf(`active_array`, notEmpty(`active_array`))",
    "func": "",
    "aliasName": "ARPU(活跃)",
    "contrast": "",
    "decimal": 2
},
{
    "name": "sum(pay_money) / uniqArrayIf(`pay_array`, notEmpty(`pay_array`))",
    "func": "",
    "aliasName": "ARPPU(付费)",
    "contrast": "",
    "decimal": 2
}
```
查询参数：
```json
{
    "id": "user-quality-80",
    "chartName": "用户数据明细",
    "chartType": "histogram",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_channel_statistic",
    "dimension": [
        {
            "name": "date_time",
            "group": "month",
            "aliasName": "时间",
            "format":"",
            "注释":"group根据下钻时间粒度作相应变化。按周时，format为Y-w-d。"
        }
    ],
    "measure": [
        {
            "name": "reg_cnts",
            "func": "sum",
            "aliasName": "注册",
            "contrast": ""
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-21"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "agent_id",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏分类。"
        },
        {
            "name": "game_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏。"
        }
    ]
}
```
# 产品分析
## 合作方名称下拉
查询参数：
```json
{
    "id": "product-analysis-0",
    "chartName": "合作方名称下拉",
    "chartType": "simple-table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_pruduct_quality_statistic",
    "dimension": [],
    "measure": [
        {
            "name": "customer_name",
            "func": "distinct",
            "aliasName": "合作方名称",
            "contrast": ""
        }
    ],
    "dashboard": [
        {
            "name": "customer_name",
            "logic": "isnotempty"
        },
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        }
    ]
}
```
## 游戏分类下拉
查询参数：
```json
{
    "id": "product-analysis-10",
    "chartName": "游戏分类下拉",
    "chartType": "simple-table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_pruduct_quality_statistic",
    "dimension": [],
    "measure": [
        {
            "name": "game_category_name",
            "func": "distinct",
            "aliasName": "游戏分类",
            "contrast": ""
        }
    ],
    "dashboard": [
        {
            "name": "game_category_name",
            "logic": "isnotempty"
        },
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "customer_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作方名称。"
        }
    ]
}
```
## 游戏下拉
查询参数：
```json
{
    "id": "product-analysis-15",
    "chartName": "游戏下拉",
    "chartType": "simple-table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_pruduct_quality_statistic",
    "dimension": [],
    "measure": [
        {
            "name": "game_name",
            "func": "distinct",
            "aliasName": "游戏",
            "contrast": ""
        }
    ],
    "dashboard": [
        {
            "name": "game_name",
            "logic": "isnotempty"
        },
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "customer_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作方名称。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏分类。"
        }
    ]
}
```
## 游戏盘面
说明：
```
左上角切换为成本分布，度量改为：
{
    "name": "sum(estimate_cost)",
    "func": "",
    "aliasName": "成本",
    "contrast": "",
    "decimal": 2,
    "注释": "预估值，name为：sum(estimate_cost)。实际值，name为：sum(real_cost)"
}
```
查询参数：
```json
{
    "id": "product-analysis-20",
    "chartName": "游戏盘面",
    "chartType": "histogram",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_pruduct_quality_statistic",
    "dimension": [
        {
            "name": "game_name",
            "group": "",
            "aliasName": "游戏"
        }
    ],
    "measure": [
        {
            "name": "sum(estimate_income)",
            "func": "",
            "aliasName": "收入",
            "contrast": "",
            "decimal": 2,
            "注释": "预估值，name为：sum(estimate_income) 。实际值，name为：sum(real_income)"
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-01","2020-09-01"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "customer_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作方名称。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏分类。"
        }
    ]
}
```
## 游戏分布-游戏名称下拉
查询参数：
```json
{
    "id": "product-analysis-25",
    "chartName": "游戏名称-游戏code下拉",
    "chartType": "simple-table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_pruduct_quality_statistic",
    "dimension": [],
    "measure": [
        {
            "name": "concat(game_name, '--' ,game_code)",
            "func": "distinct",
            "aliasName": "游戏",
            "contrast": ""
        }
    ],
    "dashboard": [
        {
            "name": "game_name",
            "logic": "isnotempty"
        },
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "customer_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作方名称。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏分类。"
        }
    ]
}
```
## 游戏分布
说明：
```
 前端使用"treeNode"中的数据绘制树。
 增加roiType参数。1：分成前roi；2：分成前roi；
```
查询参数：
```json
{
    "id": "product-analysis-30",
    "chartName": "游戏分布",
    "chartType": "histogram",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_pruduct_quality_statistic",
    "withRollup": 1,
    "rowFormat": 1,
    "dimension": [
        {
            "name": "customer_name",
            "group": "",
            "aliasName": "合作方"
        },
        {
            "name": "game_name",
            "group": "",
            "aliasName": "游戏"
        },
        {
            "name": "agent_id",
            "group": "",
            "aliasName": "渠道"
        }
    ],
    "measure": [
        {
            "name": "estimate_cp_cost",
            "func": "sum",
            "aliasName": "cp成本",
            "contrast": "",
            "decimal": 2,
            "注释": "预估值，name为：estimate_cp_cost。实际值，name为：real_cp_cost"
        },
        {
            "name": "estimate_channel_cost",
            "func": "sum",
            "aliasName": "渠道成本",
            "contrast": "",
            "decimal": 2,
            "注释": "预估值，name为：estimate_channel_cost。实际值，name为：real_channel_cost"
        },
        {
            "name": "estimate_other_cost",
            "func": "sum",
            "aliasName": "E币券成本",
            "contrast": "",
            "decimal": 2,
            "注释": "预估值，name为：estimate_other_cost。实际值，name为：real_other_cost"
        },
        {
            "name": "estimate_income",
            "func": "sum",
            "aliasName": "收入",
            "contrast": "",
            "decimal": 2,
            "注释": "预估值，name为：estimate_income 。实际值，name为：real_income"
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-01","2020-09-01"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "customer_name",
            "logic": "eq",
            "value": "合作方名称",
            "func": "",
            "注释": "全局筛选，合作方名称。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏分类。"
        },
        {
            "name": "game_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "游戏"
        }
    ]
}
```
## ROI排行榜
查询参数：
```json
{
    "id": "product-analysis-40",
    "chartName": "ROI排行榜",
    "chartType": "table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_pruduct_quality_statistic",
    "dimension": [
        {
            "name": "game_name",
            "group": "",
            "aliasName": "游戏"
        }
    ],
    "measure": [
        {
            "name": "if(sum(estimate_cost)=0, 0/0, sum(estimate_income) / sum(estimate_cost))",
            "func": "",
            "aliasName": "ROI",
            "contrast": "",
            "decimal": 2,
            "order": -1,
            "注释": "预估值，name为：if(sum(estimate_cost)=0, 0/0, sum(estimate_income) / sum(estimate_cost))。实际值，name为：if(sum(real_cost)=0,0/0,sum(real_income) / sum(real_cost))。"
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-01","2020-09-01"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "customer_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作方名称。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏分类。"
        }
    ]
}
```
## 产品数据明细
查询参数：
```json
{
    "id": "product-analysis-50",
    "chartName": "产品数据明细",
    "chartType": "table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_pruduct_quality_statistic",
    "limit": 5000,
    "dimension": [
         {
            "name": "date_time",
            "group": "month",
            "aliasName": "时间",
            "注释":"汇总时去掉该维度。按年、按月、按日切换时，调整对应的group"
        },
        {
            "name": "customer_name",
            "group": "",
            "aliasName": "合作方名称"
        },
        {
            "name": "cooperate_type",
            "group": "",
            "aliasName": "合作方式"
        }
    ],
    "measure": [
        {
            "name": "reg_cnts",
            "func": "sum",
            "aliasName": "注册",
            "contrast": "",
            "percentOfMax" : 1,
            "order": -1,
            "注释": "汇总的时候order为-1，非汇总时order不传值或传null"
        },
        {
            "name": "active_array",
            "func": "uniqArray",
            "aliasName": "活跃",
            "contrast": ""
        },
        {
            "name": "pay_array",
            "func": "uniqArray",
            "aliasName": "付费用户",
            "contrast": ""
        },
        {
            "name": "rmb_income",
            "func": "sum",
            "aliasName": "RMB充值",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "estimate_income",
            "func": "sum",
            "aliasName": "预估收入",
            "contrast": "",
            "decimal": 2,
            "percentOfMax" : 1
        },
        {
            "name": "estimate_cp_cost",
            "func": "sum",
            "aliasName": "预估CP成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "estimate_channel_cost",
            "func": "sum",
            "aliasName": "预估渠道成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "estimate_other_cost",
            "func": "sum",
            "aliasName": "预估E币券成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "estimate_cost",
            "func": "sum",
            "aliasName": "预估成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(estimate_income)/sum(estimate_cost)",
            "func": "",
            "aliasName": "预估ROI",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(estimate_income) / uniqArrayIf(`active_array`, notEmpty(`active_array`))",
            "func": "",
            "aliasName": "预估ARPU",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(estimate_income) / uniqArrayIf(`pay_array`, notEmpty(`pay_array`))",
            "func": "",
            "aliasName": "预估ARPPU",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "real_income",
            "func": "sum",
            "aliasName": "实际收入",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "real_cp_cost",
            "func": "sum",
            "aliasName": "实际CP成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "real_channel_cost",
            "func": "sum",
            "aliasName": "实际渠道成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "real_other_cost",
            "func": "sum",
            "aliasName": "实际E币券成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "real_cost",
            "func": "sum",
            "aliasName": "实际成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(real_income) / sum(real_cost)",
            "func": "",
            "aliasName": "实际ROI",
            "contrast": "",
            "decimal": 2
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-01","2020-09-01"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "customer_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作方名称。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏分类。"
        }
    ]
}
```
## 产品数据明细-展开游戏类型
查询参数：
```json
{
    "id": "product-analysis-60",
    "chartName": "产品数据明细",
    "chartType": "table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_pruduct_quality_statistic",
    "limit": 5000,
    "dimension": [
        {
            "name": "game_category_name",
            "group": "",
            "aliasName": "游戏类型"
        }
    ],
    "measure": [
        {
            "name": "reg_cnts",
            "func": "sum",
            "aliasName": "注册",
            "contrast": ""
        },
        {
            "name": "active_array",
            "func": "uniqArray",
            "aliasName": "活跃",
            "contrast": ""
        },
        {
            "name": "pay_array",
            "func": "uniqArray",
            "aliasName": "付费用户",
            "contrast": ""
        },
        {
            "name": "rmb_income",
            "func": "sum",
            "aliasName": "RMB充值",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "estimate_income",
            "func": "sum",
            "aliasName": "预估收入",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "estimate_cp_cost",
            "func": "sum",
            "aliasName": "预估CP成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "estimate_channel_cost",
            "func": "sum",
            "aliasName": "预估渠道成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "estimate_other_cost",
            "func": "sum",
            "aliasName": "预估E币券成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "estimate_cost",
            "func": "sum",
            "aliasName": "预估成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(estimate_income)/sum(estimate_cost)",
            "func": "",
            "aliasName": "预估ROI",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(estimate_income) / uniqArrayIf(`active_array`, notEmpty(`active_array`))",
            "func": "",
            "aliasName": "预估ARPU",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(estimate_income) / uniqArrayIf(`pay_array`, notEmpty(`pay_array`))",
            "func": "",
            "aliasName": "预估ARPPU",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "real_income",
            "func": "sum",
            "aliasName": "实际收入",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "real_cp_cost",
            "func": "sum",
            "aliasName": "实际CP成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "real_channel_cost",
            "func": "sum",
            "aliasName": "实际渠道成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "real_other_cost",
            "func": "sum",
            "aliasName": "实际E币券成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "real_cost",
            "func": "sum",
            "aliasName": "实际成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(real_income) / sum(real_cost)",
            "func": "",
            "aliasName": "实际ROI",
            "contrast": "",
            "decimal": 2
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "eq",
            "value": "",
            "func": "day",
            "注释": "所在行时间。如果是汇总则没有时间条件。"
        },
        {
            "name": "customer_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "所在行合作方名称。"
        },
        {
            "name": "cooperate_type",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "所在行合作方式。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏分类。"
        }
    ]
}
```
## 产品数据明细-点击游戏类型
查询参数：
```json
{
    "id": "product-analysis-70",
    "chartName": "产品数据明细",
    "chartType": "table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_pruduct_quality_statistic",
    "limit": 5000,
    "dimension": [
         {
            "name": "date_time",
            "group": "month",
            "aliasName": "时间",
            "注释":"汇总时去掉该维度。按年、按月、按日切换时，调整对应的group"
        },
        {
            "name": "customer_name",
            "group": "",
            "aliasName": "合作方名称"
        },
        {
            "name": "cooperate_type",
            "group": "",
            "aliasName": "合作方式"
        },
        {
            "name": "game_category_name",
            "group": "",
            "aliasName": "游戏类型"
        },
        {
            "name": "game_name",
            "group": "",
            "aliasName": "游戏名称"
        }
    ],
    "measure": [
        {
            "name": "reg_cnts",
            "func": "sum",
            "aliasName": "注册",
            "contrast": "",
            "percentOfMax" : 1
        },
        {
            "name": "active_array",
            "func": "uniqArray",
            "aliasName": "活跃",
            "contrast": ""
        },
        {
            "name": "pay_array",
            "func": "uniqArray",
            "aliasName": "付费用户",
            "contrast": ""
        },
        {
            "name": "rmb_income",
            "func": "sum",
            "aliasName": "RMB充值",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "estimate_income",
            "func": "sum",
            "aliasName": "预估收入",
            "contrast": "",
            "decimal": 2,
            "percentOfMax" : 1
        },
        {
            "name": "estimate_cp_cost",
            "func": "sum",
            "aliasName": "预估CP成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "estimate_channel_cost",
            "func": "sum",
            "aliasName": "预估渠道成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "estimate_other_cost",
            "func": "sum",
            "aliasName": "预估E币券成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "estimate_cost",
            "func": "sum",
            "aliasName": "预估成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(estimate_income)/sum(estimate_cost)",
            "func": "",
            "aliasName": "预估ROI",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(estimate_income) / uniqArrayIf(`active_array`, notEmpty(`active_array`))",
            "func": "",
            "aliasName": "预估ARPU",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(estimate_income) / uniqArrayIf(`pay_array`, notEmpty(`pay_array`))",
            "func": "",
            "aliasName": "预估ARPPU",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "real_income",
            "func": "sum",
            "aliasName": "实际收入",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "real_cp_cost",
            "func": "sum",
            "aliasName": "实际CP成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "real_channel_cost",
            "func": "sum",
            "aliasName": "实际渠道成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "real_other_cost",
            "func": "sum",
            "aliasName": "实际E币券成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "real_cost",
            "func": "sum",
            "aliasName": "实际成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(real_income) / sum(real_cost)",
            "func": "",
            "aliasName": "实际ROI",
            "contrast": "",
            "decimal": 2
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-01","2020-09-01"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "customer_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "所在行合作方名称。"
        },
        {
            "name": "cooperate_type",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "所在行合作方式。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "点击的游戏分类。如果是全部则不传值。"
        }
    ]
}
```
# 渠道分析
## 计费方式下拉
查询参数：
```json
{
    "id": "channel-analysis-5",
    "chartName": "渠道下拉",
    "chartType": "simple-table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_channel_statistic",
    "dimension": [],
    "measure": [
        {
            "name": "billing_type",
            "func": "distinct",
            "aliasName": "计费方式",
            "contrast": ""
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        }
    ]
}
```
## 渠道下拉
查询参数：
```json
{
    "id": "channel-analysis-0",
    "chartName": "渠道下拉",
    "chartType": "simple-table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_channel_statistic",
    "dimension": [],
    "measure": [
        {
            "name": "agent_id",
            "func": "distinct",
            "aliasName": "渠道",
            "contrast": ""
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "billing_type",
            "logic": "in",
            "value": [""],
            "func": "",
            "注释": "全局筛选，计费方式。"
        }
    ]
}
```
## 游戏分类下拉
查询参数：
```json
{
    "id": "channel-analysis-10",
    "chartName": "游戏分类下拉",
    "chartType": "simple-table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_channel_statistic",
    "dimension": [],
    "measure": [
        {
            "name": "game_category_name",
            "func": "distinct",
            "aliasName": "游戏分类",
            "contrast": ""
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "agent_id",
            "logic": "in",
            "value": [""],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "billing_type",
            "logic": "in",
            "value": [""],
            "func": "",
            "注释": "全局筛选，计费方式。"
        }
    ]
}
```
## 游戏下拉
查询参数：
```json
{
    "id": "channel-analysis-20",
    "chartName": "游戏下拉",
    "chartType": "simple-table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_channel_statistic",
    "dimension": [],
    "measure": [
        {
            "name": "game_name",
            "func": "distinct",
            "aliasName": "游戏",
            "contrast": ""
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "agent_id",
            "logic": "in",
            "value": [""],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏分类。"
        },
        {
            "name": "billing_type",
            "logic": "in",
            "value": [""],
            "func": "",
            "注释": "全局筛选，计费方式。"
        }
    ]
}
```
## 推广位下拉
查询参数：
```json
{
    "id": "channel-analysis-25",
    "chartName": "推广位下拉",
    "chartType": "simple-table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_channel_statistic",
    "dimension": [],
    "measure": [
        {
            "name": "position",
            "func": "distinct",
            "aliasName": "推广位",
            "contrast": ""
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "agent_id",
            "logic": "in",
            "value": [""],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "billing_type",
            "logic": "in",
            "value": [""],
            "func": "",
            "注释": "全局筛选，计费方式。"
        }
    ]
}
```
## 渠道质量指数
说明
```
前五个度量用来制作雷达图，最后一个度量用来制作右下角的列表。
```
查询参数：
```json
{
    "id": "channel-analysis-30",
    "chartName": "渠道质量指数",
    "chartType": "radar",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_channel_statistic",
    "dimension": [
        {
            "name": "agent_id",
            "group": "",
            "aliasName": "渠道"
        }
    ],
    "measure": [
        {
            "name": "reg_cnts",
            "func": "sum",
            "aliasName": "用户贡献",
            "contrast": ""
        },
        {
            "name": "active_array",
            "func": "uniqArray",
            "aliasName": "活跃用户",
            "contrast": ""
        },
        {
            "name": "estimate_income",
            "func": "sum",
            "aliasName": "收入贡献",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "estimate_cost",
            "func": "sum",
            "aliasName": "成本贡献",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "pay_cnts",
            "func": "sum",
            "aliasName": "付费频次",
            "contrast": ""
        },
        {
            "name": "sum(estimate_income) / sum(estimate_cost)",
            "func": "",
            "aliasName": "ROI",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(score)/count(distinct date_time)",
            "func": "",
            "aliasName": "综合评分",
            "contrast": "",
            "decimal": 0,
            "order": -1
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "agent_id",
            "logic": "in",
            "value": [""],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "billing_type",
            "logic": "in",
            "value": [""],
            "func": "",
            "注释": "全局筛选，计费方式。"
        }
    ]
}
```
## 各渠道收入百分比累计图
说明：
```
利润-实际值对应的度量为：
{
    "name": "real_profit",
    "func": "sum",
    "aliasName": "利润",
    "contrast": "",
    "decimal": 2,
    "order":-1
}
利润-预估值对应的度量为：
{
    "name": "estimate_profit",
    "func": "sum",
    "aliasName": "利润",
    "contrast": "",
    "decimal": 2,
    "order":-1
}
收入-实际值对应的度量为：
{
    "name": "real_income",
    "func": "sum",
    "aliasName": "收入",
    "contrast": "",
    "decimal": 2,
    "order":-1
}
收入-预估值对应的度量为：
{
    "name": "estimate_income",
    "func": "sum",
    "aliasName": "收入",
    "contrast": "",
    "decimal": 2,
    "order":-1
}
成本-实际值对应的度量为：
{
    "name": "real_cost",
    "func": "sum",
    "aliasName": "成本",
    "contrast": "",
    "decimal": 2,
    "order":-1
}
成本-预估值对应的度量为：
{
    "name": "estimate_cost",
    "func": "sum",
    "aliasName": "成本",
    "contrast": "",
    "decimal": 2,
    "order":-1
}
ROI-实际值对应的度量为：
{
    "name": "sum(real_income)/sum(real_cost)",
    "func": "",
    "aliasName": "ROI",
    "contrast": "",
    "decimal": 2,
    "order":-1,
    "maxvalue":9007199254740991
}
ROI-预估值对应的度量为：
{
    "name": "sum(estimate_income)/sum(estimate_cost)",
    "func": "",
    "aliasName": "ROI",
    "contrast": "",
    "decimal": 2,
    "order":-1,
    "maxvalue":9007199254740991
}
```
查询参数：
```json
{
    "id": "channel-analysis-40",
    "chartName": "各渠道收入百分比累计图",
    "chartType": "histogram",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_channel_statistic",
    "dimension": [
        {
            "name": "agent_id",
            "group": "",
            "aliasName": "渠道"
        }
    ],
    "measure": [
        {
            "name": "real_profit",
            "func": "sum",
            "aliasName": "利润",
            "contrast": "",
            "decimal": 2,
            "order":-1
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "agent_id",
            "logic": "in",
            "value": [""],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "billing_type",
            "logic": "in",
            "value": [""],
            "func": "",
            "注释": "全局筛选，计费方式。"
        }
    ],
    "screen": [
        {
            "name": "game_category_name",
            "logic": "in",
            "value": ["手机游戏"],
            "func": "",
            "注释": "图内筛选，游戏分类。"
        },
        {
            "name": "game_name",
            "logic": "in",
            "value": ["王者荣耀","qq飞车"],
            "func": "",
            "注释": "图内筛选，游戏。"
        }
    ]
}
```
## 各渠道贡献度
说明：
前端使用"sankeyNodeList"中的数据绘制桑基图
```
左上角切换到预估收入贡献度时，第一个度量改为：
{
    "name": "estimate_income",
    "func": "sum",
    "aliasName": "预估收入",
    "decimal": 2,
    "contrast": "",
    "order": -1
}
左上角切换到用户贡献度时，第一个度量改为：
{
    "name": "reg_cnts",
    "func": "sum",
    "aliasName": "用户贡献",
    "contrast": "",
    "order": -1
}
```
查询参数：
```json
{
    "id": "channel-analysis-50",
    "chartName": "各渠道贡献度",
    "chartType": "histogram",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_channel_statistic",
    "withRollup": 1,
     "rowFormat": 1,
    "dimension": [
        {
            "name": "user_type",
            "group": "",
            "aliasName": "用户类型"
        },
        {
            "name": "agent_id",
            "group": "",
            "aliasName": "渠道"
        },
        {
            "name": "channel_id",
            "group": "",
            "aliasName": "子渠道"
        },
        {
            "name": "cid_id",
            "group": "",
            "aliasName": "活动策略"
        }
    ],
    "measure": [
        {
            "name": "real_income",
            "func": "sum",
            "aliasName": "实际收入",
            "contrast": "",
            "decimal": 2,
            "order": -1
        },
        {
            "name": "arraySum(arrayMap(x -> x.2, arrayDistinct(groupArray(tuple(date_time, valid_pc)))))/count(distinct date_time)",
            "func": "",
            "aliasName": "有效pc",
            "contrast": "",
            "decimal": 0
        },
        {
            "name": "arraySum(arrayMap(x -> x.2, arrayDistinct(groupArray(tuple(date_time, start_pc)))))/count(distinct date_time)",
            "func": "",
            "aliasName": "启动pc",
            "contrast": "",
            "decimal": 0
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "agent_id",
            "logic": "in",
            "value": [""],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "billing_type",
            "logic": "in",
            "value": [""],
            "func": "",
            "注释": "全局筛选，计费方式。"
        }
    ],
    "screen": [
        {
            "name": "game_category_name",
            "logic": "in",
            "value": ["手机游戏"],
            "func": "",
            "注释": "图内筛选，游戏分类。"
        },
        {
            "name": "game_name",
            "logic": "in",
            "value": ["王者荣耀","qq飞车"],
            "func": "",
            "注释": "图内筛选，游戏。"
        }
    ]
}
```
## 各渠道贡献度-趋势
查询参数：
```json
{
    "id": "channel-analysis-60",
    "chartName": "趋势",
    "chartType": "line",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_channel_statistic",
    "dimension": [
        {
            "name": "date_time",
            "group": "day",
            "aliasName": "时间"
        }
    ],
    "measure": [
        {
            "name": "valid_pc",
            "func": "max",
            "aliasName": "有效pc",
            "contrast": ""
        },
        {
            "name": "start_pc",
            "func": "max",
            "aliasName": "启动pc",
            "contrast": ""
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "agent_id",
            "logic": "in",
            "value": [""],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "billing_type",
            "logic": "in",
            "value": [""],
            "func": "",
            "注释": "全局筛选，计费方式。"
        }
    ],
    "screen": [
        {
            "name": "game_category_name",
            "logic": "in",
            "value": ["手机游戏"],
            "func": "",
            "注释": "图内筛选，游戏分类。"
        },
        {
            "name": "game_name",
            "logic": "in",
            "value": ["王者荣耀","qq飞车"],
            "func": "",
            "注释": "图内筛选，游戏。"
        },
        {
            "name": "cid_id",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "图内筛选，活动策略。"
        }
    ]
}
```
## 渠道数据明细
查询参数：
```json
{
    "id": "channel-analysis-70",
    "chartName": "渠道数据明细",
    "chartType": "table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_channel_statistic",
    "limit": 5000,
    "dimension": [
         {
            "name": "date_time",
            "group": "month",
            "aliasName": "时间",
            "注释":"汇总时去掉该维度。按年、按月、按日切换时，调整对应的group"
        },
        {
            "name": "agent_id",
            "group": "",
            "aliasName": "渠道"
        },
        {
            "name": "billing_type",
            "group": "",
            "aliasName": "计费方式"
        }
    ],
    "measure": [
        {
            "name": "reg_cnts",
            "func": "sum",
            "aliasName": "注册",
            "contrast": "",
            "percentOfMax" : 1,
            "order": -1,
            "注释": "汇总的时候order为-1，非汇总时order不传值或传null"
        },
        {
            "name": "active_array",
            "func": "uniqArray",
            "aliasName": "活跃",
            "contrast": ""
        },
        {
            "name": "pay_array",
            "func": "uniqArray",
            "aliasName": "付费用户",
            "contrast": ""
        },
        {
            "name": "estimate_income",
            "func": "sum",
            "aliasName": "预估收入",
            "contrast": "",
            "decimal": 2,
            "percentOfMax" : 1
        },
        {
            "name": "estimate_channel_cost",
            "func": "sum",
            "aliasName": "预估渠道成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "estimate_other_cost",
            "func": "sum",
            "aliasName": "预估E币券成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(estimate_income)/(sum(estimate_channel_cost)+sum(estimate_other_cost))",
            "func": "",
            "aliasName": "预估分成前ROI",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(estimate_income)/(sum(estimate_cp_cost)+sum(estimate_channel_cost)+sum(estimate_other_cost))",
            "func": "",
            "aliasName": "预估分成后ROI",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(estimate_income) / uniqArrayIf(`active_array`, notEmpty(`active_array`))",
            "func": "",
            "aliasName": "预估ARPU",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(estimate_income) / uniqArrayIf(`pay_array`, notEmpty(`pay_array`))",
            "func": "",
            "aliasName": "预估ARPPU",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "real_income",
            "func": "sum",
            "aliasName": "实际收入",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "real_channel_cost",
            "func": "sum",
            "aliasName": "实际渠道成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "real_other_cost",
            "func": "sum",
            "aliasName": "实际E币券成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(real_income) / (sum(real_channel_cost)+sum(real_other_cost))",
            "func": "",
            "aliasName": "实际分成前ROI",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(real_income) / (sum(real_cp_cost)+sum(real_channel_cost)+sum(real_other_cost))",
            "func": "",
            "aliasName": "实际分成后ROI",
            "contrast": "",
            "decimal": 2
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "agent_id",
            "logic": "in",
            "value": [""],
            "func": "",
            "注释": "全局筛选，渠道。"
        },
        {
            "name": "position",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "图内筛选，推广位。"
        },
        {
            "name": "billing_type",
            "logic": "in",
            "value": [""],
            "func": "",
            "注释": "全局筛选，计费方式。"
        }
    ]
}
```
## 渠道数据明细-展开推广位
查询参数：
```json
{
    "id": "channel-analysis-80",
    "chartName": "渠道数据明细",
    "chartType": "table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_channel_statistic",
    "limit": 5000,
    "dimension": [
        {
            "name": "position",
            "group": "",
            "aliasName": "推广位"
        }
    ],
    "measure": [
        {
            "name": "reg_cnts",
            "func": "sum",
            "aliasName": "注册",
            "contrast": ""
        },
        {
            "name": "active_array",
            "func": "uniqArray",
            "aliasName": "活跃",
            "contrast": ""
        },
        {
            "name": "pay_array",
            "func": "uniqArray",
            "aliasName": "付费用户",
            "contrast": ""
        },
        {
            "name": "estimate_income",
            "func": "sum",
            "aliasName": "预估收入",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "estimate_channel_cost",
            "func": "sum",
            "aliasName": "预估渠道成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "estimate_other_cost",
            "func": "sum",
            "aliasName": "预估E币券成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(estimate_income)/(sum(estimate_channel_cost)+sum(estimate_other_cost))",
            "func": "",
            "aliasName": "预估分成前ROI",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(estimate_income)/(sum(estimate_cp_cost)+sum(estimate_channel_cost)+sum(estimate_other_cost))",
            "func": "",
            "aliasName": "预估分成后ROI",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(estimate_income) / uniqArrayIf(`active_array`, notEmpty(`active_array`))",
            "func": "",
            "aliasName": "预估ARPU",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(estimate_income) / uniqArrayIf(`pay_array`, notEmpty(`pay_array`))",
            "func": "",
            "aliasName": "预估ARPPU",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "real_income",
            "func": "sum",
            "aliasName": "实际收入",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "real_channel_cost",
            "func": "sum",
            "aliasName": "实际渠道成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "real_other_cost",
            "func": "sum",
            "aliasName": "实际E币券成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(real_income) / (sum(real_channel_cost)+sum(real_other_cost))",
            "func": "",
            "aliasName": "实际分成前ROI",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(real_income) / (sum(real_cp_cost)+sum(real_channel_cost)+sum(real_other_cost))",
            "func": "",
            "aliasName": "实际分成后ROI",
            "contrast": "",
            "decimal": 2
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "eq",
            "value": "",
            "func": "day",
            "注释": "所在行时间。如果是汇总则没有时间条件。"
        },
        {
            "name": "agent_id",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "所在行渠道。"
        },
        {
            "name": "billing_type",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "所在行计费方式。"
        },
        {
            "name": "position",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "图内筛选，推广位。"
        }
    ]
}
```
## 渠道数据明细-点击推广位
查询参数：
```json
{
    "id": "channel-analysis-90",
    "chartName": "渠道数据明细",
    "chartType": "table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_channel_statistic",
    "limit": 5000,
    "dimension": [
         {
            "name": "date_time",
            "group": "month",
            "aliasName": "时间",
            "注释":"汇总时去掉该维度。按年、按月、按日切换时，调整对应的group"
        },
        {
            "name": "agent_id",
            "group": "",
            "aliasName": "渠道"
        },
        {
            "name": "billing_type",
            "group": "",
            "aliasName": "计费方式"
        },
        {
            "name": "position",
            "group": "",
            "aliasName": "推广位"
        },
        {
            "name": "game_category_name",
            "group": "",
            "aliasName": "游戏类型"
        },
        {
            "name": "game_name",
            "group": "",
            "aliasName": "游戏名称"
        }
    ],
    "measure": [
        {
            "name": "reg_cnts",
            "func": "sum",
            "aliasName": "注册",
            "contrast": "",
            "percentOfMax" : 1
        },
        {
            "name": "active_array",
            "func": "uniqArray",
            "aliasName": "活跃",
            "contrast": ""
        },
        {
            "name": "pay_array",
            "func": "uniqArray",
            "aliasName": "付费用户",
            "contrast": ""
        },
        {
            "name": "estimate_income",
            "func": "sum",
            "aliasName": "预估收入",
            "contrast": "",
            "decimal": 2,
            "percentOfMax" : 1
        },
        {
            "name": "estimate_channel_cost",
            "func": "sum",
            "aliasName": "预估渠道成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "estimate_other_cost",
            "func": "sum",
            "aliasName": "预估E币券成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(estimate_income)/(sum(estimate_channel_cost)+sum(estimate_other_cost))",
            "func": "",
            "aliasName": "预估分成前ROI",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(estimate_income)/(sum(estimate_cp_cost)+sum(estimate_channel_cost)+sum(estimate_other_cost))",
            "func": "",
            "aliasName": "预估分成后ROI",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(estimate_income) / uniqArrayIf(`active_array`, notEmpty(`active_array`))",
            "func": "",
            "aliasName": "预估ARPU",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(estimate_income) / uniqArrayIf(`pay_array`, notEmpty(`pay_array`))",
            "func": "",
            "aliasName": "预估ARPPU",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "real_income",
            "func": "sum",
            "aliasName": "实际收入",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "real_channel_cost",
            "func": "sum",
            "aliasName": "实际渠道成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "real_other_cost",
            "func": "sum",
            "aliasName": "实际E币券成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(real_income) / (sum(real_channel_cost)+sum(real_other_cost))",
            "func": "",
            "aliasName": "实际分成前ROI",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(real_income) / (sum(real_cp_cost)+sum(real_channel_cost)+sum(real_other_cost))",
            "func": "",
            "aliasName": "实际分成后ROI",
            "contrast": "",
            "decimal": 2
        }
    ],
    "dashboard": [
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "agent_id",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "所在行渠道。"
        },
        {
            "name": "billing_type",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "所在行计费方式。"
        },
        {
            "name": "position",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "点击的推广位。如果是全部则不传值。"
        }
    ]
}
```
# 渠道分析V2
## 渠道分析全局筛选-一级分类下拉
查询参数：
```json
{
    "id": "channel-analysis-v2-common-0",
    "chartName": "一级分类下拉",
    "chartType": "simple-table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_channel_statistic",
    "dimension": [],
    "measure": [
        {
            "name": "category1",
            "func": "distinct",
            "aliasName": "一级业务类别",
            "contrast": ""
        }
    ],
    "dashboard": [
        {
            "name": "category1",
            "logic": "isnotempty",
            "注释": "硬编码"
        },
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "customer_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作CP方。"
        },
        {
            "name": "agent_id",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作渠道。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏类型。"
        },
        {
            "name": "game_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏名称。"
        },
        {
            "name": "game_code",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏CODE。"
        }
    ]
}
```
## 渠道分析全局筛选-二级分类下拉
查询参数：
```json
{
    "id": "channel-analysis-v2-common-10",
    "chartName": "二级分类下拉",
    "chartType": "simple-table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_channel_statistic",
    "dimension": [],
    "measure": [
        {
            "name": "category2",
            "func": "distinct",
            "aliasName": "二级业务类别",
            "contrast": ""
        }
    ],
    "dashboard": [
        {
            "name": "category2",
            "logic": "isnotempty",
            "注释": "硬编码"
        },
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "category1",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，一级业务类别。"
        },
        {
            "name": "customer_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作CP方。"
        },
        {
            "name": "agent_id",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作渠道。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏类型。"
        },
        {
            "name": "game_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏名称。"
        },
        {
            "name": "game_code",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏CODE。"
        }
    ]
}
```
## 渠道分析全局筛选-三级分类下拉
查询参数：
```json
{
    "id": "channel-analysis-v2-common-20",
    "chartName": "三级分类下拉",
    "chartType": "simple-table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_channel_statistic",
    "dimension": [],
    "measure": [
        {
            "name": "category3",
            "func": "distinct",
            "aliasName": "三级业务类别",
            "contrast": ""
        }
    ],
    "dashboard": [
        {
            "name": "category3",
            "logic": "isnotempty",
            "注释": "硬编码"
        },
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "category1",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，一级业务类别。"
        },
        {
            "name": "category2",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，二级业务类别。"
        },
        {
            "name": "customer_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作CP方。"
        },
        {
            "name": "agent_id",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作渠道。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏类型。"
        },
        {
            "name": "game_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏名称。"
        },
        {
            "name": "game_code",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏CODE。"
        }
    ]
}
```
## 渠道分析全局筛选-合作CP方
查询参数：
```json
{
    "id": "channel-analysis-v2-common-30",
    "chartName": "合作CP方",
    "chartType": "simple-table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_channel_statistic",
    "dimension": [],
    "measure": [
        {
            "name": "customer_name",
            "func": "distinct",
            "aliasName": "合作CP方",
            "contrast": ""
        }
    ],
    "dashboard": [
        {
            "name": "customer_name",
            "logic": "isnotempty",
            "注释": "硬编码"
        },
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "category1",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，一级业务类别。"
        },
        {
            "name": "category2",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，二级业务类别。"
        },
        {
            "name": "category3",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，三级业务类别。"
        },
        {
            "name": "agent_id",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作渠道。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏类型。"
        },
        {
            "name": "game_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏名称。"
        },
        {
            "name": "game_code",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏CODE。"
        }
    ]
}
```
## 渠道分析全局筛选-合作渠道
查询参数：
```json
{
    "id": "channel-analysis-v2-common-40",
    "chartName": "合作渠道",
    "chartType": "simple-table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_channel_statistic",
    "dimension": [],
    "measure": [
        {
            "name": "agent_id",
            "func": "distinct",
            "aliasName": "合作渠道",
            "contrast": ""
        }
    ],
    "dashboard": [
        {
            "name": "agent_id",
            "logic": "isnotempty",
            "注释": "硬编码"
        },
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "category1",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，一级业务类别。"
        },
        {
            "name": "category2",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，二级业务类别。"
        },
        {
            "name": "category3",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，三级业务类别。"
        },
        {
            "name": "customer_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作CP方。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏类型。"
        },
        {
            "name": "game_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏名称。"
        },
        {
            "name": "game_code",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏CODE。"
        }
    ]
}
```
## 渠道分析全局筛选-游戏类型
查询参数：
```json
{
    "id": "channel-analysis-v2-common-50",
    "chartName": "游戏类型",
    "chartType": "simple-table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_channel_statistic",
    "dimension": [],
    "measure": [
        {
            "name": "game_category_name",
            "func": "distinct",
            "aliasName": "游戏类型",
            "contrast": ""
        }
    ],
    "dashboard": [
        {
            "name": "game_category_name",
            "logic": "isnotempty",
            "注释": "硬编码"
        },
        {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "category1",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，一级业务类别。"
        },
        {
            "name": "category2",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，二级业务类别。"
        },
        {
            "name": "category3",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，三级业务类别。"
        },
        {
            "name": "customer_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作CP方。"
        },
        {
            "name": "agent_id",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作渠道。"
        },
        {
            "name": "game_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏名称。"
        },
        {
            "name": "game_code",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏CODE。"
        }
    ]
}
```
## 渠道分析全局筛选-游戏名称(游戏CODE)
查询参数：
```json
{
  "id": "channel-analysis-v2-common-60",
  "chartName": "游戏名称-游戏code下拉",
  "chartType": "table",
  "databaseName": "bi_dashboard",
  "tableName": "st_game_operate_channel_statistic",
  "dimension": [
    {
      "name": "game_name",
      "aliasName": "游戏名称"
    },
    {
      "name": "game_code",
      "aliasName": "游戏CODE"
    }
  ],
  "measure": [
  ],
  "dashboard": [
    {
      "name": "game_name",
      "logic": "isnotempty"
    },
    {
      "name": "date_time",
      "logic": "between",
      "value": [
        "2020-01-01",
        "2020-12-31"
      ],
      "func": "day",
      "注释": "全局筛选，时间。"
    },
    {
        "name": "category1",
        "logic": "eq",
        "value": "",
        "func": "",
        "注释": "全局筛选，一级业务类别。"
    },
    {
        "name": "category2",
        "logic": "eq",
        "value": "",
        "func": "",
        "注释": "全局筛选，二级业务类别。"
    },
    {
        "name": "category3",
        "logic": "eq",
        "value": "",
        "func": "",
        "注释": "全局筛选，三级业务类别。"
    },
    {
        "name": "customer_name",
        "logic": "eq",
        "value": "",
        "func": "",
        "注释": "全局筛选，合作CP方。"
    },
    {
        "name": "agent_id",
        "logic": "eq",
        "value": "",
        "func": "",
        "注释": "全局筛选，合作渠道。"
    },
    {
        "name": "game_category_name",
        "logic": "eq",
        "value": "",
        "func": "",
        "注释": "全局筛选，游戏类型。"
    }
  ]
}
```
## 渠道评分-图表
前端默认按质量评分倒序排列  
查询参数：
```json
{
    "id": "channel-analysis-v2-0",
    "chartName": "渠道评分",
    "chartType": "table",
    "databaseName": "bi_dashboard",
    "tableName": "st_game_operate_channel_statistic",
    "dimension": [
        {
            "name": "agent_id",
            "group": "",
            "aliasName": "渠道"
        },
        {
            "name": "multiIf(source_tag=1,'财务', source_tag=2, '业务', '')",
            "group": "",
            "aliasName": "数据来源"
        }
    ],
    "measure": [
        {
            "name": "1",
            "func": "",
            "aliasName": "质量评分",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(multiIf(source_tag=1,real_income,source_tag=2,estimate_income,0))",
            "func": "",
            "aliasName": "收入",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "sum(multiIf(source_tag=1,real_income,source_tag=2,estimate_income,0)) - sum(multiIf(source_tag=1,real_cost,source_tag=2,estimate_cost,0))",
            "func": "",
            "aliasName": "利润",
            "contrast": "",
            "decimal": 2,
            "order": -1
        },
        {
            "name": "sum(multiIf(source_tag=1,real_channel_cost,source_tag=2,estimate_channel_cost,0))",
            "func": "",
            "aliasName": "推广成本",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "(sum(multiIf(source_tag=1,real_income,source_tag=2,estimate_income,0)) - sum(multiIf(source_tag=1,real_cost,source_tag=2,estimate_cost,0))) / sum(multiIf(source_tag=1,real_income,source_tag=2,estimate_income,0))",
            "func": "",
            "aliasName": "利润率",
            "contrast": "",
            "digitDisplay": "percent"
        },
        {
            "name": "sum(pay_money) / uniqExactArrayIf(`pay_array`, notEmpty(`pay_array`))",
            "func": "",
            "aliasName": "ARPPU",
            "contrast": "",
            "decimal": 2
        },
        {
            "name": "reg_cnts",
            "func": "sum",
            "aliasName": "新增",
            "contrast": ""
        },
        {
            "name": "active_array",
            "func": "uniqArray",
            "aliasName": "活跃",
            "contrast": ""
        },
        {
            "name": "LTV30",
            "func": "avg",
            "aliasName": "LTV30",
            "contrast": "",
            "decimal": 2
        }
    ],
    "dashboard": [
         {
            "name": "date_time",
            "logic": "between",
            "value": ["2020-08-19","2020-08-19"],
            "func": "day",
            "注释": "全局筛选，时间。"
        },
        {
            "name": "category1",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，一级业务类别。"
        },
        {
            "name": "category2",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，二级业务类别。"
        },
        {
            "name": "category3",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，三级业务类别。"
        },
        {
            "name": "customer_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作CP方。"
        },
        {
            "name": "agent_id",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，合作渠道。"
        },
        {
            "name": "game_category_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏类型。"
        },
        {
            "name": "game_name",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏名称。"
        },
        {
            "name": "game_code",
            "logic": "eq",
            "value": "",
            "func": "",
            "注释": "全局筛选，游戏CODE。"
        }
    ]
}
```
## 渠道评分-雷达图
和左侧表格的数据一致
## 各渠道贡献度
按收入，度量为：
```json
{
      "name": "real_income",
      "func": "sum",
      "aliasName": "实际收入",
      "contrast": "",
      "decimal": 2,
      "order": -1
    }
```
按利润，度量为：
```json
    {
        "name": "sum(real_income) - sum(real_cost)",
        "func": "",
        "aliasName": "利润",
        "contrast": "",
        "decimal": 2,
        "order": -1
    }
```
按活跃用户，度量为：
```json
    {
        "name": "active_array",
        "func": "uniqArray",
        "aliasName": "活跃用户",
        "contrast": "",
        "order": -1
    }
```
按付费用户，度量为：
```json
    {
        "name": "pay_array",
        "func": "uniqArray",
        "aliasName": "付费用户",
        "contrast": "",
        "order": -1
    }
```
按业务，维度为：
```json
"dimension": [
    {
      "name": "category1"
    },
    {
      "name": "category2"
    },
    {
      "name": "category3"
    },
    {
      "name": "agent_id"
    }
  ]
```
按网吧，维度为：
```json
"dimension": [
    {
      "name": "category1"
    },
    {
      "name": "category2"
    },
    {
      "name": "multiIf(netbar_type=1,'渠道部网吧',netbar_type=2,'非网吧',netbar_type=3,'外部网吧','-')"
    }
  ]
```
按计费，维度为：
```json
"dimension": [
    {
      "name": "category1"
    },
    {
      "name": "category2"
    },
    {
      "name": "billing_type"
    }
  ]
```
按头部渠道，维度为：
```json
"dimension": [
    {
      "name": "agent_id"
    },
    {
      "name": "channel_id"
    },
    {
      "name": "cid_id"
    }
  ]
```

查询参数：
```json
{
  "id": "channel-analysis-v2-20",
  "chartName": "各渠道贡献度",
  "chartType": "histogram",
  "databaseName": "bi_dashboard",
  "tableName": "st_game_operate_channel_statistic",
  "withRollup": 1,
  "rowFormat": 1,
  "dimension": [
    {
      "name": "category1"
    },
    {
      "name": "category2"
    },
    {
      "name": "category3"
    },
    {
      "name": "agent_id"
    }
  ],
  "measure": [
    {
      "name": "real_income",
      "func": "sum",
      "aliasName": "实际收入",
      "contrast": "",
      "decimal": 2,
      "order": -1
    }
  ],
  "dashboard": [
    {
        "name": "date_time",
        "logic": "between",
        "value": ["2020-01-01","2020-12-31"],
        "func": "day",
        "注释": "全局筛选，时间。"
    },
    {
        "name": "category1",
        "logic": "eq",
        "value": "",
        "func": "",
        "注释": "全局筛选，一级业务类别。"
    },
    {
        "name": "category2",
        "logic": "eq",
        "value": "",
        "func": "",
        "注释": "全局筛选，二级业务类别。"
    },
    {
        "name": "category3",
        "logic": "eq",
        "value": "",
        "func": "",
        "注释": "全局筛选，三级业务类别。"
    },
    {
        "name": "customer_name",
        "logic": "eq",
        "value": "",
        "func": "",
        "注释": "全局筛选，合作CP方。"
    },
    {
        "name": "agent_id",
        "logic": "eq",
        "value": "",
        "func": "",
        "注释": "全局筛选，合作渠道。"
    },
    {
        "name": "game_category_name",
        "logic": "eq",
        "value": "",
        "func": "",
        "注释": "全局筛选，游戏类型。"
    },
    {
        "name": "game_name",
        "logic": "eq",
        "value": "",
        "func": "",
        "注释": "全局筛选，游戏名称。"
    },
    {
        "name": "game_code",
        "logic": "eq",
        "value": "",
        "func": "",
        "注释": "全局筛选，游戏CODE。"
    }
  ]
}
```
## 数据明细
查询参数：
```json
{
  "id": "channel-analysis-v2-30",
  "chartName": "各渠道贡献度",
  "chartType": "table",
  "databaseName": "bi_dashboard",
  "tableName": "st_game_operate_channel_statistic",
  "rowFormat": 1,
  "limit":5000,
  "dimension": [
    {
        "name": "date_time",
        "group": "month",
        "aliasName": "时间",
        "注释":"汇总时去掉该维度。按年、按月、按日切换时，调整对应的group"
    },
    {
        "name": "agent_company_name",
        "group": "",
        "aliasName": "公司",
        "注释": "按渠道时去掉此维度"
    }, 
    {
        "name": "agent_id",
        "group": "",
        "aliasName": "渠道",
        "注释": "按公司时去掉此维度"
    },
    {
        "name": "source_tag",
        "group": "",
        "aliasName": "数据来源"
    }
  ],
  "measure": [
    {
      "name": "game_code",
      "func": "count_distinct",
      "aliasName": "合作游戏数",
      "contrast": "",
      "order": -1,
      "注释": "汇总的时候order为-1，非汇总时order不传值或传null"
    },
    {
        "name": "multiIf(source_tag=1,sum(real_income) - sum(real_cost), source_tag=2, sum(estimate_income) - sum(estimate_cost), 0)",
        "func": "",
        "aliasName": "利润",
        "contrast": "",
        "decimal": 2
    },
    {
        "name": "multiIf(source_tag=1,sum(real_income), source_tag=2, sum(estimate_income), 0)",
        "func": "",
        "aliasName": "收入",
        "contrast": "",
        "decimal": 2
    },
    {
        "name": "multiIf(source_tag=1,sum(real_cp_cost), source_tag=2, sum(estimate_cp_cost), 0)",
        "func": "",
        "aliasName": "CP成本",
        "contrast": "",
        "decimal": 2
    },
    {
        "name": "multiIf(source_tag=1,sum(real_channel_cost), source_tag=2, sum(estimate_channel_cost), 0)",
        "func": "",
        "aliasName": "推广成本",
        "contrast": "",
        "decimal": 2
    },
    {
        "name": "multiIf(source_tag=1,(sum(real_income) - sum(real_cost)) / sum(real_income), source_tag=2, (sum(estimate_income) - sum(estimate_cost)) / sum(estimate_income), 0)",
        "func": "",
        "aliasName": "利润率",
        "contrast": "",
        "digitDisplay": "percent"
    },
    {
        "name": "multiIf(source_tag=1,sum(real_income) / sum(real_cost), source_tag=2, sum(estimate_income) / sum(estimate_cost), 0)",
        "func": "",
        "aliasName": "ROI",
        "contrast": "",
        "decimal": 2
    },
    {
        "name": "sum(pay_money) / uniqExactArrayIf(`pay_array`, notEmpty(`pay_array`))",
        "func": "",
        "aliasName": "ARPPU",
        "contrast": "",
        "decimal": 2
    },
    {
        "name": "sum(pay_money) / uniqExactArrayIf(`active_array`, notEmpty(`active_array`))",
        "func": "",
        "aliasName": "ARPU",
        "contrast": "",
        "decimal": 2
    },
    {
        "name": "newlogin_cnts",
        "func": "sum",
        "aliasName": "游戏新增",
        "contrast": ""
    },
    {
        "name": "reg_cnts",
        "func": "sum",
        "aliasName": "注册新增",
        "contrast": ""
    },
    {
        "name": "multiIf(source_tag=1,sum(real_cost) / sum(reg_cnts), source_tag=2, sum(estimate_cost) / sum(reg_cnts), 0)",
        "func": "",
        "aliasName": "获客成本",
        "contrast": "",
        "decimal": 2
    },
    {
        "name": "drate1",
        "func": "avg",
        "aliasName": "次留率",
        "contrast": "",
        "decimal": 2
    },
    {
        "name": "active_array",
        "func": "uniqArray",
        "aliasName": "活跃用户",
        "contrast": ""
    },
    {
        "name": "sum(newpay_users) / sum(newlogin_cnts)",
        "func": "",
        "aliasName": "新付费转化率",
        "contrast": "",
        "decimal": 2
    },
    {
        "name": "newpay_users",
        "func": "sum",
        "aliasName": "新增付费用户",
        "contrast": "",
        "decimal": 2
    },
    {
        "name": "LTV7",
        "func": "avg",
        "aliasName": "LTV7",
        "contrast": "",
        "decimal": 2
    },
    {
        "name": "LTV30",
        "func": "avg",
        "aliasName": "LTV30",
        "contrast": "",
        "decimal": 2
    },
    {
        "name": "multiIf(source_tag=1,'财务', source_tag=2, '业务', '')",
        "func": "",
        "aliasName": "数据来源",
        "contrast": ""
    }
  ],
  "dashboard": [
    {
        "name": "date_time",
        "logic": "between",
        "value": ["2020-01-01","2020-12-31"],
        "func": "day",
        "注释": "全局筛选，时间。"
    },
    {
        "name": "category1",
        "logic": "eq",
        "value": "",
        "func": "",
        "注释": "全局筛选，一级业务类别。"
    },
    {
        "name": "category2",
        "logic": "eq",
        "value": "",
        "func": "",
        "注释": "全局筛选，二级业务类别。"
    },
    {
        "name": "category3",
        "logic": "eq",
        "value": "",
        "func": "",
        "注释": "全局筛选，三级业务类别。"
    },
    {
        "name": "customer_name",
        "logic": "eq",
        "value": "",
        "func": "",
        "注释": "全局筛选，合作CP方。"
    },
    {
        "name": "agent_id",
        "logic": "eq",
        "value": "",
        "func": "",
        "注释": "全局筛选，合作渠道。"
    },
    {
        "name": "game_category_name",
        "logic": "eq",
        "value": "",
        "func": "",
        "注释": "全局筛选，游戏类型。"
    },
    {
        "name": "game_name",
        "logic": "eq",
        "value": "",
        "func": "",
        "注释": "全局筛选，游戏名称。"
    },
    {
        "name": "game_code",
        "logic": "eq",
        "value": "",
        "func": "",
        "注释": "全局筛选，游戏CODE。"
    }
  ]
}
```
