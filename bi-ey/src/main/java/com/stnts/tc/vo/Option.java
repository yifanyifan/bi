package com.stnts.tc.vo;


import com.stnts.tc.common.Constants;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Option {

	private String value;
	private String title;//  eg:基础插件覆盖PC数
	private boolean select = true ;
	private String classify;  // eg：基础插件
	private String type;  //  eg：覆盖PC数
	private String vtype = Constants.VTYPE_INT;  //eg: int, float
	private String unit = "台";  //eg: 个，台
	
	public Option(String value, String title) {
		super();
		this.value = value;
		this.title = title;
	}

	public Option(String value, String title, boolean select) {
		super();
		this.value = value;
		this.title = title;
		this.select = select;
	}

	public Option(String value, String title, String classify, String type) {
		super();
		this.value = value;
		this.title = title;
		this.classify = classify;
		this.type = type;
	}
}
