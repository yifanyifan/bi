package com.stnts.tc.vo;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.stnts.tc.common.CycleEnum;
import com.stnts.tc.utils.DateUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liang.zhang
 * @date 2020年1月18日
 * @desc TODO
 * 统计value
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjectValue implements Comparable<ObjectValue>{

	private int year;  //年份
	private int col;  //所属周期里的第多少个周期
	private String cycle;  //周期  D  W  M
	private String v;  //值
	private int index;  //数据索引
	
	@Override
	public int compareTo(ObjectValue o) {
		
		if(this.getIndex() < o.getIndex()) {
			return -1;
		}else if(this.getIndex() > o.getIndex()) {
			return 1;
		}else {
			return 0;
		}
	}
	
	public BigDecimal getV2BigDecimal() {
		if(StringUtils.isBlank(this.getV())) {
			return null;
		}
		return new BigDecimal(this.getV());
	}
	
	/**
	 * 获取对象的日期
	 * @return
	 */
	public String toDateString() {
		
		String date = null;
		try {
			
			int _year = DateUtil.year(new Date());
			CycleEnum _cycle = CycleEnum.cycle(cycle);
			switch (_cycle) {
				case DAY : {
					//2019
					int y = Integer.parseInt(String.valueOf(_year).substring(0, 2).concat(String.valueOf(this.year)));
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
		System.out.println(date);
		return date;
	}
	
	/**
	 * 获取对象的日期
	 * @return
	 */
	public Date toDate() {
		
		return DateUtil.toDate(year, col, cycle);
//		Date date = null;
//		try {
//			
//			int _year = DateUtil.year(new Date());
//			CycleEnum _cycle = CycleEnum.cycle(cycle);
//			switch (_cycle) {
//				case DAY : {
//					//2019
//					int y = Integer.parseInt(String.valueOf(_year).substring(0, 2).concat(String.valueOf(this.year)));
//					Calendar cal = Calendar.getInstance();
//					cal.set(y, 0, 1);
//					Date d = cal.getTime();
//					date = DateUtil.addDate(d, col - 1, _cycle);
//				};break;
//				case WEEK : {
//					
//				}; break;
//				case MONTH : {
//					
//				}; break;
//		
//				default:
//					/** 特例先不管 */
//					break;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		System.out.println(new SimpleDateFormat("yyyy-MM-dd").format(date));
//		return date;
	}
	
	public static void main(String[] args) {
		
		Date date = new ObjectValue(20, 48, "D", "1", 0).toDate();
		System.out.println(DateUtil.between("2020-01-18", date, CycleEnum.DAY));
	}
}
