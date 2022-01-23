package smart.model.mpmenu;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MPMenuSelectorVo {
    @ApiModelProperty(value = "菜单主键")
    private String id;
    @ApiModelProperty(value = "菜单名称")
    private String fullName;
//    @ApiModelProperty(value = "菜单类型")
//    private String type;
//    @ApiModelProperty(value = "页面地址")
//    private String url;
    @ApiModelProperty(value = "菜单上级")
    private String parentId;
//    @ApiModelProperty(value = "是否有下级菜单")
//    private Boolean hasChildren = true;
//    @ApiModelProperty(value = "下级菜单列表")
//    private List<MPMenuSelectorVo> children = new ArrayList<>();
}
