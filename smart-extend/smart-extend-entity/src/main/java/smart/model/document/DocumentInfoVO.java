package smart.model.document;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DocumentInfoVO {
    @ApiModelProperty(value = "文件名称")
    private String fullName;
    @ApiModelProperty(value = "主键id")
    private String id;
    @ApiModelProperty(value = "文档分类")
    private Integer type;
    @ApiModelProperty(value = "文档父级")
    private String parentId;

}
