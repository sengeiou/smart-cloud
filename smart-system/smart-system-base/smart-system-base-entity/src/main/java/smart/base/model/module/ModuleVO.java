package smart.base.model.module;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 功能
 */
@Data
public class ModuleVO {
    @ApiModelProperty(value = "功能主键")
    private String id;
    @ApiModelProperty(value = "功能上级")
    private String parentId;
    @ApiModelProperty(value = "功能类别【1-类别、2-页面】")
    private int type;
    @ApiModelProperty(value = "功能名称")
    private String fullName;
    @ApiModelProperty(value = "功能编码")
    private String enCode;
    @ApiModelProperty(value = "功能地址")
    private String urlAddress;
}
