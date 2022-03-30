package com.stnts.tc.vo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import com.stnts.tc.common.CycleEnum;
import com.stnts.tc.utils.DateUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ObjValue implements Comparable<ObjValue>{
	
	private int year;
	private int col;
	private String cycle;
	private Object v;
	
	private int sortK() {
		
		int fillLen = 3 - String.valueOf(col).length();
		String fill = fillLen == 0 ? "" : String.join("", Collections.nCopies(fillLen, "0"));
		return Integer.parseInt(String.format("%s%s%s", year, fill, col));
	}
	
	@Override
	public int compareTo(ObjValue o) {
		
		int k = sortK();
		int _k = o.sortK();
		if(k < _k) {
			return -1;
		}else if(k > _k) {
			return 1;
		}else {
			return 0;
		}
	}
	
	/**
	 * 获取对象的日期
	 * @return
	 */
	public String toDate() {
		
		String date = null;
		try {
			
			int _year = DateUtil.year(new Date());
			CycleEnum _cycle = CycleEnum.cycle(cycle);
			int y = Integer.parseInt(String.valueOf(_year).substring(0, 2).concat(String.valueOf(this.year)));
			switch (_cycle) {
				case DAY : {
					//2019
					Calendar cal = Calendar.getInstance();
					cal.set(y, 0, 1);
					Date d = cal.getTime();
					Date targetDate = DateUtil.addDate(d, col - 1, _cycle);
					date = new SimpleDateFormat("yyyy-MM-dd").format(targetDate);
				};break;
				case WEEK : {
					date = String.format("%s-%s", y, col);
				}; break;
				case MONTH : {
					date = String.format("%s-%s", y, col);
				}; break;
		
				default:
					/** 特例先不管 */
					break;
			}
		} catch (Exception e) {
		}
		System.out.println("toDate: " + date);
		return date;
	}
	
	public static void main(String[] args) {
		
		new ObjValue(19, 30, "D", 1).toDate();
	}
}
