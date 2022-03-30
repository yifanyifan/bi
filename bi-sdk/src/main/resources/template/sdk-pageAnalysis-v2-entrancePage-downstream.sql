(
SELECT
	date,page_title,page_url,parent_page_domain,user_type,
	if(active_user='',null,active_user) as active_user,
    if(uv='',null,uv) as uv,
	ip,pv,online_duration,page_sequence,`session`,
	all_pv,all_online_duration,is_quite,is_jump,max_page_sequence,is_active_user,if(max_page_sequence >= page_sequence, 1, 0) downstream_pv
FROM
	(SELECT * FROM banyan_bi_sdk.view_sdk_app_sdk_web_session where timeline < today() UNION all select * from banyan_bi_sdk.view_sdk_app_sdk_web_session final where timeline = today()) as view_sdk_app_sdk_web_session
left join
(SELECT
	identifier,`session`, sum(view_sdk_app_sdk_web_session.pv) as all_pv, sum(view_sdk_app_sdk_web_session.online_duration) as all_online_duration,
    max(in_time), max(page_sequence) as max_page_sequence,
    if(length(groupArrayIf(active_user, active_user!= ''))=0, 0, 1) is_active_user,
    if(max(in_time) < subtractMinutes(now(), 30), 1, 0) is_quite,
    if(max(in_time)<subtractMinutes(now(), 30) and max(page_sequence)=1, 1, 0) is_jump
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
	page_sequence = 2
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
        and parent_page_url = '#(url)'
    #end
)