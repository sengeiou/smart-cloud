package smart.message.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class MessageNoticeVO {
    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "发布人员")
    private String creatorUser;

    @ApiModelProperty(value = "发布时间",example = "1")
    private long  lastModifyTime;

    @ApiModelProperty(value = "状态(0-存草稿，1-已发布)",example = "1")
    private Integer enabledMark;
}
