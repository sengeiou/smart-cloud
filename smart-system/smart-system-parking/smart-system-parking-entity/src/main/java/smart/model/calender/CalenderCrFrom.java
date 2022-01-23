package smart.model.calender;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CalenderCrFrom {
    /**日期*/
    @JsonProperty("date")
    private String date;

    /**标识 0：工作日或者日历日 1:休息日 2:特殊日*/
    @JsonProperty("mark")
    private Integer mark;

    /**日历名称*/
    @JsonProperty("name")
    private String name;

    /** 创建时间 */
    @JsonProperty("creatortime")
    private String creatortime;

    /** 创建用户 */
    @JsonProperty("creatoruserid")
    private String creatoruserid;

    /** 最后一次修改时间 */
    @JsonProperty("lastmodifytime")
    private String lastmodifytime;

    /** 最后一次修改用户 */
    @JsonProperty("lastmodifyuserid")
    private String lastmodifyuserid;
}
