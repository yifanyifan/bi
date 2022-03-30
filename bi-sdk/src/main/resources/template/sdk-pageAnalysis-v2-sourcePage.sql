(
SELECT
    date,
    parent_page_url,
	if(parent_page_domain is null or parent_page_domain = '', '直接访问', parent_page_domain) as parent_page_domain,
	user_type,
	if(active_user='',null,active_user) as active_user,
	if(uv='',null,uv) as uv,
	ip,identifier,`session`,
	_t2.pv as pv,
	_t2.online_duration as online_duration,
	_t2.active_user_array as active_user_array,
	_t2.is_active_user as is_active_user,
	_t2.uv_array as uv_array,
	_t2.old_uv_array as old_uv_array,
	_t2.new_uv_array as new_uv_array
FROM
	(SELECT * FROM banyan_bi_sdk.view_sdk_app_sdk_web_session where timeline < today() UNION all select * from banyan_bi_sdk.view_sdk_app_sdk_web_session final where timeline = today()) as view_sdk_app_sdk_web_session
left join
(SELECT
	identifier,`session`, sum(view_sdk_app_sdk_web_session.pv) as pv, sum(view_sdk_app_sdk_web_session.online_duration) as online_duration,
	groupArrayIf(active_user, active_user!= '') as active_user_array,
	if(length(groupArrayIf(active_user, active_user!= ''))=0, 0, 1) is_active_user,
	groupArrayIf(uv, uv!= '') as uv_array,
	groupArrayIf(uv, uv!= '' and user_type=0) as old_uv_array,
	groupArrayIf(uv, uv!= '' and user_type=1) as new_uv_array
FROM
	(SELECT * FROM banyan_bi_sdk.view_sdk_app_sdk_web_session where timeline < today() UNION all select * from banyan_bi_sdk.view_sdk_app_sdk_web_session final where timeline = today()) as view_sdk_app_sdk_web_session
where
    os_type in (1,4,7)
    #if(startDate != null && endDate != null)
    and timeline BETWEEN '#(startDate)' and '#(endDate)'
    #end
    #for(x : stringMap)
      #if(x.value != null && x.value != '' && x.key == 'product')
        and #(x.key) = '#(x.value)'
      #end
    #end
    #for(x : integerMap)
      #if(x.value != null && x.key == 'os_type')
        and #(x.key) = #(x.value)
      #end
    #end
group by
	identifier,`session`) as _t2 using identifier,`session`
where
	page_sequence = 1
	and os_type in (1,4,7)
	#if(startDate != null && endDate != null)
  and timeline BETWEEN '#(startDate)' and '#(endDate)'
  #end
	#for(x : stringMap)
      #if(x.value != null && x.value != '')
        and #(x.key) = '#(x.value)'
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
    #if(url != null && url != '')
        and parent_page_domain = '#(url)'
    #end
)