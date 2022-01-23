package smart.portal.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("base_portal")
public class PortalEntity {
    @TableId("F_ID")
    private String id;


    @TableField("F_DESCRIPTION")
    private String description;


    @TableField("F_SORTCODE")
    private Long sortCode;


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


    @TableField("F_DELETEMARK")
    private Integer deleteMark;


    @TableField("F_DELETETIME")
    private Date deleteTime;


    @TableField("F_DELETEUSERID")
    private String deleteUserId;


    /**
     * 名称
     */
    @TableField("F_FULLNAME")
    private String fullName;


    /**
     * 编码
     */
    @TableField("F_ENCODE")
    private String enCode;


    /**
     * 分类(数据字典维护)
     */
    @TableField("F_CATEGORY")
    private String category;

    /**
     * 表单配置JSON
     */
    @TableField("F_FORMDATA")
    private String formData;


}
