package smart.model.bidata;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BigBigDataListVO {
    @ApiModelProperty(value = "编码")
    private String enCode;
    @ApiModelProperty(value = "名称")
    private String fullName;
    @ApiModelProperty(value = "主键id")
    private String id;
    @ApiModelProperty(value = "创建时间")
    private long creatorTime;
}
