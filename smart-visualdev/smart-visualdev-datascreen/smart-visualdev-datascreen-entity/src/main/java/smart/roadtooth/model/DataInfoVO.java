package smart.roadtooth.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DataInfoVO{
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "名称")
    private String fullName;
    @ApiModelProperty(value = "控件属性JSON包")
    private String component;
    @ApiModelProperty(value = "配置信息JSON包")
    private String detail;
    @ApiModelProperty(value = "分类")
    private String categoryId;
    @ApiModelProperty(value = "发布状态(0-未发布,1-已发布)")
    private Integer enabledMark;
}
