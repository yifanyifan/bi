(SELECT
     product,
     os_type,
     `agent_name`,
     `channel_name`,
     `cid_name`,
     `billing_name`,
     _t1.hour as date,
     _t1.active_user,
     _t2.register_user_array as yesterday_register_user
 from
     (
         SELECT
             product,
             os_type,
             `agent_name`,
             `channel_name`,
             `cid_name`,
             `billing_name`,
             hour_active_user_tuple[i].1 as hour,
             arrayReduce(
                 'groupArrayArray',
                 arrayConcat(arrayMap(x -> x.2, arraySlice(hour_active_user_tuple, 1, i)))
             ) as active_user
         from
             (
                 SELECT
                     product,
                     os_type,
                     `agent_name`,
                     `channel_name`,
                     `cid_name`,
                     `billing_name`,
                     groupArray(tuple(hour, active_user_array)) as hour_active_user_tuple
                 from
                     (select
                             *
                         from
                             (SELECT
                                     product,
                                     os_type,
                                     `agent_name`,
                                     `channel_name`,
                                     `cid_name`,
                                     `billing_name`,
                                     date as hour,
                                     groupArrayArray(active_user) as active_user_array
                                 from
                                     banyan_bi_sdk.view_sdk_app_sdk_web_register_info
                                 where
                                     timeline = '#(timeline)'
                                 group by
                                     product,
                                     os_type,
                                     `agent_name`,
                                     `channel_name`,
                                     `cid_name`,
                                     `billing_name`,
                                     date
                             ) as _t10 full
                             join (
                                 select
                                     *
                                 from
                                     (
                                         select
                                             distinct product,
                                             os_type,
                                             `agent_name`,
                                             `channel_name`,
                                             `cid_name`,
                                             `billing_name`
                                         from
                                             banyan_bi_sdk.view_sdk_app_sdk_web_register_info
                                         where
                                             view_sdk_app_sdk_web_register_info.timeline = '#(timeline)'
                                     ) as _t11,
                                     (select toDateTime('#(timeline) 00:00:00') as hour UNION all
                                     select toDateTime('#(timeline) 01:00:00') as hour UNION all
                                     select toDateTime('#(timeline) 02:00:00') UNION all
                                     select toDateTime('#(timeline) 03:00:00') UNION all
                                     select toDateTime('#(timeline) 04:00:00') UNION all
                                     select toDateTime('#(timeline) 05:00:00') UNION all
                                     select toDateTime('#(timeline) 06:00:00') UNION all
                                     select toDateTime('#(timeline) 07:00:00') UNION all
                                     select toDateTime('#(timeline) 08:00:00') UNION all
                                     select toDateTime('#(timeline) 09:00:00') UNION all
                                     select toDateTime('#(timeline) 10:00:00') UNION all
                                     select toDateTime('#(timeline) 11:00:00') UNION all
                                     select toDateTime('#(timeline) 12:00:00') UNION all
                                     select toDateTime('#(timeline) 13:00:00') UNION all
                                     select toDateTime('#(timeline) 14:00:00') UNION all
                                     select toDateTime('#(timeline) 15:00:00') UNION all
                                     select toDateTime('#(timeline) 16:00:00') UNION all
                                     select toDateTime('#(timeline) 17:00:00') UNION all
                                     select toDateTime('#(timeline) 18:00:00') UNION all
                                     select toDateTime('#(timeline) 19:00:00') UNION all
                                     select toDateTime('#(timeline) 20:00:00') UNION all
                                     select toDateTime('#(timeline) 21:00:00') UNION all
                                     select toDateTime('#(timeline) 22:00:00') UNION all
                                     select toDateTime('#(timeline) 23:00:00')) as _t12
                             ) as _t13 using product,
                             os_type,
                             `agent_name`,
                             `channel_name`,
                             `cid_name`,
                             `billing_name`,
                             hour
                         where hour <= now()
                         order by
                             hour
                     )
                 group by
                     product,
                     os_type,
                     `agent_name`,
                     `channel_name`,
                     `cid_name`,
                     `billing_name`
             ) array
             join arrayEnumerate(hour_active_user_tuple) AS i
     ) as _t1
     full join (select * from
                (select toDateTime('#(timeline) 00:00:00') as hour UNION all
                select toDateTime('#(timeline) 01:00:00') as hour UNION all
                select toDateTime('#(timeline) 02:00:00') UNION all
                select toDateTime('#(timeline) 03:00:00') UNION all
                select toDateTime('#(timeline) 04:00:00') UNION all
                select toDateTime('#(timeline) 05:00:00') UNION all
                select toDateTime('#(timeline) 06:00:00') UNION all
                select toDateTime('#(timeline) 07:00:00') UNION all
                select toDateTime('#(timeline) 08:00:00') UNION all
                select toDateTime('#(timeline) 09:00:00') UNION all
                select toDateTime('#(timeline) 10:00:00') UNION all
                select toDateTime('#(timeline) 11:00:00') UNION all
                select toDateTime('#(timeline) 12:00:00') UNION all
                select toDateTime('#(timeline) 13:00:00') UNION all
                select toDateTime('#(timeline) 14:00:00') UNION all
                select toDateTime('#(timeline) 15:00:00') UNION all
                select toDateTime('#(timeline) 16:00:00') UNION all
                select toDateTime('#(timeline) 17:00:00') UNION all
                select toDateTime('#(timeline) 18:00:00') UNION all
                select toDateTime('#(timeline) 19:00:00') UNION all
                select toDateTime('#(timeline) 20:00:00') UNION all
                select toDateTime('#(timeline) 21:00:00') UNION all
                select toDateTime('#(timeline) 22:00:00') UNION all
                select toDateTime('#(timeline) 23:00:00')) as _t4,
                (SELECT
                    product,
                     os_type,
                     `agent_name`,
                     `channel_name`,
                     `cid_name`,
                     `billing_name`,
                     groupArrayArray(register_user) as register_user_array
                 from
                     banyan_bi_sdk.view_sdk_app_sdk_web_register_info
                 where
                     timeline = subtractDays(toDate('#(timeline)'), 1)
                 group by
                     product,
                     os_type,
                     `agent_name`,
                     `channel_name`,
                     `cid_name`,
                     `billing_name`) as _t5 where hour <= now()) as _t2 using product,os_type,
     `agent_name`,
     `channel_name`,
     `cid_name`,
     `billing_name`,
     hour)