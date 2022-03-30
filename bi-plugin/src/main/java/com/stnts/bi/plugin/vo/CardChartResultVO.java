package com.stnts.bi.plugin.vo;

import com.stnts.bi.sql.vo.QueryChartResultVO;


/**
 * @author liang.zhang
 * @date 2020年7月8日
 * @desc TODO
 * 卡片result vo
 */
public class CardChartResultVO extends QueryChartResultVO{
	
	private CardVO cardVO;
	
	public CardVO getCardVO() {
		return cardVO;
	}

	public void setCardVO(CardVO cardVO) {
		this.cardVO = cardVO;
	}

	public static class CardVO{
		private String kpi;
		private String tb;
		private String hb;
		public String getKpi() {
			return kpi;
		}
		public void setKpi(String kpi) {
			this.kpi = kpi;
		}
		public String getTb() {
			return tb;
		}
		public void setTb(String tb) {
			this.tb = tb;
		}
		public String getHb() {
			return hb;
		}
		public void setHb(String hb) {
			this.hb = hb;
		}
	}
}
