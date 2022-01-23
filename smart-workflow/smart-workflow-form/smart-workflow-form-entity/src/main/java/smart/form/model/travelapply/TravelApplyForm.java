package smart.form.model.travelapply;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 出差预支申请单
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 8:46
 */
@Data
public class TravelApplyForm {
    @NotBlank(message = "起始地点不能为空")
    @ApiModelProperty(value = "起始地点")
    private String startPlace;
    @NotBlank(message = "出差人不能为空")
    @ApiModelProperty(value = "出差人")
    private String travelMan;
    @NotNull(message = "紧急程度不能为空")
    @ApiModelProperty(value = "紧急程度")
    private Integer flowUrgent;
    @NotNull(message = "结束日期不能为空")
    @ApiModelProperty(value = "结束日期")
    private Long endDate;
    @ApiModelProperty(value = "预支旅费")
    private BigDecimal prepaidTravel;
    @NotBlank(message = "目的地不能为空")
    @ApiModelProperty(value = "目的地")
    private String destination;
    @ApiModelProperty(value = "备注")
    private String description;
    @NotBlank(message = "流程标题不能为空")
    @ApiModelProperty(value = "流程标题")
    private String flowTitle;
    @NotBlank(message = "所属部门不能为空")
    @ApiModelProperty(value = "所属部门")
    private String departmental;
    @NotBlank(message = "所属职务不能为空")
    @ApiModelProperty(value = "所属职务")
    private String position;
    @NotNull(message = "申请日期不能为空")
    @ApiModelProperty(value = "申请日期")
    private Long applyDate;
    @NotBlank(message = "流程主键不能为空")
    @ApiModelProperty(value = "流程主键")
    private String flowId;
    @NotBlank(message = "流程单据不能为空")
    @ApiModelProperty(value = "流程单据")
    private String billNo;
    @NotNull(message = "开始日期不能为空")
    @ApiModelProperty(value = "开始日期")
    private Long startDate;
    @ApiModelProperty(value = "提交/保存 0-1")
    private String status;
}
