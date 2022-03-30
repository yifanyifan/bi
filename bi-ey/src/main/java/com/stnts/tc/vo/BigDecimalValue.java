package com.stnts.tc.vo;

import java.math.BigDecimal;
import java.util.Collections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liang.zhang
 * @date 2019年12月10日
 * @desc TODO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BigDecimalValue implements Val, Comparable<BigDecimalValue>{
	
	private int year;
	private int col;
	private BigDecimal v;
	
	private int sortK() {
		
		int fillLen = 3 - String.valueOf(col).length();
		String fill = fillLen == 0 ? "" : String.join("", Collections.nCopies(fillLen, "0"));
		return Integer.parseInt(String.format("%s%s%s", year, fill, col));
	}
	
	@Override
	public int compareTo(BigDecimalValue o) {
		
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
}
