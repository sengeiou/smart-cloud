package smart.form.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单信息
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Data
@TableName("ext_order")
public class OrderEntity {
    /**
     * 订单主键
     */
    @TableId("F_ID")
    private String id;

    /**
     * 客户Id
     */
    @TableField("F_CUSTOMERID")
    private String customerId;

    /**
     * 客户名称
     */
    @TableField("F_CUSTOMERNAME")
    private String customerName;

    /**
     * 业务员Id
     */
    @TableField("F_SALESMANID")
    private String salesmanId;

    /**
     * 业务员
     */
    @TableField("F_SALESMANNAME")
    private String salesmanName;

    /**
     * 订单日期
     */
    @TableField("F_ORDERDATE")
    private Date orderDate;

    /**
     * 订单编码
     */
    @TableField("F_ORDERCODE")
    private String orderCode;

    /**
     * 运输方式
     */
    @TableField("F_TRANSPORTMODE")
    private String transportMode;

    /**
     * 发货日期
     */
    @TableField("F_DELIVERYDATE")
    private Date deliveryDate;

    /**
     * 发货地址
     */
    @TableField("F_DELIVERYADDRESS")
    private String deliveryAddress;

    /**
     * 付款方式
     */
    @TableField("F_PAYMENTMODE")
    private String paymentMode;

    /**
     * 应收金额
     */
    @TableField("F_RECEIVABLEMONEY")
    private BigDecimal receivableMoney;

    /**
     * 定金比率
     */
    @TableField("F_EARNESTRATE")
    private BigDecimal earnestRate;

    /**
     * 预付定金
     */
    @TableField("F_PREPAYEARNEST")
    private BigDecimal prepayEarnest;

    /**
     * 当前状态
     */
    @TableField("F_CURRENTSTATE")
    private Integer currentState;

    /**
     * 附件信息
     */
    @TableField("F_FILEJSON")
    private String fileJson;

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

    /**
     * 有效标志 1-正常、0-作废
     */
    @TableField("F_ENABLEDMARK")
    private Integer enabledMark;

    /**
     * 创建时间
     */
    @TableField(value = "F_CREATORTIME",fill = FieldFill.INSERT)
    private Date creatorTime;

    /**
     * 创建用户
     */
    @TableField(value = "F_CREATORUSERID",fill = FieldFill.INSERT)
    private String creatorUserId;

    /**
     * 修改时间
     */
    @TableField(value = "F_LASTMODIFYTIME",fill = FieldFill.UPDATE)
    private Date lastModifyTime;

    /**
     * 修改用户
     */
    @TableField(value = "F_LASTMODIFYUSERID",fill = FieldFill.UPDATE)
    private String lastModifyUserId;

    /**
     * 删除标志
     */
    @TableField("F_DELETEMARK")
    private Integer deleteMark;

    /**
     * 删除时间
     */
    @TableField("F_DELETETIME")
    private Date deleteTime;

    /**
     * 删除用户
     */
    @TableField("F_DELETEUSERID")
    private String deleteUserId;
}
