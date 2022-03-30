package com.stnts.tc.vo;

/**
 * @author liang.zhang
 * @date 2019年12月15日
 * @desc TODO
 */
public class PluginKeyVO {
	
	private Integer id;
	private String name;
	private Integer type;
	public int getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public PluginKeyVO(Integer id, String name, Integer type) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
	}
	
	@Override
	public int hashCode() {
		return String.valueOf(id).hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj instanceof PluginKeyVO) {
			PluginKeyVO o = (PluginKeyVO) obj;
			return o.getId() == this.id;
		}
		return false;
	}
	@Override
	public String toString() {
		return String.format("%s::%s::%s", id, name, type);
	}
}
