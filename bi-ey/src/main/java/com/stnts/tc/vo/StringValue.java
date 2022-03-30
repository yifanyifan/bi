package com.stnts.tc.vo;

import java.math.BigDecimal;
import java.util.Collections;

import lombok.Data;

@Data
public class StringValue {

	private int year;
	private int col;
	private BigDecimal v;
	
	public int sortK() {
		
		int fillLen = 3 - String.valueOf(col).length();
		String fill = fillLen == 0 ? "" : String.join("", Collections.nCopies(fillLen, "0"));
		return Integer.parseInt(String.format("%s%s%s", year, fill, col));
	}
	
//	@Override
//	public int compareTo(BigDecimalValue o) {
//		
//		int k = sortK();
//		int _k = o.sortK();
//		if(k < _k) {
//			return -1;
//		}else if(k > _k) {
//			return 1;
//		}else {
//			return 0;
//		}
//	}
}
