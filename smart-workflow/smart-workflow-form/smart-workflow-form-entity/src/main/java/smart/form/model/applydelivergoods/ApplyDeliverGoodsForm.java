package smart.form.model.applydelivergoods;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 发货申请单
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 8:46
 */
@Data
public class ApplyDeliverGoodsForm {
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "流程主键")
    private String flowId;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "流程标题")
    private String flowTitle;
    @NotNull(message = "必填")
    @ApiModelProperty(value = "紧急程度")
    private Integer flowUrgent;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "流程单据")
    private String billNo;
    @NotBlank(message = "必填")
    @ApiModelProperty(value ="客户名称")
    private String customerName;
    @ApiModelProperty(value ="联系人")
    private String contacts;
    @ApiModelProperty(value ="联系电话")
    private String contactPhone;
    @ApiModelProperty(value ="客户地址")
    private String customerAddres;
    @ApiModelProperty(value ="货品所属")
    private String goodsBelonged;
    @ApiModelProperty(value ="发货日期")
    private Long invoiceDate;
    @ApiModelProperty(value ="货运公司")
    private String freightCompany;
    @ApiModelProperty(value ="发货类型")
    private String deliveryType;
    @ApiModelProperty(value ="货运单号")
    private String rransportNum;
    @ApiModelProperty(value ="货运费")
    private BigDecimal freightCharges;
    @ApiModelProperty(value ="保险金额")
    private BigDecimal cargoInsurance;
    @ApiModelProperty(value ="备注")
    private String description;
    @ApiModelProperty(value ="发货金额")
    private BigDecimal invoiceValue;
    @ApiModelProperty(value = "提交/保存 0-1")
    private String status;
    @ApiModelProperty(value ="明细")
    List<ApplyDeliverGoodsEntryInfoModel> entryList;
}
