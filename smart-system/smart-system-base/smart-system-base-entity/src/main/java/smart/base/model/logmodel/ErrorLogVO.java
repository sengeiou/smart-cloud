package smart.base.model.logmodel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ErrorLogVO {
    @ApiModelProperty(value = "创建用户")
    private String userName;
    @ApiModelProperty(value = "创建时间",example = "1")
    private Long creatorTime;
    @ApiModelProperty(value = "IP")
    private String iPAddress;
    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "异常功能")
    private String moduleName;
    @ApiModelProperty(value = "异常描述")
    private String json;
}
