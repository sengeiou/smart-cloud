package smart.permission.model.organize;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OraganizeSelectorVO {
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "父主键")
    private String parentId;
    @ApiModelProperty(value = "名称")
    private String fullName;
    @ApiModelProperty(value = "图标")
    private String icon;
    @ApiModelProperty(value = "是否可用")
    private int enabledMark;
    @ApiModelProperty(value = "是否有下级菜单")
    private boolean hasChildren = true;
    @ApiModelProperty(value = "下级菜单列表")
    private List<OraganizeSelectorVO> children = new ArrayList<>();
}
