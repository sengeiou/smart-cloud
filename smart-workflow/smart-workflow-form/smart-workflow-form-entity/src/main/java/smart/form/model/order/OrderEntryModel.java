package smart.form.model.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 订单信息
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 8:46
 */
@Data
public class OrderEntryModel {
    @NotNull(message = "必填")
    @ApiModelProperty(value = "订单日期")
    private Long remove;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "自然主键")
    private String id;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "商品Id")
    private String goodsId;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "商品编码")
    private String goodsCode;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "商品名称")
    private String goodsName;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "规格型号")
    private String specifications;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "单位")
    private String unit;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "数量")
    private String qty;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "单价")
    private String price;
    @NotBlank(message = "金额不能为空")
    @ApiModelProperty(value = "金额")
    private String amount;
    @NotBlank(message = "折扣%不能为空")
    @ApiModelProperty(value = " 折扣%")
    private String discount;
    @NotBlank(message = "税率%不能为空")
    @ApiModelProperty(value = " 税率%")
    private String cess;
    @NotBlank(message = "实际单价不能为空")
    @ApiModelProperty(value = "实际单价")
    private String actualPrice;
    @NotBlank(message = "实际金额不能为空")
    @ApiModelProperty(value = "实际金额")
    private String actualAmount;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "角标")
    private String index;

}
