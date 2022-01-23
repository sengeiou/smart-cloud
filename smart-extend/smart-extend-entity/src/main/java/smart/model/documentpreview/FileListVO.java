package smart.model.documentpreview;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FileListVO {
    @ApiModelProperty(value = "主键id")
    private String fileId;
    @ApiModelProperty(value = "文件名称")
    private String fileName;
    @ApiModelProperty(value = "文件大小")
    private String fileSize;
    @ApiModelProperty(value = "修改时间")
    private String fileTime;
    @ApiModelProperty(value = "文件类型")
    private String fileType;
}
