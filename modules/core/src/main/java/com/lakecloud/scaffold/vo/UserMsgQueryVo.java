package com.lakecloud.scaffold.vo;

import com.lakecloud.scaffold.enums.UserMsgStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.util.Date;

/**
 * <pre>
 * 用户消息 查询结果对象
 * </pre>
 *
 * @author Max.King
 * @date 2022-02-22
 */
@Data
@ToString(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "UserMsgQueryVo对象")
public class UserMsgQueryVo implements Serializable {
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

    private String bak1;

    private String bak2;

    private String bak3;

    private String bak4;

    private String bak5;
}