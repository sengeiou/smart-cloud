package smart.model.email;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EmailCofigInfoVO {

    @ApiModelProperty(value = "账户")
    private String account;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "POP3服务")
    private String pop3Host;

    @ApiModelProperty(value = "POP3端口")
    private Integer pop3Port;

    @ApiModelProperty(value = "发件人名称")
    private String senderName;

    @ApiModelProperty(value = "SMTP服务")
    private String smtpHost;

    @ApiModelProperty(value = "SMTP端口")
    private Integer smtpPort;
    @ApiModelProperty(value = "创建时间")
    private long creatorTime;
    @ApiModelProperty(value = "是否开户SSL登录(1-是,0否)")
    private Integer emailSsl;
}
