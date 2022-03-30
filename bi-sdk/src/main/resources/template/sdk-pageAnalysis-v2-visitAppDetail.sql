(
select
    `date`,
    product,
    agent_name,
    channel_name,
    cid_name,
    billing_name,
    os_type,
    user_type,
    app_version,
    `session`,
    page_sequence,
    in_time,
    page_event_type,
    is_active_user,
    page_title,
    page_url,
    parent_page_url,
    parent_page_domain,
    online_duration,
    pv,
    if(active_user='',null,active_user) as active_user,
    if(uv='',null,uv) as uv,
    identifier,
    ip,
    max_page_sequence
FROM
	(SELECT * FROM banyan_bi_sdk.view_sdk_app_sdk_web_session where timeline < today() UNION all select * from banyan_bi_sdk.view_sdk_app_sdk_web_session final where timeline = today()) as view_sdk_app_sdk_web_session
left join (
SELECT
    identifier,`session`,
    max(page_sequence) as max_page_sequence,
    if(length(groupArrayIf(active_user, active_user!= ''))=0, 0, 1) is_active_user
FROM
	(SELECT * FROM banyan_bi_sdk.view_sdk_app_sdk_web_session where timeline < today() UNION all select * from banyan_bi_sdk.view_sdk_app_sdk_web_session final where timeline = today()) as view_sdk_app_sdk_web_session
where
	os_type in (2,3)
	#if(startDate != null && endDate != null)
  and timeline BETWEEN '#(startDate)' and '#(endDate)'
  #end
	#for(x : stringMap)
      #if(x.value != null && x.value != '' && (x.key == 'product' || x.key == 'app_version'))
        and view_sdk_app_sdk_web_session.#(x.key) = '#(x.value)'
      #end
    #end
    #for(x : integerMap)
      #if(x.value != null && x.key == 'os_type')
        and #(x.key) = #(x.value)
      #end
    #end
    group by identifier,session
) as _t1 using identifier,session
where
	os_type in (2,3)
	#if(startDate != null && endDate != null)
  and timeline BETWEEN '#(startDate)' and '#(endDate)'
  #end
	#for(x : stringMap)
      #if(x.value != null && x.value != '')
        and view_sdk_app_sdk_web_session.#(x.key) = '#(x.value)'
      #end
    #end
    #for(x : integerMap)
      #if(x.value != null)
        and #(x.key) = #(x.value)
      #end
    #end
    #for(x : inMap)
      #if(x.value != null)
        and #(x.key) in #(x.value)
      #end
    #end
)