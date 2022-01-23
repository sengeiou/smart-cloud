package smart.base.model.logmodel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LoginLogVO {
    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "创建时间",example = "1")
    private Long creatorTime;
    @ApiModelProperty(value = "登陆用户")
    private String userName;
    @ApiModelProperty(value = "登陆IP")
    private String iPAddress;
    @ApiModelProperty(value = "登陆平台")
    private String platForm;
}
