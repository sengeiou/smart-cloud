package smart.base.model.cacheManage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CacheManageListVO {
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "过期时间",example = "1")
    private long overdueTime;
}
