package smart.model.calender;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CalenderJspVO {
    /**
     * 日历名称
     */
    private String name;
    /**
     * 日历年份
     */
    private Integer year;

    /**
     * 日期类型 0：日历日 1：休息日 2:特殊日期
     */
    private Integer type;
    /**
     * 日期list
     * list 为json 包含日期和classname
     */
    private List<JSONObject> list;

    /** 创建时间 */
    private Date creatortime;

    /** 创建用户 */
    private String creatoruserid;
}
