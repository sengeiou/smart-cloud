package smart.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 知识文档
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Data
@TableName("ext_document")
public class DocumentEntity {
    /**
     * 文档主键
     */
    @TableId("F_ID")
    private String id;

    /**
     * 文档父级
     */
    @TableField("F_PARENTID")
    private String parentId;

    /**
     * 文档分类
     */
    @TableField("F_TYPE")
    private Integer type;

    /**
     * 文件名称
     */
    @TableField("F_FULLNAME")
    private String fullName;

    /**
     * 文件路径
     */
    @TableField("F_FILEPATH")
    private String filePath;

    /**
     * 文件大小
     */
    @TableField("F_FILESIZE")
    private String fileSize;

    /**
     * 文件后缀
     */
    @TableField("F_FILEEXTENSION")
    private String fileExtension;

    /**
     * 阅读数量
     */
    @TableField("F_READCCOUNT")
    private Integer readcCount;

    /**
     * 是否共享
     */
    @TableField("F_ISSHARE")
    private Integer isShare;

    /**
     * 共享时间
     */
    @TableField("F_SHARETIME")
    private Date shareTime;

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
