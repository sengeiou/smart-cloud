package smart.model.activity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * Activity模型
 *
 * @版本： V3.1.0
 * @版权： 智慧停车公司
 * @作者： 开发平台组
 * @日期： 2021-12-30 17:41:03
 */
@Data
public class ActivityListVO {
    /**
     * 主键
     */
    private String id;

    /**
     * 活动名称
     */
    @JsonProperty("name")
    private String name;

    /**
     * 缩略图url
     */
    @JsonProperty("thumbnail")
    private String thumbnail;

    /**
     * 活动类型：0充值赠送类;  1满减折扣类(数据字典)
     */
    @JsonProperty("activitytype")
    private String activitytype;

    /**
     * 是否与其他活动互斥:  0互斥;  1不互斥
     */
    @JsonProperty("mutex")
    private String mutex;

    /**
     * 可重复参与次数
     */
    @JsonProperty("participate")
    private String participate;

    /**
     * 活动开始时间
     */
    //日期输出格式化
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("starttime")
    private Date starttime;

    /**
     * 活动结束时间
     */
    //日期输出格式化
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("endtime")
    private Date endtime;

    /**
     * 活动状态: 0未开始;  1进行中;  2已结束
     */
    @JsonProperty("status")
    private String status;

    /**
     * 活动目标
     */
    @JsonProperty("goal")
    private String goal;

    /**
     * 活动发起人
     */
    @JsonProperty("originator")
    private String originator;

    /**
     * 有效标志
     */
    @JsonProperty("enabledmark")
    private String enabledmark;

    /**
     * 创建时间
     */
    //日期输出格式化
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("creatortime")
    private Date creatortime;

    /**
     * 创建用户
     */
    @JsonProperty("creatoruserid")
    private String creatoruserid;

    /**
     * 修改时间
     */
    //日期输出格式化
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("lastmodifytime")
    private Date lastmodifytime;

    /**
     * 修改用户
     */
    @JsonProperty("lastmodifyuserid")
    private String lastmodifyuserid;

}
