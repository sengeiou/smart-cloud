package smart.base.model.cacheManage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CacheManageInfoVO {
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "值")
    private String value;
}
