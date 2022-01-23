package smart.model.tableexample;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TableExampleIndustryListVO {
    @ApiModelProperty(value = "父级主键")
    private String id;
    @ApiModelProperty(value = "名称")
    private String fullName;
    @ApiModelProperty(value = "图标")
    private String enCode;
    @ApiModelProperty(value = "是否有下级菜单")
    private Boolean isLeaf;
    @ApiModelProperty(value = "父级主键")
    private String parentId;
}
