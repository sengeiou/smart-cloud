package smart.model.parkingspace;


import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 *
 * ParkingSpace模型
 * @版本： V3.1.0
 * @版权： 智慧停车公司
 * @作者： 开发平台组
 * @日期： 2021-11-12 13:48:03
 */
@Data
public class ParkingSpaceListVO{
    /** 主键 */
    private String id;

    /** 停车场地ID */
    @JsonProperty("pid")
    private String pid;

    /** 设备序列号 */
    @JsonProperty("device")
    private String device;

    /** 泊位名称 */
    @JsonProperty("name")
    private String name;

    /** 车位类型：0:默认 1：共享 */
    @JsonProperty("type")
    private String type;

    /** 是否充电桩车位 0:否 1：是 */
    @JsonProperty("ischarging")
    private String ischarging;

    /** 经度 */
    @JsonProperty("lon")
    private String lon;

    /** 纬度 */
    @JsonProperty("lat")
    private String lat;

    /** 有效标志 */
    @JsonProperty("enabledmark")
    private String enabledmark;

    /** 创建时间 */
    //日期输出格式化
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("creatortime")
    private Date creatortime;

    /** 创建用户 */
    @JsonProperty("creatoruserid")
    private String creatoruserid;

    /** 修改时间 */
    //日期输出格式化
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("lastmodifytime")
    private Date lastmodifytime;

    /** 修改用户 */
    @JsonProperty("lastmodifyuserid")
    private String lastmodifyuserid;

}
