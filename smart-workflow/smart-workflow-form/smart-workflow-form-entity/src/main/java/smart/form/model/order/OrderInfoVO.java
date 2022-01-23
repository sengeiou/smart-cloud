package smart.form.model.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
public class OrderInfoVO  {
    @ApiModelProperty(value = "有效标志", example = "1")
    private Integer enabledMark;
    @ApiModelProperty(value = "制单人员")
    private String creatorUserId;
    @ApiModelProperty(value = "附件信息")
    private String fileJson;
    @ApiModelProperty(value = "付款方式")
    private String paymentMode;
    @ApiModelProperty(value = "制单时间")
    private Long creatorTime;
    @ApiModelProperty(value = "业务员Id")
    private String salesmanId;
    @ApiModelProperty(value = "预付定金")
    private String prepayEarnest;
    @ApiModelProperty(value = "运输方式")
    private String transportMode;
    @ApiModelProperty(value = "客户名称")
    private String customerName;
    @ApiModelProperty(value = "发货日期")
    private Long deliveryDate;
    @ApiModelProperty(value = "订单主键")
    private String id;
    @ApiModelProperty(value = "业务员")
    private String salesmanName;
    @ApiModelProperty(value = "客户Id")
    private String customerId;
    @ApiModelProperty(value = "修改时间")
    private Long lastModifyTime;
    @ApiModelProperty(value = "应收金额")
    private String receivableMoney;
    @ApiModelProperty(value = "发货地址")
    private String deliveryAddress;
    @ApiModelProperty(value = "定金比率")
    private String earnestRate;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "修改用户")
    private String lastModifyUserId;
    @ApiModelProperty(value = "订单日期")
    private Long orderDate;
    @ApiModelProperty(value = "订单编号")
    private String orderCode;
    List<OrderInfoOrderEntryModel> goodsList;
    List<OrderInfoOrderReceivableModel> collectionPlanList;
}
