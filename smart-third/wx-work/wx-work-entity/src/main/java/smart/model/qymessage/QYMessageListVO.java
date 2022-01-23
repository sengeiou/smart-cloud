package smart.model.qymessage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class QYMessageListVO {
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "消息类型(1-文本,2-图片,3-语音,4-视频，5-图文)")
    private Integer msgType;
    @ApiModelProperty(value = "状态")
    private Integer enabledMark;
    @ApiModelProperty(value = "发送给谁")
    private String toUser;
}
