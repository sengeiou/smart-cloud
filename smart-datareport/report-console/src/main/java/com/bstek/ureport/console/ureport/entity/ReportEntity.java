package com.bstek.ureport.console.ureport.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("data_report")
public class ReportEntity {

    /**
     * 主键
     */
    @TableId("F_Id")
    private String id;

    /**
     * 报表名称
     */
    @TableField("F_FullName")
    private String fullName;

    /**
     * 报表内容
     */
    @TableField("F_Content")
    private String content;

    /**
     * 字典分类
     */
    @TableField("F_CategoryId")
    private String categoryId;

    /**
     * 编码
     */
    @TableField("F_EnCode")
    private String enCode;

    /**
     * 状态(0-默认，禁用，1-启用)
     */
    @TableField("F_EnabledMark")
    private Integer enabledMark;

    /**
     * 排序码
     */
    @TableField("F_SortCode")
    private Long sortCode;

    /**
     * 描述
     */
    @TableField("F_Description")
    private String description;

    /**
     * 创建时间
     */
    @TableField("F_CreatorTime")
    private Date creatorTime;

    /**
     * 创建用户
     */
    @TableField("F_CreatorUserId")
    private String creatorUser;

    /**
     * 编辑时间
     */
    @TableField("F_LastModifyTime")
    private Date lastModifyTime;

    /**
     * 编辑用户
     */
    @TableField("F_LastModifyUserId")
    private String lastModifyUser;

    /**
     * 删除标志
     */
    @TableField("F_DeleteMark")
    private Integer deleteMark;

    /**
     * 删除时间
     */
    @TableField("F_DeleteTime")
    private String deleteTime;

    /**
     * 删除用户
     */
    @TableField("F_DeleteUserId")
    private String deleteUserId;

}
