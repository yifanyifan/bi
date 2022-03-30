INSERT
	INTO
	bi_plugin.plugin_err_dwb_realtime_daily_010( partition_date ,
	channel_id ,
	err_pc_arr ,
	num_err,
	channel_name )
SELECT
	e.*,
	cd.channel_name
FROM
	(
	select
		partition_date,
		channel_id ,
		groupBitmapOr(err_pc_arr_state) as err_pc_arr,
		sumMerge(num_err_state) as num_err
	FROM
		bi_plugin.plugin_err_dwb_realtime_daily
	where
		partition_date = '#(partition_date)'
	group by
		partition_date,
		channel_id ) e
join bi_plugin.plugin_channel_dim cd on
	e.channel_id = cd.channel_id 