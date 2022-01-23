package smart.base.model.button;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 按钮
 */
@Data
public class ButtonVO {
    @ApiModelProperty(value = "按钮主键")
    private String id;
    @ApiModelProperty(value = "按钮上级")
    private String parentId;
    @ApiModelProperty(value = "按钮名称")
    private String fullName;
    @ApiModelProperty(value = "按钮编码")
    private String enCode;
    @ApiModelProperty(value = "按钮图标")
    private String icon;
    @ApiModelProperty(value = "请求地址")
    private String urlAddress;
    @ApiModelProperty(value = "功能主键")
    private String moduleId;
}
