package com.lakecloud.scaffold.param;

import com.lakecloud.scaffold.enums.UserMsgStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import io.geekidea.springbootplus.framework.core.pagination.BasePageOrderParam;

import java.util.Date;
import java.util.List;

/**
 * <pre>
 * 用户消息 分页参数对象
 * </pre>
 *
 * @author Max.King
 * @date 2022-02-22
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "用户消息分页参数")
public class UserMsgPageParam extends BasePageOrderParam {
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
    private List<UserMsgStatusEnum> statusList;
    @ApiModelProperty("回复消息ID")
    private Long replyId;
    @ApiModelProperty("消息发送时间开始")
    private Date sentTimeStart;
    @ApiModelProperty("消息发送时间结束")
    private Date sentTimeEnd;
    @ApiModelProperty("接收方查看时间开始")
    private Date receiveTimeStart;
    @ApiModelProperty("接收方查看时间结束")
    private Date receiveTimeEnd;
    @ApiModelProperty("接收方回复时间开始")
    private Date replyTimeStart;
    @ApiModelProperty("接收方回复时间结束")
    private Date replyTimeEnd;

}
