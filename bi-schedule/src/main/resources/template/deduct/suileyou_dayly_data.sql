SELECT
    _t1.key1 as dateNow,
    _t2.pid as pid,
    _t2.ccid as ccid,
    _t1.product_code as product_code,
    multiIf(
        product_code = '270200',
        '比特球云盘',
        product_code = '380001',
        '随乐游',
        ''
    ) as product_name,
    _t2.channel_id as channel_id,
    _t2.channel_name as channel_name,
    _t2.sub_channel_id as sub_channel_id,
    _t2.sub_channel_name as sub_channel_name,
    _t2.charge_rule as charge_rule,
    _t1.value1 as uv,
    _t1.value3 as reg_count,
    _t1.value2 as pay_fee
from
    (
        select
            toDate(dt) as key1,
            product_id as product_code,
            upper(source_pid) as pid,
            bitmapCardinality(
                bitmapAndnot(
                    groupBitmapOrState(uuid_bit),
                    groupBitmapOrState(uuid_with_uid_bit)
                )
            ) + groupBitmapOr(uid_bit) as value1,
            arraySum(maxMap(pay_fee).2) as value2,
            groupBitmapOr(register_user_bit) as value3
        from
            bi_sdk.rt_ads_sdk_all_agg_d
        where
            (
                1 = 1
                and product_id in ('270200', '380001')
                and toDate(dt) = '#(dateStr)'
            )
        group by
            toDate(dt),
            product_id,
            upper(source_pid)
    ) as _t1
    join bi_sdk.mysql_dm_channel_promotion_all_view as _t2 on (
        upper(_t2.product_code) = upper(_t1.product_code)
        and upper(_t2.pid) = upper(_t1.pid)
    )
where
    (
        (
            _t2.product_code = '270200'
            and _t2.department_code = '01.03'
        )
        or (
            _t2.product_code = '380001'
            and _t2.department_code = '01.03'
        )
    )
    and (
        _t2.pid is not null
        and _t2.pid != ''
    )