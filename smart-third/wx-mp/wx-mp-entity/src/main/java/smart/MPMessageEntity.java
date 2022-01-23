package smart;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 公众号群发消息
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Data
@TableName("wechat_mpmessage")
public class MPMessageEntity {
    /**
     * 主键
     */
    @TableId("F_ID")
    private String id;

    /**
     * 全部用户 0:不是全部 1.全部
     */
    @TableField("F_ISTOALL")
    private String isToAll;

    /**
     * 公众号主键
     */
    @TableField("F_OPENID")
    private String openId;

    /**
     * 群发消息类型
     */
    @TableField("F_MSGTYPE")
    private Integer msgType;

    /**
     * 图文标题
     */
    @TableField("F_TITLE")
    private String title;

    /**
     * 图文消息缩略图的media_id
     */
    @TableField("F_THUMBMEDIAID")
    private String thumbMediaId;

    /**
     * 作者
     */
    @TableField("F_AUTHOR")
    private String author;

    /**
     * 原文链接
     */
    @TableField("F_CONTENTSOURCEURL")
    private String contentSourceUrl;

    /**
     * 图文内容
     */
    @TableField("F_CONTENT")
    private String content;

    /**
     * 文本内容
     */
    @TableField("F_TXTCONTENT")
    private String txtContent;

    /**
     * 图文消息的描述
     */
    @TableField("F_DIGEST")
    private String digest;

    /**
     * 封面状态
     */
    @TableField("F_SHOWCOVERPIC")
    private String showCoverPic;

    /**
     * 缩略图的URL
     */
    @TableField("F_THUMBURL")
    private String thumbUrl;

    /**
     * 是否打开评论
     */
    @TableField("F_NEEDOPENCOMMENT")
    private Integer needOpenComment;

    /**
     * 是否粉丝才可评论
     */
    @TableField("F_ONLYFANSCANCOMMENT")
    private Integer onlyFansCanComment;

    /**
     * 消息ID
     */
    @TableField("F_MSGID")
    private String msgId;

    /**
     * 消息数据ID
     */
    @TableField("F_MSGDATAIID")
    private String msgDataIid;

    /**
     * 附件
     */
    @TableField("F_FILEJSON")
    private String fileJson;

    /**
     * 排序码
     */
    @TableField("F_SORTCODE")
    private Long sortCode;

    /**
     * 备注
     */
    @TableField("F_DESCRIPTION")
    private String description;

    /**
     * 发送时间
     */
    @TableField("F_SENDDATE")
    private Date sendDate;

    /**
     * 发送人
     */
    @TableField("F_SENDUSER")
    private String sendUser;

    /**
     * 有效标志
     */
    @TableField("F_ENABLEDMARK")
    private Integer enabledMark;

    /**
     * 标签Id
     */
    @TableField("F_TAGID")
    private String tagId;
}
