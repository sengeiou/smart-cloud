package smart.form.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单明细
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Data
@TableName("ext_orderentry")
public class OrderEntryEntity {
    /**
     * 订单明细主键
     */
    @TableId("F_ID")
    private String id;

    /**
     * 订单主键
     */
    @TableField("F_ORDERID")
    private String orderId;

    /**
     * 商品Id
     */
    @TableField("F_GOODSID")
    private String goodsId;

    /**
     * 商品编码
     */
    @TableField("F_GOODSCODE")
    private String goodsCode;

    /**
     * 商品名称
     */
    @TableField("F_GOODSNAME")
    private String goodsName;

    /**
     * 规格型号
     */
    @TableField("F_SPECIFICATIONS")
    private String specifications;

    /**
     * 单位
     */
    @TableField("F_UNIT")
    private String unit;

    /**
     * 数量
     */
    @TableField("F_QTY")
    private BigDecimal qty;

    /**
     * 单价
     */
    @TableField("F_PRICE")
    private BigDecimal price;

    /**
     * 金额
     */
    @TableField("F_AMOUNT")
    private BigDecimal amount;

    /**
     * 折扣%
     */
    @TableField("F_DISCOUNT")
    private BigDecimal discount;

    /**
     * 税率%
     */
    @TableField("F_CESS")
    private BigDecimal cess;

    /**
     * 实际单价
     */
    @TableField("F_ACTUALPRICE")
    private BigDecimal actualPrice;

    /**
     * 实际金额
     */
    @TableField("F_ACTUALAMOUNT")
    private BigDecimal actualAmount;

    /**
     * 描述
     */
    @TableField("F_DESCRIPTION")
    private String description;

    /**
     * 排序码
     */
    @TableField("F_SORTCODE")
    private Long sortCode;
}
