insert into
	banyan_bi_sdk.acc_reg_pay(
		date,
		agg_dim,
		product,
		agent_name,channel_name,cid_name,billing_name,
		acc_reg_count,
		acc_pay_user_count,
		acc_pay_fee
	)
select
    '#(date)',
    concat('pid',if(agent_name!='',':ag',''),if(channel_name!='',':ch',''),if(cid_name!='',':cn',''),if(billing_name!='',':bl','')) as agg_dim,
    product,
    agent_name,channel_name,cid_name,billing_name,
    uniqExactArrayIf(register_user, notEmpty(register_user)) as acc_reg_count,
    uniqExactArrayIf(pay_user, notEmpty(pay_user)) as acc_pay_user_count,
    sum(pay_fee_sum) as acc_pay_fee
from
    banyan_bi_sdk.sdk_app_sdk_web_register_info_local
where
    toDate(`date`) <= '#(date)'
group by
	product,
	agent_name,channel_name,cid_name,billing_name
	 with rollup having product != '';