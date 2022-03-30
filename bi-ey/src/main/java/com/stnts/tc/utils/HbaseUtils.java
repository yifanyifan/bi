package com.stnts.tc.utils;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * @author liang.zhang
 * @email zhangliang1119@stnts.com
 * @date 2017年5月8日 - 下午3:38:31
 *  造数据用的哈
 */
public class HbaseUtils {

	private static Connection conn;

	public static Connection getConn() {

		if (null == conn || conn.isClosed()) {

			Configuration conf = HBaseConfiguration.create();
			conf.set("hbase.zookeeper.quorum", "st001,st002,st003,st004,st005");
			conf.set("zookeeper.znode.parent", "/hbase_online");
			conf.set("hbase.zookeeper.property.clientPort", "2181");
			conf.set("hbase.rootdir", "/hbase_online");
			try {
				conn = ConnectionFactory.createConnection(conf);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return conn;
	}

	public static String get(String rowkey, String colFamily, String col, String tableName) {

		Connection conn = getConn();
		try {
			Table table = conn.getTable(TableName.valueOf(tableName));
			Get get = new Get(Bytes.toBytes(rowkey));
			if (StringUtils.isNotEmpty(colFamily)) {
				get.addFamily(Bytes.toBytes(colFamily));
			}
			if (StringUtils.isNotEmpty(col)) {
				get.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(col));
			}
			Result result = table.get(get);
			List<Cell> cells = result.listCells();
			String value = null;
			if (cells != null) {
				for (Cell cell : cells) {
					value = Bytes.toString(CellUtil.cloneValue(cell));
				}
			}
			table.close();
			return value;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void putOne(String rowkey, String colFamily, String col, String val, String tableName) {

		try {
			Connection conn = getConn();
			Table table = conn.getTable(TableName.valueOf(tableName));
			Put put = new Put(Bytes.toBytes(rowkey));
			if (StringUtils.isNotEmpty(col)) {
				put.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(col), Bytes.toBytes(val));
			}
			table.put(put);
			;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	public static void putMany(String rowkey, String colFamily, String[] colnames, String[] vals, String tableName) {

		try {
			Connection conn = getConn();
			Table table = conn.getTable(TableName.valueOf(tableName));
			List<Put> puts = new ArrayList<Put>();
			for (int i = 0; i < colnames.length; i++) {
				Put put = new Put(Bytes.toBytes(rowkey));
				put.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(colnames[i]), Bytes.toBytes(vals[i]));
				puts.add(put);
			}
			table.put(puts);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	public static void close() {

		if (null != conn && !conn.isClosed()) {
			try {
				conn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void initData(String kpi,int cycle, String... vtypes) {
		
		Connection conn = HbaseUtils.getConn();
		try {
			Table table = conn.getTable(TableName.valueOf("tc:index_test"));
			List<String> ints = new ArrayList<String>();
			for(int i  = 0 ; i <= cycle ; i++) {
				ints.add(String.valueOf(i));
			}
			for(String vtype : vtypes) {
				String rowkey = String.format("%s%s", kpi, vtype);
				System.out.println(rowkey);
				HbaseUtils.putMany(rowkey, "kpi", ints.toArray(new String[ints.size()]), ints.toArray(new String[ints.size()]), "tc:index_test");
			}
			table.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (IOException e) {
			}
		}
	}
	
	public static void initDataR(String kpi, int cycle, String... vtypes) {
		
		Connection conn = HbaseUtils.getConn();
		try {
			Table table = conn.getTable(TableName.valueOf("tc:index_test"));
			List<String> vs = new ArrayList<String>();
			List<String> cols = new ArrayList<String>();
			for(int i  = 0 ; i <= cycle ; i++) {
				cols.add(String.valueOf(i));
				vs.add(String.format("%s%s", "0.", i));
			}
			for(String vtype : vtypes) {
				String rowkey = String.format("%s%s", kpi, vtype);
				HbaseUtils.putMany(rowkey, "kpi", cols.toArray(new String[cols.size()]), vs.toArray(new String[vs.size()]), "tc:index_test");
			}
			table.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (IOException e) {
			}
		}
	}
	
	public static void globalBasicBar() {
		
		List<String> kpis = new ArrayList<String>();
//		List<String> bsau = TcUtil.initScoreKey("19_B_S_AU_%s");
//		List<String> bsc = TcUtil.initScoreKey("19_B_S_C_%s");
//		List<String> pcau = TcUtil.initScoreKey("19_P_S_AU_%s");
//		List<String> pcc = TcUtil.initScoreKey("19_P_S_C_%s");
//		kpis.addAll(bsau);
//		kpis.addAll(bsc);
//		kpis.addAll(pcau);
//		kpis.addAll(pcc);
		List<String> ks = new ArrayList<String>();
		ks.add("19_B_S_AUL_LA");
		ks.add("19_B_S_AUL_LB");
		ks.add("19_B_S_AUL_LC");
		ks.add("19_B_S_AUL_LD");
		ks.add("19_P_S_AUL_LA");
		ks.add("19_P_S_AUL_LB");
		ks.add("19_P_S_AUL_LC");
		ks.add("19_P_S_AUL_LD");
		
		ks.add("19_B_S_CL_LA");
		ks.add("19_B_S_CL_LB");
		ks.add("19_B_S_CL_LC");
		ks.add("19_B_S_CL_LD");
		ks.add("19_P_S_CL_LA");
		ks.add("19_P_S_CL_LB");
		ks.add("19_P_S_CL_LC");
		ks.add("19_P_S_CL_LD");
		kpis.addAll(ks);
		kpis.stream().forEach(kpi -> {
			initDataR(kpi, 365, "_R_D");
//			initData(kpi, 365, "_V_D");
		});
	}
	
	public static void globalBasicChannel() {
		initDataR("19_C_O_R_ER", 365, "_V_D", "_TV_D", "_HV_D");
		initDataR("19_C_O_R_TP", 365, "_V_D", "_TV_D", "_HV_D");
		initDataR("19_C_O_R_AP", 365, "_V_D", "_TV_D", "_HV_D");
		initDataR("19_C_AL_R_ER", 365, "_V_D", "_TV_D", "_HV_D");
		initDataR("19_C_AL_R_TP", 365, "_V_D", "_TV_D", "_HV_D");
		initDataR("19_C_AL_R_AP", 365, "_V_D", "_TV_D", "_HV_D");
		initDataR("19_Y_REQ_R", 365, "_V_D", "_TV_D", "_HV_D");
		initDataR("19_Y_CPM_R", 365, "_V_D", "_TV_D", "_HV_D");
		
//		initDataR("19_C_O_R_ER", 365, "_TR_D", "_HR_D");
//		initDataR("19_C_O_R_TP", 365, "_TR_D", "_HR_D");
//		initDataR("19_C_O_R_AP", 365, "_TR_D", "_HR_D");
//		initDataR("19_C_AL_R_ER", 365, "_TR_D", "_HR_D");
//		initDataR("19_C_AL_R_TP", 365, "_TR_D", "_HR_D");
//		initDataR("19_C_AL_R_AP", 365, "_TR_D", "_HR_D");
//		initDataR("19_Y_REQ_R", 365, "_TR_D", "_HR_D");
//		initDataR("19_Y_CPM_R", 365, "_TR_D", "_HR_D");
	}
	
	public static void globalBarRe() {
		
		initDataR("19_B_RE_6", 12, "_R_M");
		initDataR("19_B_RE_7", 12, "_R_M");
		initDataR("19_B_RE_8", 12, "_R_M");
		initDataR("19_B_RE_9", 12, "_R_M");
		initDataR("19_B_RE_10", 12, "_R_M");
		initDataR("19_B_RE_11", 12, "_R_M");
	}
	
	public static void indexBar() {
		
//		putOne("中国人网吧", "info", "gid", "10101010", "tc:index_bar");
//		putOne("10101010", "info", "gid", "10101010", "tc:index_bar");
//		putOne("厉害了", "info", "gid", "10101010", "tc:index_bar");
//		putOne("英雄联盟", "info", "gid", "10101010", "tc:index_bar");
//		putOne("万事如意", "info", "gid", "10101010", "tc:index_bar");
//		putOne("网吧1", "info", "info", "1", "tc:index_bar");
//		putOne("网吧2", "info", "info", "2", "tc:index_bar");
//		putOne("网吧3", "info", "info", "3", "tc:index_bar");
//		putOne("网吧4", "info", "info", "4", "tc:index_bar");
//		putOne("那个网吧3", "info", "info", "5", "tc:index_bar");
//		putOne("那个网吧4", "info", "info", "6", "tc:index_bar");
//		putOne("这个网吧3", "info", "info", "7", "tc:index_bar");
//		putOne("这个网吧4", "info", "info", "8", "tc:index_bar");
//		
//		putOne("插件1", "info", "info", "101", "tc:index_plugin");
//		putOne("插件2", "info", "info", "102", "tc:index_plugin");
//		putOne("插件3", "info", "info", "103", "tc:index_plugin");
//		putOne("插件4", "info", "info", "104", "tc:index_plugin");
		putOne("1插件", "info", "info", "1", "tc:index_plugin");
		putOne("2插件", "info", "info", "2", "tc:index_plugin");
		putOne("3插件", "info", "info", "3", "tc:index_plugin");
		putOne("4插件", "info", "info", "4", "tc:index_plugin");
		
	}

	public static void main(String[] args) {
		
//		indexBar();
//		globalBarRe();
//		globalBasicChannel();
		globalBasicBar();
		
//		initDataR("19_B_VA", "_TR_D", "_HR_D");
//		initDataR("19_B_A", "_TR_D", "_HR_D");
//		initDataR("19_B_OC", "_TR_D", "_HR_D");
//		initDataR("19_P_BU", "_TR_D", "_HR_D");
//		initDataR("19_B_VA", "_TR_D", "_HR_D");
//		initDataR("19_P_BA_A", "_TR_D", "_HR_D");
//		initDataR("19_P_PR_A", "_TR_D", "_HR_D");

//		Connection conn = HbaseUtils.getConn();
//		try {
//			Table table = conn.getTable(TableName.valueOf("tc:index_test"));
//			Get get = new Get(Bytes.toBytes("B_VA_V_D"));
//			Result result = table.get(get);
//			List<Cell> cells = result.listCells();
//			for (Cell cell : cells) {
//				System.out.println(Bytes.toString(CellUtil.cloneQualifier(cell)));
//				System.out.println(Bytes.toString(CellUtil.cloneValue(cell)));
//			}
//			HbaseUtils.putMany(rowkey, colFamily, colnames, vals, tableName);
//			table.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			try {
//				conn.close();
//			} catch (IOException e) {
//			}
//		}

		// String str =
		// "plug_get_qq_info,11029298,40167EA6E5B6,042,90CD11C73E03A0DEB337C3446E0D1069,6136AD36,,,,0,,0,0,0,,,,,,,,,,,,,,,%7B%22ec%22:0%2C%22em%22:%22success%22%2C%22tag%22:%5B%7B%22co%22:%22%E5%8F%8C%E9%B1%BC%E5%BA%A7%22%2C%22id%22:%2213507414855926084948%22%7D%2C%7B%22co%22:%2290%E5%90%8E%22%2C%22id%22:%223618043347730980933%22%7D%2C%7B%22co%22:%22%E8%BF%90%E5%8A%A8%22%2C%22id%22:%224775026943611287095%22%7D%2C%7B%22co%22:%22%E6%B8%B8%E6%88%8F%22%2C%22id%22:%224762518206506141882%22%7D%2C%7B%22co%22:%22%E5%B0%8F%E6%B8%85%E6%96%B0%22%2C%22id%22:%224168786554789460002%22%7D%2C%7B%22co%22:%22%E7%94%B5%E8%A7%86%E5%89%A7%22%2C%22id%22:%2212613177001401699113%22%7D%2C%7B%22co%22:%22%E6%89%8B%E6%9C%BA%E6%8E%A7%22%2C%22id%22:%22639454881956003743%22%7D%2C%7B%22co%22:%22%E7%88%B1%E7%8B%97%22%2C%22id%22:%2217168216440080056340%22%7D%2C%7B%22co%22:%22%E8%B5%B7%E5%BA%8A%E5%9B%B0%E9%9A%BE%E6%88%B7%22%2C%22id%22:%2211893195252635968410%22%7D%2C%7B%22co%22:%22%E5%8A%A8%E6%BC%AB%22%2C%22id%22:%22464633551361363735%22%7D%2C%7B%22co%22:%22%E5%96%84%E8%89%AF%22%2C%22id%22:%226061658196376279959%22%7D%5D%7D,%7B%22retcode%22:0%2C%22result%22:%7B%22p2c%22:%2244_3%22%2C%22face%22:675%2C%22birthday%22:%7B%22month%22:2%2C%22year%22:1996%2C%22day%22:23%7D%2C%22phone%22:%22159******90%22%2C%22gender_id%22:1%2C%22allow%22:1%2C%22extflag%22:131584%2C%22college%22:%22%22%2C%22lbs_addr_detail%22:%7B%22street_no%22:%22%E7%9F%B3%E9%BE%99%E8%B7%AF17%E5%8F%B7%22%2C%22village%22:%22%E9%B9%8A%E5%B1%B1%E7%A4%BE%E5%8C%BA%22%2C%22street%22:%22%E7%9F%B3%E9%BE%99%E8%B7%AF%22%2C%22name%22:%22%E4%B8%AD%E5%9B%BD%2C%E5%B9%BF%E4%B8%9C%E7%9C%81%2C%E6%B7%B1%E5%9C%B3%E5%B8%82%2C%E5%AE%9D%E5%AE%89%E5%8C%BA%22%2C%22province%22:%22%E5%B9%BF%E4%B8%9C%E7%9C%81%22%2C%22town%22:%22%E5%A4%A7%E6%B5%AA%E8%A1%97%E9%81%93%22%2C%22code%22:%22440306%22%2C%22district%22:%22%E5%AE%9D%E5%AE%89%E5%8C%BA%22%2C%22nation%22:%22%E4%B8%AD%E5%9B%BD%22%2C%22city%22:%22%E6%B7%B1%E5%9C%B3%E5%B8%82%22%7D%2C%22cft_flag%22:0%2C%22h_zone%22:%2223%22%2C%22reg_type%22:0%2C%22city%22:%22%E6%B7%B1%E5%9C%B3%22%2C%22h_city%22:%229%22%2C%22city_id%22:%223%22%2C%22personal%22:%22%E6%9C%89%E6%97%B6%E5%80%99%EF%BC%8C%E4%BD%A0%E6%83%B3%E8%AF%81%E6%98%8E%E7%BB%99%E4%B8%80%E4%B8%87%E4%B8%AA%E4%BA%BA%E7%9C%8B%EF%BC%8C%E5%88%B0%E5%90%8E%E6%9D%A5%EF%BC%8C%E4%BD%A0%E5%8F%91%E7%8E%B0%E5%8F%AA%E5%BE%97%E5%88%B0%E4%BA%86%E4%B8%80%E4%B8%AA%E6%98%8E%E7%99%BD%E7%9A%84%E4%BA%BA%EF%BC%8C%E9%82%A3%E5%B0%B1%E5%A4%9F%E4%BA%86%E3%80%82%5Cr%22%2C%22shengxiao%22:1%2C%22province%22:%22%E5%B9%BF%E4%B8%9C%22%2C%22gender%22:%22male%22%2C%22longitude%22:113.995753%2C%22s_flag%22:0%2C%22occupation%22:%22%20%22%2C%22zone_id%22:%22%22%2C%22province_id%22:%2244%22%2C%22country_id%22:%221%22%2C%22constel%22:2%2C%22blood%22:5%2C%22homepage%22:%22%20%22%2C%22country%22:%22%E4%B8%AD%E5%9B%BD%22%2C%22flag%22:327680580%2C%22h_country%22:%221%22%2C%22nick%22:%22%E5%9B%A0%E4%B8%BA%E9%81%87%E8%A7%81%E4%BD%A0%E3%80%82%22%2C%22email%22:%22912390753@qq.com.%22%2C%22gps_flag%22:0%2C%22h_province%22:%2244%22%2C%22latitude%22:22.663171%2C%22mobile%22:%22152********%22%7D%7D,116.24.59.50,1497868032";
		// String[] mess = str.split(",", -1);
		// System.out.println(mess[4]);
		// System.out.println(mess[5]);
		// System.out.println(mess[31]);
	}
}
