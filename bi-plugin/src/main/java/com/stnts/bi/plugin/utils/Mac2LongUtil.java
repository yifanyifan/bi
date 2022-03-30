package com.stnts.bi.plugin.utils;

public class Mac2LongUtil {

	public static int hash = 0;

	public static void main(String[] args) {

		String mac = "AB-16-7E-B1-7D-74";
		System.out.println(hashCode(mac.replaceAll("-", "")));
	}

	public static int hashCode(String s) {
		System.out.println(s);
		char[] value = s.toCharArray();
		int h = hash;
		if (h == 0 && value.length > 0) {
			char val[] = value;

			for (int i = 0; i < value.length; i++) {
				h = 31 * h + val[i];
			}
			hash = h;
		}
		return h;
	}
}
