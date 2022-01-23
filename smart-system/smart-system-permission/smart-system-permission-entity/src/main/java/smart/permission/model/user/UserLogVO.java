package smart.permission.model.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserLogVO {
    @ApiModelProperty(value = "登录时间")
    private long creatorTime;
    @ApiModelProperty(value = "登录用户")
    private String userName;
    @ApiModelProperty(value = "登录IP")
    private String iPAddress;
    @ApiModelProperty(value = "摘要")
    private String platForm;
}
