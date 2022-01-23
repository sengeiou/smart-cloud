package smart.form.model.officesupplies;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 领用办公用品申请表
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 8:46
 */
@Data
public class OfficeSuppliesForm {
    @NotBlank(message = "主键id不能为空")
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "用品名称")
    private String articlesName;
    @NotNull(message = "紧急程度不能为空")
    @ApiModelProperty(value = "紧急程度")
    private Integer flowUrgent;
    @ApiModelProperty(value = "用品分类")
    private String classification;
    @ApiModelProperty(value = "用品编码")
    private String articlesId;
    @ApiModelProperty(value = "申请原因")
    private String applyReasons;
    @NotBlank(message = "申请人员不能为空")
    @ApiModelProperty(value = "申请人员")
    private String applyUser;
    @NotBlank(message = "流程标题不能为空")
    @ApiModelProperty(value = "流程标题")
    private String flowTitle;
    @ApiModelProperty(value = "领用仓库")
    private String useStock;
    @ApiModelProperty(value = "用品数量")
    private String articlesNum;
    @NotNull(message = "申请时间不能为空")
    @ApiModelProperty(value = "申请时间")
    private Long  applyDate;
    @NotBlank(message = "所属部门不能为空")
    @ApiModelProperty(value = "所属部门")
    private String department;
    @NotBlank(message = "流程主键不能为空")
    @ApiModelProperty(value = "流程主键")
    private String flowId;
    @NotBlank(message = "流程单据不能为空")
    @ApiModelProperty(value = "流程单据")
    private String billNo;
    @ApiModelProperty(value = "提交/保存 0-1")
    private String status;

}
