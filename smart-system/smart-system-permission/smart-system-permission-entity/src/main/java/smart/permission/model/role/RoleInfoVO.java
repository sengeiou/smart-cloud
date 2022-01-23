package smart.permission.model.role;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RoleInfoVO {
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "名称")
    private String fullName;
    @ApiModelProperty(value = "编码")
    private String enCode;
    @ApiModelProperty(value = "类型")
    private String type;
    @ApiModelProperty(value = "状态")
    private Integer enabledMark;
    @ApiModelProperty(value = "备注")
    private String description;
    @ApiModelProperty(value = "排序")
    private long sortCode;
}
