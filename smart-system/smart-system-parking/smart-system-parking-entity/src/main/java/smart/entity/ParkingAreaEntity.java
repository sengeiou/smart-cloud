package smart.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 *
 * 停车场片区
 * @版本： V3.1.0
 * @版权： SmartCloud项目开发组
 * @作者： SmartCloud
 * @日期： 2021-11-15 15:56:54
 */
@Data
@TableName("p_parking_area")
public class ParkingAreaEntity  {
    /** 编号 */
    @TableId("F_ID")
    @JsonProperty("id")
    private String id;

    /** 名称 */
    @TableField("F_NAME")
    @JsonProperty("name")
    private String name;

    /** 片区地址（详细地址） */
    @TableField("F_ADDRESS")
    @JsonProperty("address")
    private String address;

    /** 区层 默认0：地面停车场，楼层：n或-n层表示 */
    @TableField("F_Floor")
    @JsonProperty("floor")
    private String floor;

    /** 经度 */
    @TableField("F_LON")
    @JsonProperty("lon")
    private String lon;

    /** 纬度 */
    @TableField("F_LAT")
    @JsonProperty("lat")
    private String lat;


    /** 片区巡检员打卡签到范围 */
    @TableField("F_PUNCHINRANGE")
    @JsonProperty("punchinrange")
    private String punchinrange;

    /** 片区巡检员 */
    @TableField("F_CONTACTUSERID")
    @JsonProperty("contactuserid")
    private String contactuserid;

    /** 片区介绍 */
    @TableField("F_DESCRIPTION")
    @JsonProperty("description")
    private String description;

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
