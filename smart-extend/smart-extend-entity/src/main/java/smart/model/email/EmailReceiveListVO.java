package smart.model.email;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EmailReceiveListVO {
    @ApiModelProperty(value = "是否已读(1-已读，0-未)")
    private Integer isRead;
    @ApiModelProperty(value = "附件")
    private String attachment;
    @ApiModelProperty(value = "时间")
    private Long fdate;
    @ApiModelProperty(value = "发件人")
    private String id;
    @ApiModelProperty(value = "是否标星(1-是,0-否)")
    private Integer starred;
    @ApiModelProperty(value = "发件人")
    private String sender;
    @ApiModelProperty(value = "主题")
    private String subject;
    @ApiModelProperty(value = "创建时间")
    private Long creatorTime;

}
