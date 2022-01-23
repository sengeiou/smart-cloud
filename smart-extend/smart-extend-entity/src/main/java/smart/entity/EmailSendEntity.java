package smart.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 邮件发送
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Data
@TableName("ext_emailsend")
public class EmailSendEntity {
    /**
     * 主键
     */
    @TableId("F_ID")
    private String id;

    /**
     * 类型 1-外部、0-内部
     */
    @TableField("F_TYPE")
    private Integer type;

    /**
     * 发件人
     */
    @TableField("F_SENDER")
    private String sender;

    /**
     * 收件人
     */
    @TableField("F_TO")
    private String recipient;

    /**
     * 抄送人
     */
    @TableField("F_CC")
    private String cc;

    /**
     * 密送人
     */
    @TableField("F_BCC")
    private String bcc;

    /**
     * 颜色
     */
    @TableField("F_COLOUR")
    private String colour;

    /**
     * 主题
     */
    @TableField("F_SUBJECT")
    private String subject;

    /**
     * 正文
     */
    @TableField("F_BODYTEXT")
    private String bodyText;

    /**
     * 附件
     */
    @TableField("F_ATTACHMENT")
    private String attachment;

    /**
     * 状态 -1-草稿、0-正在投递、1-投递成功
     */
    @TableField("F_STATE")
    private Integer state;

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
