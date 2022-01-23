package smart.form.model.archivalborrow;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 档案借阅申请
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 8:46
 */
@Data
public class ArchivalBorrowForm {
    @NotNull(message = "紧急程度不能为空")
    @ApiModelProperty(value = "紧急程度")
    private Integer flowUrgent;
    @ApiModelProperty(value = "申请原因")
    private String applyReason;
    @NotBlank(message = "档案编码不能为空")
    @ApiModelProperty(value = "档案编码")
    private String archivesId;
    @NotBlank(message = "借阅方式不能为空")
    @ApiModelProperty(value = "借阅方式")
    private String borrowMode;
    @NotBlank(message = "申请人员不能为空")
    @ApiModelProperty(value = "申请人员")
    private String applyUser;
    @NotBlank(message = "流程标题不能为空")
    @ApiModelProperty(value = "流程标题")
    private String flowTitle;
    @NotNull(message = "归还时间不能为空")
    @ApiModelProperty(value = "归还时间")
    private Long returnDate;
    @NotBlank(message = "档案名称不能为空")
    @ApiModelProperty(value = "档案名称")
    private String archivesName;
    @NotBlank(message = "借阅部门不能为空")
    @ApiModelProperty(value = "借阅部门")
    private String borrowingDepartment;
    @NotNull(message = "借阅时间不能为空")
    @ApiModelProperty(value = "借阅时间")
    private Long borrowingDate;
    @NotBlank(message = "档案属性不能为空")
    @ApiModelProperty(value = "档案属性")
    private String archivalAttributes;
    @NotBlank(message = "流程主键不能为空")
    @ApiModelProperty(value = "流程主键")
    private String flowId;
    @NotBlank(message = "流程单据不能为空")
    @ApiModelProperty(value = "流程单据")
    private String billNo;
    @ApiModelProperty(value = "提交/保存 0-1")
    private String status;
}
