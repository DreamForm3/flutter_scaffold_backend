package com.lakecloud.scaffold.entity;

import com.lakecloud.scaffold.enums.UserMsgStatusEnum;
import io.geekidea.springbootplus.framework.common.entity.BaseEntity;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.Version;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import io.geekidea.springbootplus.framework.core.validator.groups.Update;

/**
 * 用户消息
 *
 * @author Max.King
 * @since 2022-02-22
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "UserMsg对象")
public class UserMsg extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("发送用户")
    private Long sendUser;
    @ApiModelProperty("接收用户")
    private Long receiveUser;
    @ApiModelProperty("消息标题")
    private String title;
    @ApiModelProperty("消息正文")
    private String content;
    @ApiModelProperty("消息状态，UNREAD：未读；READ：已读；REPLIED：已回复")
    private UserMsgStatusEnum status;
    @ApiModelProperty("回复消息ID")
    private Long replyId;
    @ApiModelProperty("消息发送时间")
    private Date sentTime;
    @ApiModelProperty("接收方查看时间")
    private Date receiveTime;
    @ApiModelProperty("接收方回复时间")
    private Date replyTime;


    @ApiModelProperty(hidden = true)
    private String bak1;
    @ApiModelProperty(hidden = true)
    private String bak2;
    @ApiModelProperty(hidden = true)
    private String bak3;
    @ApiModelProperty(hidden = true)
    private String bak4;
    @ApiModelProperty(hidden = true)
    private String bak5;
}
