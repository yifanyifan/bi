(select
	agg_dim,
	toStartOfWeek(dm_bi_sdk_user_sum_d.dt, 1) as dt,
	product_id,
	agent_name,channel_name,cid_name,billing_name,
	max(`wau`) as wau,
	max(all_users_cnt) as all_users_cnt
from
	banyan_bi_sdk.dm_bi_sdk_user_sum_d
group by
	agg_dim,
	toStartOfWeek(dm_bi_sdk_user_sum_d.dt, 1),
	product_id,
	agent_name,channel_name,cid_name,billing_name)