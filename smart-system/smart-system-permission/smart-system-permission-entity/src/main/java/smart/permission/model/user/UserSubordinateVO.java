package smart.permission.model.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSubordinateVO {
    @ApiModelProperty(value = "头像")
    private String avatar;
    @ApiModelProperty(value = "用户名")
    private String userName;
    @ApiModelProperty(value = "部门")
    private String department;
}
