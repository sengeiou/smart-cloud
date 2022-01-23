package smart.model.qymessage;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QYMessageSentForm {
    @NotNull(message = "必填")
    @ApiModelProperty(value = "消息类型(1-文本,2-图片,3-语音,4-视频，5-图文)")
    private Integer msgType;
    @NotNull(message = "必填")
    @ApiModelProperty(value = "是否所有用户(0-否,1-是)")
    private Integer all;
    @ApiModelProperty(value = "发送范围")
    private String toUserId;
    @ApiModelProperty(value = "文本内容，视频描述")
    private String txtContent;
    @ApiModelProperty(value = "图片，语音，视频")
    private String fileJson;
    @ApiModelProperty(value = "标题")
    private String title;
    @ApiModelProperty(value = "摘要")
    @JSONField(name = "abstract")
    private String fabstract;
    @ApiModelProperty(value = "作者")
    private String author;
    @ApiModelProperty(value = "图文内容")
    private String content;
    @ApiModelProperty(value = "原文链接")
    private String contentSourceUrl;
}
