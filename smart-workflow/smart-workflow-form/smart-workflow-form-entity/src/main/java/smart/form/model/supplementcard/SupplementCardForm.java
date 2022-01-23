package smart.form.model.supplementcard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 补卡申请
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 8:46
 */
@Data
public class SupplementCardForm {
    @NotNull(message = "必填")
    @ApiModelProperty(value = "紧急程度")
    private Integer flowUrgent;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "员工姓名")
    private String fullName;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "备注")
    private String description;
    @ApiModelProperty(value = "证明人")
    private String witness;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "流程标题")
    private String flowTitle;
    @NotNull(message = "必填")
    @ApiModelProperty(value = "开始时间")
    private Long  startTime;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "所在职务")
    private String position;
    @ApiModelProperty(value = "补卡次数")
    private String supplementNum;
    @NotNull(message = "必填")
    @ApiModelProperty(value = "结束时间")
    private Long  endTime;
    @NotNull(message = "必填")
    @ApiModelProperty(value = "申请日期")
    private Long  applyDate;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "所在部门")
    private String department;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "流程主键")
    private String flowId;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "流程单据")
    private String billNo;
    @ApiModelProperty(value = "提交/保存 0-1")
    private String status;

}
