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
import io.geekidea.springbootplus.config.constant.CommonRedisKey;
import io.geekidea.springbootplus.config.properties.JwtProperties;
import io.geekidea.springbootplus.config.properties.SpringBootPlusProperties;
import io.geekidea.springbootplus.framework.shiro.cache.LoginRedisService;
import io.geekidea.springbootplus.framework.shiro.jwt.JwtToken;
import io.geekidea.springbootplus.framework.shiro.util.JwtTokenUtil;
import io.geekidea.springbootplus.framework.shiro.util.JwtUtil;
import io.geekidea.springbootplus.framework.shiro.util.SaltUtil;
import io.geekidea.springbootplus.framework.shiro.vo.LoginSysUserVo;
import io.geekidea.springbootplus.framework.util.PasswordUtil;
import io.geekidea.springbootplus.system.convert.SysUserConvert;
import io.geekidea.springbootplus.system.entity.SysDepartment;
import io.geekidea.springbootplus.system.entity.SysRole;
import io.geekidea.springbootplus.system.entity.SysUser;
import io.geekidea.springbootplus.system.enums.StateEnum;
import io.geekidea.springbootplus.system.exception.VerificationCodeException;
import io.geekidea.springbootplus.system.mapper.SysUserMapper;
import io.geekidea.springbootplus.system.param.LoginParam;
import io.geekidea.springbootplus.system.service.*;
import io.geekidea.springbootplus.system.vo.LoginSysUserTokenVo;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * ?????????????????????
 * </p>
 *
 * @author geekidea
 * @date 2019-05-23
 **/
@Api
@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    @Lazy
    @Autowired
    private LoginRedisService loginRedisService;

    @Lazy
    @Autowired
    private JwtProperties jwtProperties;

    @Lazy
    @Autowired
    private SysUserMapper sysUserMapper;

    @Lazy
    @Autowired
    private SysDepartmentService sysDepartmentService;

    @Lazy
    @Autowired
    private SysRoleService sysRoleService;

    @Lazy
    @Autowired
    private SysRolePermissionService sysRolePermissionService;

    @Lazy
    @Autowired
    private SpringBootPlusProperties springBootPlusProperties;

    @Lazy
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private VerificationCodeService verificationCodeService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public LoginSysUserTokenVo login(LoginParam loginParam) throws Exception {
        // ???????????????
        checkVerifyCode(loginParam.getVerifyToken(), loginParam.getCode());

        String username = loginParam.getUsername();
        // ???????????????????????????????????????
        SysUser sysUser = getSysUserByUsername(username);
        if (sysUser == null) {
            log.error("????????????,loginParam:{}", loginParam);
            throw new AuthenticationException("????????????????????????");
        }
        if (StateEnum.DISABLE.getCode().equals(sysUser.getState())) {
            throw new AuthenticationException("???????????????");
        }

        // ??????????????????????????????????????????????????????
        // ?????????????????????123456
        // ???????????????????????????sha256(123456)
        // ?????????????????????sha256(sha256(123456) + salt)
        String encryptPassword = PasswordUtil.encrypt(loginParam.getPassword(), sysUser.getSalt());
        if (!encryptPassword.equals(sysUser.getPassword())) {
            throw new AuthenticationException("????????????????????????");
        }

        // ????????????????????????????????????????????????
        LoginSysUserVo loginSysUserVo = SysUserConvert.INSTANCE.sysUserToLoginSysUserVo(sysUser);

        // ????????????
        SysDepartment sysDepartment = sysDepartmentService.getById(sysUser.getDepartmentId());
        if (sysDepartment == null) {
            throw new AuthenticationException("???????????????");
        }
        if (!StateEnum.ENABLE.getCode().equals(sysDepartment.getState())) {
            throw new AuthenticationException("???????????????");
        }
        loginSysUserVo.setDepartmentId(sysDepartment.getId())
                .setDepartmentName(sysDepartment.getName());

        // ????????????????????????
        Long roleId = sysUser.getRoleId();
        SysRole sysRole = sysRoleService.getById(roleId);
        if (sysRole == null) {
            throw new AuthenticationException("???????????????");
        }
        if (StateEnum.DISABLE.getCode().equals(sysRole.getState())) {
            throw new AuthenticationException("???????????????");
        }
        loginSysUserVo.setRoleId(sysRole.getId())
                .setRoleName(sysRole.getName())
                .setRoleCode(sysRole.getCode());

        // ????????????????????????
        Set<String> permissionCodes = sysRolePermissionService.getPermissionCodesByRoleId(roleId);
        if (CollectionUtils.isEmpty(permissionCodes)) {
            throw new AuthenticationException("????????????????????????");
        }
        loginSysUserVo.setPermissionCodes(permissionCodes);

        // ?????????????????????????????????
        String newSalt = SaltUtil.getSalt(sysUser.getSalt(), jwtProperties);

        // ??????token??????????????????
        Long expireSecond = jwtProperties.getExpireSecond();
        String token = JwtUtil.generateToken(username, newSalt, Duration.ofSeconds(expireSecond));
        log.debug("token:{}", token);

        // ??????AuthenticationToken
        JwtToken jwtToken = JwtToken.build(token, username, newSalt, expireSecond);

        boolean enableShiro = springBootPlusProperties.getShiro().isEnable();
        if (enableShiro) {
            // ???SecurityUtils?????????????????? subject
            Subject subject = SecurityUtils.getSubject();
            // ??????????????????
            subject.login(jwtToken);
        } else {
            log.warn("?????????Shiro");
        }

        // ?????????????????????Redis
        loginRedisService.cacheLoginInfo(jwtToken, loginSysUserVo);
        log.debug("????????????,username:{}", username);

        // ?????????????????????redis
        String tokenSha256 = DigestUtils.sha256Hex(token);
        redisTemplate.opsForValue().set(tokenSha256, loginSysUserVo, 1, TimeUnit.DAYS);

        // ??????token???????????????????????????
        LoginSysUserTokenVo loginSysUserTokenVo = new LoginSysUserTokenVo();
        loginSysUserTokenVo.setToken(token);
        loginSysUserTokenVo.setLoginSysUserVo(loginSysUserVo);
        return loginSysUserTokenVo;
    }

    @Override
    public void checkVerifyCode(String verifyToken, String code) throws Exception {
        // ???????????????????????????????????????
        if (!springBootPlusProperties.isEnableVerifyCode()) {
            return;
        }
        verificationCodeService.checkVerifyCode(verifyToken, code);
    }

    @Override
    public void logout(HttpServletRequest request) throws Exception {
        Subject subject = SecurityUtils.getSubject();
        //??????
        subject.logout();
        // ??????token
        String token = JwtTokenUtil.getToken(request);
        String username = JwtUtil.getUsername(token);
        // ??????Redis????????????
        loginRedisService.deleteLoginInfo(token, username);
        log.info("????????????,username:{},token:{}", username, token);
    }

    @Override
    public SysUser getSysUserByUsername(String username) throws Exception {
        SysUser sysUser = new SysUser().setUsername(username);
        return sysUserMapper.selectOne(new QueryWrapper<SysUser>(sysUser));
    }

}
