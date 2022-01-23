package smart.engine.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 流程引擎
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Data
@TableName("flow_engine")
public class FlowEngineEntity {
    /**
     * 流程主键
     */
    @TableId("F_ID")
    private String id;

    /**
     * 流程编码
     */
    @TableField("F_ENCODE")
    private String enCode;

    /**
     * 流程名称
     */
    @TableField("F_FULLNAME")
    private String fullName;

    /**
     * 流程类型
     */
    @TableField("F_TYPE")
    private int type;

    /**
     * 流程分类
     */
    @TableField("F_CATEGORY")
    private String category;

    /**
     * 可见类型 0-全部可见、1-指定经办
     */
    @TableField("F_VISIBLETYPE")
    private Integer visibleType;

    /**
     * 图标
     */
    @TableField("F_ICON")
    private String icon;

    /**
     * 图标背景色
     */
    @TableField("F_ICONBACKGROUND")
    private String iconBackground;

    /**
     * 流程版本
     */
    @TableField("F_VERSION")
    private String version;

    /**
     * 表单字段
     */
    @TableField("F_FormTemplateJson")
    private String formData;

    /**
     * 表单分类
     */
    @TableField("F_FORMTYPE")
    private Integer formType;

    /**
     * 流程引擎
     */
    @TableField("F_FLOWTEMPLATEJSON")
    private String flowTemplateJson;

    /**
     * 描述
     */
    @TableField("F_DESCRIPTION")
    private String description;

    /**
     * 列表
     */
    @TableField("F_TABLES")
    private String tables;

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
    private String creatorUser;

    /**
     * 修改时间
     */
    @TableField(value = "F_LASTMODIFYTIME",fill = FieldFill.UPDATE)
    private Date lastModifyTime;

    /**
     * 修改用户
     */
    @TableField(value = "F_LASTMODIFYUSERID",fill = FieldFill.UPDATE)
    private String lastModifyUser;

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
