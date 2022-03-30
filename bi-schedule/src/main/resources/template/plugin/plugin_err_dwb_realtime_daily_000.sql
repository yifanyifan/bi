 INSERT
	INTO
	bi_plugin.plugin_err_dwb_realtime_daily_000( partition_date ,
	err_pc_arr ,
	num_err )
SELECT
	partition_date,
	groupBitmapOr(err_pc_arr_state) as err_pc_arr,
	sumMerge(num_err_state) as num_err
FROM
	bi_plugin.plugin_err_dwb_realtime_daily 
	where partition_date = '#(partition_date)' 
group by
	partition_date 