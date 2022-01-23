package smart.model.mptag;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MPTagListVO {
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "图标")
    private String icon;
    @ApiModelProperty(value = "名称")
    private String fullName;
}
