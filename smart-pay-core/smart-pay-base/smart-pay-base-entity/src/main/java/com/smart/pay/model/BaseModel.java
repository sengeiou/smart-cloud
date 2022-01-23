package com.smart.pay.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <li>TODO</li>
 *
 * @author wangpeng
 * @date 2021/11/11 10:33
 * @see com.smart.model
 * @since 1.0
 **/
@Data
public class BaseModel implements Serializable {

    /**
     * 排序码
     */
    @TableField("F_SortCode")
    private Long sortCode;

    /**
     * 有效标志
     */
    @TableField("F_EnabledMark")
    private Integer enabledMark;

    /**
     * 创建时间
     */
    @TableField(value = "F_CreatorTime",fill = FieldFill.INSERT)
    private Date creatorTime;

    /**
     * 创建用户
     */
    @TableField(value = "F_CreatorUserId",fill = FieldFill.INSERT)
    private String creatorUserId;

    /**
     * 修改时间
     */
    @TableField(value = "F_LastModifyTime",fill = FieldFill.UPDATE)
    private Date lastModifyTime;

    /**
     * 修改用户
     */
    @TableField(value = "F_LastModifyUserId",fill = FieldFill.UPDATE)
    private String lastModifyUserId;

    /**
     * 删除标志
     */
    @TableField("F_DeleteMark")
    private Integer deleteMark;

    /**
     * 删除时间
     */
    @TableField("F_DeleteTime")
    private Date deleteTime;

    /**
     * 删除用户
     */
    @TableField("F_DeleteUserId")
    private String deleteUserId;

}
