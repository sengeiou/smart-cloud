package smart.permission.model.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class UserModifyPasswordForm {
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "旧密码,需要 MD5 加密后传输")
    private String oldPassword;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "新密码")
    private String password;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "验证码")
    private String code;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "验证码标识")
    private String timestamp;
}
