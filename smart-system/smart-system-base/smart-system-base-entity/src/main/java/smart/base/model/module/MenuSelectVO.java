package smart.base.model.module;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MenuSelectVO {
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "父主键")
    private String parentId;
    @ApiModelProperty(value = "名称")
    private String fullName;
    private Integer isButtonAuthorize;
    private Integer isColumnAuthorize;
    private Integer isDataAuthorize;
    private Long sortCode;
    @ApiModelProperty(value = "图标")
    private String icon;
    @ApiModelProperty(value = "是否有下级菜单")
    private boolean hasChildren;
    @ApiModelProperty(value = "下级菜单列表")
    private List<MenuSelectVO> children = new ArrayList<>();
}
