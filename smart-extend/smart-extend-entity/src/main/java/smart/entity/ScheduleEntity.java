package smart.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 日程安排
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Data
@TableName("ext_schedule")
public class ScheduleEntity {
    /**
     * 日程主键
     */
    @TableId("F_ID")
    private String id;

    /**
     * 日程标题
     */
    @TableField("F_TITLE")
    private String title;

    /**
     * 日程内容
     */
    @TableField("F_CONTENT")
    private String content;

    /**
     * 日程颜色
     */
    @TableField("F_COLOUR")
    private String colour;

    /**
     * 颜色样式
     */
    @TableField("F_COLOURCSS")
    private String colourCss;

    /**
     * 开始时间
     */
    @TableField("F_STARTTIME")
    private Date startTime;

    /**
     * 结束时间
     */
    @TableField("F_ENDTIME")
    private Date endTime;

    /**
     * App提醒
     */
    @TableField("F_APPALERT")
    private Integer appAlert;

    /**
     * 提醒设置
     */
    @TableField("F_EARLY")
    private Integer early;

    /**
     * 邮件提醒
     */
    @TableField("F_MAILALERT")
    private Integer mailAlert;

    /**
     * 微信提醒
     */
    @TableField("F_WECHATALERT")
    private Integer weChatAlert;

    /**
     * 短信提醒
     */
    @TableField("F_MOBILEALERT")
    private Integer mobileAlert;

    /**
     * 系统提醒
     */
    @TableField("F_SYSTEMALERT")
    private Integer systemAlert;

    /**
     * 描述
     */
    @TableField("F_DESCRIPTION")
    private String description;

    /**
     * 排序码
     */
    @TableField("F_SORTCODE")
    private Long sortCode;

    /**
     * 有效标志
     */
    @TableField("F_ENABLEDMARK")
    private Integer enabledMark;

    /**
     * 创建时间
     */
    @TableField(value = "F_CREATORTIME",fill = FieldFill.INSERT)
    private Date creatorTime;

    /**
     * 创建用户
     */
    @TableField(value = "F_CREATORUSERID",fill = FieldFill.INSERT)
    private String creatorUserId;

    /**
     * 修改时间
     */
    @TableField(value = "F_LASTMODIFYTIME",fill = FieldFill.UPDATE)
    private Date lastModifyTime;

    /**
     * 修改用户
     */
    @TableField(value = "F_LASTMODIFYUSERID",fill = FieldFill.UPDATE)
    private String lastModifyUserId;

    /**
     * 删除标志
     */
    @TableField("F_DELETEMARK")
    private Integer deleteMark;

    /**
     * 删除时间
     */
    @TableField("F_DELETETIME")
    private Date deleteTime;

    /**
     * 删除用户
     */
    @TableField("F_DELETEUSERID")
    private String deleteUserId;
}
