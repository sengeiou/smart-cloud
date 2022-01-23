package smart.model.currenuser;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MenuTreeVO {
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "名称")
    private String fullName;
    @ApiModelProperty(value = "菜单编码")
    private String enCode;
    @ApiModelProperty(value = "父主键")
    private String parentId;
    @ApiModelProperty(value = "图标")
    private String icon;
    @ApiModelProperty(value = "是否有下级菜单")
    private Boolean hasChildren = true;
    @ApiModelProperty(value = "菜单地址")
    private String urlAddress;
    @ApiModelProperty(value = "链接目标")
    private String linkTarget;
    @ApiModelProperty(value = "下级菜单列表")
    private List<MenuTreeVO> children = new ArrayList<>();
    @ApiModelProperty(value = "菜单分类【1-类别、2-页面】")
    private Integer type;
    private String propertyJson;
    private Long sortCode;
}
