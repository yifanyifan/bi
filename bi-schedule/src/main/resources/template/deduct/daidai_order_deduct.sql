select
    DISTINCT a.id as order_id,
    a.uid,
    (a.diamond_amount / 100) as pay_fee,
    a.pay_type,
    a.ctime,
    a.channel_id as pay_pid,
    b.reg_channel as reg_pid,
    '320000' as product_code,
    '带带' as product_name,
    c.channel_id,
    c.channel_name,
    c.sub_channel_id,
    c.sub_channel_name,
    c.ccid as reg_ccid,
    0 as deductStatus,
    c.charge_rule
from
    dd.weplay_recharge_order a
    left join dd.daidai_weplay_user_list_new b on a.uid = b.uid
    left join (
        select
            DISTINCT pid,
            case
                when (
                    ccid_settlement is not null
                    and ccid_settlement != ''
                ) then ccid_settlement
                else ccid
            end as ccid,
            product_code,
            product_name,
            case
                when (
                    channel_id_settlement is not null
                    and channel_id_settlement != 0
                ) then channel_id_settlement
                else channel_id
            end as channel_id,
            case
                when (
                    channel_name_settlement is not null
                    and channel_name_settlement != ''
                ) then channel_name_settlement
                else channel_name
            end as channel_name,
            sub_channel_id,
            sub_channel_name,
            charge_rule
        from
            tfc_center.dm_channel_promotion_all
        where
            product_code = '320000'
            and from_unixtime(unix_timestamp(check_start_date), 'yyyyMMdd') <= '#(dateStr)'
            and from_unixtime(unix_timestamp(check_end_date), 'yyyyMMdd') >= '#(dateStr)'
            and flag = 1
    ) c on upper(b.reg_channel) = upper(c.pid)
where
    from_unixtime(unix_timestamp(ctime), 'yyyyMMdd') = '#(dateStr)'
    and pay_status = 2
    and a.uid not in (
        select
            distinct cast(uid as bigint)
        from
            ad_hoc.weplay_test_id
    )