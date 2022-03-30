ALTER TABLE bi_dashboard.st_game_operate_roi_statistic ADD column real_coupon_income Float32 COMMENT '实际E币券' after real_income;
ALTER TABLE bi_dashboard.st_game_operate_roi_statistic ADD column real_rmb_income Float32 COMMENT '实际人民币' after real_income;

ALTER TABLE bi_dashboard.st_game_operate_roi_statistic ADD column real_coupon_cost Float32 COMMENT '实际E币券成本' after real_cost;
ALTER TABLE bi_dashboard.st_game_operate_roi_statistic ADD column real_channel_cost Float32 COMMENT '实际渠道成本' after real_cost;
ALTER TABLE bi_dashboard.st_game_operate_roi_statistic ADD column real_cp_cost Float32 COMMENT '实际CP成本' after real_cost;

ALTER TABLE bi_dashboard.st_game_operate_roi_statistic ADD column estimate_rmb_income Float32 COMMENT '预估人民币' after real_roi;
ALTER TABLE bi_dashboard.st_game_operate_roi_statistic ADD column estimate_coupon_income Float32 COMMENT '预估E币券' after real_roi;

ALTER TABLE bi_dashboard.st_game_operate_roi_statistic ADD column estimate_coupon_cost Float32 COMMENT '预估E币券成本' after estimate_cost;
ALTER TABLE bi_dashboard.st_game_operate_roi_statistic ADD column estimate_channel_cost Float32 COMMENT '预估渠道成本' after estimate_cost;
ALTER TABLE bi_dashboard.st_game_operate_roi_statistic ADD column estimate_cp_cost Float32 COMMENT '预估CP成本' after estimate_cost;