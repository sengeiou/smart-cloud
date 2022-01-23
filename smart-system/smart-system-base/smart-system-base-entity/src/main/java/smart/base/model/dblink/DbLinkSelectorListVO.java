package smart.base.model.dblink;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DbLinkSelectorListVO {
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "数据库类型")
    private String fullName;
    @ApiModelProperty(value = "数据库名称")
    private String dbType;
}
