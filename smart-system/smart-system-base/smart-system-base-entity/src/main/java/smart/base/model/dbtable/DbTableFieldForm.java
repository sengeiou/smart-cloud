package smart.base.model.dbtable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
public class DbTableFieldForm{
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "字段名")
    private String field;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "字段说明")
    private String fieldName;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "数据类型")
    private String dataType;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "数据长度")
    private String dataLength;
    @NotNull(message = "必填")
    @ApiModelProperty(value = "允许空")
    private Integer allowNull;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "插入位置")
    private String index;
    private Integer primaryKey;
}
