package smart.permission.model.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class UserLanguageForm {
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "语言代码")
    private String language;
}
