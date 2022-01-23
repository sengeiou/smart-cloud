package smart;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 企业号部门
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Data
@TableName("wechat_qydepartment")
public class QYDepartmentEntity {
    /**
     * 机构主键
     */
    @TableId("F_ID")
    private String id;

    /**
     * 机构上级
     */
    @TableField("F_ORGANIZEID")
    private String organizeId;

    /**
     * 机构编码
     */
    @TableField("F_WECHATDEPTID")
    private Integer weChatDeptId;

    /**
     * 机构分类
     */
    @TableField("F_CATEGORY")
    private String category;

    /**
     * 机构编码
     */
    @TableField("F_ENCODE")
    private String enCode;

    /**
     * 机构名称
     */
    @TableField("F_FULLNAME")
    private String fullName;

    /**
     * 机构主管
     */
    @TableField("F_MANAGERID")
    private String managerId;

    /**
     * 扩展属性
     */
    @TableField("F_PROPERTYJSON")
    private String propertyJson;

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
     * 父级
     */
    @TableField("F_PARENTID")
    private String parentId;

    /**
     * 微信排序
     */
    @TableField("F_ORDER")
    private Integer forder;

    /**
     * 提交状态
     */
    @TableField("F_SUBMITSTATE")
    private String submitState;

    /**
     * 微信父级
     */
    @TableField("F_WECHATPARENTID")
    private Integer weChatParentId;

    /**
     * 同步状态
     */
    @TableField("F_SYNCSTATE")
    private Integer syncState;
}
