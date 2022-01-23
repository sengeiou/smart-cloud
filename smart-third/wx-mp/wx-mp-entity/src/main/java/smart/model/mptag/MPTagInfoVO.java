package smart.model.mptag;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MPTagInfoVO {
    @ApiModelProperty(value = "标签Id")
    private int id;
    @ApiModelProperty(value = "标签名字")
    private String name;
}
