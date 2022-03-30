package com.stnts.bi.schedule.sys;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.stnts.bi.schedule.common.BiJob;

/**
 * @author liang.zhang
 * @date 2020年5月19日
 * @desc TODO
 * 将用户中心用户同步到BI系统  每小时跑一次
 */
@Component
public class UserSyncJob implements BiJob{

	@Scheduled(cron = "0 0 0/1 * * *")
	@Override
	public void work() {


	}
}
