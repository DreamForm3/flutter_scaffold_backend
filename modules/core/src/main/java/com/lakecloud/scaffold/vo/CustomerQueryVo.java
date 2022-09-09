package com.lakecloud.scaffold.vo;

import io.geekidea.springbootplus.framework.common.entity.RedisFuzzySearchKey;
import io.geekidea.springbootplus.framework.util.RedisUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.util.Date;

/**
 * <pre>
 * 客户 查询结果对象
 * </pre>
 *
 * @author Alex.King
 * @date 2020-09-17
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "CustomerQueryVo对象")
public class CustomerQueryVo implements Serializable, RedisFuzzySearchKey {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("客户姓名")
    private String name;

    @ApiModelProperty("联系方式")
    private String contactInfo;

    @ApiModelProperty("创建用户")
    private Long createUser;

    @ApiModelProperty("状态，0：禁用，1：启用")
    private Integer state;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("逻辑删除标记")
    private Boolean isDelete;

    @ApiModelProperty("数据版本")
    private Integer version;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改时间")
    private Date updateTime;

    @Override
    public String getRedisFuzzySearchKey() {
        return RedisUtils.getRedisFuzzySearchKey(getClass(), id, name, contactInfo);
    }
}