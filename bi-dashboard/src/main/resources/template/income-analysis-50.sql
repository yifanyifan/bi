(
select
	`category1` as category1,
	`category2` as category2,
	sum(`estimate_income`) as estimate_income,
	sum(`estimate_cost`) as estimate_cost,
	sum(`real_income`) as real_income,
	sum(`real_cost`) as real_cost,
	any(income_target_s) as income_target_s,
    any(income_target_a) as income_target_a,
    any(income_target_b) as income_target_b,
    any(income_target_c) as income_target_c,
    any(income_target_d) as income_target_d,
    any(profit_target_s) as profit_target_s,
    any(profit_target_a) as profit_target_a,
    any(profit_target_b) as profit_target_b,
    any(profit_target_c) as profit_target_c,
    any(profit_target_d) as profit_target_d
from
	bi_dashboard.st_game_operate_roi_statistic
where
	(1 = 1
	and toDate(`date_time`) >= '#(startDate)'
	and toDate(`date_time`) <= '#(endDate)'
	#for(x : stringMap)
      #if(x.value != null && x.value != '')
        and #(x.key) = '#(x.value)'
      #end
    #end
    #for(x : inMap)
      #if(x.value != null)
        and #(x.key) in #(x.value)
      #end
    #end
	)

group by
	`category1`,
	`category2`
)