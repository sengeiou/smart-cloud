package smart;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 消息发送
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Data
@TableName("wechat_qymessage")
public class QYMessageEntity {
    /**
     * 主键
     */
    @TableId("F_ID")
    private String id;

    /**
     * 消息类型
     */
    @TableField("F_MSGTYPE")
    private Integer msgType;

    /**
     * 全员
     */
    @TableField("F_ALL")
    @JSONField(name = "all")
    private Integer fAll;

    /**
     * 保密状态
     */
    @TableField("F_SAFE")
    private Integer safe;

    /**
     * 成员主键集
     */
    @TableField("F_TOUSERID")
    private String toUserId;

    /**
     * 部门主键集
     */
    @TableField("F_TOPARTY")
    private String toParty;

    /**
     * 标签主键集
     */
    @TableField("F_TOTAG")
    private String toTag;

    /**
     * 企业应用主键
     */
    @TableField("F_AGENTID")
    private String agentId;

    /**
     * 媒体主键
     */
    @TableField("F_MEDIAID")
    private String mediaId;

    /**
     * 图文标题
     */
    @TableField("F_TITLE")
    private String title;

    /**
     * 图文作者
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
     * 摘要
     */
    @TableField("F_ABSTRACT")
    @JSONField(name = "abstract")
    private String fabstract;

    /**
     * 封面状态
     */
    @TableField("F_SHOWCOVERPIC")
    private Integer showCoverPic;

    /**
     * 发送时间
     */
    @TableField("F_SENDDATE")
    private Date sendDate;

    /**
     * 发送人
     */
    @TableField("F_SENDUSERID")
    private String sendUserId;

    /**
     * 文本内容
     */
    @TableField("F_TXTCONTENT")
    private String txtContent;

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
     * 状态
     */
    @TableField("F_ENABLEDMARK")
    private Integer enabledMark;

    /**
     * 无效用户
     */
    @TableField("F_INVALIDUSER")
    private String invalidUser;

    /**
     * 无效机构
     */
    @TableField("F_INVALIDPARTY")
    private String invalidParty;

    /**
     * 无效标签
     */
    @TableField("F_INVALIDTAG")
    private String invalidTag;

    /**
     * 备注
     */
    @TableField("F_DESCRIPTION")
    private String description;

    /**
     * 图片链接
     */
    @TableField("F_PICURL")
    private String picUrl;
}
