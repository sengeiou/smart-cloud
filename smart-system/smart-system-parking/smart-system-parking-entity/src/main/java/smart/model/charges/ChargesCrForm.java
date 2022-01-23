package smart.model.charges;


import lombok.Data;
import java.util.List;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * Charges模型
 * @版本： V3.1.0
 * @版权： SmartCloud项目开发组
 * @作者： SmartCloud
 * @日期： 2021-12-10 15:18:55
 */
@Data
public class ChargesCrForm  {
    /** 24小时收费上限（分） */
    @JsonProperty("day_money_ceiling")
    private Integer day_money_ceiling;

    /** 有效结束时间 */
    @JsonProperty("end_time")
    private Long end_time;

    /** 创建时间 */
    @JsonProperty("creatortime")
    private String creatortime;

    /** 创建用户 */
    @JsonProperty("creatoruserid")
    private String creatoruserid;

    /** 有效标识 0：无效 1：有效 */
    @JsonProperty("enabledmark")
    private String enabledmark;

    /** 最后一次修改时间 */
    @JsonProperty("lastmodifytime")
    private String lastmodifytime;

    /** 最后一次修改用户 */
    @JsonProperty("lastmodifyuserid")
    private String lastmodifyuserid;

    /** 提前缴费的免费停车时长(分钟) */
    @JsonProperty("free_time")
    private Integer free_time;

    /** 单笔收费上限（分） */
    @JsonProperty("money_ceiling")
    private Integer money_ceiling;

    /** 单笔收费下限（分） */
    @JsonProperty("money_floor")
    private Integer money_floor;

    /** 标准描述 */
    @JsonProperty("remark")
    private String remark;

    /** 收费标准名称 */
    @JsonProperty("standard_name")
    private String standard_name;

    /** 收费类型 1:自然日24小时 2:连续24小时 */
    @JsonProperty("standard_type")
    private Integer standard_type;

    /** 有效开始时间 */
    @JsonProperty("start_time")
    private Long start_time;


}
