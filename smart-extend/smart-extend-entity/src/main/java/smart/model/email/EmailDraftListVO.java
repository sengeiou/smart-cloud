package smart.model.email;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EmailDraftListVO {
    @ApiModelProperty(value = "附件")
    private String attachment;
    @ApiModelProperty(value = "发件人")
    private String id;
    @ApiModelProperty(value = "主题")
    private String subject;
    @ApiModelProperty(value = "收件人")
    private String recipient;
    @ApiModelProperty(value = "创建时间")
    private long creatorTime;
}
