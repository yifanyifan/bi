INSERT
	INTO
	bi_plugin.plugin_dwb_realtime_daily_110(partition_date ,
	plugin_id ,
	channel_id,
	start_pc_arr ,
	conf_pc_arr ,
	effect_pc_arr ,
	click_pc_arr ,
	num_start ,
	num_conf ,
	num_effect ,
	num_click ,
	plugin_name ,
	channel_name ) 
select
	pdr.partition_date as partition_date,
	pdr.plugin_id as plugin_id,
	pdr.channel_id as channel_id ,
	pdr.start_pc_arr as start_pc_arr ,
	pdr.conf_pc_arr as conf_pc_arr ,
	pdr.effect_pc_arr as effect_pc_arr ,
	pdr.click_pc_arr as click_pc_arr ,
	pdr.num_start as num_start ,
	pdr.num_conf as num_conf ,
	pdr.num_effect as num_effect ,
	pdr.num_click as num_click ,
	pd.plugin_name as plugin_name,
	pcd.channel_name as channel_name
from
	(
	select
		partition_date,
		plugin_id,
		channel_id,
		groupBitmapOr(start_pc_arr_state) as start_pc_arr,
		groupBitmapOr(conf_pc_arr_state) as conf_pc_arr,
		groupBitmapOr(effect_pc_arr_state) as effect_pc_arr,
		groupBitmapOr(click_pc_arr_state) as click_pc_arr,
		sumMerge(num_start_state) as num_start,
		sumMerge(num_conf_state) as num_conf,
		sumMerge(num_effect_state) as num_effect,
		sumMerge(num_click_state) as num_click
	from
		bi_plugin.plugin_dwb_realtime_daily 
	where 
	    partition_date = '#(partition_date)' 
	group by
		partition_date,
		plugin_id ,
		channel_id) pdr
join bi_plugin.plugin_dim pd on
	pdr.plugin_id = pd.plugin_id
join bi_plugin.plugin_channel_dim pcd on
	pdr.channel_id = pcd.channel_id