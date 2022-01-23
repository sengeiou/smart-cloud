package smart.model.tableexample;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class TableExampleCrForm {
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "负责人")
    private String principal;

    @ApiModelProperty(value = "交互日期")
    private long interactionDate;

    @ApiModelProperty(value = "立顶人")
    private String jackStands;

    @NotBlank(message = "必填")
    @ApiModelProperty(value = "项目编码")
    private String projectCode;

    @ApiModelProperty(value = "项目阶段")
    private String projectPhase;

    @ApiModelProperty(value = "已用金额",example = "1")
    private Long tunesAmount;

    @NotBlank(message = "必填")
    @ApiModelProperty(value = "项目类型")
    private String projectType;

    @ApiModelProperty(value = "费用金额",example = "1")
    private Long costAmount;

    @ApiModelProperty(value = "预计收入",example = "1")
    private Long projectedIncome;

    @ApiModelProperty(value = "备注")
    private String description;

    @NotBlank(message = "必填")
    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "客户名称")
    private String customerName;

}
