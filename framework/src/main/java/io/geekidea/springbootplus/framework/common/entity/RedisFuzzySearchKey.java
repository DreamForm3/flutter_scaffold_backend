package io.geekidea.springbootplus.framework.common.entity;

import io.swagger.annotations.ApiModel;

@ApiModel("RedisKey")
public interface RedisFuzzySearchKey {
    /**
     * 生成redis key
     * @return
     */
    String getRedisFuzzySearchKey();
}
