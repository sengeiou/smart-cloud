package smart.model.device;


import lombok.Data;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 *
 * Device模型
 * @版本： V3.1.0
 * @版权： 智慧停车公司
 * @作者： 开发平台组
 * @日期： 2021-11-28 12:22:21
 */
@Data
public class DeviceListVO{
    /** 主键 */
    private String id;

    /** 设备名称 */
    @JsonProperty("name")
    private String name;

    /** 系统编号 */
    @JsonProperty("code")
    private String code;

    /** 设备SN */
    @JsonProperty("sn")
    private String sn;

    /** 心跳周期 */
    @JsonProperty("heartbeatcycle")
    private String heartbeatcycle;

    /** 上网方式 */
    @JsonProperty("networktype")
    private String networktype;

    /** 设备状态 */
    @JsonProperty("devicestatus")
    private String devicestatus;

    /** 在线状态 */
    @JsonProperty("onlinestatus")
    private String onlinestatus;

    /** 告警状态 */
    @JsonProperty("alarmstatus")
    private String alarmstatus;

    /** 设备属性JSON */
    @JsonProperty("devicepropertyjson")
    private String devicepropertyjson;

    /** 设备最后一次遥测数据JSON */
    @JsonProperty("lasttelemetrydatajson")
    private String lasttelemetrydatajson;

    /** 设备最后一次心跳数据JSON */
    @JsonProperty("lastheartbeatdatajson")
    private String lastheartbeatdatajson;

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
