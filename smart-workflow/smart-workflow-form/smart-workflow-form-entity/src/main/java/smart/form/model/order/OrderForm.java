package smart.form.model.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 订单信息
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 8:46
 */
@Data
public class OrderForm {
   @NotNull(message = "必填")
   @ApiModelProperty(value = "订单日期")
   private Long orderDate;
   @NotBlank(message = "必填")
   @ApiModelProperty(value = "订单编号")
   private String orderCode;
   @ApiModelProperty(value = "应收金额")
   private String receivableMoney;
   @ApiModelProperty(value = "定金比率")
   private String earnestRate;
   @ApiModelProperty(value = "预付定金")
   private String prepayEarnest;
   @NotBlank(message = "必填")
   @ApiModelProperty(value = "客户名称")
   private String customerName;
   @NotBlank(message = "必填")
   @ApiModelProperty(value = "客户Id")
   private String customerId;
   @NotBlank(message = "必填")
   @ApiModelProperty(value = "业务员Id")
   private String salesmanId;
   @NotBlank(message = "必填")
   @ApiModelProperty(value = "业务员")
   private String salesmanName;
   @NotBlank(message = "必填")
   @ApiModelProperty(value = "付款方式")
   private String paymentMode;
   @ApiModelProperty(value = "描述")
   private String description;
   @ApiModelProperty(value = "运输方式")
   private String transportMode;
   @ApiModelProperty(value = "发货日期")
   private Long deliveryDate;
   @ApiModelProperty(value = "发货地址")
   private String deliveryAddress;
   @ApiModelProperty(value = "附件信息")
   private String fileJson;
   @ApiModelProperty(value = "自然主键")
   private String id;
   private List<OrderEntryModel> goodsList;
   private List<OrderReceivableModel> collectionPlanList;
   /**0.提交 1.保存**/
   private String status;
   /**指定用户**/
   private String freeApproverUserId;
}

