package smart.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 *
 * 收费标准
 * @版本： V3.1.0
@author SmartCloud项目开发组
 * @日期： 2021-12-10 15:18:55
 */
@Data
@TableName("p_charges")
public class ChargesEntity  {
    /** id */
    @TableId("ID")
    private String id;

    /** 收费标准名称 */
    @TableField("STANDARD_NAME")
    private String standard_name;

    /** 收费类型 1:自然日24小时 2:连续24小时 */
    @TableField("STANDARD_TYPE")
    private String standard_type;

    /** 单笔收费上限（分） */
    @TableField("MONEY_CEILING")
    private String money_ceiling;

    /** 单笔收费下限（分） */
    @TableField("MONEY_FLOOR")
    private String money_floor;

    /** 24小时收费上限（分） */
    @TableField("DAY_MONEY_CEILING")
    private String day_money_ceiling;

    /** 有效开始时间 */
    @TableField("START_TIME")
    private Date start_time;

    /** 有效结束时间 */
    @TableField("END_TIME")
    private Date end_time;

    /** 提前缴费的免费停车时长(分钟) */
    @TableField("FREE_TIME")
    private String free_time;

    /** 标准描述 */
    @TableField("REMARK")
    private String remark;

    /** 扩展字段 */
    @TableField("EXPAND")
    private String expand;

    /** 有效标识 0：无效 1：有效 */
    @TableField("F_ENABLEDMARK")
    @JsonProperty("enabledmark")
    private String enabledmark;

    /** 创建时间 */
    @TableField("F_CREATORTIME")
    @JsonProperty("creatortime")
    private Date creatortime;

    /** 创建用户 */
    @TableField("F_CREATORUSERID")
    @JsonProperty("creatoruserid")
    private String creatoruserid;

    /** 最后一次修改时间 */
    @TableField("F_LASTMODIFYTIME")
    @JsonProperty("lastmodifytime")
    private Date lastmodifytime;

    /** 最后一次修改用户 */
    @TableField("F_LASTMODIFYUSERID")
    @JsonProperty("lastmodifyuserid")
    private String lastmodifyuserid;

}
