(
SELECT
	toStartOfMonth(_t1.`dt`) as `dt`,
	product_id,
	pid,
	dictGetStringOrDefault(
        'bi_sdk.dict_mysql_dm_product_channel_dim',
        'agent_name',
        (product_id, upper(pid)),
        '未知'
    ) as `agent_name`,
    dictGetStringOrDefault(
        'bi_sdk.dict_mysql_dm_product_channel_dim',
        'channel_name',
        (product_id, upper(pid)),
        '未知'
    ) as `channel_name`,
    dictGetStringOrDefault(
        'bi_sdk.dict_mysql_dm_product_channel_dim',
        'sub_channel_name',
        (product_id, upper(pid)),
        '未知'
    ) as `sub_channel_name`,
    concat(
        dictGetStringOrDefault(
            'bi_sdk.dict_mysql_dm_product_channel_dim',
            'pid_alias',
            (product_id, upper(pid)),
            '未知'
        ),
        '(',
        upper(pid),
        ')'
    ) as `billing_name`,
	os_type,
	os_name,
	dateDiff('month', toStartOfMonth(`dt`), toStartOfMonth(interval_date)) as `time_interval`,
	groupBitmapMergeState(initial_value) as `initial_value`,
	maxMap(`pay_fee`) as `pay_fee`
FROM
	bi_sdk.rt_ads_sdk_ltv_payment_day as _t1
group by
	toStartOfMonth(_t1.`dt`),
	product_id,
	pid,
	os_type,
	os_name,
	toStartOfMonth(interval_date)
)