package smart.base.model.language;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class LanguageListDTO {
    @ApiModelProperty(value = "编码")
    @JSONField(name = "F_ENCODE")
    private String encode;

    @JSONField(name = "F_SIGNKEY")
    @ApiModelProperty(value = "标记")
    private String signKey;

    @ApiModelProperty(value = "语言列表")
    private Map<String,Object> languageEnCodes;

}
