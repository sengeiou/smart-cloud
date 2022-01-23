package smart.form.model.rewardpunishment;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 行政赏罚单
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 8:46
 */
@Data
public class RewardPunishmentForm {
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "赏罚原因")
    private String reason;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "流程标题")
    private String flowTitle;
    @NotNull(message = "必填")
    @ApiModelProperty(value = "紧急程度")
    private Integer flowUrgent;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "员工姓名")
    private String fullName;
    @NotNull(message = "必填")
    @ApiModelProperty(value = "填表日期")
    private Long fillFromDate;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "赏罚金额")
    private BigDecimal rewardPun;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "员工职位")
    private String position;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "员工部门")
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
