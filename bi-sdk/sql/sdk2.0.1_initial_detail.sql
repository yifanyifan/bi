-- ALTER TABLE banyan_bi_sdk.payment_retain_day_materialized_view drop column `initial_detail`;


ALTER TABLE banyan_bi_sdk.payment_retain_day_materialized_view ADD column `initial_detail` SimpleAggregateFunction(groupUniqArrayArray, Array(String)) after `initial`;

insert into banyan_bi_sdk.payment_retain_day_materialized_view(`date`,product_id,channel_id,os_type,os_name,`interval`,`interval_date`,`initial_detail`)
SELECT
	toDate(_t1.`date`) as `date`,
	product_id,
	channel_id,
	os_type,
	os_name,
	`interval`,
    toDate(_t1.`date`) as `interval_date`,
	groupUniqArrayArray(_t1.initial) as `initial_detail`
FROM
	banyan_bi_sdk.payment_retain_local as _t1
where `interval` = 0
group by
	toDate(_t1.`date`),
	product_id,
	channel_id,
	os_type,
	os_name,
	`interval`,
    toDate(_t1.`interval_date`);
