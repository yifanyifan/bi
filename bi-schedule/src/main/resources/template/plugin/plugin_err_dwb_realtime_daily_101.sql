INSERT
	INTO
	bi_plugin.plugin_err_dwb_realtime_daily_101( partition_date ,
	plugin_id ,
	err_code ,
	err_pc_arr ,
	num_err,
	plugin_name,
	err_name )
SELECT
	e.partition_date as partition_date,
	e.plugin_id as plugin_id ,
	e.err_code as err_code ,
	e.err_pc_arr as err_pc_arr,
	e.num_err as num_err,
	pd.plugin_name as plugin_name,
	ed.err_name as err_name
FROM
	(
	select
		partition_date,
		plugin_id,
		err_code ,
		groupBitmapOr(err_pc_arr_state) as err_pc_arr,
		sumMerge(num_err_state) as num_err
	FROM
		bi_plugin.plugin_err_dwb_realtime_daily
	where
		partition_date = '#(partition_date)'
	group by
		partition_date,
		plugin_id,
		err_code ) e
join bi_plugin.plugin_dim pd on
	e.plugin_id = pd.plugin_id
join bi_plugin.plugin_err_dim ed on
	e.err_code = ed.err_code 