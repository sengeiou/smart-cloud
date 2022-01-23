package smart.base.model.language;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class LanguageCrForm {
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "翻译分类")
    private String languageTypeId;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "翻译标记")
    private String signKey;
    @NotNull(message = "必填")
    @ApiModelProperty(value = "语言和名称")
    List<LanguageCrModel> translateList;

}
