package smart.base.model.monitor;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DiskModel {
    @ApiModelProperty(value = "硬盘总容量")
    private String total;
    @ApiModelProperty(value = "空闲硬盘")
    private String available;
    @ApiModelProperty(value = "已使用")
    private String used;
    @ApiModelProperty(value = "已使用百分比")
    private String usageRate;
}
