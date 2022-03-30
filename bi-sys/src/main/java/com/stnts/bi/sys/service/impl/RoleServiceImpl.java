package com.stnts.bi.sys.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.PermEntity;
import com.stnts.bi.entity.sys.RoleEntity;
import com.stnts.bi.entity.sys.RolePermEntity;
import com.stnts.bi.entity.sys.UserRoleEntity;
import com.stnts.bi.mapper.sys.PermMapper;
import com.stnts.bi.mapper.sys.RoleMapper;
import com.stnts.bi.mapper.sys.RolePermMapper;
import com.stnts.bi.mapper.sys.UserRoleMapper;
import com.stnts.bi.sys.common.Constants;
import com.stnts.bi.sys.params.RoleParam;
import com.stnts.bi.sys.params.RolePermParam;
import com.stnts.bi.sys.service.RoleService;
import com.stnts.bi.sys.utils.SysUtil;
import com.stnts.bi.sys.vos.PermTopVO;
import com.stnts.bi.sys.vos.PermVO;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleMapper roleMapper;

	@Autowired
	private RolePermMapper rolePermMapper;

	@Autowired
	private UserRoleMapper userRoleMapper;

	@Autowired
	private PermMapper permMapper;

	@Override
	public ResultEntity<List<RoleEntity>> listRoles() {
		List<RoleEntity> roles = null;
		try {
//			roles = this.roleMapper.selectList(null);
			roles = roleMapper.selectRoles();
		} catch (Exception e) {
			log.warn("listRoles failed, err: " + e.getMessage());
			return ResultEntity.exception(e.getMessage());
		}
		return ResultEntity.success(roles);
	}

	@Override
	public ResultEntity<String> addOne(RoleParam param) {
		int result = 0;
		try {
			if (param == null || !param.valid())
				return ResultEntity.param(Constants.MSG_PARAM_ROLE_NAME_NOTNULL);
			RoleEntity role = new RoleEntity(param.getName(), param.getRoleDesc());
			result = this.roleMapper.insert(role);
		} catch (Exception e) {
			if (e instanceof org.springframework.dao.DuplicateKeyException)
				return ResultEntity.exception(Constants.MSG_MYSQL_DUPLICATE_PK);
			if (e instanceof org.springframework.dao.DataIntegrityViolationException)
				return ResultEntity.exception(Constants.MSG_MYSQL_TOOLONG);
			log.warn("addOne failed, err: " + e.getMessage());
			return ResultEntity.exception(e.getMessage());
		}
		return (result > 0) ? ResultEntity.success(null) : ResultEntity.failure(null);
	}

	@Override
	public ResultEntity<String> update(Integer roleId, RoleParam param) {
		int result = 0;
		try {
			if (null == roleId || !param.valid())
				return ResultEntity.param(Constants.MSG_PARAM_ROLE_NAME_NOTNULL);
			RoleEntity role = new RoleEntity(param.getName(), param.getRoleDesc());
			role.setId(roleId);
			result = this.roleMapper.updateById(role);
		} catch (Exception e) {
			if (e instanceof org.springframework.dao.DuplicateKeyException)
				return ResultEntity.exception(Constants.MSG_MYSQL_DUPLICATE_PK);
			if (e instanceof org.springframework.dao.DataIntegrityViolationException)
				return ResultEntity.exception(Constants.MSG_MYSQL_TOOLONG);
			log.warn("update failed, err: " + e.getMessage());
			return ResultEntity.exception(e.getMessage());
		}
		return (result > 0) ? ResultEntity.success(null) : ResultEntity.failure(null);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ResultEntity<String> modRolePerm(Integer roleId, RolePermParam rolePermParam) {
		try {
			// || !rolePermParam.valid()
			if (null == roleId){
				return ResultEntity.param(Constants.MSG_PARAM_ROLE_PERM_NOTNULL);
			}
			String permCode = rolePermParam.getPermCode();
			QueryWrapper<PermEntity> permQuery = new QueryWrapper<PermEntity>();
			permQuery.likeRight("perm_code", permCode);
			permQuery.eq("perm_type", 2);
			List<Integer> permIds = this.permMapper.selectList(permQuery).stream()
					.map(PermEntity::getPermId).collect(Collectors.toList());
			if(CollectionUtil.isNotEmpty(permIds)){
				int delSize = this.rolePermMapper.deleteByPermIds(roleId, permIds);
				log.info("{}, {}", permIds.size(), delSize);
				List<RolePermEntity> rolePerms = rolePermParam.getPerms().stream()
						.map(permId -> new RolePermEntity(roleId, permId)).collect(Collectors.toList());
				if(CollectionUtil.isNotEmpty(rolePerms)){
					this.rolePermMapper.insertBatch(rolePerms);
				}
			}
			return ResultEntity.success(null);
		} catch (Exception e) {
			log.warn("modRolePerm failed, err: " + e.getMessage());
			throw e;
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ResultEntity<String> delRole(Integer roleId) {
		try {
			QueryWrapper<UserRoleEntity> userRoleWrapper = new QueryWrapper<UserRoleEntity>();
			userRoleWrapper.eq("role_id", roleId);
			int count = this.userRoleMapper.selectCount(userRoleWrapper);
			if (count > 0)
				return ResultEntity.failure(Constants.MSG_PARAM_PERM_USER_NOTZERO);
			UpdateWrapper<RolePermEntity> rolePermWrapper = new UpdateWrapper<RolePermEntity>();
			rolePermWrapper.eq("role_id", roleId);
			this.rolePermMapper.delete(rolePermWrapper);
			this.roleMapper.deleteById(roleId);
			return ResultEntity.success(null);
		} catch (Exception e) {
			log.warn("delRole failed, err: " + e.getMessage());
			throw e;
//			return ResultEntity.exception(e.getMessage());
		}
	}

	@Override
	public ResultEntity<List<PermTopVO>> detail(Integer roleId) {
	    List<PermTopVO> permTops = new ArrayList<>();
	    try {
	      List<PermEntity> perms = this.permMapper.selectList(null);
	      List<PermEntity> rootPerms = (List<PermEntity>)perms.stream().filter(perm -> (perm.getParentPermId() == -1)).collect(Collectors.toList());
	      Map<Integer, PermEntity> permMap = new HashMap<>();
	      perms.stream().forEach(perm -> permMap.put(perm.getPermId(), perm));
	      List<PermEntity> leafMenus = this.permMapper.selectListMenuForLeaf();
	      List<PermVO> voList = leafMenus.stream().map(leaf -> {
	            PermVO vo = new PermVO();
	            vo.setLeafId(leaf.getPermId());
	            fill(vo, leaf, permMap);
	            return vo;
	          }).sorted(Comparator.comparing(PermVO::getFstMenuId)).collect(Collectors.toList());
	      List<Integer> rolePermIds = new ArrayList<>();
	      if (null != roleId && roleId != 0) {
	        QueryWrapper<RolePermEntity> rolePermQuery = new QueryWrapper<RolePermEntity>();
	        rolePermQuery.eq("role_id", roleId);
	        rolePermIds.addAll(rolePermMapper.selectList(rolePermQuery).stream().map(RolePermEntity::getPermId).collect(Collectors.toList()));
	      } 
	      List<PermEntity> opList = perms.stream().filter(perm -> perm.getPermType() == 2).collect(Collectors.toList());
	      voList.forEach(vo -> vo.setPerms(opList.stream().filter(op -> op.getParentPermId().intValue() == vo.getLeafId()).map(op -> {
              int permId = op.getPermId();
              if (rolePermIds.contains(permId)) {
                  op.setChecked(true);
              }
              return op;
	      }).sorted(Comparator.comparing(PermEntity::getOrderNum)).collect(Collectors.toList())));
	      rootPerms.forEach(root -> {
	            PermTopVO permTop = new PermTopVO();
	            permTop.setId(root.getPermId());
	            permTop.setName(root.getPermName());
	            permTop.setPermCode(root.getPermCode());
	            permTop.setPerms(voList.stream().filter(vo -> vo.getTopId().intValue() == root.getPermId()).collect(Collectors.toList()));
	            permTops.add(permTop);
	          });
	    } catch (Exception e) {
	      log.warn("detail failed, err: " + e.getMessage());
	      return ResultEntity.exception(e.getMessage());
	    } 
	    return ResultEntity.success(permTops);
	  }

	private void fill(PermVO vo, PermEntity perm, Map<Integer, PermEntity> permMap) {
		int parentId = perm.getParentPermId().intValue();
		fillVo(vo, perm);
		if (parentId == -1)
			return;
		fill(vo, permMap.get(Integer.valueOf(parentId)), permMap);
	}

	private void fillVo(PermVO vo, PermEntity perm) {
		int id = perm.getPermId().intValue();
		int len = String.valueOf(id).length();
		switch (len) {
		case 4:
			vo.setThdMenuId(Integer.valueOf(id));
			vo.setThdMenuName(perm.getPermName());
			break;
		case 3:
			vo.setSecMenuId(Integer.valueOf(id));
			vo.setSecMenuName(perm.getPermName());
			break;
		case 2:
			vo.setFstMenuId(Integer.valueOf(id));
			vo.setFstMenuName(perm.getPermName());
			break;
		case 1:
			vo.setTopId(Integer.valueOf(id));
			vo.setTopName(perm.getPermName());
			break;
		}
	}
}
