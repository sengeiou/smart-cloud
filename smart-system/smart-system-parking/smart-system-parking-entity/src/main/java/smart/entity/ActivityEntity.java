package smart.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * 活动表
 *
 * @版本： V3.1.0
 * @版权： 智慧停车公司
 * @作者： 开发平台组
 * @日期： 2021-12-30 17:41:03
 */
@Data
@TableName("p_activity")
public class ActivityEntity {
    /**
     * 主键id
     */
    @TableId("F_ID")
    @JsonProperty("id")
    private String id;

    /**
     * 活动名称
     */
    @TableField("F_NAME")
    @JsonProperty("name")
    private String name;

    /**
     * 活动描述
     */
    @TableField("F_DEPICT")
    @JsonProperty("depict")
    private String depict;

    /**
     * 缩略图url
     */
    @TableField("F_THUMBNAIL")
    @JsonProperty("thumbnail")
    private String thumbnail;

    /**
     * 活动类型：0充值赠送类;  1满减折扣类(数据字典)
     */
    @TableField("F_ACTIVITYTYPE")
    @JsonProperty("activitytype")
    private String activitytype;

    /**
     * 是否与其他活动互斥:  0互斥;  1不互斥
     */
    @TableField("F_MUTEX")
    @JsonProperty("mutex")
    private String mutex;

    /**
     * 可重复参与次数
     */
    @TableField("F_PARTICIPATE")
    @JsonProperty("participate")
    private String participate;

    /**
     * 活动开始时间
     */
    @TableField("F_STARTTIME")
    @JsonProperty("starttime")
    private Date starttime;

    /**
     * 活动结束时间
     */
    @TableField("F_ENDTIME")
    @JsonProperty("endtime")
    private Date endtime;

    /**
     * 活动状态: 0未开始;  1进行中;  2已结束
     */
    @TableField("F_STATUS")
    @JsonProperty("status")
    private String status;

    /**
     * 活动目标
     */
    @TableField("F_GOAL")
    @JsonProperty("goal")
    private String goal;

    /**
     * 活动发起人
     */
    @TableField("F_ORIGINATOR")
    @JsonProperty("originator")
    private String originator;

    /**
     * 有效标志
     */
    @TableField("F_ENABLEDMARK")
    @JsonProperty("enabledmark")
    private String enabledmark;

    /**
     * 创建时间
     */
    @TableField("F_CREATORTIME")
    @JsonProperty("creatortime")
    private Date creatortime;

    /**
     * 创建用户
     */
    @TableField("F_CREATORUSERID")
    @JsonProperty("creatoruserid")
    private String creatoruserid;

    /**
     * 修改时间
     */
    @TableField("F_LASTMODIFYTIME")
    @JsonProperty("lastmodifytime")
    private Date lastmodifytime;

    /**
     * 修改用户
     */
    @TableField("F_LASTMODIFYUSERID")
    @JsonProperty("lastmodifyuserid")
    private String lastmodifyuserid;

}
