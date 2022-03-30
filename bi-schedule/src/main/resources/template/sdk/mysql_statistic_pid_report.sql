INSERT into banyan_bi_sdk.mysql_statistic_pid_report
(date,pid,product_code,product_name,ccid,channel_id,channel_name,sub_channel_id,sub_channel_name,charge_rule,uv,reg_count,pay_fee,created_at)
SELECT
	_t1.key1 as `date`,
	_t2.pid,
	_t1.product_code,
	multiIf(product_code = '270200', '比特球云盘', product_code = '380001', '随乐游', '')  as product_name,
    _t2.`ccid`,
	_t2.`channel_id`,
	_t2.`channel_name`,
	_t2.`sub_channel_id`,
	_t2.`sub_channel_name`,
	_t2.`charge_rule`,
	_t1.value1 as uv,
	_t1.value3 as reg_count,
	_t1.value2 as pay_fee,
	now() as created_at
from
	(
	select
		toDate(`date`) as key1,
		product_id as product_code,
        upper(channel_id) as pid,
		bitmapCardinality(
			bitmapAndnot(
				groupBitmapOrState(uv_bit),
				groupBitmapOrState(uv_with_uid_bit)
			)
		) + uniqExactArrayMerge(uid_uniq) as value1,
		arraySum(maxMap(pay_fee).2) as value2,
		uniqExactArrayMerge(register_user_uniq) as value3
	from
		banyan_bi_sdk.sdk_app_web_register_payment_agg_day
	where
		(
			1 = 1
			and `product_id` in ('270200', '380001')
			and toDate(`date`) = '#(date)'
		)
	group by
		toDate(`date`),product_id,upper(channel_id)
	) as _t1
	join banyan_bi_sdk.mysql_dm_channel_promotion_all_view as _t2 on (upper(_t2.product_code) = upper(_t1.product_code) and upper(_t2.pid) = upper(_t1.pid))
where (_t2.product_code = '270200' and _t2.department_code = '01.03') or (_t2.product_code = '380001' and _t2.department_code = '01.03');