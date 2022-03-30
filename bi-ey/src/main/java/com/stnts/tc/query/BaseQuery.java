package com.stnts.tc.query;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.stnts.tc.common.CycleEnum;
import com.stnts.tc.utils.DateUtil;

import lombok.Data;

/**
 * @author liang.zhang
 * @date 2019年11月20日
 * @desc TODO
 */
@Data
public class BaseQuery implements Query{
	
	protected String cycle;
	protected String srcBeginDate;
	protected String srcEndDate;
	protected String destBeginDate;
	protected String destEndDate;
	protected boolean index = true;  //是否默认界面
	
	public String beginDateDefault() {
		CycleEnum c = CycleEnum.cycle(cycle);
		switch (c) {
			case WEEK : {
				Date preWeek = DateUtil.preWeek(); 
				Date beginDate = DateUtil.addDate(preWeek, -11, CycleEnum.WEEK);
				int year = DateUtil.year(beginDate);
				int week = DateUtil.weekOfYear(beginDate);
				return String.format("%s-%s", year, week);
			}
			case MONTH : {
				Date preMonth = DateUtil.preMonth();
				Date beginDate = DateUtil.addDate(preMonth, -11, CycleEnum.MONTH);
				int year = DateUtil.year(beginDate);
				int month = DateUtil.month(beginDate);
				return String.format("%s-%s", year, month);
			}
			default : {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date preDay = DateUtil.yesterday();
				Date beginDate = DateUtil.addDate(preDay, -29, CycleEnum.DAY);
				return sdf.format(beginDate);
			}
		}
	}
	
	public String endDateDefault() {
		CycleEnum c = CycleEnum.cycle(cycle);
		switch (c) {
			case WEEK : {
				Date preWeek = DateUtil.preWeek(); 
				int year = DateUtil.year(preWeek);
				int week = DateUtil.weekOfYear(preWeek);
				return String.format("%s-%s", year, week);
			}
			case MONTH : {
				Date preMonth = DateUtil.preMonth();
				int year = DateUtil.year(preMonth);
				int month = DateUtil.month(preMonth);
				return String.format("%s-%s", year, month); 
			}
			default : {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date preDay = DateUtil.yesterday();
				return sdf.format(preDay);
			}
		}
	}
	
	/**
	 * 是否有对比
	 * @return
	 */
	public boolean isComp() {
		return StringUtils.isNotBlank(destBeginDate) && StringUtils.isNotBlank(destEndDate);
	}
	
	/**
	 * 所选日期是否跨天 跨周 或 跨月
	 * @return
	 */
	public boolean isOverDate() {
		return !StringUtils.equals(srcBeginDate, srcEndDate);
	}
	
	/**
	 *  页面是否显示同比环比
	 *  默认页面显示
	 *  选择是非跨天的日期显示
	 * @return
	 */
	public boolean isShowComp() {
		if(isComp())  //如果有对比  则不显示同环比
			return false;
		return index || !isOverDate();
	}

	@Override
	public boolean isOk() {
		return true;
	}
}
