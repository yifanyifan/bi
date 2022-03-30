insert into banyan_bi_sdk.sdk_app_sdk_web_session_page
SELECT
	sdk_app_sdk_web_session_local.`date`,
	sdk_app_sdk_web_session_local.product,
	if(isNull(bi_channel_maintain.agent_name) OR (bi_channel_maintain.agent_name = ''), '其他', bi_channel_maintain.agent_name) AS agent_name,
	if(isNull(bi_channel_maintain.channel_name) OR (bi_channel_maintain.channel_name = ''), '其他', bi_channel_maintain.channel_name) AS channel_name,
	if(isNull(bi_channel_maintain.cid_name) OR (bi_channel_maintain.cid_name = ''), '其他', bi_channel_maintain.cid_name) AS cid_name,
	if(isNull(bi_channel_maintain.billing_name) OR (bi_channel_maintain.billing_name = ''), '其他', bi_channel_maintain.billing_name) AS billing_name,
	multiIf(sdk_app_sdk_web_session_local.source_mq = 1, sdk_app_sdk_web_session_local.os_type, sdk_app_sdk_web_session_local.source_mq = 0, multiIf(sdk_app_sdk_web_session_local.os_type IN (2, 6), 2, sdk_app_sdk_web_session_local.os_type IN (3, 7), 3, sdk_app_sdk_web_session_local.os_type = 4, 4,sdk_app_sdk_web_session_local.os_type =5 and lower(sdk_app_sdk_web_session_local.os) ='ios',2,sdk_app_sdk_web_session_local.os_type = 5 and lower(sdk_app_sdk_web_session_local.os) ='android', 3, sdk_app_sdk_web_session_local.os_type), sdk_app_sdk_web_session_local.os_type) AS os_type,
	sdk_app_sdk_web_session_local.user_type,
	sdk_app_sdk_web_session_local.app_version,
	sdk_app_sdk_web_session_local.`session`,
	sdk_app_sdk_web_session_local.page_sequence,
	sdk_app_sdk_web_session_local.in_time,
	sdk_app_sdk_web_session_local.page_event_type,
	sdk_app_sdk_web_session_local.version,
	if(sdk_app_sdk_web_session_local.page_title is null or sdk_app_sdk_web_session_local.page_title = '', '未知', sdk_app_sdk_web_session_local.page_title),
	if(sdk_app_sdk_web_session_local.page_url is null or sdk_app_sdk_web_session_local.page_url = '', '未知', sdk_app_sdk_web_session_local.page_url),
	--sdk_app_sdk_web_session_local.parent_page_url,
	if(sdk_app_sdk_web_session_local.page_sequence = 1, sdk_app_sdk_web_session_local.parent_page_url, multiIf(empty(_t2.page_url), sdk_app_sdk_web_session_local.parent_page_url, isNull(_t2.page_url), sdk_app_sdk_web_session_local.parent_page_url, _t2.page_url)) as parent_page_url,
	sdk_app_sdk_web_session_local.parent_page_domain,
	sdk_app_sdk_web_session_local.online_duration,
	sdk_app_sdk_web_session_local.pv,
	sdk_app_sdk_web_session_local.active_user,
	sdk_app_sdk_web_session_local.uv,
	sdk_app_sdk_web_session_local.identifier,
	sdk_app_sdk_web_session_local.ip,
	sdk_app_sdk_web_session_local.timeline
FROM
	banyan_bi_sdk.sdk_app_sdk_web_session_local
	LEFT JOIN (
    	select
    		product, session, identifier, argMax(page_url, version) as page_url, (page_sequence+1) as page_sequence
    	from
    		banyan_bi_sdk.sdk_app_sdk_web_session_local
    	where sdk_app_sdk_web_session_local.timeline in (toDate('#(start_time)'),toDate('#(end_time)')) and toStartOfHour(date) >= subtractMinutes(toDateTime('#(start_time)'), 60) and toStartOfHour(date) <= '#(end_time)' and page_sequence > 0
    	group by product, session, identifier, page_sequence) as _t2 on
    	_t2.product = sdk_app_sdk_web_session_local.product
    	and _t2.session = sdk_app_sdk_web_session_local.session
    	and _t2.identifier = sdk_app_sdk_web_session_local.identifier
    	and _t2.page_sequence = sdk_app_sdk_web_session_local.page_sequence
	LEFT JOIN banyan_bi_sdk.bi_channel_maintain ON
		bi_channel_maintain.product_id = sdk_app_sdk_web_session_local.product
		AND bi_channel_maintain.billing_id = sdk_app_sdk_web_session_local.channel
where sdk_app_sdk_web_session_local.timeline in (toDate('#(start_time)'),toDate('#(end_time)')) and toStartOfHour(sdk_app_sdk_web_session_local.date) >= '#(start_time)' and toStartOfHour(sdk_app_sdk_web_session_local.date) <= '#(end_time)'