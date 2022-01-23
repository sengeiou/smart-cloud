package smart.roadtooth.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ImageVO {
    @ApiModelProperty(value = "图片名称")
    private String fileName;
}
