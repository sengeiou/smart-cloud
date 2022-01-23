package smart.model.parking;


import lombok.Data;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 *
 * Parking模型
 * @版本： V3.1.0
 * @版权： SmartCloud项目开发组
 * @作者： SmartCloud
 * @日期： 2021-11-15 14:30:27
 */
@Data
public class ParkingListVO{
    /** 主键 */
    private String id;

    /** 编号 */
    @JsonProperty("paid")
    private String paid;

    /** 名称 */
    @JsonProperty("name")
    private String name;

    /** 0:路侧停车场 1：商业园区 2：住宅社区 3：写字楼 4：交通枢纽 */
    @JsonProperty("type")
    private String type;

    /** 省份 */
    @JsonProperty("leveladdress")
    private String leveladdress;

    /** 停车点地址（详细地址） */
    @JsonProperty("address")
    private String address;

    /** 车场管理员 */
    @JsonProperty("contactuserid")
    private String[] contactuserid;

    /** 泊位总数量 */
    @JsonProperty("spacetotal")
    private Long spacetotal;

    /** 开放长租泊位数量 */
    @JsonProperty("longrentalspacetotal")
    private Long longrentalspacetotal;

    /** 开放预约车位数 */
    @JsonProperty("reservedspacetotal")
    private Long reservedspacetotal;

    /** 充电桩数量 */
    @JsonProperty("chargingpiletotal")
    private Long chargingpiletotal;

    /** 是否自营 0：否 1：是 */
    @JsonProperty("isselfsupport")
    private String isselfsupport;

    /** 是否支持预支付 0：否 1：是 */
    @JsonProperty("issupportadvancepayment")
    private String issupportadvancepayment;

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
