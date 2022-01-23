package smart.permission.model.organize;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OraganizeListVO {
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "父主键")
    private String parentId;
    @ApiModelProperty(value = "名称")
    private String fullName;
    @ApiModelProperty(value = "编码")
    private String enCode;
    @ApiModelProperty(value = "备注")
    private String description;
    @ApiModelProperty(value = "状态")
    private int enabledMark;
    private Long creatorTime;
    @ApiModelProperty(value = "是否有下级菜单")
    private boolean hasChildren = true;
    @ApiModelProperty(value = "下级菜单列表")
    private List<OraganizeListVO> children = new ArrayList<>();
    @ApiModelProperty(value = "排序")
    private Long sortCode;
}
