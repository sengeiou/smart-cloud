package smart.model.customercar;


import lombok.Data;
import java.util.List;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * CustomerCar模型
 * @版本： V3.1.0
 * @版权： 智慧停车公司
 * @作者： 开发平台组
 * @日期： 2021-11-17 11:44:41
 */
@Data
public class CustomerCarCrForm  {
    /** 所属车主用户id */
    @JsonProperty("cuid")
    private String cuid;

    /** 车牌号 */
    @JsonProperty("platenumber")
    private String platenumber;

    /** 是否默认车牌 0：否 1：是 */
    @JsonProperty("isdefaultplate")
    private String isdefaultplate;

    /** 号牌类型:1,小型汽车	,2大型汽车,3专用汽车,3特种车,3新能源汽车 */
    @JsonProperty("platetype")
    private String platetype;

    /** 车辆类型:1临时车 2月租车 3储值车 4免费车 */
    @JsonProperty("cartype")
    private String cartype;

    /** 车辆识别代号VIN */
    @JsonProperty("vin")
    private String vin;

    /** 使用限制 0：无限制 1：黑名单 2：其它 */
    @JsonProperty("limiteduse")
    private String limiteduse;

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
