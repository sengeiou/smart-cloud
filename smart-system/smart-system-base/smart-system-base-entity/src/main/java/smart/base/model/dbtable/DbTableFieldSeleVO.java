package smart.base.model.dbtable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DbTableFieldSeleVO {
    @ApiModelProperty(value = "字段名")
    private String field;
    @ApiModelProperty(value = "字段说明")
    private String fieldName;
}
