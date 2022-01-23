package smart.base.model.dbtable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class DbTableInfoVO {
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "表名")
    private String table;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "表说明")
    private String tableName;
    private String newTable;
}
