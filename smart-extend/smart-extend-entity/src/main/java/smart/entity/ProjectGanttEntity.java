package smart.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 项目计划
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Data
@TableName("ext_projectgantt")
public class ProjectGanttEntity {
    /**
     * 项目主键
     */
    @TableId("F_ID")
    private String id;

    /**
     * 项目上级
     */
    @TableField("F_PARENTID")
    private String parentId;

    /**
     * 项目主键
     */
    @TableField("F_PROJECTID")
    private String projectId;

    /**
     * 项目类型
     */
    @TableField("F_TYPE")
    private Integer type;

    /**
     * 项目编码
     */
    @TableField("F_ENCODE")
    private String enCode;

    /**
     * 项目名称
     */
    @TableField("F_FULLNAME")
    private String fullName;

    /**
     * 项目工期
     */
    @TableField("F_TIMELIMIT")
    private BigDecimal timeLimit;

    /**
     * 项目标记
     */
    @TableField("F_SIGN")
    private String sign;

    /**
     * 标记颜色
     */
    @TableField("F_SIGNCOLOR")
    private String signColor;

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
     * 当前进度
     */
    @TableField("F_SCHEDULE")
    private Integer schedule;

    /**
     * 负责人
     */
    @TableField("F_MANAGERIDS")
    private String managerIds;

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
    /**
     * 项目状态(1-进行中，2-已暂停)
     */
    @TableField("F_State")
    private Integer state;
}
