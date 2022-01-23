package smart.model.schedule;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ScheduleListVO{
    @ApiModelProperty(value = "日程主键")
    private String id;
    @ApiModelProperty(value = "开始时间")
    private long startTime;
    @ApiModelProperty(value = "开始时间")
    private long endTime;
    @ApiModelProperty(value = "颜色")
    private String colour;
    @ApiModelProperty(value = "日程内容")
    private String content;
}
