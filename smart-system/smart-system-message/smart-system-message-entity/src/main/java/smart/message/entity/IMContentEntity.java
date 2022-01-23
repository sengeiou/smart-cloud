package smart.message.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 聊天内容
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Data
@TableName("base_imcontent")
public class IMContentEntity {
    /**
     * 聊天主键
     */
    @TableId("F_ID")
    private String id;

    /**
     * 发送者
     */
    @TableField("F_SENDUSERID")
    private String sendUserId;

    /**
     * 发送时间
     */
    @TableField("F_SENDTIME")
    private Date sendTime;

    /**
     * 接收者
     */
    @TableField("F_RECEIVEUSERID")
    private String receiveUserId;

    /**
     * 接收时间
     */
    @TableField("F_RECEIVETIME")
    private Date receiveTime;

    /**
     * 内容
     */
    @TableField("F_CONTENT")
    private String content;

    /**
     * 内容
     */
    @TableField("F_CONTENTTYPE")
    private String contentType;

    /**
     * 状态
     */
    @TableField("F_STATE")
    private Integer state;

}
