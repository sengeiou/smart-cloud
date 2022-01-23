package smart.scheduletask.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 定时任务
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Data
@TableName("base_timetask")
public class TimeTaskEntity {
    /**
     * 定时任务主键
     */
    @TableId("F_ID")
    private String id;

    /**
     * 任务编码
     */
    @TableField("F_ENCODE")
    private String enCode;

    /**
     * 任务名称
     */
    @TableField("F_FULLNAME")
    private String fullName;

    /**
     * 执行类型 1.接口 2.存储过程
     */
    @TableField("F_EXECUTETYPE")
    private String executeType;

    /**
     * 执行内容
     */
    @TableField("F_EXECUTECONTENT")
    private String executeContent;

    /**
     * 执行周期
     */
    @TableField("F_EXECUTECYCLEJSON")
    private String executeCycleJson;

    /**
     * 最后运行时间
     */
    @TableField("F_LASTRUNTIME")
    private Date lastRunTime;

    /**
     * 下次运行时间
     */
    @TableField("F_NEXTRUNTIME")
    private Date nextRunTime;

    /**
     * 运行次数
     */
    @TableField("F_RUNCOUNT")
    private Integer runCount;

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
