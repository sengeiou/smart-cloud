package smart;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 公众号素材
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Data
@TableName("wechat_mpmaterial")
public class MPMaterialEntity {
    /**
     * 主键
     */
    @TableId("F_ID")
    private String id;

    /**
     * 公众号主键
     */
    @TableField("F_OPENID")
    private String openId;

    /**
     * 上传素材类型
     */
    @TableField("F_MATERIALSTYPE")
    private Integer materialsType;

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
     * 上传素材时间
     */
    @TableField("F_UPLOADDATE")
    private Date uploadDate;

    /**
     * 上传素材用户
     */
    @TableField("F_UPLOADUSER")
    private String uploadUser;

    /**
     * 有效标志
     */
    @TableField("F_ENABLEDMARK")
    private Integer enabledMark;

    @TableField("F_INTRODUCTION")
    private String introduction;

    /**
     * 上传返回路径
     */
    @TableField("F_RETURNURL")
    private String returnUrl;

    /**
     * 公众号素材Id
     */
    @TableField("F_MEDIAID")
    private String mediaId;
}
