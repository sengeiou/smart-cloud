package smart.form.model.applydelivergoods;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 发货申请单
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 8:46
 */
@Data
public class ApplyDeliverGoodsEntryInfoModel {
    @ApiModelProperty(value = "发货明细主键")
    private String id;
    @ApiModelProperty(value = "发货主键")
    private String invoiceId;
    @ApiModelProperty(value = "商品名称")
    private String goodsName;
    @ApiModelProperty(value = "规格型号")
    private String specifications;
    @ApiModelProperty(value = "单位")
    private String unit;
    @ApiModelProperty(value = "数量")
    private String qty;
    @ApiModelProperty(value = "单价")
    private BigDecimal price;
    @ApiModelProperty(value = "金额")
    private BigDecimal amount;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "排序码",example = "1")
    private Long sortCode;
}
