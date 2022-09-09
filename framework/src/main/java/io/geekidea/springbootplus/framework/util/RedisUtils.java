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

package io.geekidea.springbootplus.framework.util;

import com.alibaba.fastjson.JSON;
import io.geekidea.springbootplus.config.constant.CommonRedisKey;
import io.geekidea.springbootplus.framework.common.entity.RedisFuzzySearchKey;
import io.geekidea.springbootplus.framework.core.pagination.Paging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Function;

/**
 * @author geekidea
 * @date 2018-11-08
 */
@Component
@Slf4j
public class RedisUtils {

    private static RedisUtils redisUtils;

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 将当前对象赋值给静态对象,调用spring组件: redisCacheUtil.redisTemplate.xxx()
     */
    @PostConstruct
    public void init(){
        redisUtils = this;
    }

    /**
     * 根据类和方法获取对应的查询缓存 key
     * @param clazz
     * @param methodName
     * @return
     */
    public static String getRedisCacheKey(Class clazz, String methodName) {
        return clazz.getName() + "-" + methodName;
    }

    /**
     * 清除某个类的某个方法的全部查询缓存
     * @param clazz
     * @param methodName
     * @return
     */
    public long clearQueryCache(Class clazz, String methodName) {
        String pattern = CommonRedisKey.QUERY_CACHE_PREFIX + getRedisCacheKey(clazz, methodName) + "*";
        Paging<String> keyPaging = findKeysForPage(pattern, 1, Long.MAX_VALUE, false);
        long count = batchDelete(keyPaging.getRecords());
        return count;
    }


    /**
     * 查找匹配key
     * @param pattern key
     * @return /
     */
    public List<String> scan(String pattern) {
        ScanOptions options = ScanOptions.scanOptions().match(pattern).build();
        RedisConnectionFactory factory = redisTemplate.getConnectionFactory();
        RedisConnection rc = Objects.requireNonNull(factory).getConnection();
        Cursor<byte[]> cursor = rc.scan(options);
        List<String> result = new ArrayList<>();
        while (cursor.hasNext()) {
            result.add(new String(cursor.next()));
        }
        try {
            RedisConnectionUtils.releaseConnection(rc, factory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 分页查询 key
     * @param patternKey key
     * @param page 页码，从 1 开始
     * @param size 每页数目
     * @param lazy lazy = true 意味着拿到当前页面所需要的数据就会直接返回，这样的话总条目数是不一定准的，
     *             但是会比较快，此时total最多会多2页，主要目的是能有下一页按钮
     * @return /
     */
    public Paging<String> findKeysForPage(String patternKey, long page, long size, boolean lazy) {
        if (page < 1 || size < 0) {
            throw new IllegalArgumentException("page 从 1 开始，size 不能小于 0");
        }

        long tmpIndex = 0;
        long startIndex = (page - 1) * size;
        long endIndex = page * size;
        ScanOptions.ScanOptionsBuilder builder = ScanOptions.scanOptions().match(patternKey);
        // 如果是lazy模式，扫描到目标数量再加2页就足够了，否则就是全部扫描
        if (lazy) {
            builder = builder.count(endIndex + size * 2);
        }
        ScanOptions options = builder.build();
        List<String> result = new ArrayList();
        RedisConnectionFactory factory = redisTemplate.getConnectionFactory();;
        RedisConnection rc = null;
        try {
            rc = Objects.requireNonNull(factory).getConnection();
            Cursor<byte[]> cursor = rc.scan(options);
            String key;
            while (cursor.hasNext()) {
                key = new String(cursor.next());
                if (tmpIndex >= startIndex && tmpIndex < endIndex) {
                    result.add(key);
                }
                tmpIndex++;
                // 获取到满足条件的数据后，就可以退出了
                if (lazy && tmpIndex >= endIndex + size * 2) {
                    break;
                }
            }
        } catch (Exception e) {
            log.error("Redis scan 发生异常", e);
        } finally {
            RedisConnectionUtils.releaseConnection(rc, factory, false);
        }
        Paging<String> paging = new Paging();
        paging.setTotal(tmpIndex);
        paging.setRecords(result);
        return paging;
    }

    /**
     * 分页查询value
     * @param patternKey key
     * @param page 页码，从 1 开始
     * @param size 每页数目
     * @param clazz 要返回的对象的类型
     * @param lazy lazy = true 意味着拿到当前页面所需要的数据就会直接返回，这样的话总条目数是不一定准的，
     *             但是会比较快，此时最多会多返回2页，主要目的是能有下一页按钮
     * @param <T>
     * @return
     */
    public <T> Paging<T> findValuesForPage(String patternKey, long page, long size, Class<T> clazz, boolean lazy) {
        // 先搜索到所有的 key
        Paging<String> keysPaging = findKeysForPage(patternKey, page, size, lazy);
        List<String> valueList = stringRedisTemplate.opsForValue().multiGet(keysPaging.getRecords());
        List<T> dataList = new ArrayList<>();
        for (String value : valueList) {
            dataList.add(JSON.parseObject(value, clazz));
        }
        Paging<T> paging = new Paging();
        paging.setTotal(keysPaging.getTotal());
        paging.setRecords(dataList);
        return paging;
    }

    /**
     * 批量给Redis设置值，每 1000 条批量设置一次
     * @param objectList
     */
    public void  batchSetValues(List<? extends RedisFuzzySearchKey> objectList) {
        if (objectList == null || objectList.isEmpty()) {
            return;
        }

        int count = 0;
        String redisKey, redisValue;
        Map<String, String> map = new HashMap<>();
        for (RedisFuzzySearchKey obj : objectList) {
            redisKey = obj.getRedisFuzzySearchKey();
            redisValue = JSON.toJSONString(obj);
            map.put(redisKey, redisValue);
            count++;
            // 每1000条批量存到redis
            if (count == 1000) {
                stringRedisTemplate.opsForValue().multiSet(map);
                map.clear();
                count = 0;
            }
        }
        // 处理尾巴
        if (count > 0) {
            stringRedisTemplate.opsForValue().multiSet(map);
            map.clear();
            count = 0;
        }
    }

    /**
     * 从 redis 批量删除数据，每 1000 条批量删除一次
     * @param keyList
     * @return
     */
    public long batchDelete(List<String> keyList) {
        if (keyList == null || keyList.isEmpty()) {
            return 0;
        }
        long count = 0;
        List<List<String>> splitedList = ListUtils.splitList(keyList, 1000);
        for (List<String> subList : splitedList) {
            count += stringRedisTemplate.delete(subList);
        }
        return count;
    }

    /**
     * 根据对象的ID来更新redis里模糊查询缓存的值
     * @param objId
     * @param clazz
     * @param getObjById
     * @param <T>
     */
    public <T extends RedisFuzzySearchKey> void updateRedisFuzzySearchValueById(Long objId, Class<T> clazz, Function<Long, T> getObjById) {
        if (objId == null || clazz == null || getObjById == null) {
            return;
        }
        T obj = getObjById.apply(objId);
        if (obj == null) {
            return;
        }
        // 先清掉原来的数据
        List<String> keyList = getRedisFuzzySearchKeyListById(clazz, objId);
        batchDelete(keyList);
        // 然后塞进去新数据
        String redisKey = obj.getRedisFuzzySearchKey();
        String redisValue = JSON.toJSONString(obj);
        stringRedisTemplate.opsForValue().set(redisKey, redisValue);
    }



    /**
     * 获取去 redis scan 的 pattern
     * @param clazz
     * @param args
     * @return
     */
    public static String getRedisScanPattern(Class clazz, String... args) {
        if (clazz == null || args == null) {
            throw new IllegalArgumentException("clazz, args 都不能为 null");
        }

        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            if (arg == null) {
                sb.append("*");
            } else {
                sb.append("*").append(arg).append("*");
            }
            sb.append(":");
        }
        // 删除最后一个冒号
        sb.deleteCharAt(sb.length() - 1);
        return String.format(CommonRedisKey.FUZZY_SEARCH_CACHE, clazz.getName(), "*", sb.toString());
    }

    /**
     * 获取用于模糊查询的 redis key
     * @param clazz
     * @param id
     * @param args
     * @return
     */
    public static String getRedisFuzzySearchKey(Class clazz, Long id, String... args) {
        if (clazz == null || id == null || args == null) {
            throw new IllegalArgumentException("clazz, id, args 都不能为 null");
        }

        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(arg == null ? "" : arg).append(":");
        }
        // 删除最后一个冒号
        sb.deleteCharAt(sb.length() - 1);
        return String.format(CommonRedisKey.FUZZY_SEARCH_CACHE, clazz.getName(), id, sb.toString());
    }

    /**
     * 根据对象的类以及对象的ID来获取redis模糊搜索数据缓存的key
     * @param clazz
     * @param id
     * @return
     */
    public List<String> getRedisFuzzySearchKeyListById(Class clazz, Long id) {
        if (clazz == null || id == null) {
            throw new IllegalArgumentException("clazz, id 都不能为 null");
        }

        String pattern = String.format(CommonRedisKey.FUZZY_SEARCH_CACHE, clazz.getName(), id, "*");
        return findKeysForPage(pattern, 1, Long.MAX_VALUE, false).getRecords();
    }
}
