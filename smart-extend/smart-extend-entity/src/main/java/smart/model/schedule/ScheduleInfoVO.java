package smart.model.schedule;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ScheduleInfoVO {
    @ApiModelProperty(value = "日程主键")
    private String id;
    @ApiModelProperty(value = "开始时间(时间戳)")
    private long startTime;
    @ApiModelProperty(value = "结束时间(时间戳)")
    private long endTime;
    @ApiModelProperty(value = "日程内容")
    private String content;
    @ApiModelProperty(value = "提醒设置",example = "1")
    private Integer early;
    @ApiModelProperty(value = "APP提醒(1-提醒，0-不提醒)",example = "1")
    private Integer appAlert;
    @ApiModelProperty(value = "日程颜色")
    private String colour;
    @ApiModelProperty(value = "颜色样式")
    private String colourCss;
    @ApiModelProperty(value = "微信提醒(1-提醒，0-不提醒)",example = "1")
    private Integer weChatAlert;
    @ApiModelProperty(value = "邮件提醒(1-提醒，0-不提醒)",example = "1")
    private Integer mailAlert;
    @ApiModelProperty(value = "短信提醒(1-提醒，0-不提醒)",example = "1")
    private Integer mobileAlert;
}
