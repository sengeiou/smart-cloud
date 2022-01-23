package smart.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 *
 * 车位表
 * @版本： V3.1.0
 * @版权： 智慧停车公司
 * @作者： 开发平台组
 * @日期： 2021-11-12 13:48:03
 */
@Data
@TableName("p_parking_space")
public class ParkingSpaceEntity  {
    /** 编号 */
    @TableId("F_ID")
    @JsonProperty("id")
    private String id;

    /** 停车场地ID */
    @TableField("F_PID")
    @JsonProperty("pid")
    private String pid;

    /** 设备序列号 */
    @TableField("F_DEVICE")
    @JsonProperty("device")
    private String device;

    /** 泊位名称 */
    @TableField("F_NAME")
    @JsonProperty("name")
    private String name;

    /** 车位类型：0:默认 1：共享 */
    @TableField("F_TYPE")
    @JsonProperty("type")
    private String type;

    /** 是否充电桩车位 0:否 1：是 */
    @TableField("F_ISCHARGING")
    @JsonProperty("ischarging")
    private String ischarging;

    /** 经度 */
    @TableField("F_LON")
    @JsonProperty("lon")
    private String lon;

    /** 纬度 */
    @TableField("F_LAT")
    @JsonProperty("lat")
    private String lat;

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
