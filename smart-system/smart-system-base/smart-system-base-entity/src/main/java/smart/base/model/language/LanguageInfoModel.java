package smart.base.model.language;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LanguageInfoModel {
    @ApiModelProperty(value = "翻译分类")
    private String languageTypeId;
    @ApiModelProperty(value = "语言")
    private String signKey;
}
