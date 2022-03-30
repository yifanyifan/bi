(
SELECT
    identifier,`session`,sum(pv) as pv,sum(online_duration)/60000 as online_duration,
    if(length(groupArrayIf(active_user, active_user!= ''))=0, 0, 1) is_active_user
FROM
	(SELECT * FROM banyan_bi_sdk.view_sdk_app_sdk_web_session where timeline < today() UNION all select * from banyan_bi_sdk.view_sdk_app_sdk_web_session final where timeline = today()) as view_sdk_app_sdk_web_session
where
	os_type in (1,4,7)
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
    group by identifier,`session`
)

