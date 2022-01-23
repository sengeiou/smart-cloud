package smart.base.model.systemconfig;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class EmailTestForm {
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "邮箱地址")
    private String account;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "邮箱密码")
    private String password;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "POP3服务")
    private String pop3Host;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "POP3端口")
    private Integer pop3Port;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "SMTP服务")
    private String smtpHost;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "SMTP端口")
    private Integer smtpPort;
    @ApiModelProperty(value = "ssl登录")
    private String emailSsl;
}
