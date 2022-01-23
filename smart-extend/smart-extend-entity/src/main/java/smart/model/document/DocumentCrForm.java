package smart.model.document;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
public class DocumentCrForm {
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "文件夹名称")
    private String fullName;
    @NotNull(message = "必填")
    @ApiModelProperty(value = "文档分类")
    private Integer type;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "文档父级")
    private String parentId;
}
