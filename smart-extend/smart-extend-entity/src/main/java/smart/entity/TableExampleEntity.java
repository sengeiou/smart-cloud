package smart.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 表格示例数据
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Data
@TableName("ext_tableexample")
public class TableExampleEntity {
    /**
     * 主键
     */
    @TableId("F_ID")
    private String id;

    /**
     * 交互日期
     */
    @TableField("F_INTERACTIONDATE")
    private Date interactionDate;

    /**
     * 项目编码
     */
    @TableField("F_PROJECTCODE")
    private String projectCode;

    /**
     * 项目名称
     */
    @TableField("F_PROJECTNAME")
    private String projectName;

    /**
     * 负责人
     */
    @TableField("F_PRINCIPAL")
    private String principal;

    /**
     * 立顶人
     */
    @TableField("F_JACKSTANDS")
    private String jackStands;

    /**
     * 项目类型
     */
    @TableField("F_PROJECTTYPE")
    private String projectType;

    /**
     * 项目阶段
     */
    @TableField("F_PROJECTPHASE")
    private String projectPhase;

    /**
     * 客户名称
     */
    @TableField("F_CUSTOMERNAME")
    private String customerName;

    /**
     * 费用金额
     */
    @TableField("F_COSTAMOUNT")
    private BigDecimal costAmount;

    /**
     * 已用金额
     */
    @TableField("F_TUNESAMOUNT")
    private BigDecimal tunesAmount;

    /**
     * 预计收入
     */
    @TableField("F_PROJECTEDINCOME")
    private BigDecimal projectedIncome;

    /**
     * 登记人
     */
    @TableField("F_REGISTRANT")
    private String registrant;

    /**
     * 登记时间
     */
    @TableField("F_REGISTERDATE")
    private Date registerDate;

    /**
     * 备注
     */
    @TableField("F_DESCRIPTION")
    private String description;

    /**
     * 标记
     */
    @TableField("F_SIGN")
    private String sign;

    /**
     * 批注列表Json
     */
    @TableField("F_POSTILJSON")
    private String postilJson;

    /**
     * 批注总数
     */
    @TableField("F_POSTILCOUNT")
    private Integer postilCount;

    /**
     * 有效标志
     */
    @TableField("F_ENABLEDMARK")
    private Integer enabledMark;

    /**
     * 排序码
     */
    @TableField("F_SORTCODE")
    private Long sortCode;

    /**
     * 编辑时间
     */
    @TableField(value = "F_LASTMODIFYTIME",fill = FieldFill.UPDATE)
    private Date lastModifyTime;

    /**
     * 编辑用户
     */
    @TableField(value = "F_LASTMODIFYUSERID",fill = FieldFill.UPDATE)
    private String lastModifyUserId;
}
