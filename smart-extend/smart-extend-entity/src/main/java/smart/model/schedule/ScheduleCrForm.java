package smart.model.schedule;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
public class ScheduleCrForm {
    @ApiModelProperty(value = "必填")
    private long startTime;
    @NotNull(message = "必填")
    @ApiModelProperty(value = "结束时间(时间戳)")
    private long endTime;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "日程内容")
    private String content;
    @ApiModelProperty(value = "提醒设置")
    private Integer early;
    @ApiModelProperty(value = "APP提醒(1-提醒，0-不提醒)")
    private Integer appAlert;
    @ApiModelProperty(value = "日程颜色")
    private String colour;
    @ApiModelProperty(value = "颜色样式")
    private String colourCss;
    @ApiModelProperty(value = "微信提醒(1-提醒，0-不提醒)")
    private Integer weChatAlert;
    @ApiModelProperty(value = "邮件提醒(1-提醒，0-不提醒)")
    private Integer mailAlert;
    @ApiModelProperty(value = "短信提醒(1-提醒，0-不提醒)")
    private Integer mobileAlert;

}
