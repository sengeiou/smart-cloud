package smart.base.model.column;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 列表
 */
@Data
public class ColumnListVO {
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "列表名称")
    private String fullName;
    @ApiModelProperty(value = "编码")
    private String enCode;
    @ApiModelProperty(value = "表格")
    private String bindTable;
    @ApiModelProperty(value = "是否启用")
    private Integer enabledMark;
}
