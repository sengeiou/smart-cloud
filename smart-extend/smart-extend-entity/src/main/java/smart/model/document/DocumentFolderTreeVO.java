package smart.model.document;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DocumentFolderTreeVO {
    @ApiModelProperty(value = "名称")
    private String fullName;
    @ApiModelProperty(value = "图标")
    private String icon;
    @ApiModelProperty(value = "是否有下级菜单")
    private Boolean hasChildren;
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "父级主键")
    private String parentId;

    private List<DocumentFolderTreeVO> children;
}
