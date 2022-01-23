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
 * 设备表
 * @版本： V3.1.0
 * @版权： 智慧停车公司
 * @作者： 开发平台组
 * @日期： 2021-11-28 12:22:22
 */
@Data
@TableName("p_device")
public class DeviceEntity  {
    /** 主键id */
    @TableId("F_ID")
    @JsonProperty("id")
    private String id;

    /** 设备名称 */
    @TableField("F_NAME")
    @JsonProperty("name")
    private String name;

    /** 系统编号 */
    @TableField("F_CODE")
    @JsonProperty("code")
    private String code;

    /** 设备SN */
    @TableField("F_SN")
    @JsonProperty("sn")
    private String sn;

    /** 设备类型 */
    @TableField("F_TYPE")
    @JsonProperty("type")
    private String type;

    /** 心跳周期 */
    @TableField("F_HEARTBEATCYCLE")
    @JsonProperty("heartbeatcycle")
    private String heartbeatcycle;

    /** 上网方式 */
    @TableField("F_NETWORKTYPE")
    @JsonProperty("networktype")
    private String networktype;

    /** 设备状态 */
    @TableField("F_DEVICESTATUS")
    @JsonProperty("devicestatus")
    private String devicestatus;

    /** 在线状态 */
    @TableField("F_ONLINESTATUS")
    @JsonProperty("onlinestatus")
    private String onlinestatus;

    /** 告警状态 */
    @TableField("F_ALARMSTATUS")
    @JsonProperty("alarmstatus")
    private String alarmstatus;

    /** 设备属性JSON */
    @TableField("F_DEVICEPROPERTYJSON")
    @JsonProperty("devicepropertyjson")
    private String devicepropertyjson;

    /** 设备最后一次遥测数据JSON */
    @TableField("F_LASTTELEMETRYDATAJSON")
    @JsonProperty("lasttelemetrydatajson")
    private String lasttelemetrydatajson;

    /** 设备最后一次心跳数据JSON */
    @TableField("F_LASTHEARTBEATDATAJSON")
    @JsonProperty("lastheartbeatdatajson")
    private String lastheartbeatdatajson;

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
