/*
 * Copyright 2019-2029 geekidea(https://github.com/geekidea)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.geekidea.springbootplus.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.geekidea.springbootplus.framework.common.exception.BusinessException;
import io.geekidea.springbootplus.framework.common.exception.DaoException;
import io.geekidea.springbootplus.framework.common.exception.SpringBootPlusException;
import io.geekidea.springbootplus.framework.common.service.impl.BaseServiceImpl;
import io.geekidea.springbootplus.framework.core.pagination.PageInfo;
import io.geekidea.springbootplus.framework.core.pagination.Paging;
import io.geekidea.springbootplus.system.entity.SysRole;
import io.geekidea.springbootplus.system.entity.SysRolePermission;
import io.geekidea.springbootplus.system.enums.StateEnum;
import io.geekidea.springbootplus.system.mapper.SysRoleMapper;
import io.geekidea.springbootplus.system.param.sysrole.SysRolePageParam;
import io.geekidea.springbootplus.system.param.sysrole.UpdateSysRolePermissionParam;
import io.geekidea.springbootplus.system.service.SysPermissionService;
import io.geekidea.springbootplus.system.service.SysRolePermissionService;
import io.geekidea.springbootplus.system.service.SysRoleService;
import io.geekidea.springbootplus.system.service.SysUserService;
import io.geekidea.springbootplus.system.vo.SysRoleQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.collections4.SetUtils.SetView;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * <pre>
 * ???????????? ???????????????
 * </pre>
 *
 * @author geekidea
 * @since 2019-10-24
 */
@Slf4j
@Service
public class SysRoleServiceImpl extends BaseServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysPermissionService sysPermissionService;

    @Autowired
    private SysRolePermissionService sysRolePermissionService;

    @Autowired
    private SysUserService sysUserService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveSysRole(SysRole sysRole) throws Exception {
        String code = sysRole.getCode();
        // ??????????????????code?????????
        if (isExistsByCode(code)) {
            throw new BusinessException("?????????????????????");
        }
        // ????????????
        boolean saveRoleResult = super.save(sysRole);
        if (!saveRoleResult) {
            throw new DaoException("??????????????????");
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateSysRole(SysRole sysRole) throws Exception {
        Long roleId = sysRole.getId();
        // ????????????????????????
        if (getById(roleId) == null) {
            throw new BusinessException("??????????????????");
        }
        // ????????????
        sysRole.setUpdateTime(LocalDateTime.now());
        boolean updateResult = updateById(sysRole);
        if (!updateResult) {
            throw new DaoException("????????????????????????");
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteSysRole(Long id) throws Exception {
        // ?????????????????????????????????????????????????????????????????????
        boolean isExistsUser = sysUserService.isExistsSysUserByRoleId(id);
        if (isExistsUser) {
            throw new DaoException("????????????????????????????????????????????????");
        }
        // ??????????????????
        boolean deleteRoleResult = removeById(id);
        if (!deleteRoleResult) {
            throw new DaoException("??????????????????");
        }

        // ???????????????????????????????????????????????????
        boolean hasPermission = sysRolePermissionService.hasPermission(id);
        if (hasPermission) {
            // ??????????????????????????????
            boolean deletePermissionResult = sysRolePermissionService.deleteSysRolePermissionByRoleId(id);
            if (!deletePermissionResult) {
                throw new DaoException("??????????????????????????????");
            }
        }
        return true;
    }

    @Override
    public SysRoleQueryVo getSysRoleById(Serializable id) throws Exception {
        SysRoleQueryVo sysRoleQueryVo = sysRoleMapper.getSysRoleById(id);
        if (sysRoleQueryVo == null) {
            throw new SpringBootPlusException("???????????????");
        }
        List<Long> permissionIds = sysRolePermissionService.getPermissionIdsByRoleId((Long) id);
        sysRoleQueryVo.setPermissions(new HashSet<>(permissionIds));
        return sysRoleQueryVo;
    }

    @Override
    public Paging<SysRole> getSysRolePageList(SysRolePageParam sysRolePageParam) throws Exception {
        Page<SysRole> page = new PageInfo<>(sysRolePageParam, OrderItem.desc("id"));
        // ???????????????????????????mybatisplus????????????????????????
        LambdaQueryWrapper<SysRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        String keyword = sysRolePageParam.getKeyword();
        String name = sysRolePageParam.getName();
        String code = sysRolePageParam.getCode();
        Integer state = sysRolePageParam.getState();
        if (StringUtils.isNotBlank(keyword)) {
            lambdaQueryWrapper
                    .like(SysRole::getName, keyword)
                    .or()
                    .like(SysRole::getCode, keyword);
        }
        if (StringUtils.isNotBlank(name)) {
            lambdaQueryWrapper.like(SysRole::getName, name);
        }
        if (StringUtils.isNotBlank(code)) {
            lambdaQueryWrapper.like(SysRole::getCode, code);
        }
        if (state != null) {
            lambdaQueryWrapper.eq(SysRole::getState, state);
        }
        IPage<SysRole> iPage = sysRoleMapper.selectPage(page, lambdaQueryWrapper);
        return new Paging<SysRole>(iPage);
    }

    @Override
    public boolean isEnableSysRole(Long id) throws Exception {
        SysRole sysRole = (SysRole) new SysRole()
                .setState(StateEnum.ENABLE.getCode())
                .setId(id);
        int count = sysRoleMapper.selectCount(new QueryWrapper<>(sysRole));
        return count > 0;
    }

    @Override
    public boolean isExistsByCode(String code) throws Exception {
        SysRole sysRole = new SysRole().setCode(code);
        return sysRoleMapper.selectCount(new QueryWrapper<>(sysRole)) > 0;
    }

    @Override
    public boolean updateSysRolePermission(UpdateSysRolePermissionParam param) throws Exception {
        Long roleId = param.getRoleId();
        List<Long> permissionIds = param.getPermissionIds();
        // ????????????????????????
        SysRole sysRole = getById(roleId);
        if (sysRole == null) {
            throw new BusinessException("??????????????????");
        }
        if (CollectionUtils.isNotEmpty(permissionIds)) {
            // ??????????????????????????????
            if (!sysPermissionService.isExistsByPermissionIds(permissionIds)) {
                throw new BusinessException("????????????id????????????");
            }
        }
        // ?????????????????????id??????
        List<Long> beforeList = sysRolePermissionService.getPermissionIdsByRoleId(roleId);
        // ????????????
        // before???1,2,3,4,5,6
        // after??? 1,2,3,4,7,8
        // ??????5,6 ??????7,8
        // ???????????????????????????deleted?????????@TableLogic??????
        Set<Long> beforeSet = new HashSet<>(beforeList);
        Set<Long> afterSet = new HashSet<>(permissionIds);
        SetView<Long> deleteSet = SetUtils.difference(beforeSet, afterSet);
        SetView<Long> addSet = SetUtils.difference(afterSet, beforeSet);
        log.debug("deleteSet = " + deleteSet);
        log.debug("addSet = " + addSet);

        if (CollectionUtils.isNotEmpty(deleteSet)) {
            // ??????????????????
            LambdaUpdateWrapper<SysRolePermission> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(SysRolePermission::getRoleId, roleId);
            updateWrapper.in(SysRolePermission::getPermissionId, deleteSet);
            boolean deleteResult = sysRolePermissionService.remove(updateWrapper);
            if (!deleteResult) {
                throw new DaoException("??????????????????????????????");
            }
        }

        if (CollectionUtils.isNotEmpty(addSet)) {
            // ??????????????????
            boolean addResult = sysRolePermissionService.saveSysRolePermissionBatch(roleId, addSet);
            if (!addResult) {
                throw new DaoException("??????????????????????????????");
            }
        }

        return true;
    }

}
