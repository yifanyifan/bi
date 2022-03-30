package com.stnts.bi.sys.service.impl;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import com.stnts.bi.entity.sys.*;
import com.stnts.bi.mapper.sys.*;
import com.stnts.bi.sys.utils.OlapUtil;
import com.stnts.bi.vo.OlapPermSubVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.util.Md5Utils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sys.common.SysConfig;
import com.stnts.bi.sys.service.LoginService;
import com.stnts.bi.vo.PermTreeVO;
import com.stnts.bi.vo.SimplePermVO;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LoginServiceImpl implements LoginService{
	
	@Autowired
	private SysConfig sysConfig;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private PermMapper permMapper;
	
	@Autowired
	private ProductMapper productMapper;
	
	@Autowired
	private DepartmentMapper departmentMapper;

	@Autowired
	private OlapUserPermMapper olapUserPermMapper;

	@Autowired
	private OlapPermMapper olapPermMapper;

	@Override
	public UserEntity findById(Integer userId) {
		
		try {
//			return userMapper.selectById(userId);
			return userMapper.findUserById(userId);
		} catch (Exception e) {
			log.warn("查询用户报错，错误信息：{}", e.getMessage());
			return null;
		}
	}

	@Override
	public ResultEntity<String> syncUser() {

		try {
			String urlTemplate = sysConfig.getEhomeApi();
			String appId = sysConfig.getEhomeAppId();
			String key = sysConfig.getEhomeKey();
			long timestamp = Instant.now().toEpochMilli();
			// appId=USERCENTER&timestamp=ms 密钥
			String signStr = String.format("appId=%s&status=-1&timestamp=%s%s", appId, timestamp, key);
			String sign = Md5Utils.getMD5(signStr.getBytes()).toLowerCase();
			String api = String.format(urlTemplate, appId, timestamp, sign);
//			System.out.println(api);
			String apiResult = HttpUtil.get(api, 3000);
//			System.out.println(apiResult);
			if(StringUtils.isNotBlank(apiResult)) {
				JSONObject apiObj = JSON.parseObject(apiResult);
				if(apiObj.getIntValue("code") == 0) {  //成功
					JSONArray users = apiObj.getJSONArray("data");
					int size = users.size();
					List<UserEntity> userEntitys = new ArrayList<UserEntity>(size);
					log.info("当前用户总数: " + size);
					for(int i = 0 ; i < size ; i++) {
						userEntitys.add(users.getJSONObject(i).toJavaObject(UserEntity.class));
					}
//					userEntitys.stream().forEach(System.out::println);
					int cnt = userMapper.insertUsers(userEntitys);
					log.info("同步用户总数：" + cnt);
				}
			}
			return ResultEntity.success(null);
		} catch (Exception e) {
			return ResultEntity.exception(e.getMessage());
		}
	}
	
	@Override
	public ResultEntity<String> syncDepartment() {
		
		try {
			String urlTemplate = sysConfig.getEhomeDepartmentApi();
			String appId = sysConfig.getEhomeAppId();
			String key = sysConfig.getEhomeKey();
			long timestamp = Instant.now().toEpochMilli();
			// appId=USERCENTER&timestamp=ms 密钥
			String signStr = String.format("appId=%s&status=-1&timestamp=%s%s", appId, timestamp, key);
			String sign = Md5Utils.getMD5(signStr.getBytes()).toLowerCase();
			String api = String.format(urlTemplate, appId, timestamp, sign);
			String apiResult = HttpUtil.get(api, 3000);
			if(StringUtils.isNotBlank(apiResult)) {
				JSONObject apiObj = JSON.parseObject(apiResult);
				if(apiObj.getIntValue("code") == 0) {  //成功
					JSONArray users = apiObj.getJSONArray("data");
					int size = users.size();
					List<DepartmentEntity> departments = new ArrayList<DepartmentEntity>(size);
					log.info("当前部门记录总数: " + size);
					for(int i = 0 ; i < size ; i++) {
						departments.add(users.getJSONObject(i).toJavaObject(DepartmentEntity.class));
					}
//					departments.stream().forEach(System.out::println);
					int cnt = departmentMapper.insertDepartments(departments);
					log.info("同步部门记录总数：" + cnt);
				}
			}
			
			return ResultEntity.success(null);
		} catch (Exception e) {
			return ResultEntity.exception(e.getMessage());
		}
	}

	@Override
	public void fillPermForOlap(UserEntity user) {

		try{
			//拥有权限的头部菜单
			Integer userId = user.getId();
//			List<OlapPermSubVO> listResultEntity = selectRootPermList(userId);
			List<OlapPermSubVO> listResultEntity = Collections.emptyList();
			List<OlapPermEntity> validPermListNoUrl = olapPermMapper.listValid(userId);
			if(CollectionUtil.isNotEmpty(validPermListNoUrl)){

				List<Integer> dashboardIds = validPermListNoUrl.stream().map(OlapPermEntity::getOlapPermId).collect(Collectors.toList());
				Map<Integer, String> urlMap = OlapUtil.loadOlapUrl(dashboardIds, sysConfig.getOlapAppId(), sysConfig.getKeyFromOlap(), sysConfig.getOlapApiUrl());
				List<OlapPermEntity> validPermList = validPermListNoUrl.stream().filter(page -> urlMap.containsKey(page.getOlapPermId())).map(perm -> {
					perm.setUrl(urlMap.get(perm.getOlapPermId()));
					return perm;
				}).collect(Collectors.toList());
				QueryWrapper<OlapPermEntity> queryWrapper = new QueryWrapper<>();
				queryWrapper.orderByAsc("parent_perm_id", "perm_type", "order_num");
				List<OlapPermEntity> olapPermAll = olapPermMapper.selectList(queryWrapper);
				//page的父亲ID集合
				List<String> pagePermList = validPermList.stream().map(OlapPermEntity::getParentPermId).collect(Collectors.toList());
				//menu的父亲ID集合
				List<String> menuPermList = olapPermAll.stream().filter(p -> pagePermList.contains(p.getPermId())).map(OlapPermEntity::getParentPermId).collect(Collectors.toList());
				List<OlapPermEntity> rootList = olapPermAll.stream().filter(p -> menuPermList.contains(p.getPermId())).collect(Collectors.toList());
				listResultEntity = rootList.stream().map(this::toSubVO).collect(Collectors.toList());
				//加载权限树
				//page按照父ID分组
				Map<String, List<OlapPermEntity>> pageMap = validPermList.stream().collect(Collectors.groupingBy(OlapPermEntity::getParentPermId, Collectors.toList()));
				Map<String, List<OlapPermEntity>> menuMap = olapPermAll.stream().filter(perm -> pagePermList.contains(perm.getPermId())).map(perm -> {
					perm.setChildren(pageMap.get(perm.getPermId()));
					return perm;
				}).collect(Collectors.groupingBy(OlapPermEntity::getParentPermId, Collectors.toList()));
				List<OlapPermEntity> tree = olapPermAll.stream().filter(perm -> menuPermList.contains(perm.getPermId())).collect(Collectors.toList()).stream().map(perm -> {
					perm.setChildren(menuMap.get(perm.getPermId()));
					return perm;
				}).collect(Collectors.toList());

				user.setOlapPermTree(tree);
			}

			user.setOlapPermList(listResultEntity);

			Map<String, List<String>> olapPermMap = new HashMap<>();
//			Set<String> collect = listResultEntity.stream().map(OlapPermSubVO::getPermId).collect(Collectors.toSet());
			listResultEntity.stream().collect(Collectors.groupingBy(OlapPermSubVO::getBiPermId, Collectors.toList()))
			.forEach((biPermId, permList) -> {
				List<String> groupPermList = permList.stream().map(OlapPermSubVO::getPermId).distinct().collect(Collectors.toList());
				olapPermMap.put(String.valueOf(biPermId), groupPermList);
			});
			user.setOlapPermSet(olapPermMap);

			//追加一个BI于OLAP权限映射表
			List<OlapPermSubVO> olapPermSubVOS = olapPermMapper.listPerm();
			user.setOlapPermDict(olapPermSubVOS);

		}catch(Exception e){
			e.printStackTrace();
		}
	}

//	public List<OlapPermSubVO> selectRootPermList(Integer userId) {
//
//		List<OlapPermSubVO> result = Collections.emptyList();
//		try {
//			QueryWrapper<OlapUserPermEntity> queryWrapper = new QueryWrapper<>();
//			queryWrapper.eq("user_id", userId);
//			List<OlapUserPermEntity> olapUserPermEntities = olapUserPermMapper.selectList(queryWrapper);
//			if(CollectionUtil.isNotEmpty(olapUserPermEntities)){
//				List<String> permList = olapUserPermEntities.stream().map(OlapUserPermEntity::getPermId).collect(Collectors.toList());
//				List<OlapPermEntity> olapPermAll = olapPermMapper.selectList(null);
//				List<String> pagePermList = olapPermAll.stream().filter(p -> permList.contains(p.getPermId())).map(OlapPermEntity::getParentPermId).collect(Collectors.toList());
//				List<String> menuPermList = olapPermAll.stream().filter(p -> pagePermList.contains(p.getPermId())).map(OlapPermEntity::getParentPermId).collect(Collectors.toList());
//				List<OlapPermEntity> rootList = olapPermAll.stream().filter(p -> menuPermList.contains(p.getPermId())).collect(Collectors.toList());
////				List<OlapPermEntity> menuList = olapPermMapper.list(permList, null);
////				List<String> menuPermList = menuList.stream().map(OlapPermEntity::getPermId).collect(Collectors.toList());
////				List<OlapPermEntity> rootList = olapPermMapper.list(menuPermList, null);
//				result = rootList.stream().map(this::toSubVO).collect(Collectors.toList());
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		return result;
//	}

	private OlapPermSubVO toSubVO(OlapPermEntity olapPermEntity){
		OlapPermSubVO vo = new OlapPermSubVO();
		vo.setPermId(olapPermEntity.getPermId());
		vo.setPermName(olapPermEntity.getPermName());
		vo.setStatus(olapPermEntity.getStatus());
		vo.setBiPermId(olapPermEntity.getBiPermId());
		vo.setProductId(olapPermEntity.getProductId());
		return vo;
	}

	@Override
	public void fillPerm(UserEntity user) {
		
		try {
			
			List<UserRoleEntity> roles = user.getRoles();
			if(null != roles && !roles.isEmpty()) {
				
				//查出用户角色
				List<Integer> roleIds = roles.stream().map(UserRoleEntity::getRoleId).collect(Collectors.toList());
				//查出角色对应的权限
				List<SimplePermVO> permCkList = permMapper.selectListSimplePermByRoleIds(roleIds);
				if(null != permCkList && !permCkList.isEmpty()) {

					//追加产品线Set,在做权限控制时使用
//					Set<String> productSet = roles.stream().map(UserRoleEntity::getProductIds).map(p -> p.split(",")).flatMap(Arrays::stream).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
//					user.setProductSet(productSet);
					//获取权限Set,在做权限控制时使用
					Set<String> permCodeSet = permCkList.stream().map(SimplePermVO::getCode).collect(Collectors.toSet());
					//查出权限列表  并 组装树..
					List<PermEntity> permList = permMapper.selectList(null);
					List<SimplePermVO> permTree = load(-1, permList, permCodeSet);
					user.setPermSet(permCodeSet);
					
					//这棵权限树得更新一下SDK的，这里真的麻烦
					//根据角色一个一个来
//					List<SimplePermVO> sdk = new ArrayList<SimplePermVO>();
//					roles.stream().filter(role -> StringUtils.isNotBlank(role.getProductIds())).forEach(role -> {
//						//查出对应角色的权限，并赋予产品线上，绑定到SDK上
//						List<ProductEntity> products = role.getProducts();
////						products.forEach(System.out::println);
//						int roleId = role.getRoleId();
//						//这个角色对应的权限
//						List<SimplePermVO> permTreeByRole = loadPermTree(Arrays.asList(roleId));
////						permTreeByRole.stream().filter(StringUtils.equals(SimplePermVO::getCode, "sdk"))
//						//基于这个角色的SDK权限树
//						SimplePermVO sdkItem = permTreeByRole.stream().filter(p -> StringUtils.equals(p.getCode(), "sdk")).findAny().orElse(null);
//						if(null != sdkItem) {
//							sdk.addAll(products.stream().map(product -> {
//								//TODO 这里修改了
//								return new SimplePermVO(product.getProductId(), product.getProductName(), null, sdkItem.getPerms());
//							}).collect(Collectors.toList()));
//						}
//					});
					//原有SDK权限树
//					SimplePermVO sdkItem = permTree.stream().filter(p -> StringUtils.equals(p.getCode(), "sdk")).findAny().orElse(null);
//					if(null != sdkItem) {
////						System.out.println("SDK SIZE ========> " + sdk.size());
//						sdkItem.setPerms(sdk);
//						try{
//							//这个地方  得把 SDK权限整理下  不然不好判断呀
//							Map<String, List<String>> sdkPermMap = new HashMap<>();
//							sdk.stream().map(perm -> {
//								Map<String, List<String>> map = new HashMap<>();
//								List<String> permCodes = handlerVo(perm);
//								map.put(perm.getId(), permCodes);
//								return map;
//							}).forEach(map -> map.forEach(sdkPermMap::put));
//
//							user.setSdkPermMap(sdkPermMap);
//						}catch(Exception e){
//							//pass
//						}
//					}
					user.setPermTree(permTree);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("fillPerm failed, err: {}", e.getMessage());
		}
	}

	private List<String> handlerVo(SimplePermVO simplePermVO){

		List<String> permCodeList = new ArrayList<>();
		List<SimplePermVO> perms = simplePermVO.getPerms();
		if(CollectionUtil.isNotEmpty(perms)){
			for(SimplePermVO permVO : perms){
				permCodeList.addAll(handlerVo(permVO));
			}
		}else{
			permCodeList.add(simplePermVO.getCode());
		}
		return permCodeList;
	}

	/**
	 * @param roleIds
	 * @return
	 */
	private List<SimplePermVO> loadPermTree(List<Integer> roleIds){
		
		List<SimplePermVO> permTree = new ArrayList<SimplePermVO>();
		if(null != roleIds && !roleIds.isEmpty()) {
			
			//查出角色对应的权限
			List<SimplePermVO> permCkList = permMapper.selectListSimplePermByRoleIds(roleIds);
			if(null != permCkList && !permCkList.isEmpty()) {

				//获取权限Set,在做权限控制时使用
				Set<String> permCodeSet = permCkList.stream().map(SimplePermVO::getCode).collect(Collectors.toSet());
				//查出权限列表  并 组装树..
				List<PermEntity> permList = permMapper.selectList(null);
				permTree = load(-1, permList, permCodeSet);
			}
		}
		return permTree;
	}
	
	/**
	 * @param parentId
	 * @param permList
	 * @param permCodeSet
	 * @return
	 */
	private List<SimplePermVO> load(int parentId, List<PermEntity> permList, Set<String> permCodeSet){
		
		List<PermEntity> subPermList = permList.stream().filter(p -> p.getParentPermId() == parentId).collect(Collectors.toList());
		return subPermList.stream().filter(p -> {
			//菜单直接过  permCodeSet为null表示不检验是否有权限 如果是操作
			return p.getPermType() == 1 || null == permCodeSet || (p.getPermType() == 2 && permCodeSet.contains(p.getPermCode()));
		}).map(p -> {
			SimplePermVO top = new SimplePermVO();
			//TODO 这里修改了
			top.setId(String.valueOf(p.getPermId()));
			top.setName(p.getPermName());
			top.setCode(p.getPermCode());
			List<SimplePermVO> perms = load(p.getPermId(), permList, permCodeSet);
			top.setPerms(perms);
			return top;
		}).collect(Collectors.toList());
	}

	@Override
	public ResultEntity<PermTreeVO> loadPermTree() {
		
		PermTreeVO vo = new PermTreeVO();
		try {
			
			QueryWrapper<ProductEntity> qw = new QueryWrapper<ProductEntity>();
			qw.eq("status", 1);
			List<ProductEntity> productEntitys = productMapper.selectList(qw);
//			List<String> products = productEntitys.stream().map(ProductEntity::getProductName).collect(Collectors.toList());
			vo.setProducts(productEntitys);
			//查出权限列表  并 组装树..
			List<PermEntity> permList = permMapper.selectList(null);
			List<SimplePermVO> permTree = load(-1, permList, null);
			vo.setPerms(permTree);
			return ResultEntity.success(vo);
		} catch (Exception e) {
			log.warn("loadPermTree failed, msg: {}", e.getMessage());
			return ResultEntity.exception(e.getMessage());
		}
	}
}
