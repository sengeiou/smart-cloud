package smart.model.projectgantt;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProjectGanttManagerIModel {
    @ApiModelProperty(value = "账号+名字")
    private String account;
    @ApiModelProperty(value = "用户头像")
    private String headIcon;
}
