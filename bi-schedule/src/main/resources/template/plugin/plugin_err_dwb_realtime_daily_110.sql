INSERT
	INTO
	bi_plugin.plugin_err_dwb_realtime_daily_110( partition_date ,
	plugin_id ,
	channel_id ,
	err_pc_arr ,
	num_err,
	plugin_name,
	channel_name )
SELECT
	e.partition_date as partition_date,
	e.plugin_id as plugin_id ,
	e.channel_id as channel_id ,
	e.err_pc_arr as err_pc_arr,
	e.num_err as num_err,
	pd.plugin_name as plugin_name,
	cd.channel_name as channel_name
FROM
	(
	select
		partition_date,
		plugin_id,
		channel_id ,
		groupBitmapOr(err_pc_arr_state) as err_pc_arr,
		sumMerge(num_err_state) as num_err
	FROM
		bi_plugin.plugin_err_dwb_realtime_daily
	where
		partition_date = '#(partition_date)'
	group by
		partition_date,
		plugin_id,
		channel_id) e
join bi_plugin.plugin_dim pd on
	e.plugin_id = pd.plugin_id
join bi_plugin.plugin_channel_dim cd on
	e.channel_id = cd.channel_id 