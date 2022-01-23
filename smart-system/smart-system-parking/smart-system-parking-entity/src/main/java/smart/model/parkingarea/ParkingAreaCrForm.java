package smart.model.parkingarea;


import lombok.Data;
import java.util.List;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * ParkingArea模型
 * @版本： V3.1.0
 * @版权： SmartCloud项目开发组
 * @作者： SmartCloud
 * @日期： 2021-11-15 15:56:54
 */
@Data
public class ParkingAreaCrForm  {
    /** 名称 */
    @JsonProperty("name")
    private String name;

    /** 片区地址（详细地址） */
    @JsonProperty("address")
    private String address;

    /** 区层 默认0：地面停车 */
    @JsonProperty("floor")
    private String floor;

    /** 经度 */
    @JsonProperty("lon")
    private BigDecimal lon;

    /** 纬度 */
    @JsonProperty("lat")
    private BigDecimal lat;

    /** 片区巡检员打卡签到范围 */
    @JsonProperty("punchinrange")
    private Integer punchinrange;

    /** 片区巡检员 */
    @JsonProperty("contactuserid")
    private String contactuserid;

    /** 片区介绍 */
    @JsonProperty("description")
    private String description;

    /** 有效标志 */
    @JsonProperty("enabledmark")
    private String enabledmark;

    /** 创建时间 */
    @JsonProperty("creatortime")
    private String creatortime;

    /** 创建用户 */
    @JsonProperty("creatoruserid")
    private String creatoruserid;

    /** 修改时间 */
    @JsonProperty("lastmodifytime")
    private String lastmodifytime;

    /** 修改用户 */
    @JsonProperty("lastmodifyuserid")
    private String lastmodifyuserid;


}
