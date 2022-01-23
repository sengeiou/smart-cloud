package smart.model.mpmenu;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MPMenuInfoVO {
    @ApiModelProperty(value = "菜单类型")
    private String type;
    @ApiModelProperty(value = "菜单主键")
    private String id;
    @ApiModelProperty(value = "菜单上级")
    private String parentId;
    @ApiModelProperty(value = "菜单名称")
    private String fullName;
    @ApiModelProperty(value = "页面地址")
    private String url;
    @ApiModelProperty(value = "文字内容")
    private String content;
    @ApiModelProperty(value = "排序码")
    private long sortCode;
}
