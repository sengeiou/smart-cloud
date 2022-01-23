package smart.permission.model.role;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
public class RoleCrForm {
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "角色名称")
    private String fullName;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "角色编号")
    private String enCode;
    @NotNull(message = "必填")
    @ApiModelProperty(value = "角色类型(id)")
    private String type;
    @NotNull(message = "必填")
    @ApiModelProperty(value = "状态")
    private int enabledMark;
    private String description;
    @ApiModelProperty(value = "排序")
    private long sortCode;
}
