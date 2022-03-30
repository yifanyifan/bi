(
select
    if(active_user='',null,active_user) as active_user,
    if(uv='',null,uv) as uv,
    *,
    if(page_sequence = max_page_sequence, 1, 0) as is_quite
FROM
	(SELECT * FROM banyan_bi_sdk.view_sdk_app_sdk_web_session where timeline < today() UNION all select * from banyan_bi_sdk.view_sdk_app_sdk_web_session final where timeline = today()) as view_sdk_app_sdk_web_session
left join (
SELECT
    identifier,`session`,max(page_sequence) as max_page_sequence,
    if(length(groupArrayIf(active_user, active_user!= ''))=0, 0, 1) is_active_user
FROM
	(SELECT * FROM banyan_bi_sdk.view_sdk_app_sdk_web_session where timeline < today() UNION all select * from banyan_bi_sdk.view_sdk_app_sdk_web_session final where timeline = today()) as view_sdk_app_sdk_web_session
where
	in_time < subtractMinutes(now(), 30)
	and os_type in (2,3)
  #if(startDate != null && endDate != null)
  and timeline BETWEEN '#(startDate)' and '#(endDate)'
  #end
	#for(x : stringMap)
      #if(x.value != null && x.value != '' && x.key == 'product')
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
    #if(url != null && url != '')
        and view_sdk_app_sdk_web_session.page_url = '#(url)'
    #end
)