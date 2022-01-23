package smart.permission.model.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class UserThemeForm {
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "系统主题")
    private String theme;
}
