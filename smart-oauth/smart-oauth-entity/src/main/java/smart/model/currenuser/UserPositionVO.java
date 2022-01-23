package smart.model.currenuser;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserPositionVO {
    @ApiModelProperty(value = "岗位id")
    private String id;
    @ApiModelProperty(value = "岗位名称")
    private String name;
}
