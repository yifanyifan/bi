package com.stnts.tc.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.stnts.tc.common.Constants;
import com.stnts.tc.common.CycleEnum;

import cn.hutool.core.date.DateUnit;

/**
 * @author liang.zhang
 * @date 2019年11月18日
 * @desc TODO
 */
public class DateUtil {
	
	/**
	 * 获取日期是全年的第多少天
	 * @param date
	 * @return
	 */
	public static int dayOfYear(Date date) {
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_YEAR);
	}
	
	
	public static int dayOfYear(String date) throws ParseException {
		
		Date d = new SimpleDateFormat("yyyy-MM-dd").parse(date);
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		return cal.get(Calendar.DAY_OF_YEAR);
	}
	
	/**
	 * 获取日期是全年第多少周
	 * @param date
	 * @return
	 */
	public static int weekOfYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int week = cal.get(Calendar.WEEK_OF_YEAR);
		cal.add(Calendar.DAY_OF_YEAR, -7);
		int _week = cal.get(Calendar.WEEK_OF_YEAR);
		return week < _week ? _week : week;
	}
	
	/**
	 * 比较是不是同一年
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static boolean isSameYear(String d1, String d2) {
		return d1.substring(0, 4).equals(d2.substring(0, 4));
	}
	
	/**
	 * 两日期之间间隔多少天
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static int gapDays(Date d1, Date d2) {
		return gap(d1, d2, CycleEnum.DAY);
	}
	
	/**
	 * 昨天的日期
	 * @return
	 */
	public static Date yesterday() {
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_MONTH, -1);
		return cal.getTime();
	}
	
	public static String yesterday2str() {
		return new SimpleDateFormat("yyyy-MM-dd").format(yesterday());
	}
	
	/**
	 * 上一周
	 * @return
	 */
	public static Date preWeek() {
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.WEEK_OF_YEAR, -1);
		return cal.getTime();
	}
	
	/**
	 * 日期增加..
	 * @param volumn
	 * @param cycle
	 * @return
	 */
	public static Date addDate(Date date, int volumn, CycleEnum cycle) {
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		switch (cycle) {
			case WEEK : {
				cal.add(Calendar.WEEK_OF_MONTH, volumn);
			}; break;
			case MONTH : {
				cal.add(Calendar.MONTH, volumn);
			}; break;
			default : {
				cal.add(Calendar.DAY_OF_MONTH, volumn);
			}
		}
		return cal.getTime();
	}
	
	public static Date addDate(int volumn, CycleEnum cycle) {
		return addDate(new Date(), volumn, cycle);
	}
	
	public static Date preMonth() {
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MONTH, -1);
		return cal.getTime();
	}
	
	/**
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date addWeek(Date date, int weeks) {
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.WEEK_OF_YEAR, weeks);
		return cal.getTime();
	}
	
	/**
	 * 两日期之间间隔多少个月
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static int gapMonths(Date d1, Date d2) {
		return gap(d1, d2, CycleEnum.MONTH);
	}
	
	/**
	 * 两日期之间间隔多少年
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static int gapYears(Date d1, Date d2) {
		return gap(d1, d2, CycleEnum.YEAR);
	}
	
	/**
	 * 两字符串日期相差多少年
	 * @param d1
	 * @param d2
	 * @return
	 * @throws Exception
	 */
	public static int gapYears(String d1, String d2) throws Exception {
		return year(d2) - year(d1);
	}
	
	public static int gap(Date d1, Date d2, CycleEnum cycle) {
		
		LocalDate ld1 = date2LocalDate(d1);
		LocalDate ld2 = date2LocalDate(d2);
		Period period = Period.between(ld1, ld2);
		switch (cycle) {
			case MONTH : return period.getMonths();
			case YEAR : return period.getYears();
			default : return period.getDays();
		}
	}
	
	/**
	 * 返回当前年有多少天  多少周
	 * @param year
	 * @return
	 */
	public static int include(int year, CycleEnum cycle) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, 11, 31, 23, 59, 59);
		Date d = cal.getTime();
		switch (cycle) {
			case WEEK : return weekOfYear(d);
			case MONTH : return 12;
			case YEAR : return 1;
			default : return dayOfYear(d);
		}
	}
	
	/**
	 * 当前year有多少天
	 * @param year
	 * @return
	 */
	public static int days(int year) {
		return include(year, CycleEnum.DAY);
	}
	
	/**
	 * 当前year有多少年
	 * @param year
	 * @return
	 */
	public static int weeks(int year) {
		return include(year, CycleEnum.WEEK);
	}
	
	public static LocalDate date2LocalDate(Date d) {
		Instant instant = d.toInstant();
		ZoneId zone = ZoneId.systemDefault();
		return LocalDateTime.ofInstant(instant, zone).toLocalDate();
	}
	
	/**
	 * 比较是不是同一年
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static boolean isSameYear(Date d1, Date d2) {
		return year(d1) == year(d2);
	}
	
//	public static boolean isSameYear(String d1, String d2) {
//		return Integer.parseInt(d2.substring(0, 4)) - Integer.parseInt(d1.substring(0, 4));
//	}
	
	/**
	 * 根据今年是第多少天获取日期
	 * @param dayOfYear
	 * @param formatter
	 * @return
	 */
	public static String dateByDay(int dayOfYear, String formatter) {
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_YEAR, dayOfYear);
		Date d = cal.getTime();
		return new SimpleDateFormat(formatter).format(d);
	}
	
	public static String dateByDay(int dayOfYear) {
		return dateByDay(dayOfYear, "yyyy-MM-dd");
	}
	
	/**
	 * 返回年份
	 * @param d
	 * @return
	 */
	public static int year(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		return cal.get(Calendar.YEAR);
	}
	
	public static int month(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		return cal.get(Calendar.MONTH) + 1;  //少一个月
	}
	
	public static Date str2Date(String date, String formatter) throws Exception{
		return new SimpleDateFormat(formatter).parse(date);
	}
	
	public static Date str2Date(String date) throws Exception{
		return str2Date(date, "yyyy-MM-dd");
	}
	
	/**
	 * 获取年份
	 * @param d
	 * @return
	 * @throws Exception
	 */
	public static int year(String d) throws Exception {
		return year(str2Date(d));
	}
	
	/**
	 * 日期差值
	 * @param srcBeginDate
	 * @param date
	 * @param cycle
	 * @return
	 */
	public static int between(String srcBeginDate, Date date, CycleEnum cycle) {
		
		int interval = 0;
		Date referDate = null;
		try {
			switch(cycle) {
				case DAY : {
					referDate = new SimpleDateFormat("yyyy-MM-dd").parse(srcBeginDate);
					interval = (int)cn.hutool.core.date.DateUtil.betweenDay(referDate, date, true);
				}; break;
				case WEEK : {
					//通过第多少周找到日期
					referDate = getDateByWeek(srcBeginDate);
					interval = (int)cn.hutool.core.date.DateUtil.between(referDate, date, DateUnit.WEEK);
				}; break;
				case MONTH : {
					referDate = new SimpleDateFormat("yyyy-MM").parse(srcBeginDate);
					interval = (int)cn.hutool.core.date.DateUtil.betweenMonth(referDate, date, true);
				}; break;
				default : {}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//		System.out.println("referDate: " + new SimpleDateFormat("yyyy-MM-dd").format(referDate));
//		System.out.println("date:" + new SimpleDateFormat("yyyy-MM-dd").format(date));
		return interval;
	}
	
	/**
	 * 日期差值
	 * @param srcBeginDate
	 * @param date
	 * @param cycle
	 * @return
	 */
	public static int between(String srcBeginDate, String srcEndDate, CycleEnum cycle) {
		
		int interval = -2;
		try {
			switch(cycle) {
				case DAY : {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Date beginDate = sdf.parse(srcBeginDate);
					Date endDate = sdf.parse(srcEndDate);
					interval = (int)cn.hutool.core.date.DateUtil.betweenDay(beginDate, endDate, true);
				}; break;
				case WEEK : {
					
					Date beginDate = getDateByWeek(srcBeginDate);
					Date endDate = getDateByWeek(srcEndDate);
					interval = (int)cn.hutool.core.date.DateUtil.between(beginDate, endDate, DateUnit.WEEK);
				}; break;
				case MONTH : {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
					Date beginDate = sdf.parse(srcBeginDate);
					Date endDate = sdf.parse(srcEndDate);
					interval = (int)cn.hutool.core.date.DateUtil.betweenMonth(beginDate, endDate, true);
				}; break;
				default : {}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return interval + 1;
	}
	
	public static Date getDateByWeek(String weekStr) throws Exception {
		
		String[] srcBeginDates = StringUtils.split(weekStr, Constants.KEY_DATE_SPLIT);
		String year = srcBeginDates[0];
		String firstOfYear = year.concat("-01-01");
		int weekLag = Integer.parseInt(srcBeginDates[1]);
		Date firstDateOfYear = str2Date(firstOfYear);
		System.out.println(new SimpleDateFormat("yyyy-MM-dd").format(firstDateOfYear));
		System.out.println("weekLag: " + weekLag);
		return addWeek(firstDateOfYear, weekLag);
	}
	
	public static Date toDate(int year, int col, String cycle) {
		
		Date date = null;
//		String fmt = "yyyy-MM-dd";
		try {
			
			int _year = DateUtil.year(new Date());
			CycleEnum _cycle = CycleEnum.cycle(cycle);
			int y = Integer.parseInt(String.valueOf(_year).substring(0, 2).concat(String.valueOf(year)));
			Calendar cal = Calendar.getInstance();
			cal.set(y, 0, 1, 0, 0, 0);
			Date firstDay = cal.getTime();
			int colLag = _cycle.compareTo(CycleEnum.WEEK) == 0 ? col : col - 1;
			date = DateUtil.addDate(firstDay, colLag, _cycle);
//			switch (_cycle) {
//				case DAY : {
//					//2019
//					fmt = "yyyy-MM-dd";
//				};break;
//				case WEEK : {
//					System.out.println(y + "-" + col);
//				}; break;
//				case MONTH : {
//					fmt = "yyyy-MM";
//				}; break;
//		
//				default:
//					/** 特例先不管 */
//					break;
//			}
		} catch (Exception e) {
		}
		return date;
	}
	
	/**
	 * 获取对象的日期
	 * @return
	 */
	public String toDateString(int year, int col, String cycle) {
		
		String date = null;
		try {
			
			int _year = DateUtil.year(new Date());
			CycleEnum _cycle = CycleEnum.cycle(cycle);
			switch (_cycle) {
				case DAY : {
					//2019
					int y = Integer.parseInt(String.valueOf(_year).substring(0, 2).concat(String.valueOf(year)));
					Calendar cal = Calendar.getInstance();
					cal.set(y, 0, 1);
					Date d = cal.getTime();
					Date targetDate = DateUtil.addDate(d, col - 1, _cycle);
					date = new SimpleDateFormat("yyyy-MM-dd").format(targetDate);
				};break;
				case WEEK : {
					
				}; break;
				case MONTH : {
					
				}; break;
		
				default:
					/** 特例先不管 */
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static void main(String[] args) {
		
		try {
//			Date d1 = new SimpleDateFormat("yyyyMMdd").parse("20180304");
//			Date d2 = new SimpleDateFormat("yyyyMMdd").parse("20200304");
//			System.out.println(dayOfYear(d1));
//			System.out.println(dayOfYear(d2));
//			System.out.println("isSameYear: " + isSameYear("2019-01-02", "2018-03-04"));
//			
//			System.out.println(weeks(2016));
//			Date d = new Date();
//			System.out.println("day: " + dayOfYear(d));
//			System.out.println("week: " + weekOfYear(d));
//			Date preDay = addDate(-1, CycleEnum.DAY);
//			Date beginDay = addDate(preDay, -30, CycleEnum.DAY);
//			System.out.println("preDay: " + dayOfYear(preDay));
//			System.out.println("beginDay: " + dayOfYear(beginDay));
//			Date preWeek = addDate(-1, CycleEnum.WEEK);
//			Date beginWeek = addDate(preWeek, -7, CycleEnum.WEEK);
//			System.out.println("preWeek: " + weekOfYear(preWeek));
//			System.out.println("beginWeek: " + weekOfYear(beginWeek));
//			
//			System.out.println(preMonth());
			System.out.println(addWeek(str2Date("2020-01-01"), 2));
			
			System.out.println(between("2019-1", "2019-2", CycleEnum.MONTH));
			System.out.println(include(2019, CycleEnum.DAY));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
