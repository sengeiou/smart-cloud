package smart.form.model.receiptsign;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 收文签呈单
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 8:46
 */
@Data
public class ReceiptSignForm {
    @ApiModelProperty(value = "相关附件")
    private String fileJson;
    @NotBlank(message = "流程标题不能为空")
    @ApiModelProperty(value = "流程标题")
    private String flowTitle;
    @NotNull(message = "紧急程度不能为空")
    @ApiModelProperty(value = "紧急程度")
    private Integer flowUrgent;
    @ApiModelProperty(value = "收文标题")
    private String receiptTitle;
    @NotNull(message = "收文日期不能为空")
    @ApiModelProperty(value = "收文日期")
    private Long receiptDate;
    @ApiModelProperty(value = "收文部门")
    private String department;
    @NotBlank(message = "流程主键不能为空")
    @ApiModelProperty(value = "流程主键")
    private String flowId;
    @NotBlank(message = "流程单据不能为空")
    @ApiModelProperty(value = "流程单据")
    private String billNo;
    @ApiModelProperty(value = "收文简述")
    private String receiptPaper;
    @ApiModelProperty(value = "收文人")
    private String collector;
    @ApiModelProperty(value = "提交/保存 0-1")
    private String status;

}
