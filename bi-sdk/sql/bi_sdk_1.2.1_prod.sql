

ALTER TABLE banyan_bi_sdk.sdk_app_web_local ADD column `visitor` Array(String) after new_uv;

ALTER TABLE banyan_bi_sdk.sdk_app_sdk_web_local ADD column `visitor` Array(String) after new_uv;

drop TABLE banyan_bi_sdk.view_sdk_app_sdk_web ON CLUSTER cluster_group01;
CREATE TABLE banyan_bi_sdk.view_sdk_app_sdk_web ON
CLUSTER cluster_group01 as banyan_bi_sdk.sdk_app_sdk_web_local ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
sdk_app_sdk_web_local);

ALTER TABLE banyan_bi_sdk.sdk_app_sdk_web_register_info_local ADD column `visitor` Array(String) after new_uv;

drop TABLE banyan_bi_sdk.view_sdk_app_sdk_web_register_info ON CLUSTER cluster_group01;
CREATE TABLE banyan_bi_sdk.view_sdk_app_sdk_web_register_info ON
CLUSTER cluster_group01 as banyan_bi_sdk.sdk_app_sdk_web_register_info_local ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
sdk_app_sdk_web_register_info_local);


ALTER TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_d_local ADD column `os_name` String after pay_amount;
ALTER TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_d_local ADD column `os_type` UInt8 after pay_amount;

ALTER TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_w_local ADD column `os_name` String after pay_amount;
ALTER TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_w_local ADD column `os_type` UInt8 after pay_amount;

ALTER TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_m_local ADD column `os_name` String after pay_amount;
ALTER TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_m_local ADD column `os_type` UInt8 after pay_amount;

drop TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_d ON CLUSTER cluster_group01;
CREATE TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_d ON
CLUSTER cluster_group01 as banyan_bi_sdk.dws_bi_sdk_user_retain_d_local ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
dws_bi_sdk_user_retain_d_local);

drop TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_m ON CLUSTER cluster_group01;
CREATE TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_m ON
CLUSTER cluster_group01 as banyan_bi_sdk.dws_bi_sdk_user_retain_m_local ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
dws_bi_sdk_user_retain_m_local);

drop TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_w ON CLUSTER cluster_group01;
CREATE TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_w ON
CLUSTER cluster_group01 as banyan_bi_sdk.dws_bi_sdk_user_retain_w_local ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
dws_bi_sdk_user_retain_w_local);