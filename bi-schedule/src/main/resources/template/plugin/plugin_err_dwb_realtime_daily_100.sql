INSERT
	INTO
	bi_plugin.plugin_err_dwb_realtime_daily_100( partition_date ,
	plugin_id ,
	err_pc_arr ,
	num_err,
	plugin_name )
SELECT
	e.*,
	pd.plugin_name 
FROM
	(
	select
		partition_date,
		plugin_id ,
		groupBitmapOr(err_pc_arr_state) as err_pc_arr,
		sumMerge(num_err_state) as num_err
	FROM
		bi_plugin.plugin_err_dwb_realtime_daily
	where
		partition_date = '#(partition_date)'
	group by
		partition_date,
		plugin_id ) e
join bi_plugin.plugin_dim pd on
	e.plugin_id = pd.plugin_id 