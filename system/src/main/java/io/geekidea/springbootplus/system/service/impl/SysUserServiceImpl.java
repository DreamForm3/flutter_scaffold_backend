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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.geekidea.springbootplus.config.properties.SpringBootPlusProperties;
import io.geekidea.springbootplus.framework.common.exception.BusinessException;
import io.geekidea.springbootplus.framework.common.service.impl.BaseServiceImpl;
import io.geekidea.springbootplus.framework.core.pagination.PageInfo;
import io.geekidea.springbootplus.framework.core.pagination.Paging;
import io.geekidea.springbootplus.framework.shiro.util.SaltUtil;
import io.geekidea.springbootplus.framework.util.PasswordUtil;
import io.geekidea.springbootplus.framework.util.PhoneUtil;
import io.geekidea.springbootplus.system.entity.SysUser;
import io.geekidea.springbootplus.system.enums.StateEnum;
import io.geekidea.springbootplus.system.mapper.SysUserMapper;
import io.geekidea.springbootplus.system.param.sysuser.ResetPasswordParam;
import io.geekidea.springbootplus.system.param.sysuser.SysUserPageParam;
import io.geekidea.springbootplus.system.param.sysuser.UpdatePasswordParam;
import io.geekidea.springbootplus.system.service.SysDepartmentService;
import io.geekidea.springbootplus.system.service.SysRoleService;
import io.geekidea.springbootplus.system.service.SysUserService;
import io.geekidea.springbootplus.system.vo.SysUserQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;


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
public class SysUserServiceImpl extends BaseServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysDepartmentService sysDepartmentService;

    @Lazy
    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SpringBootPlusProperties springBootPlusProperties;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveSysUser(SysUser sysUser) throws Exception {
        // ???????????????????????????
        boolean isExists = isExistsByUsername(sysUser.getUsername());
        if (isExists) {
            throw new BusinessException("??????????????????");
        }
        // ?????????????????????
        checkDepartmentAndRole(sysUser.getDepartmentId(), sysUser.getRoleId());
        sysUser.setId(null);

        // ????????????
        String salt = null;
        String password = sysUser.getPassword();
        // ??????????????????????????????????????????
        if (StringUtils.isBlank(password)) {
            salt = springBootPlusProperties.getLoginInitSalt();
            password = springBootPlusProperties.getLoginInitPassword();
        } else {
            salt = SaltUtil.generateSalt();
        }
        // ????????????
        sysUser.setSalt(salt);
        sysUser.setPassword(PasswordUtil.encrypt(password, salt));

        // ??????????????????????????????????????????
        if (StringUtils.isBlank(sysUser.getAvatar())) {
            sysUser.setAvatar(springBootPlusProperties.getLoginInitHead());
        }

        // ??????????????????
        return super.save(sysUser);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateSysUser(SysUser sysUser) throws Exception {
        // ?????????????????????
        checkDepartmentAndRole(sysUser.getDepartmentId(), sysUser.getRoleId());
        // ??????????????????
        SysUser updateSysUser = getById(sysUser.getId());
        if (updateSysUser == null) {
            throw new BusinessException("????????????????????????");
        }

        // ??????????????????
        updateSysUser.setNickname(sysUser.getNickname())
                .setPhone(sysUser.getPhone())
                .setEmail(sysUser.getEmail())
                .setGender(sysUser.getGender())
                .setAvatar(sysUser.getAvatar())
                .setRemark(sysUser.getRemark())
                .setState(sysUser.getState())
                .setDepartmentId(sysUser.getDepartmentId())
                .setRoleId(sysUser.getRoleId())
                .setUpdateTime(LocalDateTime.now());
        return super.updateById(updateSysUser);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteSysUser(Long id) throws Exception {
        return super.removeById(id);
    }

    @Override
    public SysUserQueryVo getSysUserById(Serializable id) throws Exception {
        return sysUserMapper.getSysUserById(id);
    }

    @Override
    public Paging<SysUserQueryVo> getSysUserPageList(SysUserPageParam sysUserPageParam) throws Exception {
        Page<SysUserQueryVo> page = new PageInfo<>(sysUserPageParam, OrderItem.desc("id"));
        IPage<SysUserQueryVo> iPage = sysUserMapper.getSysUserPageList(page, sysUserPageParam);

        // ????????????????????????
        if (iPage != null && CollectionUtils.isNotEmpty(iPage.getRecords())) {
            iPage.getRecords().forEach(vo -> {
                vo.setPhone(PhoneUtil.desensitize(vo.getPhone()));
            });
        }

        return new Paging(iPage);
    }

    @Override
    public boolean isExistsByUsername(String username) throws Exception {
        SysUser selectSysUser = new SysUser().setUsername(username);
        return sysUserMapper.selectCount(new QueryWrapper<>(selectSysUser)) > 0;
    }

    @Override
    public void checkDepartmentAndRole(Long departmentId, Long roleId) throws Exception {
        // ????????????????????????????????????
        boolean isEnableDepartment = sysDepartmentService.isEnableSysDepartment(departmentId);
        if (!isEnableDepartment) {
            throw new BusinessException("??????????????????????????????");
        }
        // ????????????????????????????????????
        boolean isEnableRole = sysRoleService.isEnableSysRole(roleId);
        if (!isEnableRole) {
            throw new BusinessException("??????????????????????????????");
        }
    }

    @Override
    public boolean isExistsSysUserByRoleId(Long roleId) throws Exception {
        SysUser sysUser = new SysUser()
                .setState(StateEnum.ENABLE.getCode())
                .setRoleId(roleId);
        return sysUserMapper.selectCount(new QueryWrapper(sysUser)) > 0;
    }

    @Override
    public boolean updatePassword(UpdatePasswordParam updatePasswordParam) throws Exception {
        String oldPassword = updatePasswordParam.getOldPassword();
        String newPassword = updatePasswordParam.getNewPassword();
        String confirmPassword = updatePasswordParam.getConfirmPassword();
        if (!newPassword.equals(confirmPassword)) {
            throw new BusinessException("??????????????????????????????");
        }
        if (newPassword.equals(oldPassword)) {
            throw new BusinessException("?????????????????????????????????");
        }

        // ???????????????????????????
        SysUser sysUser = getById(updatePasswordParam.getUserId());
        if (sysUser == null) {
            throw new BusinessException("???????????????");
        }
        if (StateEnum.DISABLE.getCode().equals(sysUser.getState())) {
            throw new BusinessException("???????????????");
        }
        // ??????????????????
        String salt = sysUser.getSalt();
        String encryptOldPassword = PasswordUtil.encrypt(oldPassword, salt);
        if (!sysUser.getPassword().equals(encryptOldPassword)) {
            throw new BusinessException("???????????????");
        }
        // ???????????????
        String encryptNewPassword = PasswordUtil.encrypt(newPassword, salt);

        // ????????????
        SysUser updateSysUser = (SysUser) new SysUser()
                .setPassword(encryptNewPassword)
                .setUpdateTime(LocalDateTime.now())
                .setId(sysUser.getId());
        return updateById(updateSysUser);
    }

    @Override
    public boolean updateSysUserHead(Long id, String headPath) throws Exception {
        SysUser sysUser = (SysUser) new SysUser()
                .setAvatar(headPath)
                .setId(id);
        return updateById(sysUser);
    }

    @Override
    public boolean resetPassword(ResetPasswordParam resetPasswordParam) throws Exception {
        String newPassword = resetPasswordParam.getNewPassword();
        String confirmPassword = resetPasswordParam.getConfirmPassword();
        if (!newPassword.equals(confirmPassword)) {
            throw new BusinessException("??????????????????????????????");
        }
        // ???????????????????????????
        SysUser sysUser = getById(resetPasswordParam.getUserId());
        if (sysUser == null) {
            throw new BusinessException("???????????????");
        }
        if (StateEnum.DISABLE.getCode().equals(sysUser.getState())) {
            throw new BusinessException("???????????????");
        }
        // ??????????????????
        String salt = sysUser.getSalt();
        // ???????????????
        String encryptNewPassword = PasswordUtil.encrypt(newPassword, salt);

        // ????????????
        SysUser updateSysUser = (SysUser) new SysUser()
                .setPassword(encryptNewPassword)
                .setUpdateTime(LocalDateTime.now())
                .setId(sysUser.getId());
        return updateById(updateSysUser);
    }
}
