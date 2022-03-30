INSERT
INTO bi_plugin.plugin_dwb_realtime_daily_000(partition_date,
                                             start_pc_arr,
                                             conf_pc_arr,
                                             effect_pc_arr,
                                             click_pc_arr,
                                             num_start,
                                             num_conf,
                                             num_effect,
                                             num_click)
select pdr.partition_date as partition_date,
       pdr.start_pc_arr   as start_pc_arr,
       pdr.conf_pc_arr    as conf_pc_arr,
       pdr.effect_pc_arr  as effect_pc_arr,
       pdr.click_pc_arr   as click_pc_arr,
       pdr.num_start      as num_start,
       pdr.num_conf       as num_conf,
       pdr.num_effect     as num_effect,
       pdr.num_click      as num_click
from (
         select partition_date,
                groupBitmapOr(start_pc_arr_state)  as start_pc_arr,
                groupBitmapOr(conf_pc_arr_state)   as conf_pc_arr,
                groupBitmapOr(effect_pc_arr_state) as effect_pc_arr,
                groupBitmapOr(click_pc_arr_state)  as click_pc_arr,
                sumMerge(num_start_state)          as num_start,
                sumMerge(num_conf_state)           as num_conf,
                sumMerge(num_effect_state)         as num_effect,
                sumMerge(num_click_state)          as num_click
         from bi_plugin.plugin_dwb_realtime_daily
         where partition_date = '#(partition_date)'
         group by partition_date) pdr