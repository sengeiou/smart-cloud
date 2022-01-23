package smart.model.tableexample;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.math.BigDecimal;

/**
 * 行编辑
 */
@Data
public class TableExampleRowUpForm {
   @NotBlank(message = "必填")
   @ApiModelProperty(value = "项目名称")
    private String projectName;

   @NotBlank(message = "必填")
   @ApiModelProperty(value = "项目编码")
   private String projectCode;

   @NotBlank(message = "必填")
   @ApiModelProperty(value = "项目类型")
   private String projectType;

   @ApiModelProperty(value = "项目阶段")
   private String projectPhase;

   @ApiModelProperty(value = "交互日期")
   private long interactionDate;

   @ApiModelProperty(value = "客户名称")
   private String customerName;

   @NotBlank(message = "必填")
   @ApiModelProperty(value = "负责人")
   private String principal;

   @ApiModelProperty(value = "立顶人")
   private String jackStands;

   @ApiModelProperty(value = "费用金额",example = "1")
   private BigDecimal costAmount;

   @ApiModelProperty(value = "已用金额",example = "1")
   private BigDecimal tunesAmount;

   @ApiModelProperty(value = "预计收入",example = "1")
   private BigDecimal projectedIncome;

   @ApiModelProperty(value = "备注")
   private String description;

   private String oper;
}
