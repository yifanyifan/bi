(select * from banyan_bi_sdk.view_acc_reg_pay union all
select date,agg_dim,product,agent_name,channel_name,cid_name,billing_name,
    _t1.acc_reg_count + _t2.acc_reg_count,
    acc_pay_user_count,acc_pay_fee from
(select
    today() as date,
    concat('pid',if(agent_name!='',':ag',''),if(channel_name!='',':ch',''),if(cid_name!='',':cn',''),if(billing_name!='',':bl','')) as agg_dim,
    product,
    agent_name,channel_name,cid_name,billing_name,
    (uniqExactArrayIf(register_user, notEmpty(register_user) and timeline=today())) as acc_reg_count,
    uniqExactArrayIf(pay_user, notEmpty(pay_user)) as acc_pay_user_count,
    sum(pay_fee_sum) as acc_pay_fee
from
    banyan_bi_sdk.sdk_app_sdk_web_register_info_local
group by
	product,
	agent_name,channel_name,cid_name,billing_name
	 with rollup having product != '') as _t1
	 left join
	 (select product,agent_name,channel_name,cid_name,billing_name,acc_reg_count from banyan_bi_sdk.view_acc_reg_pay where date = yesterday()) as _t2
	 using product,agent_name,channel_name,cid_name,billing_name
)