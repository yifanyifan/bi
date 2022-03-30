insert into banyan_bi_sdk.sdk_app_sdk_web_local
SELECT
	assumeNotNull(date) AS date,
	assumeNotNull(product) AS product,
	assumeNotNull(agent_name) AS agent_name,
	assumeNotNull(channel_name) AS channel_name,
	assumeNotNull(cid_name) AS cid_name,
	assumeNotNull(billing_name) AS billing_name,
	assumeNotNull(clean_os_type) AS os_type,
	multiIf(os_type = 1, 'Web', os_type = 7, 'PC', os_type = 4, 'H5', os_type = 2, 'iOS', os_type = 3, 'Android', os_type = 6, '微信小程序', os_type = 8, '微信小游戏', os_type = 9, 'TV', 'other') AS os_name,
	assumeNotNull(os) AS os,
	assumeNotNull(os_version) AS os_version,
	assumeNotNull(app_version) AS app_version,
	concat(os, ' ', toString(os_version)) AS os_concat_version,
	assumeNotNull(screen_width) AS screen_width,
	assumeNotNull(screen_height) AS screen_height,
	concat(screen_width, 'x', screen_height) AS resolution,
	assumeNotNull(clean_browser) AS browser,
	assumeNotNull(clean_country) AS country,
	assumeNotNull(clean_province) AS province,
	assumeNotNull(clean_city) AS city,
	assumeNotNull(clean_brand) AS brand,
	assumeNotNull(clean_model) AS model,
	assumeNotNull(clean_carrier) AS carrier,
    now() AS create_time,
    toDate(date) as timeline,
	assumeNotNull(pv) AS pv,
	assumeNotNull(start_sessions) AS start_sessions,
	assumeNotNull(uv) AS uv,
	assumeNotNull(new_uv) AS new_uv,
	assumeNotNull(visitor) AS visitor,
	assumeNotNull(active_user) AS active_user,
	assumeNotNull(online_duration) AS online_duration,
	assumeNotNull(ips) AS ips
FROM
(
	SELECT
		date,
		product,
		if(isNull(bi_channel_maintain.agent_name) OR (bi_channel_maintain.agent_name = ''), '其他', bi_channel_maintain.agent_name) AS agent_name,
		if(isNull(bi_channel_maintain.channel_name) OR (bi_channel_maintain.channel_name = ''), '其他', bi_channel_maintain.channel_name) AS channel_name,
		if(isNull(bi_channel_maintain.cid_name) OR (bi_channel_maintain.cid_name = ''), '其他', bi_channel_maintain.cid_name) AS cid_name,
		if(isNull(bi_channel_maintain.billing_name) OR (bi_channel_maintain.billing_name = ''), '其他', bi_channel_maintain.billing_name) AS billing_name,
		multiIf(source_mq = 1, os_type, source_mq = 0, multiIf(os_type IN (2, 6), 2, os_type IN (3, 7), 3, os_type = 4, 4,os_type =5 and lower(os) ='ios',2,os_type = 5 and lower(os) ='android', 3, os_type), os_type) AS clean_os_type,
		os, os_version, app_version, screen_width, screen_height,
		if(isNull(browser) OR (browser = ''), '未知', browser) AS clean_browser,
		if(isNull(country) OR (country = ''), '未知', country) AS clean_country,
		if(isNull(province) OR (province = ''), '未知', province) AS clean_province,
		if(isNull(city) OR (city = ''), '未知', city) AS clean_city,
		if(isNull(brand) OR (brand = ''), '未知', brand) AS clean_brand,
		if(isNull(model) OR (model = ''), '未知', model) AS clean_model,
		if(isNull(carrier) OR (carrier = ''), '未知', carrier) AS clean_carrier,
		sum(pv) AS pv,
		groupArrayArray(arrayFilter(x -> notEmpty(x), start_sessions)) AS start_sessions,
		groupArrayArray(arrayFilter(x -> notEmpty(x), uv)) AS uv,
		groupArrayArray(arrayFilter(x -> notEmpty(x), new_uv)) AS new_uv,
		groupArrayArray(arrayFilter(x -> notEmpty(x), visitor)) AS visitor,
		groupArrayArray(arrayFilter(x -> notEmpty(x), active_user)) AS active_user,
		sum(online_duration) AS online_duration,
		groupArrayArray(arrayFilter(x -> notEmpty(x), ips)) AS ips
	FROM
		banyan_bi_sdk.sdk_app_web_local FINAL
	LEFT JOIN banyan_bi_sdk.bi_channel_maintain ON
		(bi_channel_maintain.product_id = sdk_app_web_local.product)
		AND (bi_channel_maintain.billing_id = sdk_app_web_local.channel)
	where
        sdk_app_web_local.date >= '#(start_time)'
        and sdk_app_web_local.date <= '#(end_time)'
	GROUP BY
		date, product,
		if(isNull(bi_channel_maintain.agent_name) OR (bi_channel_maintain.agent_name = ''), '其他', bi_channel_maintain.agent_name),
		if(isNull(bi_channel_maintain.channel_name) OR (bi_channel_maintain.channel_name = ''), '其他', bi_channel_maintain.channel_name),
		if(isNull(bi_channel_maintain.cid_name) OR (bi_channel_maintain.cid_name = ''), '其他', bi_channel_maintain.cid_name),
		if(isNull(bi_channel_maintain.billing_name) OR (bi_channel_maintain.billing_name = ''), '其他', bi_channel_maintain.billing_name),
		multiIf(source_mq = 1, os_type, source_mq = 0, multiIf(os_type IN (2, 6), 2, os_type IN (3, 7), 3, os_type = 4, 4,os_type =5 and lower(os) ='ios',2,os_type = 5 and lower(os) ='android', 3, os_type), os_type),
		os, os_version, app_version, screen_width, screen_height,
		if(isNull(browser) OR (browser = ''), '未知', browser),
        if(isNull(country) OR (country = ''), '未知', country),
        if(isNull(province) OR (province = ''), '未知', province),
        if(isNull(city) OR (city = ''), '未知', city),
		if(isNull(brand) OR (brand = ''), '未知', brand),
		if(isNull(model) OR (model = ''), '未知', model),
		if(isNull(carrier) OR (carrier = ''), '未知', carrier))
