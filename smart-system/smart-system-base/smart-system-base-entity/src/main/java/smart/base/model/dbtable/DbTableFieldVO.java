package smart.base.model.dbtable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DbTableFieldVO {
    @ApiModelProperty(value = "字段名")
    private String field;
    @ApiModelProperty(value = "字段说明")
    private String fieldName;
    @ApiModelProperty(value = "数据类型")
    private String dataType;
    @ApiModelProperty(value = "数据长度")
    private String dataLength;
    @ApiModelProperty(value = "主键")
    private Integer primaryKey;
    @ApiModelProperty(value = "允许空")
    private Integer allowNull;
}
