insert into
	banyan_bi_sdk.acc_sdk_app_web (
		date,
		agg_dim,
		product,
		agent_name,channel_name,cid_name,billing_name,
		acc_active_user,
		acc_visit_count,
		acc_visit_user
	)
SELECT
    '#(date)',
    concat('pid',if(agent_name!='',':ag',''),if(channel_name!='',':ch',''),if(cid_name!='',':cn',''),if(billing_name!='',':bl','')) as agg_dim,
	product,
	agent_name,channel_name,cid_name,billing_name,
	uniqExactArrayIf(active_user, notEmpty(active_user)) as acc_active_user,
	uniqExactArrayIf(start_sessions, notEmpty(start_sessions)) as acc_visit_count,
	uniqExactArrayIf(uv, notEmpty(uv)) as acc_visit_user
from
	banyan_bi_sdk.sdk_app_sdk_web_local
where
    toDate(`date`) <= '#(date)'
group by
	product,
	agent_name,channel_name,cid_name,billing_name
	with rollup having product != '';