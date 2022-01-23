package smart.model.mpmessage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class MPMessageListVO {
    private String id;
    @ApiModelProperty(value = "消息类型(1-文本,2-图片,3-语音,4-视频，5-图文)")
    private Integer msgType;
    @ApiModelProperty(value = "发送用户")
    private String sendUser;
    @ApiModelProperty(value = "发送日期")
    private Date sendDate;
    @ApiModelProperty(value = "发送状态(1-成功,0-失败)")
    private Integer enabledMark;
}
