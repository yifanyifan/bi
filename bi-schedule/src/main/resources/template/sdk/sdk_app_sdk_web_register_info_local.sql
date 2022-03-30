insert into banyan_bi_sdk.sdk_app_sdk_web_register_info_local
SELECT
	assumeNotNull(date) AS date,
	assumeNotNull(product) AS product,
	assumeNotNull(agent_name) AS agent_name,
	assumeNotNull(channel_name) AS channel_name,
	assumeNotNull(cid_name) AS cid_name,
	assumeNotNull(billing_name) AS billing_name,
	assumeNotNull(clean_os_type) AS os_type,
	multiIf(os_type = 1, 'Web', os_type = 7, 'PC', os_type = 4, 'H5', os_type = 2, 'iOS', os_type = 3, 'Android', os_type = 6, '微信小程序', os_type = 8, '微信小游戏', os_type = 9, 'TV', 'other') AS os_name,
	now() AS create_time,
	toDate(date) as timeline,
	assumeNotNull(pv) as pv,
	assumeNotNull(start_sessions) as start_sessions,
	assumeNotNull(uv) as uv,
	assumeNotNull(new_uv) as new_uv,
	assumeNotNull(visitor) as visitor,
	assumeNotNull(active_user) as active_user,
	assumeNotNull(online_duration) as online_duration,
	assumeNotNull(uid_array) AS register_user,
	assumeNotNull(yesterday_uid_array) AS yesterday_register_user,
	assumeNotNull(arrayIntersect(active_user, yesterday_register_user)) AS yesterday_register_today_active_user,
	assumeNotNull(pay_user) as pay_user,
	assumeNotNull(pay_fee_sum) as pay_fee_sum,
	assumeNotNull(pay_user_fee) as pay_user_fee,
	assumeNotNull(pay_frequency) as pay_frequency,
	assumeNotNull(original_channel_pay_user) as original_channel_pay_user,
	assumeNotNull(original_channel_pay_fee_sum) as original_channel_pay_fee_sum,
	assumeNotNull(original_channel_pay_frequency) as original_channel_pay_frequency
FROM
	(SELECT
        date, 
        product, 
        agent_name, channel_name, cid_name, billing_name,
        clean_os_type,
        pv AS pv, 
        start_sessions AS start_sessions, 
        uv AS uv, 
        new_uv AS new_uv,
        visitor AS visitor,
        active_user AS active_user, 
        online_duration AS online_duration,
        uid_array AS uid_array, 
        yesterday_uid_array AS yesterday_uid_array,
        uid AS pay_user, 
        pay_fee AS pay_fee_sum, 
        pay_user_fee AS pay_user_fee, 
        pay_frequency as pay_frequency,
        original_channel_uid AS original_channel_pay_user, 
        original_channel_pay_fee AS original_channel_pay_fee_sum, 
        original_channel_pay_frequency as original_channel_pay_frequency
    FROM
        (
        SELECT
            date, product, 
            if(bi_channel_maintain.agent_name is null or bi_channel_maintain.agent_name = '', '其他', bi_channel_maintain.agent_name) as agent_name,
            if(bi_channel_maintain.channel_name is null or bi_channel_maintain.channel_name = '', '其他', bi_channel_maintain.channel_name) as channel_name,
            if(bi_channel_maintain.cid_name is null or bi_channel_maintain.cid_name = '', '其他', bi_channel_maintain.cid_name) as cid_name,
            if(bi_channel_maintain.billing_name is null or bi_channel_maintain.billing_name = '', '其他', bi_channel_maintain.billing_name) as billing_name,
            multiIf(source_mq = 1, os_type, source_mq = 0, multiIf(os_type IN (2, 6), 2, os_type IN (3, 7), 3, os_type = 4, 4,os_type =5 and lower(os) ='ios',2,os_type = 5 and lower(os) ='android', 3, os_type), os_type) AS clean_os_type,
            sum(pv) AS pv, groupArrayArray(arrayFilter(x->notEmpty(x), start_sessions)) AS start_sessions, 
            groupArrayArray(arrayFilter(x->notEmpty(x), uv)) AS uv, 
            groupArrayArray(arrayFilter(x->notEmpty(x), new_uv)) AS new_uv,
            groupArrayArray(arrayFilter(x->notEmpty(x), visitor)) AS visitor,
            groupArrayArray(arrayFilter(x->notEmpty(x), active_user)) AS active_user, 
            sum(online_duration) AS online_duration
        FROM
            banyan_bi_sdk.sdk_app_web_local FINAL
        LEFT JOIN banyan_bi_sdk.bi_channel_maintain on
            bi_channel_maintain.product_id = sdk_app_web_local.product
            and bi_channel_maintain.billing_id = sdk_app_web_local.channel
        where sdk_app_web_local.date >= '#(start_time)' and sdk_app_web_local.date <= '#(end_time)'
        GROUP BY
            date, product,
            if(bi_channel_maintain.agent_name is null or bi_channel_maintain.agent_name = '', '其他', bi_channel_maintain.agent_name),
            if(bi_channel_maintain.channel_name is null or bi_channel_maintain.channel_name = '', '其他', bi_channel_maintain.channel_name),
            if(bi_channel_maintain.cid_name is null or bi_channel_maintain.cid_name = '', '其他', bi_channel_maintain.cid_name),
            if(bi_channel_maintain.billing_name is null or bi_channel_maintain.billing_name = '', '其他', bi_channel_maintain.billing_name),
            multiIf(source_mq = 1, os_type, source_mq = 0, multiIf(os_type IN (2, 6), 2, os_type IN (3, 7), 3, os_type = 4, 4,os_type =5 and lower(os) ='ios',2,os_type = 5 and lower(os) ='android', 3, os_type), os_type)) as view_sdk_app_sdk_web_register_info_final
    FULL OUTER JOIN (
        SELECT
            date, product, agent_name, channel_name, cid_name, billing_name, os_type AS clean_os_type, uid_array, yesterday_uid_array,
            uid, pay_user_fee, pay_fee, pay_frequency, original_channel_uid, original_channel_pay_fee, original_channel_pay_frequency
        FROM
            (SELECT
             	register_date as date, product, agent_name, channel_name, cid_name, billing_name, os_type, uid_array, yesterday_uid_array
             FROM
             	(SELECT
             		date AS register_date, product,
             		if(isNull(bi_channel_maintain.agent_name) OR (bi_channel_maintain.agent_name = ''), '其他', bi_channel_maintain.agent_name) AS agent_name,
             		if(isNull(bi_channel_maintain.channel_name) OR (bi_channel_maintain.channel_name = ''), '其他', bi_channel_maintain.channel_name) AS channel_name,
             		if(isNull(bi_channel_maintain.cid_name) OR (bi_channel_maintain.cid_name = ''), '其他', bi_channel_maintain.cid_name) AS cid_name,
             		if(isNull(bi_channel_maintain.billing_name) OR (bi_channel_maintain.billing_name = ''), '其他', bi_channel_maintain.billing_name) AS billing_name,
             		os_type, groupArray(uid) AS uid_array
             	FROM
             		(SELECT product, uid, argMax(date, version) as date,argMax(channel, version) as channel,argMax(os_type, version) as os_type
                    from banyan_bi_sdk.register_info_local
                    where register_info_local.uid in (SELECT uid from banyan_bi_sdk.register_info_local where register_info_local.date >= '#(start_time)' and register_info_local.date <= '#(end_time)')
                    group by product, uid ) as register_info_local_final
             	LEFT JOIN banyan_bi_sdk.bi_channel_maintain ON
             		(bi_channel_maintain.product_id = register_info_local_final.product)
             		AND (bi_channel_maintain.billing_id = register_info_local_final.channel)
             	where
             		register_date >= '#(start_time)' and register_date <= '#(end_time)'
             	GROUP BY
             		date, product,
             		if(isNull(bi_channel_maintain.agent_name) OR (bi_channel_maintain.agent_name = ''), '其他', bi_channel_maintain.agent_name),
             		if(isNull(bi_channel_maintain.channel_name) OR (bi_channel_maintain.channel_name = ''), '其他', bi_channel_maintain.channel_name),
             		if(isNull(bi_channel_maintain.cid_name) OR (bi_channel_maintain.cid_name = ''), '其他', bi_channel_maintain.cid_name),
             		if(isNull(bi_channel_maintain.billing_name) OR (bi_channel_maintain.billing_name = ''), '其他', bi_channel_maintain.billing_name),
             		os_type) AS _t1
             FULL OUTER JOIN (
             	SELECT
             		addDays(date, 1) AS register_date, product,
             		if(isNull(bi_channel_maintain.agent_name) OR (bi_channel_maintain.agent_name = ''), '其他', bi_channel_maintain.agent_name) AS agent_name,
             		if(isNull(bi_channel_maintain.channel_name) OR (bi_channel_maintain.channel_name = ''), '其他', bi_channel_maintain.channel_name) AS channel_name,
             		if(isNull(bi_channel_maintain.cid_name) OR (bi_channel_maintain.cid_name = ''), '其他', bi_channel_maintain.cid_name) AS cid_name,
             		if(isNull(bi_channel_maintain.billing_name) OR (bi_channel_maintain.billing_name = ''), '其他', bi_channel_maintain.billing_name) AS billing_name,
             		os_type, groupArray(uid) AS yesterday_uid_array
             	FROM
             		(SELECT product, uid, argMax(date, version) as date,argMax(channel, version) as channel,argMax(os_type, version) as os_type
                    from banyan_bi_sdk.register_info_local
                    where register_info_local.uid in (SELECT uid from banyan_bi_sdk.register_info_local where register_info_local.date >= subtractDays(toDateTime('#(start_time)'), 1) and register_info_local.date <= subtractDays(toDateTime('#(end_time)'), 1))
                    group by product, uid ) as register_info_local_final
             	LEFT JOIN banyan_bi_sdk.bi_channel_maintain ON
             		(bi_channel_maintain.product_id = register_info_local_final.product)
             		AND (bi_channel_maintain.billing_id = register_info_local_final.channel)
             	WHERE
             		register_date >= '#(start_time)' and register_date <= '#(end_time)' and  register_date <= now()
             	GROUP BY
             		date, product,
             		if(isNull(bi_channel_maintain.agent_name) OR (bi_channel_maintain.agent_name = ''), '其他', bi_channel_maintain.agent_name),
                    if(isNull(bi_channel_maintain.channel_name) OR (bi_channel_maintain.channel_name = ''), '其他', bi_channel_maintain.channel_name),
                    if(isNull(bi_channel_maintain.cid_name) OR (bi_channel_maintain.cid_name = ''), '其他', bi_channel_maintain.cid_name),
                    if(isNull(bi_channel_maintain.billing_name) OR (bi_channel_maintain.billing_name = ''), '其他', bi_channel_maintain.billing_name),
             		os_type) AS _t2
             		USING (register_date, product, agent_name, channel_name, cid_name, billing_name, os_type)) as register_info_yesterday
        FULL OUTER JOIN (
            SELECT
                date, os_type, product, agent_name, channel_name, cid_name, billing_name, uid, pay_user_fee, pay_fee, pay_frequency, original_channel_uid, original_channel_pay_fee, original_channel_pay_frequency
            from
                (SELECT
					payment_info_local.date AS date,
					payment_info_local.os_type AS os_type,
					payment_info_local.product AS product,
					if(isNull(register_info_local_final.agent_name) OR (register_info_local_final.agent_name = ''), '其他', register_info_local_final.agent_name) AS agent_name,
					if(isNull(register_info_local_final.channel_name) OR (register_info_local_final.channel_name = ''), '其他', register_info_local_final.channel_name) AS channel_name,
					if(isNull(register_info_local_final.cid_name) OR (register_info_local_final.cid_name = ''), '其他', register_info_local_final.cid_name) AS cid_name,
					if(isNull(register_info_local_final.billing_name) OR (register_info_local_final.billing_name = ''), '其他', register_info_local_final.billing_name) AS billing_name,
					groupArrayIf(payment_info_local.uid, notEmpty(payment_info_local.uid)) AS uid,
					groupArray((payment_info_local.uid, payment_info_local.pay_fee)) AS pay_user_fee,
					sum(payment_info_local.pay_fee) AS pay_fee,
					sum(payment_info_local.pay_frequency) as pay_frequency
				FROM
					banyan_bi_sdk.payment_info_local FINAL
				LEFT JOIN (
                    SELECT
                        uid,
                        assumeNotNull(product) AS product,
                        if(isNull(bi_channel_maintain.agent_name) OR (bi_channel_maintain.agent_name = ''), '其他', bi_channel_maintain.agent_name) AS agent_name,
                        if(isNull(bi_channel_maintain.channel_name) OR (bi_channel_maintain.channel_name = ''), '其他', bi_channel_maintain.channel_name) AS channel_name,
                        if(isNull(bi_channel_maintain.cid_name) OR (bi_channel_maintain.cid_name = ''), '其他', bi_channel_maintain.cid_name) AS cid_name,
                        if(isNull(bi_channel_maintain.billing_name) OR (bi_channel_maintain.billing_name = ''), '其他', bi_channel_maintain.billing_name) AS billing_name
                    FROM
                        (SELECT product, uid, argMax(date, version) as date, argMax(channel, version) as channel, argMax(os_type, version) as os_type
                        from banyan_bi_sdk.register_info_local
                        where uid in (SELECT uid FROM banyan_bi_sdk.payment_info_local where payment_info_local.date >= '#(start_time)' and payment_info_local.date <= '#(end_time)')
                        group by product, uid) as register_info_local_final_temp
                    LEFT JOIN banyan_bi_sdk.bi_channel_maintain ON
                        bi_channel_maintain.product_id = register_info_local_final_temp.product AND bi_channel_maintain.billing_id = register_info_local_final_temp.channel) AS register_info_local_final ON
					register_info_local_final.uid = payment_info_local.uid AND register_info_local_final.product = payment_info_local.product
				where payment_info_local.date >= '#(start_time)' and payment_info_local.date <= '#(end_time)'
				GROUP BY
					date, os_type, product,
					if(isNull(register_info_local_final.agent_name) OR (register_info_local_final.agent_name = ''), '其他', register_info_local_final.agent_name),
					if(isNull(register_info_local_final.channel_name) OR (register_info_local_final.channel_name = ''), '其他', register_info_local_final.channel_name),
					if(isNull(register_info_local_final.cid_name) OR (register_info_local_final.cid_name = ''), '其他', register_info_local_final.cid_name),
					if(isNull(register_info_local_final.billing_name) OR (register_info_local_final.billing_name = ''), '其他', register_info_local_final.billing_name)) as view_payment_info_local_register_channel
            full join (
                SELECT
					payment_info_local.date AS date,
					payment_info_local.os_type AS os_type,
					payment_info_local.product AS product,
					if(isNull(bi_channel_maintain.agent_name) OR (bi_channel_maintain.agent_name = ''), '其他', bi_channel_maintain.agent_name) AS agent_name,
					if(isNull(bi_channel_maintain.channel_name) OR (bi_channel_maintain.channel_name = ''), '其他', bi_channel_maintain.channel_name) AS channel_name,
					if(isNull(bi_channel_maintain.cid_name) OR (bi_channel_maintain.cid_name = ''), '其他', bi_channel_maintain.cid_name) AS cid_name,
					if(isNull(bi_channel_maintain.billing_name) OR (bi_channel_maintain.billing_name = ''), '其他', bi_channel_maintain.billing_name) AS billing_name,
					groupArray(payment_info_local.uid) AS original_channel_uid,
					sum(payment_info_local.pay_fee) AS original_channel_pay_fee,
					sum(payment_info_local.pay_frequency) as original_channel_pay_frequency
				FROM
					banyan_bi_sdk.payment_info_local FINAL
				LEFT JOIN banyan_bi_sdk.bi_channel_maintain ON
					bi_channel_maintain.product_id = payment_info_local.product AND bi_channel_maintain.billing_id = payment_info_local.channel
				where payment_info_local.date >= '#(start_time)' and payment_info_local.date <= '#(end_time)'
				GROUP BY
					date, os_type, product,
					if(isNull(bi_channel_maintain.agent_name) OR (bi_channel_maintain.agent_name = ''), '其他', bi_channel_maintain.agent_name),
					if(isNull(bi_channel_maintain.channel_name) OR (bi_channel_maintain.channel_name = ''), '其他', bi_channel_maintain.channel_name),
					if(isNull(bi_channel_maintain.cid_name) OR (bi_channel_maintain.cid_name = ''), '其他', bi_channel_maintain.cid_name),
					if(isNull(bi_channel_maintain.billing_name) OR (bi_channel_maintain.billing_name = ''), '其他', bi_channel_maintain.billing_name)) as view_payment_info_local_original_channel
                    using date, os_type, product, agent_name, channel_name, cid_name, billing_name) AS view_payment_info_group
                USING (date, product, agent_name, channel_name, cid_name, billing_name, os_type)) AS register_payment
            USING (date, product, agent_name, channel_name, cid_name, billing_name, clean_os_type));