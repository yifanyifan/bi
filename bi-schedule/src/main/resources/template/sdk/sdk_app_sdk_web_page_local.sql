insert into banyan_bi_sdk.sdk_app_sdk_web_page_local
SELECT
	assumeNotNull(date) as date,
	assumeNotNull(product) as product,
	assumeNotNull(agent_name) as agent_name,
	assumeNotNull(channel_name) as channel_name,
	assumeNotNull(cid_name) as cid_name,
	assumeNotNull(billing_name) as billing_name,
	assumeNotNull(clean_os_type) as os_type,
	assumeNotNull(user_type) as user_type,
	assumeNotNull(app_version) as app_version,
	assumeNotNull(page_title) as page_title,
	assumeNotNull(page_url) as page_url,
	now() AS create_time,
    toDate(date) as timeline,
	assumeNotNull(pv) AS pv,
	assumeNotNull(in_sessions) AS in_sessions,
	assumeNotNull(start_sessions) AS start_sessions,
	assumeNotNull(uv) AS uv,
	assumeNotNull(active_user) AS active_user,
	assumeNotNull(online_duration) AS online_duration,
	online_duration / 60000 AS online_duration_m,
	assumeNotNull(ips) AS ips
FROM
	(SELECT
		date,
		multiIf(source_mq = 1, os_type, source_mq = 0, multiIf(os_type IN (2, 6), 2, os_type IN (3, 7), 3, os_type = 4, 4,os_type =5 and lower(os) ='ios',2,os_type = 5 and lower(os) ='android', 3, os_type), os_type) AS clean_os_type,
		product,
		if(bi_channel_maintain.agent_name is null or bi_channel_maintain.agent_name = '', '其他', bi_channel_maintain.agent_name) AS agent_name,
		if(bi_channel_maintain.channel_name is null or bi_channel_maintain.channel_name = '', '其他', bi_channel_maintain.channel_name) AS channel_name,
		if(bi_channel_maintain.cid_name is null or bi_channel_maintain.cid_name = '', '其他', bi_channel_maintain.cid_name) AS cid_name,
		if(bi_channel_maintain.billing_name is null or bi_channel_maintain.billing_name = '', '其他', bi_channel_maintain.billing_name) AS billing_name,
		user_type, app_version, page_title, page_url,
		sum(pv) AS pv, groupArrayArray(in_sessions) AS in_sessions,
		groupArrayArray(arrayFilter(x -> notEmpty(x), start_sessions)) AS start_sessions,
		groupArrayArray(arrayFilter(x -> notEmpty(x), uv)) AS uv,
		groupArrayArray(arrayFilter(x -> notEmpty(x), active_user)) AS active_user,
		sum(online_duration) AS online_duration,
		groupArrayArray(arrayFilter(x -> notEmpty(x), ips)) AS ips
	FROM
		banyan_bi_sdk.sdk_app_web_local FINAL
    LEFT JOIN banyan_bi_sdk.bi_channel_maintain on
    	bi_channel_maintain.product_id = sdk_app_web_local.product
    	and bi_channel_maintain.billing_id = sdk_app_web_local.channel
    where
        sdk_app_web_local.date >= '#(start_time)'
        and sdk_app_web_local.date <= '#(end_time)'
	GROUP BY
		date,
		multiIf(source_mq = 1, os_type, source_mq = 0, multiIf(os_type IN (2, 6), 2, os_type IN (3, 7), 3, os_type = 4, 4,os_type =5 and lower(os) ='ios',2,os_type = 5 and lower(os) ='android', 3, os_type), os_type),
		product,
		if(bi_channel_maintain.agent_name is null or bi_channel_maintain.agent_name = '', '其他', bi_channel_maintain.agent_name),
		if(bi_channel_maintain.channel_name is null or bi_channel_maintain.channel_name = '', '其他', bi_channel_maintain.channel_name),
        if(bi_channel_maintain.cid_name is null or bi_channel_maintain.cid_name = '', '其他', bi_channel_maintain.cid_name),
        if(bi_channel_maintain.billing_name is null or bi_channel_maintain.billing_name = '', '其他', bi_channel_maintain.billing_name),
		user_type, app_version, page_title, page_url)