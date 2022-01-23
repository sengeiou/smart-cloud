package smart.model.qydepart;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class QYDepartListVO {
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "父主键")
    private String parentId;
    @ApiModelProperty(value = "名称")
    private String fullName;
    @ApiModelProperty(value = "编码")
    private String enCode;
    @ApiModelProperty(value = "分类")
    private String category;
    @ApiModelProperty(value = "状态")
    private Integer syncState;
    @ApiModelProperty(value = "提交状态")
    private String submitState;
    @ApiModelProperty(value = "备注")
    private String description;
    @ApiModelProperty(value = "最后修改时间")
    private Long lastModifyTime;
    @ApiModelProperty(value = "是否有下级菜单")
    private Boolean hasChildren;
    @ApiModelProperty(value = "下级菜单列表")
    private List<QYDepartListVO> children;
}
