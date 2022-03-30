(
	SELECT
		toDate(_t1.date) as date,
		maxMap(([xxHash32(concat(toString(_t1.date),product_id,channel_id,toString(os_type),app_version))],[pv])) as pv,
		uniqExactArrayState(session) as session_uniq,
		groupBitmapOrState(bitmapBuild(arrayMap(x->xxHash32(x), session))) as session_bit,
		uniqExactArrayState(session_again) as session_again_uniq,
		groupBitmapOrState(bitmapBuild(arrayMap(x->xxHash32(x), session_again))) as session_again_bit,
		maxMap(([xxHash32(concat(toString(_t1.date),product_id,channel_id,toString(os_type),app_version))],[online_duration])) as online_duration,
		uniqExactArrayState(uv) as uv_uniq,
		groupBitmapOrState(bitmapBuild(arrayMap(x->xxHash32(x), uv))) as uv_bit,
		uniqExactArrayState(uv_first) as uv_first_uniq,
		groupBitmapOrState(bitmapBuild(arrayMap(x->xxHash32(x), uv_first))) as uv_first_bit,
		uniqExactArrayState(uv_with_uid) as uv_with_uid_uniq,
		groupBitmapOrState(bitmapBuild(arrayMap(x->xxHash32(x), uv_with_uid))) as uv_with_uid_bit,
		uniqExactArrayState(uid) as uid_uniq,
		groupBitmapOrState(bitmapBuild(arrayMap(x->xxHash32(x), uid))) as uid_bit,
		uniqExactArrayState(register_user) as register_user_uniq,
		groupBitmapOrState(bitmapBuild(arrayMap(x->xxHash32(x), register_user))) as register_user_bit,
		uniqExactArrayState(pay_user) as pay_user_uniq,
		uniqExactArrayState(pay_user_register_today) as pay_user_register_today_uniq,
		uniqExactArrayState(pay_user_register_before) as pay_user_register_before_uniq,
		uniqExactArrayState(pay_user_first) as pay_user_first_uniq,
		uniqExactArrayState(pay_user_again) as pay_user_again_uniq,
		maxMap((arrayMap(x->xxHash32(x.1), pay_fee),arrayMap(x->x.2, pay_fee))) as pay_fee,
		maxMap((arrayMap(x->xxHash32(x.1), pay_fee_register_today),arrayMap(x->x.2, pay_fee_register_today))) as pay_fee_register_today,
		maxMap((arrayMap(x->xxHash32(x.1), pay_fee_register_before),arrayMap(x->x.2, pay_fee_register_before))) as pay_fee_register_before,
		maxMap((arrayMap(x->xxHash32(x.1), pay_fee_register_yesterday),arrayMap(x->x.2, pay_fee_register_yesterday))) as pay_fee_register_yesterday,
		maxMap((arrayMap(x->xxHash32(x.1), pay_fee_first),arrayMap(x->x.2, pay_fee_first))) as pay_fee_first,
		maxMap((arrayMap(x->xxHash32(x.1), pay_fee_again),arrayMap(x->x.2, pay_fee_again))) as pay_fee_again,
		maxMap((arrayMap(x->xxHash32(x.1), pay_fee_first_day),arrayMap(x->x.2, pay_fee_first_day))) as pay_fee_first_day,
		uniqExactArrayState(success_order_id) as success_order_id_uniq,
		uniqExactArrayState(order_id) as order_id_uniq
	FROM banyan_bi_sdk.sdk_app_web_register_payment_agg_local as _t1
	WHERE
		toHour(_t1.`date`) <= toHour(now())
		and if(toHour(_t1.`date`) = toHour(now()), toMinute(_t1.`date`), 0) <= toMinute(now())
		and toDate(`date`) in (#(dateSql))
		and #(conditionSql)
	GROUP BY toDate(_t1.date)
	) AS _t1 left join
	(
	SELECT
		_t1.date as date,
		groupBitmapOrState(tomorrow_uid_bit) as tomorrow_uid_bit
	FROM banyan_bi_sdk.sdk_app_web_register_payment_agg_day as _t1
	where
		 toDate(`date`) in (#(dateSql))
         and #(conditionSql)
	GROUP BY _t1.date
	) as _t2 using (date)