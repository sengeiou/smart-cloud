package smart.model.email;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class EmailCheckForm {
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "POP3端口")
    private String pop3Port;
    @ApiModelProperty(value = "ssl登录")
    private String emailSsl;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "邮箱地址")
    private String account;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "POP3服务")
    private String pop3Host;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "邮箱密码")
    private String password;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "SMTP服务")
    private String smtpHost;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "SMTP端口")
    private String smtpPort;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "显示名称")
    private String senderName;

}
