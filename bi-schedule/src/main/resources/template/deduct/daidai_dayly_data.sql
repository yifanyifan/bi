with startpc as (
    select
        substring(cid, 1, 15) as pid,
        count(distinct mac) as pc
    from
        lau_dd_start
    where
        timeline = '#(dateStr)'
        and info = 'start'
    group by
        substring(cid, 1, 15)
),
uvtable as (
    select
        substring(channelid, 1, 15) as pid,
        sum(uv) as uv
    from
        project.bdtj_uv
    where
        ctime = '#(dateStr)'
        and businesses = 'daidai'
    group by
        substring(channelid, 1, 15)
),
reg as (
    select
        substring(reg_channel, 1, 15) as pid,
        count(1) as reg_count
    from
        dd.daidai_weplay_user_list_new
    where
        reg_time = '#(dateStr)'
    group by
        substring(reg_channel, 1, 15)
),
pay as (
    select
        substring(b.reg_channel, 1, 15) as pid,
        cast(sum(a.diamond_amount / 100) as decimal(18, 4)) as pay_fee,
        count(distinct a.id) as pay_count
    from
        dd.weplay_recharge_order a
        left join dd.daidai_weplay_user_list_new b on a.uid = b.uid
    where
        from_unixtime(unix_timestamp(ctime), 'yyyyMMdd') = '#(dateStr)'
        and pay_status = 2
        and a.uid not in (
            select
                distinct cast(uid as bigint)
            from
                ad_hoc.weplay_test_id
        )
    group by
        substring(b.reg_channel, 1, 15)
)
select
    DISTINCT '#(dateStr)' as `dateNow`,
    p.pid as pid,
    case
        when (
            p.ccid_settlement is not null
            and p.ccid_settlement != ''
        ) then p.ccid_settlement
        else p.ccid
    end as ccid,
    '320000' as product_code,
    '带带' as product_name,
    case
        when (
            p.channel_id_settlement is not null
            and p.channel_id_settlement != 0
        ) then p.channel_id_settlement
        else p.channel_id
    end as channel_id,
    case
        when (
            p.channel_name_settlement is not null
            and p.channel_name_settlement != ''
        ) then p.channel_name_settlement
        else p.channel_name
    end as channel_name,
    p.sub_channel_id as sub_channel_id,
    p.sub_channel_name as sub_channel_name,
    p.charge_rule as charge_rule,
    coalesce(pc, 0) as pc,
    coalesce(uv, 0) as uv,
    coalesce(reg_count, 0) as reg_count,
    coalesce(pay_fee, 0) as pay_fee,
    coalesce(pay_count, 0) as pay_order_count,
    current_timestamp() as created_at
from
    startpc full
    outer join uvtable on upper(startpc.pid) = upper(uvtable.pid) full
    outer join reg on upper(coalesce(startpc.pid, uvtable.pid)) = upper(reg.pid) full
    outer join pay on upper(coalesce(startpc.pid, uvtable.pid, reg.pid)) = upper(pay.pid)
    left outer join tfc_center.dm_channel_promotion_all p on (
        upper(
            coalesce(startpc.pid, uvtable.pid, reg.pid, pay.pid)
        ) = upper(p.pid)
        and p.flag = 1
    )
where
    coalesce(startpc.pid, uvtable.pid, reg.pid, pay.pid) is not null
    and coalesce(startpc.pid, uvtable.pid, reg.pid, pay.pid) != ''
    and (
        p.pid is not null
        and p.pid != ''
    )