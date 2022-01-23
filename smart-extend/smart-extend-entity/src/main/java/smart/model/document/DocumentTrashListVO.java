package smart.model.document;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DocumentTrashListVO {
    @ApiModelProperty(value = "主键id")
    private String id;
    @ApiModelProperty(value = "文件夹名称")
    private String fullName;
    @ApiModelProperty(value = "删除日期")
    private String deleteTime;
    @ApiModelProperty(value = "大小")
    private String fileSize;
}
