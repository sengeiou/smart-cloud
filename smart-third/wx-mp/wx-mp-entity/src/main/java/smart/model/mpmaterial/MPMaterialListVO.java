package smart.model.mpmaterial;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MPMaterialListVO {
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "图文标题")
    private String title;
    @ApiModelProperty(value = "作者")
    private String author;
    @ApiModelProperty(value = "上传素材时间")
    private long uploadDate;
    @ApiModelProperty(value = "上传素材用户")
    private String uploadUser;
    @ApiModelProperty(value = "有效标志")
    private Integer enabledMark;
    @ApiModelProperty(value = "图文消息的描述")
    private String digest;
    @ApiModelProperty(value = "原文链接")
    private String contentSourceUrl;
    @ApiModelProperty(value = "附件名称")
    private String fileJson;
    @ApiModelProperty(value = "图片路径")
    private String returnUrl;
    @ApiModelProperty(value = "公众号素材Id")
    private String mediaId;
}
