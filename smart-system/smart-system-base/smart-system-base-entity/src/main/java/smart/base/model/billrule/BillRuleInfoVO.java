package smart.base.model.billrule;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BillRuleInfoVO {
    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "业务名称")
    private String fullName;
    @ApiModelProperty(value = "流水位数")
    private String enCode;
    @ApiModelProperty(value = "流水前缀")
    private String prefix;
    @ApiModelProperty(value = "流水日期格式")
    private String dateFormat;
    @ApiModelProperty(value = "流水位数")
    private Integer digit;
    @ApiModelProperty(value = "流水起始")
    private String startNumber;
    @ApiModelProperty(value = "流水范例")
    private String example;
    @ApiModelProperty(value = "状态(0-禁用，1-启用)")
    private Integer enabledMark;
    @ApiModelProperty(value = "流水说明")
    private String description;
    @ApiModelProperty(value = "排序码")
    private long sortCode;
}
