package smart.permission.model.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class UserResetPasswordForm {
    @NotBlank(message = "必填")
    @ApiModelProperty("用户id")
    private String id;
    @NotBlank(message = "必填")
    @ApiModelProperty("新密码，需要 MD5 加密后传输")
    private String userPassword;
    @NotBlank(message = "必填")
    @ApiModelProperty("重复新密码")
    private String validatePassword;
}
