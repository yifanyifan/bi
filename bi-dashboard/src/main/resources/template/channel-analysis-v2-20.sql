(SELECT
  agg_dim,
  date_time,
      if(`category1` = '', '-', `category1`) as category1,
      if(`category2` = '', '-', `category2`) as category2,
      if(`category3` = '', '-', `category3`) as category3,
      if(`customer_id` = '', '-', `customer_id`) as customer_id,
      if(`customer_name` = '', '-', `customer_name`) as customer_name,
      if(`cooperate_type` = '', '-', `cooperate_type`) as cooperate_type,
      if(`agent_company_name` = '', '-', `agent_company_name`) as agent_company_name,
      if(`agent_id` = '', '-', `agent_id`) as agent_id,
      if(`channel_id` = '', '-', `channel_id`) as channel_id,
      if(`cid_id` = '', '-', `cid_id`) as cid_id,
      if(`billing_type` = '', '-', `billing_type`) as billing_type,
      game_category,
      if(`game_category_name` = '', '-', `game_category_name`) as game_category_name,
      if(`game_code` = '', '-', `game_code`) as game_code,
      if(`game_name` = '', '-', `game_name`) as game_name,
      netbar_type,
      source_tag,
  reg_cnts,
  reg_array,
  newlogin_cnts,
  newlogin_array,
  active_cnts,
  active_array,
  newpay_users,
  newpay_array,
  newpay_money,
  pay_users,
  pay_array,
  pay_cnts,
  pay_money,
  drate1,
  LTV1,
  LTV7,
  LTV15,
  LTV30,
  arpu,
  arppu,
  real_income,
  real_cost,
  real_cp_cost,
  real_channel_cost,
  real_other_cost,
  real_roi,
  real_profit,
  timeline
FROM
  bi_dashboard.st_game_operate_channel_statistic)