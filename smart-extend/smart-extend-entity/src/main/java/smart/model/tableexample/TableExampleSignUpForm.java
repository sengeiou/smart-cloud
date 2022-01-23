package smart.model.tableexample;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 更新标签
 */
@Data
public class TableExampleSignUpForm {
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "项目标记")
    private String sign;
}
