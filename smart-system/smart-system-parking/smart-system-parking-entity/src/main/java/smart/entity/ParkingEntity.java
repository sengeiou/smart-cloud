package smart.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 *
 * 停车场
 * @版本： V3.1.0
 * @版权： SmartCloud项目开发组
 * @作者： SmartCloud
 * @日期： 2021-11-15 14:30:27
 */
@Data
@TableName("p_parking")
public class ParkingEntity  {
    /** 编号 */
    @TableId("F_ID")
    @JsonProperty("id")
    private String id;

    /** 所属片区ID */
    @TableField("F_PAID")
    @JsonProperty("paid")
    private String paid;

    /** 名称 */
    @TableField("F_NAME")
    @JsonProperty("name")
    private String name;

    /** 停车场实景图片 */
    @TableField("F_SHOWPICTURES")
    @JsonProperty("showpictures")
    private String showpictures;

    /** 停车场平面图片 */
    @TableField("F_PLANEPICTURE")
    @JsonProperty("planepicture")
    private String planepicture;

    /** 0:路侧停车场 1：商业园区 2：住宅社区 3：写字楼 4：交通枢纽 */
    @TableField("F_TYPE")
    @JsonProperty("type")
    private String type;

    /** 省份 */
    @TableField("F_LEVELADDRESS")
    @JsonProperty("leveladdress")
    private String leveladdress;

    /** 停车点地址（详细地址） */
    @TableField("F_ADDRESS")
    @JsonProperty("address")
    private String address;

    /** 经度 */
    @TableField("F_LNG")
    @JsonProperty("lng")
    private String lng;

    /** 纬度 */
    @TableField("F_LAT")
    @JsonProperty("lat")
    private String lat;

    /** 车场管理员 */
    @TableField("F_CONTACTUSERID")
    @JsonProperty("contactuserid")
    private String contactuserid;

    /** 泊位总数量 */
    @TableField("F_SPACETOTAL")
    @JsonProperty("spacetotal")
    private String spacetotal;

    /** 开放长租泊位数量 */
    @TableField("F_LONGRENTALSPACETOTAL")
    @JsonProperty("longrentalspacetotal")
    private String longrentalspacetotal;

    /** 开放预约车位数 */
    @TableField("F_RESERVEDSPACETOTAL")
    @JsonProperty("reservedspacetotal")
    private String reservedspacetotal;

    /** 充电桩数量 */
    @TableField("F_CHARGINGPILETOTAL")
    @JsonProperty("chargingpiletotal")
    private String chargingpiletotal;

    /** 是否自营 0：否 1：是 */
    @TableField("F_ISSELFSUPPORT")
    @JsonProperty("isselfsupport")
    private String isselfsupport;

    /** 是否支持预支付 0：否 1：是 */
    @TableField("F_ISSUPPORTADVANCEPAYMENT")
    @JsonProperty("issupportadvancepayment")
    private String issupportadvancepayment;

    /** 车场信息二维码 */
    @TableField("F_PARKINGINFOQRCODE")
    @JsonProperty("parkinginfoqrcode")
    private String parkinginfoqrcode;

    /** 停车场介绍 */
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
