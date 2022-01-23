package smart.base.model.dbtable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DbTableListVO {
    @ApiModelProperty(value = "连接名称")
    private String fullName;
    @ApiModelProperty(value = "主键")
    private String id;
}
