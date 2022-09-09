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

package io.geekidea.springbootplus.scheduled;

import io.geekidea.springbootplus.framework.common.api.ApiResult;
import io.geekidea.springbootplus.framework.common.controller.BaseController;
import io.geekidea.springbootplus.framework.log.annotation.Module;
import io.geekidea.springbootplus.framework.log.annotation.OperationLog;
import io.geekidea.springbootplus.framework.log.enums.OperationLogType;
import io.geekidea.springbootplus.scheduled.DataCacheScheduled;
import io.geekidea.springbootplus.system.vo.SysUserQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * 系统调度 控制器
 *
 * @author Alex.King
 * @since 2020-09-17
 */
@Slf4j
@RestController
@RequestMapping("/schedules")
@Module("system")
@Api(value = "系统调度API", tags = {"系统调度"})
public class ScheduledController extends BaseController {

    @Autowired
    private DataCacheScheduled dataCacheScheduled;



    /**
     * 获取系统用户详情
     */
    @GetMapping("/customerDataCacheSchedule")
    @OperationLog(name = "手动执行顾客用户模糊查询的数据缓存调度", type = OperationLogType.MANUAL_SCHEDULED)
    @ApiOperation(value = "手动执行顾客用户模糊查询的数据缓存调度", response = ApiResult.class)
    public ApiResult getSysUser(HttpServletResponse response) {
        try {
            dataCacheScheduled.customerDataCacheSchedule();
        } catch (Exception e) {
            log.error("手动执行顾客用户模糊查询的数据缓存调度发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.ok();
    }



}

