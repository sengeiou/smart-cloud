package smart.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 *
 * 车辆信息表
 * @版本： V3.1.0
 * @版权： 智慧停车公司
 * @作者： 开发平台组
 * @日期： 2021-11-17 11:44:41
 */
@Data
@TableName("p_customer_car")
public class CustomerCarEntity  {
    /** 主键id */
    @TableId("F_ID")
    @JsonProperty("id")
    private String id;

    /** 所属车主用户id */
    @TableField("F_CUID")
    @JsonProperty("cuid")
    private String cuid;

    /** 车牌号 */
    @TableField("F_PLATENUMBER")
    @JsonProperty("platenumber")
    private String platenumber;

    /** 是否默认车牌 0：否 1：是 */
    @TableField("F_ISDEFAULTPLATE")
    @JsonProperty("isdefaultplate")
    private String isdefaultplate;

    /** 号牌类型 plateType 系统字典 */
    @TableField("F_PLATETYPE")
    @JsonProperty("platetype")
    private String platetype;

    /** 车辆类型 carType 系统字典 */
    @TableField("F_CARTYPE")
    @JsonProperty("cartype")
    private String cartype;

    /** 车辆识别代号VIN */
    @TableField("F_VIN")
    @JsonProperty("vin")
    private String vin;

    /** 使用限制 0：无限制 1：黑名单 2：其它 */
    @TableField("F_LIMITEDUSE")
    @JsonProperty("limiteduse")
    private String limiteduse;

    /** 有效标志 */
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

    /** 修改时间 */
    @TableField("F_LASTMODIFYTIME")
    @JsonProperty("lastmodifytime")
    private Date lastmodifytime;

    /** 修改用户 */
    @TableField("F_LASTMODIFYUSERID")
    @JsonProperty("lastmodifyuserid")
    private String lastmodifyuserid;

}
