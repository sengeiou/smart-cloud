package smart.model.email;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EmailInfoVO{
    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "邮件主题")
    private String subject;
    @ApiModelProperty(value = "抄送人")
    private String cc;
    @ApiModelProperty(value = "密送人")
    private String bcc;
    @ApiModelProperty(value = "发件人姓名")
    private String senderName;
    @ApiModelProperty(value = "发件人邮箱")
    private String sender;
    @ApiModelProperty(value = "时间")
    private Long fdate;
    @ApiModelProperty(value = "创建时间")
    private Long creatorTime;
    @ApiModelProperty(value = "收件箱收件人")
    private String maccount;
    @ApiModelProperty(value = "发送收件人")
    private String recipient;
    @ApiModelProperty(value = "附件对象")
    private String attachment;
    @ApiModelProperty(value = "邮件内容")
    private String bodyText;
}
