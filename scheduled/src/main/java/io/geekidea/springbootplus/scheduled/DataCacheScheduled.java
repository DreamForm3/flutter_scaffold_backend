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

import com.lakecloud.scaffold.service.CustomerService;
import com.lakecloud.scaffold.vo.CustomerQueryVo;
import io.geekidea.springbootplus.config.constant.CommonRedisKey;
import io.geekidea.springbootplus.framework.core.pagination.Paging;
import io.geekidea.springbootplus.framework.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * 数据缓存调度，把需要的数据放到缓存里面
 *
 * @author geekidea
 * @date 2020/3/16
 **/
@Slf4j
@Component
public class DataCacheScheduled {

    @Autowired
    RedisLockRegistry redisLockRegistry;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    CustomerService customerService;
    @Autowired
    RedisUtils redisUtils;

    /**
     * 顾客用户模糊查询的数据缓存，每天凌晨三点执行一次
     */
    @Scheduled(cron = "0 0 3 * * ? ")
    public void customerDataCacheSchedule() {
        long time = System.currentTimeMillis();
        // 首先抢锁，避免重复执行
        Lock lock = redisLockRegistry.obtain("customer-data-cache-schedule");
        boolean hasLock = false;
        String lockKey = String.format(CommonRedisKey.SCHEDULE_LOCK, "customerDataCacheSchedule");
        // 获取锁之前先拿一个值
        String lockValue1 = stringRedisTemplate.opsForValue().get(lockKey);
        try {
            hasLock = lock.tryLock(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // do nothing
        }
        if (!hasLock) {
            log.info("顾客的数据缓存调度，抢锁失败退出执行");
            return;
        }
        // 抢到锁以后再拿一次值
        String lockValue2 = stringRedisTemplate.opsForValue().get(lockKey);
        // 如果拿到锁前后的值是不一样的，说明调度已经跑过了，不用再跑了
        if (!Objects.equals(lockValue1, lockValue2)) {
            log.info("顾客的数据缓存调度，调度已经被别的节点执行，本节点退出执行");
            return;
        }

        try {
            // 首先要拿到全量的数据
            List<CustomerQueryVo> allCustomerList = customerService.getAllCustomerList();
            // 清除当前的缓存
            clearCustomerDataCache();
            redisUtils.batchSetValues(allCustomerList);
            // 执行结果 +1
            stringRedisTemplate.opsForValue().increment(lockKey);
        } catch (Exception e) {
            log.error("顾客的数据缓存调度异常", e);
        } finally {
            // 无论如何要试放锁
            try {
                lock.unlock();
            } catch (IllegalStateException e) {
                // RedisLockRegistry 锁默认 60 秒后就会自己释放，这个异常表示已经自己释放了，直接忽略
            }
        }
        log.info("顾客的数据缓存调度处理完毕，耗时：" + (System.currentTimeMillis() - time) + "毫秒");
    }

    /**
     * 清除顾客的数据缓存
     * @return
     */
    public long clearCustomerDataCache() {
        // 匹配所有的顾客
        String pattern = String.format(CommonRedisKey.FUZZY_SEARCH_CACHE, CustomerQueryVo.class.getName(), "*", "*");
        Paging<String> keyPaging = redisUtils.findKeysForPage(pattern, 1, Long.MAX_VALUE, false);
        long deletedCount = redisUtils.batchDelete(keyPaging.getRecords());
        return deletedCount;
    }

}
