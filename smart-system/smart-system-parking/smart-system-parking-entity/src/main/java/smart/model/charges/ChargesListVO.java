package smart.model.charges;


import lombok.Data;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class ChargesListVO{
    /** 主键 */
    private String id;

    /** 24小时收费上限（分） */
    @JsonProperty("day_money_ceiling")
    private Long day_money_ceiling;

    /** 有效结束时间 */
    //日期输出格式化
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("end_time")
    private Date end_time;

    /** 创建时间 */
    //日期输出格式化
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("creatortime")
    private Date creatortime;

    /** 创建用户 */
    @JsonProperty("creatoruserid")
    private String creatoruserid;

    /** 有效标识 0：无效 1：有效 */
    @JsonProperty("enabledmark")
    private String enabledmark;

    /** 最后一次修改时间 */
    //日期输出格式化
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("lastmodifytime")
    private Date lastmodifytime;

    /** 最后一次修改用户 */
    @JsonProperty("lastmodifyuserid")
    private String lastmodifyuserid;

    /** 提前缴费的免费停车时长(分钟) */
    @JsonProperty("free_time")
    private Long free_time;

    /** 单笔收费上限（分） */
    @JsonProperty("money_ceiling")
    private Long money_ceiling;

    /** 单笔收费下限（分） */
    @JsonProperty("money_floor")
    private Long money_floor;

    /** 标准描述 */
    @JsonProperty("remark")
    private String remark;

    /** 收费标准名称 */
    @JsonProperty("standard_name")
    private String standard_name;

    /** 收费类型 1:自然日24小时 2:连续24小时 */
    @JsonProperty("standard_type")
    private Long standard_type;

    /** 有效开始时间 */
    //日期输出格式化
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("start_time")
    private Date start_time;

}
