package smart.model.qymessage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class QYMessageUserListVO {
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "图标")
    private String icon;
    @ApiModelProperty(value = "昵称")
    private String nickName;
}
