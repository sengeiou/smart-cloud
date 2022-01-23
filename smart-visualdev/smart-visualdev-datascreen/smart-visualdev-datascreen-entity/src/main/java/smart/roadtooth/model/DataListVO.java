package smart.roadtooth.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DataListVO {
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "名称")
    private String fullName;
    @ApiModelProperty(value = "大屏截图")
    private String screenShot;
    @ApiModelProperty(value = "发布状态(0-未发布,1-已发布)")
    private Integer enabledMark;
}
