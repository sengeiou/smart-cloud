package smart.form.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 出库单明细
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Data
@TableName("wform_outboundorderentry")
public class OutboundEntryEntity {
    /**
     * 主键
     */
    @TableId("F_ID")
    private String id;

    /**
     * 出库单号
     */
    @TableField("F_OUTBOUNDID")
    private String outboundId;

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
    private String qty;

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
     * 备注
     */
    @TableField("F_DESCRIPTION")
    private String description;

    /**
     * 排序码
     */
    @TableField("F_SORTCODE")
    private Long sortCode;
}
