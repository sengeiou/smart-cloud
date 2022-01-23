package smart.permission.model.organize;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
public class OraganizeCrForm {
    @NotBlank(message = "公司上级不能为空")
    private String parentId;
    @NotBlank(message = "公司名称不能为空")
    private String fullName;
    @NotBlank(message = "公司编码不能为空")
    private String enCode;
    private String description;
    @NotNull(message = "公司状态不能为空")
    private Integer enabledMark;
    private OraganizeCrModel propertyJson;
    @ApiModelProperty(value = "排序")
    private Long sortCode;
}
