package com.stnts.tc.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stnts.tc.common.Constants;
import com.stnts.tc.common.CycleEnum;
import com.stnts.tc.common.HbaseConfiguration;
import com.stnts.tc.common.OpEnum;
import com.stnts.tc.vo.BarGetVO;
import com.stnts.tc.vo.IndexVO;
import com.stnts.tc.vo.KpiSearch;
import com.stnts.tc.vo.ObjValue;
import com.stnts.tc.vo.ObjectValue;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liang.zhang
 * @date 2019年11月18日
 * @desc TODO
 *   新版的 hbase客户端  会做数据填充
 */
@Component
@Slf4j
public class HbaseClient {

	@Autowired
	private HbaseConfiguration hbaseConf;

	private static Configuration conf = HBaseConfiguration.create();
	private ExecutorService pool = Executors.newFixedThreadPool(10);
	
	private Connection conn = null;

	@PostConstruct
	public void init() {
		
		conf.set("hbase.zookeeper.quorum", hbaseConf.getQuorum());
		conf.set("zookeeper.znode.parent", hbaseConf.getParent());
		conf.set("hbase.zookeeper.property.clientPort", hbaseConf.getClientPort());
		conf.set("hbase.rootdir", hbaseConf.getRootDir());
		conn = getConn();
	}

	public Connection getConnection() {

		int i = 1;
		do {
			try {
				
				conn = ConnectionFactory.createConnection(conf, pool);
			} catch (Exception e) {
				if(i > 3) {
					log.warn("[*]HbaseClient getConn failed After {} retries!!!", i);
				}
			}
		}while(i++ < 4 || null == conn || (null != conn && conn.isClosed()));
		return conn;
	}
	
	public Connection getConn() {
		return null != conn && !conn.isClosed() ? conn : getConnection();
	}
	
	/**
	 * 通过rowkey前缀搜索
	 * @param tableName
	 * @param pre 
	 * @return
	 */
	public List<IndexVO> scanByIndex(String tableName, String pre, String family, String qualifier){
		
		List<IndexVO> vos = new ArrayList<IndexVO>();
		try {
			Table table = getConn().getTable(TableName.valueOf(tableName));
			Scan scan = new Scan();
//			RowFilter filter = new RowFilter(CompareOp.EQUAL, new RegexStringComparator(rePre));
			scan.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier));
			boolean ignoreId2Id = false;
			boolean limit ;
			int limitSize = 10;
			limit = StringUtils.equals(tableName, hbaseConf.getBarIndexTable());
			if(StringUtils.isNotBlank(pre)) {
//				Filter filter = new PrefixFilter(Bytes.toBytes(pre));
				Filter filter = new RowFilter(CompareOp.EQUAL, new SubstringComparator(pre.trim())); 
				scan.setFilter(filter);
				try {
					Integer.parseInt(pre.trim());
					limitSize = 20;
				} catch (Exception e) {
					limitSize = (StringUtils.equalsAny(pre.trim(), "网", "吧", "网吧", "网咖", "咖")) ? 20 : Integer.MAX_VALUE;
				}
			}else {
				ignoreId2Id = true;
				//如果是网吧的话 只返回10条记录
			}
			ResultScanner rs = table.getScanner(scan);
			for(Iterator<Result> it = rs.iterator() ; it.hasNext() ;) {
				
				Result r = it.next();
				String row = new String(r.getRow());
				String v = new String(r.getValue(Bytes.toBytes(family), Bytes.toBytes(qualifier)));
				IndexVO index = new IndexVO(row, v);
				if(!ignoreId2Id || (ignoreId2Id && !index.id2Id())) {
					vos.add(index);
				}
				if(limit) {
					if(vos.size() >= limitSize) {
						break;
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
			log.warn("HbaseUtil gets failed, msg: {}", e.getMessage());
		} finally {
		}
		return vos;
	}
	
	/**
	 * 从hbase查询数据，无数据时填null
	 * @param kpiSearches
	 * @param op
	 * @param isMerge
	 * @param srcBeginDate  
	 * @param srcEndDate
	 * @return
	 */
	public Map<String, Object> pulls(List<KpiSearch> kpiSearches, OpEnum op, boolean isMerge, String srcBeginDate, String srcEndDate, CycleEnum cycle) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			
			Table table = getConn().getTable(TableName.valueOf(hbaseConf.getTableName()));
			List<Get> gets = new ArrayList<Get>();
			kpiSearches.forEach(kpiSearch -> {
				String rowkey = kpiSearch.getRowkey();
				int begin = kpiSearch.getBegin();
				int end = kpiSearch.getEnd();
				Get get = new Get(Bytes.toBytes(rowkey));
				for(int i = begin; i <= end; i++) {
					get.addColumn(Bytes.toBytes(hbaseConf.getColumnFamily()), Bytes.toBytes(String.valueOf(i)));
				}
				gets.add(get);
			});
			Result[] rs = table.get(gets);
			Map<String, List<ObjectValue>> dat = new HashMap<String, List<ObjectValue>>();
			for(Result r : rs) {
				
				if(!r.isEmpty()) {
					
					String rowkey = Bytes.toString(r.getRow());
					List<Cell> cells = r.listCells();
					List<ObjectValue> ovs = new ArrayList<ObjectValue>();
					cells.forEach(cell -> {
						String c = Bytes.toString(CellUtil.cloneQualifier(cell));
						String v = Bytes.toString(CellUtil.cloneValue(cell));
//						if(StringUtils.isNotBlank(v)) {
							ObjectValue ov = TcUtil.v2ObjectValue(rowkey, c, v, srcBeginDate);
							ovs.add(ov);
//						}
					});
					//这次排序可以不做
//					Collections.sort(ovs);
					if(!ovs.isEmpty()) {
						dat.put(rowkey, ovs);
					}
				}
			}
			if(!dat.isEmpty() && isMerge) {
				result = TcUtil.merge(dat, op, srcBeginDate, srcEndDate, cycle);
			}else {
				//这里比较特殊，留存会到这个地方，也需要补数
//				int len = DateUtil.between(srcBeginDate, srcEndDate, cycle);
//				log.info(JSON.toJSONString(dat));
				result.putAll(dat);
			}
			table.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("HbaseUtil pulls failed, msg: {}", e.getMessage());
		}finally {
		}
		return result;
	}
	
	/**
	 * 从hbase查询数据，无数据时填null
	 * @param kpiSearches
	 * @param op
	 * @param isMerge
	 * @param srcBeginDate  
	 * @param srcEndDate
	 * @return
	 */
	public Map<String, Object> pulls(List<KpiSearch> kpiSearches, String srcBeginDate, String srcEndDate, CycleEnum cycle) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			
			Table table = getConn().getTable(TableName.valueOf(hbaseConf.getTableName()));
			List<Get> gets = new ArrayList<Get>();
			kpiSearches.stream().forEach(kpiSearch -> {
				String rowkey = kpiSearch.getRowkey();
				int begin = kpiSearch.getBegin();
				int end = kpiSearch.getEnd();
				Get get = new Get(Bytes.toBytes(rowkey));
				for(int i = begin; i <= end; i++) {
					get.addColumn(Bytes.toBytes(hbaseConf.getColumnFamily()), Bytes.toBytes(String.valueOf(i)));
				}
				gets.add(get);
			});
			Result[] rs = table.get(gets);
			Map<String, List<ObjectValue>> dat = new HashMap<String, List<ObjectValue>>();
			for(Result r : rs) {
				
				if(!r.isEmpty()) {
					
					String rowkey = Bytes.toString(r.getRow());
					List<Cell> cells = r.listCells();
					List<ObjectValue> ovs = new ArrayList<ObjectValue>();
					cells.stream().forEach(cell -> {
						String c = Bytes.toString(CellUtil.cloneQualifier(cell));
						String v = Bytes.toString(CellUtil.cloneValue(cell));
//						if(StringUtils.isNotBlank(v)) {
							ObjectValue ov = TcUtil.v2ObjectValue(rowkey, c, v, srcBeginDate);
							ovs.add(ov);
//						}
					});
					//这次排序可以不做
//					Collections.sort(ovs);
					if(!ovs.isEmpty()) {
						dat.put(rowkey, ovs);
					}
				}
			}
			if(!dat.isEmpty()) {
				result = TcUtil.merge2Obj(dat, srcBeginDate, srcEndDate, cycle);
			}else {
			}
			table.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("HbaseUtil pulls failed, msg: {}", e.getMessage());
		}finally {
		}
		return result;
	}
	
	/**
	 * 结果集 
	 * 不同年份的key同一个指标有多个值
	 * 需要做进一步指标值合并
	 * @param kpiSearches
	 * @return
	 */
	public Map<String, Object> list(List<KpiSearch> kpiSearches) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			
			Table table = getConn().getTable(TableName.valueOf(hbaseConf.getTableName()));
			List<Get> gets = new ArrayList<Get>();
			kpiSearches.stream().forEach(kpiSearch -> {
				String rowkey = kpiSearch.getRowkey();
				int begin = kpiSearch.getBegin();
				int end = kpiSearch.getEnd();
				Get get = new Get(Bytes.toBytes(rowkey));
				for(int i = begin; i <= end; i++) {
					get.addColumn(Bytes.toBytes(hbaseConf.getColumnFamily()), Bytes.toBytes(String.valueOf(i)));
				}
				gets.add(get);
			});
			Result[] rs = table.get(gets);
			for(Result r : rs) {
				if(!r.isEmpty()) {
					
					String rowkey = Bytes.toString(r.getRow());
					List<Cell> cells = r.listCells();
					List<ObjValue> vs = new ArrayList<ObjValue>();
					cells.stream().forEach(cell -> {
						String c = Bytes.toString(CellUtil.cloneQualifier(cell));
						String v = Bytes.toString(CellUtil.cloneValue(cell));
						if(StringUtils.isNotBlank(v)) {
							ObjValue value = TcUtil.v2ObjV(rowkey, c, v);
							vs.add(value);
						}
					});
					Collections.sort(vs);
					result.put(rowkey, vs);
				}
			}
			table.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("HbaseUtil pulls failed, msg: {}", e.getMessage());
		}
		return result;
	}
	
	/**
	 * 通过指标字符串查询
	 * @param kpis
	 * @param cycle
	 * @param beginDate
	 * @param endDate
	 * @param op
	 * @param vtypes
	 * @return
	 */
	public Map<String, Object> gets(List<String> kpis, CycleEnum cycle, String beginDate, String endDate, OpEnum op, String... vtypes) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			
			List<KpiSearch> kpiSearches = new ArrayList<KpiSearch>();
			for(String k : kpis) {
				kpiSearches.addAll(TcUtil.kpis(k, beginDate, endDate, cycle, vtypes));
			}
			result = pulls(kpiSearches, op, true, beginDate, endDate, cycle);
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("HbaseUtil gets failed, msg: {}", e.getMessage());
		}
		return result;
	}
	
	/**
	 * 获取网吧指标信息
	 * @param vos
	 * @return
	 */
	public Map<String, JSONObject> getsForBar(List<BarGetVO> vos){
		
		Map<String, JSONObject> result = new HashMap<String, JSONObject>();
		
		try {
			
			Table table = getConn().getTable(TableName.valueOf(hbaseConf.getTableName()));
			List<Get> gets = vos.stream().map(vo -> {
				Get get = new Get(Bytes.toBytes((vo.getRowkey())));
				get.addFamily(Bytes.toBytes(hbaseConf.getColumnFamily()));
				get.addColumn(Bytes.toBytes(hbaseConf.getColumnFamily()), Bytes.toBytes(vo.getCol()));
				return get;
			}).collect(Collectors.toList());
			Result[] rs = table.get(gets);
			if(null != rs && rs.length > 0) {
				
				Arrays.asList(rs).stream().forEach(r -> {
					
					String rowkey = Bytes.toString(r.getRow());
					if(!r.isEmpty()) {
						
						List<Cell> cells = r.listCells();
						Cell cell = cells.get(0);
						String v = Bytes.toString(CellUtil.cloneValue(cell));
						if(StringUtils.isNotBlank(v)) {
							
							JSONObject jsonObj = JSON.parseObject(v);
							//19_B_DE_K_101_V_D
							String gid = StringUtils.split(rowkey, Constants.KEY_SPLIT)[4];
							result.put(gid, jsonObj);
						}
					}else {
						//注意
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("HbaseUtil getsForBar failed, msg: {}", e.getMessage());
		} finally {
		}
		
		return result;
	}
	
	public Map<String, JSONObject> getsForBar(List<String> rowkeys, String colFamily, String col){
		
		Map<String, JSONObject> result = new HashMap<String, JSONObject>();
		try {
			
			Table table = getConn().getTable(TableName.valueOf(hbaseConf.getBarInfoTable()));
			List<Get> gets = rowkeys.stream().map(rowkey -> {
				Get get = new Get(Bytes.toBytes((rowkey)));
				get.addFamily(Bytes.toBytes(colFamily));
				get.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(col));
				return get;
			}).collect(Collectors.toList());
			Result[] rs = table.get(gets);
			if(null != rs && rs.length > 0) {
				
				Arrays.asList(rs).stream().forEach(r -> {
					
//					String rowkey = Bytes.toString(r.getRow());
					if(!r.isEmpty()) {
						
						List<Cell> cells = r.listCells();
						Cell cell = cells.get(0);
						String v = Bytes.toString(CellUtil.cloneValue(cell));
						if(StringUtils.isNotBlank(v)) {
							
							JSONObject jsonObj = JSON.parseObject(v);
							result.put(jsonObj.getString(Constants.KEY_GID), jsonObj);
						}
					}else {
						//注意
					}
				});
			}
		} catch (Exception e) {
			log.warn("HbaseUtil getsForBar failed, msg: {}", e.getMessage());
		} finally {
		}
		return result;
	}

	/**
	 * @param rowkey
	 * @param colFamily
	 * @param col
	 * @param tableName
	 * @return
	 */
	public String get(String rowkey, String colFamily, String col, String tableName) {

		String value = null;
		try {

			Table table = getConn().getTable(TableName.valueOf(tableName));
			Get get = new Get(Bytes.toBytes(rowkey));
			if (StringUtils.isNotEmpty(colFamily)) {
				get.addFamily(Bytes.toBytes(colFamily));
			}
			if (StringUtils.isNotEmpty(col)) {
				get.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(col));
			}
			Result result = table.get(get);
			List<Cell> cells = result.listCells();
			if (cells != null) {
				for (Cell cell : cells) {
					value = Bytes.toString(CellUtil.cloneValue(cell));
				}
			}
			table.close();
			return value;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
		return null;
	}

	/**
	 * @param k 指标
	 * @param srcBeginDate  开始日期 
	 * d：201-01-01; w：2019-45; m：2019-12
	 * @param srcEndDate  结束日期 
	 * @param cycle  周期：日（D）、周（W）、月（M）
	 * @param vtypes 值类型：V  TBV  TVR  HBV   HBR
	 * @throws Exception 
	 */
	public List<KpiSearch> select(String k, String srcBeginDate, String srcEndDate, CycleEnum cycle, String... vtypes) throws Exception {
		
		List<KpiSearch> kpis = new ArrayList<KpiSearch>();
		switch (cycle) {
			case DAY:{
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date beginDate = sdf.parse(srcBeginDate);
				Date endDate = sdf.parse(srcEndDate);
				int gapDays = DateUtil.gapDays(beginDate, endDate);
				if(gapDays > 365) {
					throw new RuntimeException("不得超过365天");
				}
				if(DateUtil.isSameYear(beginDate, endDate)) {  //同一年就好说了
					int year = DateUtil.year(beginDate);
					int begin = DateUtil.dayOfYear(beginDate);
					int end = DateUtil.dayOfYear(endDate);
					for(String vtype : vtypes) {  //不同类型的值是一行
						String rowkey = TcUtil.rowkey(year, k, vtype, cycle.toString());
						KpiSearch kpi = new KpiSearch(rowkey, begin, end);
						kpis.add(kpi);
					}
				}else {
					
				}
				
			};break;
			case WEEK:{
				
			};break;
			case MONTH:{
				
			};break;
			default : { /** do Nothing */ }
		}
		return kpis;
	}

	@PreDestroy
	public void close() {

		if (null != conn && !conn.isClosed()) {
			try {
				conn.close();
			} catch (IOException e) {
			}
		}
	}
	
	public static void main(String[] args) {

		HbaseClient hbaseUtil = new HbaseClient();
		Connection conn = hbaseUtil.getConn();
		try {
			Table table = conn.getTable(TableName.valueOf("tc:index_test"));
			Get get = new Get(Bytes.toBytes("B_VA_V_D"));
			Result result = table.get(get);
			List<Cell> cells = result.listCells();
			for (Cell cell : cells) {
				System.out.println(Bytes.toString(CellUtil.cloneQualifier(cell)));
				System.out.println(Bytes.toString(CellUtil.cloneValue(cell)));
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
}
