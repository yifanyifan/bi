(select
 	agg_dim,
 	toStartOfMonth(dm_bi_sdk_user_sum_d.dt) as dt,
 	product_id,
 	agent_name,channel_name,cid_name,billing_name,
 	max(`mau`) as mau,
 	max(all_users_cnt) as all_users_cnt
 from
 	banyan_bi_sdk.dm_bi_sdk_user_sum_d
 group by
 	agg_dim,
 	toStartOfMonth(dm_bi_sdk_user_sum_d.dt),
 	product_id,
 	agent_name,channel_name,cid_name,billing_name)