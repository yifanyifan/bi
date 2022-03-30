insert into banyan_bi_sdk.view_payment_info_register_info_local
SELECT
    assumeNotNull(payment_info_local.date) AS date,
    assumeNotNull(payment_info_local.os_type) AS os_type,
    multiIf(os_type = 1, 'Web', os_type = 7, 'PC', os_type = 4, 'H5', os_type = 2, 'iOS', os_type = 3, 'Android', os_type = 6, '微信小程序', os_type = 8, '微信小游戏', os_type = 9, 'TV', 'other') AS os_name,
    assumeNotNull(payment_info_local.product) AS product,
    assumeNotNull(payment_info_local.channel) AS original_channel,
    assumeNotNull(payment_info_local.uid) AS uid,
    if(isNull(register_info_local_final.agent_name) OR (register_info_local_final.agent_name = ''), '其他', register_info_local_final.agent_name) AS agent_name,
    if(isNull(register_info_local_final.channel_name) OR (register_info_local_final.channel_name = ''), '其他', register_info_local_final.channel_name) AS channel_name,
    if(isNull(register_info_local_final.cid_name) OR (register_info_local_final.cid_name = ''), '其他', register_info_local_final.cid_name) AS cid_name,
    if(isNull(register_info_local_final.billing_name) OR (register_info_local_final.billing_name = ''), '其他', register_info_local_final.billing_name) AS billing_name,
    payment_info_local.pay_fee AS pay_fee,
    payment_info_local.pay_frequency AS pay_frequency,
    payment_info_local.pay_order_ids AS pay_order_ids,
    assumeNotNull(register_info_local_final.date) AS register_date,
    assumeNotNull(register_info_local_final.os_type) AS register_os_type,
    payment_info_local.create_time AS create_time,
    toDate(payment_info_local.timeline) AS timeline
FROM
    banyan_bi_sdk.payment_info_local
    LEFT JOIN
    (SELECT
        uid,
        date,
        assumeNotNull(product) AS product,
        if(isNull(bi_channel_maintain.agent_name) OR (bi_channel_maintain.agent_name = ''), '其他', bi_channel_maintain.agent_name) AS agent_name,
        if(isNull(bi_channel_maintain.channel_name) OR (bi_channel_maintain.channel_name = ''), '其他', bi_channel_maintain.channel_name) AS channel_name,
        if(isNull(bi_channel_maintain.cid_name) OR (bi_channel_maintain.cid_name = ''), '其他', bi_channel_maintain.cid_name) AS cid_name,
        if(isNull(bi_channel_maintain.billing_name) OR (bi_channel_maintain.billing_name = ''), '其他', bi_channel_maintain.billing_name) AS billing_name,
        os_type
    FROM
        (SELECT product, uid, argMax(date, version) as date,argMax(channel, version) as channel,argMax(os_type, version) as os_type
        from banyan_bi_sdk.register_info_local
        where uid in (SELECT uid FROM banyan_bi_sdk.payment_info_local where toDate(payment_info_local.date) = '#(date)')
        group by product, uid) as register_info_local_final_temp
        LEFT JOIN banyan_bi_sdk.bi_channel_maintain ON
        bi_channel_maintain.product_id = register_info_local_final_temp.product AND bi_channel_maintain.billing_id = register_info_local_final_temp.channel) AS register_info_local_final
    ON payment_info_local.uid = register_info_local_final.uid AND register_info_local_final.product = payment_info_local.product
where toDate(payment_info_local.date) = '#(date)'
