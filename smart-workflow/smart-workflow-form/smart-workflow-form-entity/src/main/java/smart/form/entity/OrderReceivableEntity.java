package smart.form.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单收款
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Data
@TableName("ext_orderreceivable")
public class OrderReceivableEntity {
    /**
     * 订单收款主键
     */
    @TableId("F_ID")
    private String id;

    /**
     * 订单主键
     */
    @TableField("F_ORDERID")
    private String orderId;

    /**
     * 收款摘要
     *
     */
    @TableField("F_ABSTRACT")
    @JSONField(name = "abstract")
    private String fabstract;

    /**
     * 收款日期
     */
    @TableField("F_RECEIVABLEDATE")
    private Date receivableDate;

    /**
     * 收款比率
     */
    @TableField("F_RECEIVABLERATE")
    private BigDecimal receivableRate;

    /**
     * 收款金额
     */
    @TableField("F_RECEIVABLEMONEY")
    private BigDecimal receivableMoney;

    /**
     * 收款方式
     */
    @TableField("F_RECEIVABLEMODE")
    private String receivableMode;

    /**
     * 收款状态
     */
    @TableField("F_RECEIVABLESTATE")
    private Integer receivableState;

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
