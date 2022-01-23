package smart.model.projectgantt;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ProjectGanttCrForm {

    @NotNull(message = "必填")
    @ApiModelProperty(value = "完成进度")
    private Integer schedule;

    @NotBlank(message = "必填")
    @ApiModelProperty(value = "项目名称")
    private String fullName;

    @NotBlank(message = "必填")
    @ApiModelProperty(value = "项目编码")
    private String enCode;

    @NotBlank(message = "必填")
    @ApiModelProperty(value = "参与人员")
    private String managerIds;

    @NotNull(message = "必填")
    @ApiModelProperty(value = "开始时间")
    private long startTime;

    @NotNull(message = "必填")
    @ApiModelProperty(value = "结束时间")
    private long endTime;

    @NotNull(message = "必填")
    @ApiModelProperty(value = "项目工期")
    private BigDecimal timeLimit;

    @ApiModelProperty(value = "项目描述")
    private String description;

    @ApiModelProperty(value = "项目状态")
    private Integer state;

}
