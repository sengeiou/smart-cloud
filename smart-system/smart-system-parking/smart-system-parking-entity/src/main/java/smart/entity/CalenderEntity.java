package smart.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@TableName("p_calender")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalenderEntity {
    /** id */
    @TableId("F_id")
    private String id;

    /**日历名称*/
    @TableField("F_name")
    @JsonProperty("name")
    private String name;

    /** 日期 */
    @TableField("F_date")
    @JsonProperty("date")
    private String date;

    /** 标识 0：工作日或者日历日 1:休息日 2:特殊日
     * （日历日包含所有的日期，工作日为非休息日） */
    @TableField("F_mark")
    @JsonProperty("mark")
    private Integer mark;


    /** 创建时间 */
    @TableField("F_CREATORTIME")
    @JsonProperty("creatortime")
    private Date creatortime;

    /** 创建用户 */
    @TableField("F_CREATORUSERID")
    @JsonProperty("creatoruserid")
    private String creatoruserid;

    /** 最后一次修改时间 */
    @TableField("F_LASTMODIFYTIME")
    @JsonProperty("lastmodifytime")
    private Date lastmodifytime;

    /** 最后一次修改用户 */
    @TableField("F_LASTMODIFYUSERID")
    @JsonProperty("lastmodifyuserid")
    private String lastmodifyuserid;
}
