package smart.message.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 消息接收
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Data
@TableName("base_messagereceive")
public class MessageReceiveEntity {
    /**
     * 收件主键
     */
    @TableId("F_ID")
    private String id;

    /**
     * 消息主键
     */
    @TableField("F_MESSAGEID")
    private String messageId;

    /**
     * 用户主键
     */
    @TableField("F_USERID")
    private String userId;

    /**
     * 是否阅读
     */
    @TableField("F_ISREAD")
    private Integer isRead;

    /**
     * 阅读时间
     */
    @TableField("F_READTIME")
    private Date readTime;

    /**
     * 阅读次数
     */
    @TableField("F_READCOUNT")
    private Integer readCount;
}
