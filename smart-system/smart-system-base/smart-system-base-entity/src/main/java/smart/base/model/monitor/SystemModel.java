package smart.base.model.monitor;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SystemModel {
    @ApiModelProperty(value = "系统")
    private String os;
    @ApiModelProperty(value = "服务器IP")
    private String ip;
    @ApiModelProperty(value = "运行时间")
    private String day;
}
