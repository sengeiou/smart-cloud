package smart.model.mpmaterial;

import lombok.Data;

@Data
public class MPMaterialModel {
    //消息类型
    private int type;
    //公众号号用户主键
    private String openId;
    //  视频素材、图文消息的标题
    private String title;
    //视频素材的说明
    private String description;
    //视频素材的下载链接
    private String downUrl;
    //图文消息缩略图的media_id，可以在基础支持上传多媒体文件接口中获得
    private String thumbMediaId;
    //图文消息的作者
    private String author;
    //在图文消息页面点击“阅读原文”后的页面
    private String contentSourceUrl;
    //文本、图文消息页面的内容，支持HTML标签
    private String content;
    //图文消息的描述
    private String digest;
    // 是否显示封面，1为显示，0为不显示
    private String showCoverPic;
    //缩略图的URL
    private String thumbUrl;
    // 是否打开评论，0不打开，1打开
    private int needOpenComment;
    //是否粉丝才可评论，0所有人可评论，1粉丝才可评论
    private int onlyFansCanComment;
    //消息ID
    private String msgId;
    //消息数据ID
    private String msgDataId;
    //上传文件
    private String filejson;
    //文本内容
    private String txtcontent;
    //用于设定是否向全部用户发送
    private String isToAll;
    //上传素材主键
    private String mediaId;
    //获取素材名称
    private String name;
    //素材路径
    private String url;
    //这个素材的最后更新时间
    private String updateTime;
    //视频描述
    private String introduction;
    //上传返回路径
    private String fReturnUrl;
}
