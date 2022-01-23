package smart.permission.model.organize;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OraganizeInfoVO {
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "父主键")
    private String parentId;
    @ApiModelProperty(value = "名称")
    private String fullName;
    @ApiModelProperty(value = "编码")
    private String enCode;
    @ApiModelProperty(value = "状态")
    private int enabledMark;
    @ApiModelProperty(value = "备注")
    private String description;
    @ApiModelProperty(value = "扩展属性")
    private String propertyJson;
    @ApiModelProperty(value = "排序")
    private Long sortCode;
}
