package smart.base.model.monitor;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MemoryModel {
    @ApiModelProperty(value = "总内存")
    private String total;
    @ApiModelProperty(value = "空闲内存")
    private String available;
    @ApiModelProperty(value = "已使用")
    private String used;
    @ApiModelProperty(value = "已使用百分比")
    private String usageRate;
}
