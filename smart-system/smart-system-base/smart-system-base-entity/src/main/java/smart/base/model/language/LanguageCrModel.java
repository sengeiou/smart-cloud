package smart.base.model.language;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class LanguageCrModel {
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "名称")
    private String fullName;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "翻译语言")
    private String language;

}
