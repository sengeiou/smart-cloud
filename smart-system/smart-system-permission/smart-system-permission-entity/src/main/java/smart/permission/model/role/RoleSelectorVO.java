package smart.permission.model.role;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RoleSelectorVO {
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "名称")
    private String fullName;
    private String type;

}
